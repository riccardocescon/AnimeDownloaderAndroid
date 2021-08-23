package com.pyrosegames.animedownloader.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.pyrosegames.animedownloader.MainActivity;
import com.pyrosegames.animedownloader.Options;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.util.List;

public class AnimeDownloaderTask extends AsyncTask {

    private String animeNameString;
    private Activity mActivity;
    private List<String> urls;
    private int startEp;
    private int endEp;
    private ProgressDialog dialog;

    public AnimeDownloaderTask(Activity mActivity, List<String> urls, int startEp, int endEp, String animeNameString, ProgressDialog dialog){
        this.mActivity = mActivity;
        this.urls = urls;
        this.startEp = startEp;
        this.endEp = endEp;
        this.animeNameString = animeNameString;
        this.dialog = dialog;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        File animeDownloaderDir = new File(Options.getInstance(mActivity).getDestinationPath(), "animeDownloader");
        File currentAnimeDir = new File(animeDownloaderDir, animeNameString);

        int counter = 0;
        for(String url : urls) {
            counter++;
            if(counter < startEp)
                continue;
            final int finalCounter1 = counter;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("I'm downloading ep : " + finalCounter1 + " / " + endEp + " of " + animeNameString + "\nDownloaded : 0%");
                }
            });

            try {
                YoutubeDL.getInstance().init(mActivity.getApplication());

                YoutubeDLRequest request = new YoutubeDLRequest(url);
                request.addOption("-f", "best");
                request.addOption("-o", currentAnimeDir.getAbsolutePath() + "/" + animeNameString + "_" + counter);
                final int finalCounter = counter;
                YoutubeDL.getInstance().execute(request, new DownloadProgressCallback() {
                    @Override
                    public void onProgressUpdate(final float progress, final long etaInSeconds) {
                        Log.d("URL_", progress + "% (ETA " + etaInSeconds + " seconds)");

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setMessage("I'm downloading ep : " + finalCounter + " / " + endEp + " of " + animeNameString + "\nDownloaded : " + progress + "% (ETA " + etaInSeconds + " seconds)");
                            }
                        });

                        if(finalCounter == endEp && etaInSeconds < 2){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });

            } catch (YoutubeDLException | InterruptedException e) {
                Log.d("URL_", "failed to initialize youtubedl-android", e);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity.getBaseContext(), "I can't download this anime, sorry master : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                Intent homeIntent = new Intent(mActivity.getBaseContext(), MainActivity.class);
                mActivity.startActivity(homeIntent);
                return null;
            }
        }
        return null;
    }
}
