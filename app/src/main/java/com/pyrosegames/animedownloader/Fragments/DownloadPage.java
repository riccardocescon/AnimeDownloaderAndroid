package com.pyrosegames.animedownloader.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pyrosegames.animedownloader.R;
import com.pyrosegames.animedownloader.Services.DownloadAnimeService;
import com.pyrosegames.animedownloader.Tasks.AnimeDownloaderTask;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DownloadPage extends Fragment {

    private FragmentManager fm;
    private String animeNameString;
    private List<String> urls;
    private AnimeUnityWebPage.OnWebAction callback;
    private int startEp = -1, endEp;

    private Activity mActivity;
    private ProgressDialog dialog;

    private String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/animeDownloader";

    public DownloadPage(Activity mActivity, FragmentManager fm, String animeName, List<String> epUrls, int startEp, int endEp, AnimeUnityWebPage.OnWebAction callback){
        this.fm = fm;
        this.animeNameString = animeName;
        this.callback = callback;
        this.urls = epUrls;
        this.startEp = startEp;
        this.endEp = endEp;
        this.mActivity = mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_page, container, false);

        EditText destiantionFolder = view.findViewById(R.id.download_folder_name_input);
        destiantionFolder.setText(destinationPath);

        TextView animeName = view.findViewById(R.id.download_anime_anime_name);
        animeName.setText(animeNameString);
        animeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open AnimeSaturn
                androidx.fragment.app.FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.download_fragment, new AnimeUnityWebPage(mActivity, callback));
                ft.commit();
            }
        });

        EditText fromEp = view.findViewById(R.id.download_anime_info_from_ep_input);
        if(startEp != -1) {
            fromEp.setText(String.valueOf(startEp));
        }

        EditText toEp = view.findViewById(R.id.download_anime_info_to_ep_input);
        if(startEp != -1) {
            toEp.setText(String.valueOf(endEp));
        }

        Button downloadButton = view.findViewById(R.id.download_anime_download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAnime();
            }
        });

        return view;
    }

    private void downloadAnime(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(mActivity);
                dialog.setMessage("I'm downloading ep : 0 of " + animeNameString);
                dialog.setCancelable(false);
                dialog.setInverseBackgroundForced(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }
        });

        Intent intent = new Intent(getContext(), DownloadAnimeService.class);
        String[] urlsArray = Arrays.copyOf(urls.toArray(), urls.toArray().length, String[].class);
        intent.putExtra("urls", urlsArray);
        intent.putExtra("startEp", startEp);
        intent.putExtra("endEp", endEp);
        intent.putExtra("animeNameString", animeNameString);
        mActivity.startService(intent);
    }
}
