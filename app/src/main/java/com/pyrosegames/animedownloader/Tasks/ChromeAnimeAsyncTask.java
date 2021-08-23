package com.pyrosegames.animedownloader.Tasks;

import android.content.Context;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.Capability;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChromeAnimeAsyncTask extends AsyncTask {

    public interface OnActionComplete{
        void onPageLoaded(String url, int firstEp, int lastEp);
    }

    private Context mContext;
    private String url = "";
    private OnActionComplete callback;

    public ChromeAnimeAsyncTask(Context mContext, String url, OnActionComplete callback){
        this.mContext = mContext;
        this.url = url;
        this.callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            URL oracle = new URL("https://www.animesaturn.it/anime/Death-Note-ITA-a");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            String inputLine;
            List<String> urls = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("<a href")){
                    if(inputLine.contains("https://www.animesaturn.it/ep/")) {
                        Log.d("URL_", "adding...");
                        String[] parts = inputLine.split("\"");
                        urls.add(parts[1]);
                    }
                }
            }
            for (String current : urls
                 ) {
                Log.d("URL_", current);
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
