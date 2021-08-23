package com.pyrosegames.animedownloader.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AnimePageParserTask extends AsyncTask {

    public interface OnParserComplete{
        void performAction(int firstEp, int lastEp, List<String> urls);
    }

    public enum ParseType{
        Episodes,
        UrlLink
    }

    private OnParserComplete callback;
    private String url;
    private ParseType parseType;

    public AnimePageParserTask(String url, ParseType parseType, OnParserComplete callback){
        this.url = url;
        this.callback = callback;
        this.parseType = parseType;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            List<String> urls = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("<a href")){
                    if(parseType == ParseType.Episodes){
                        if(inputLine.contains("https://www.animesaturn.it/ep/")) {
                            Log.d("URL_", "adding...");
                            String[] parts = inputLine.split("\"");
                            urls.add(parts[1]);
                        }
                    }else if(parseType == ParseType.UrlLink){
                        if(inputLine.contains("https://www.animesaturn.it/watch")){
                            String[] parts = inputLine.split("\"");
                            urls.add(parts[1]);
                        }
                    }

                }

            }
            for (String current : urls
            ) {
                Log.d("URL_", current);
            }
            in.close();
            callback.performAction(1, urls.size(), urls);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
