package com.pyrosegames.animedownloader.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pyrosegames.animedownloader.MainActivity;
import com.pyrosegames.animedownloader.R;
import com.pyrosegames.animedownloader.Tasks.AnimePageParserTask;
import com.pyrosegames.animedownloader.Tasks.ChromeAnimeAsyncTask;
import com.pyrosegames.animedownloader.Utils;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class AnimeUnityWebPage extends Fragment{

    private WebView webView;
    private Activity mActivity;
    private ProgressDialog dialog;

    public interface OnWebAction{
        void onNewPageOpened(String url, int firstEp, int lastEp);
        void onAllUrlFetched(List<String> urls);
    }
    private OnWebAction callback;

    public AnimeUnityWebPage(Activity mActivity, OnWebAction callback){
        this.mActivity = mActivity;
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anime_unity_web_page, container, false);

        IntentFilter filter = new IntentFilter(Utils.INTENT_FILTER_SELECT_ANIME);
        mActivity.registerReceiver(new Receiver(mActivity), filter);

        try{
            final List<String> fetcehdUrls = new ArrayList<>();

            webView = view.findViewById(R.id.download_web_view);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("https://www.animesaturn.it/");
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading (WebView view, final String url){
                    //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                    webView.loadUrl(url);
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

                    new AnimePageParserTask(url, AnimePageParserTask.ParseType.Episodes, new AnimePageParserTask.OnParserComplete() {
                        @Override
                        public void performAction(int firstEp, final int lastEp, List<String> urls) {

                            for(String currentRedirectPage : urls){
                                new AnimePageParserTask(currentRedirectPage, AnimePageParserTask.ParseType.UrlLink, new AnimePageParserTask.OnParserComplete() {
                                    @Override
                                    public void performAction(int firstEp, int finalEp, List<String> urls) {
                                        fetcehdUrls.add(urls.get(0));
                                        if(dialog != null && fetcehdUrls.size() == lastEp){
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                }).execute();
                            }

                            callback.onNewPageOpened(url, firstEp, lastEp);
                            callback.onAllUrlFetched(fetcehdUrls);
                        }
                    }).execute();
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
        return  null;
    }

    private class Receiver extends BroadcastReceiver {

        private Activity mActivity;

        public Receiver(Activity mActivity){
            this.mActivity = mActivity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean ended = intent.getBooleanExtra("info_ended", false);
        }
    }
}
