package com.pyrosegames.animedownloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.pyrosegames.animedownloader.Tasks.AnimePageParserTask;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class AAnimeSaturnWebPage extends Fragment {

    WebView webView;

    private Button selectButton, cancelButton;
    private ProgressDialog dialog;
    private String currentURL = "";

    private List<String> allUrls = new ArrayList<>();

    public interface OnWebEnded{
        void onAnimeSelected(String animeName, List<String> urls);
    }

    private OnWebEnded callback;

    public AAnimeSaturnWebPage(OnWebEnded callback){
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_anime_saturn_web_page, container, false);

        final Activity mActivity = getActivity();

        OptionsActivity.Theme.ButtonsColor buttonsColor = Options.getInstance(mActivity).getButtonsColors(mActivity);

        selectButton = view.findViewById(R.id.adownload_web_select_button);
        selectButton.setBackgroundColor(buttonsColor.getPositiveButtonColor());
        selectButton.setTextColor(buttonsColor.getPositiveTextColor());
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String animeName = extractAnimeName(currentURL);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog == null){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog = new ProgressDialog(mActivity);
                                    dialog.setMessage("I'm loading...");
                                    dialog.setCancelable(false);
                                    dialog.setInverseBackgroundForced(true);
                                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    dialog.show();
                                }
                            });
                        }
                    }
                });

                new AnimePageParserTask(currentURL, AnimePageParserTask.ParseType.Episodes, new AnimePageParserTask.OnParserComplete() {
                    @Override
                    public void performAction(int firstEp, final int lastEp, List<String> urls) {
                        for(String currentRedirectPage : urls){
                            new AnimePageParserTask(currentRedirectPage, AnimePageParserTask.ParseType.UrlLink, new AnimePageParserTask.OnParserComplete() {
                                @Override
                                public void performAction(int firstEp, int finalEp, List<String> urls) {
                                    allUrls.add(urls.get(0));
                                    if(dialog != null && allUrls.size() == lastEp){
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                            }
                                        });
                                        callback.onAnimeSelected(animeName, allUrls);
                                    }
                                }
                            }).execute();
                        }
                    }
                }).execute();
            }
        });

        cancelButton = view.findViewById(R.id.adownload_web_cancel_button);
        cancelButton.setBackgroundColor(buttonsColor.getNegativeButtonColor());
        cancelButton.setTextColor(buttonsColor.getNegativeTextColor());

        try{
            webView = view.findViewById(R.id.adownload_web_view);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("https://www.animesaturn.it/");
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading (WebView view, final String url){
                    //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                    webView.loadUrl(url);
                    currentURL = url;
                    return true;
                }
            });
            return view;
        }catch (Exception e){
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Catched error AnimeUnityWebPage", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    private String extractAnimeName(String url){
        String animeStaticUrl = "https://www.animesaturn.it/ep/";
        String[] parts = url.split("/");
        String name = parts[parts.length - 1];
        name = name.replace("-", "_");
        return name;
    }
}
