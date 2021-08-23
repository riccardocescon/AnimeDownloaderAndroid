package com.pyrosegames.animedownloader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Options {
    private static Options options;
    private String destinationPath;
    private String tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/temp/animeDownloader";

    private final String defaultPath = Environment.DIRECTORY_DOWNLOADS + "/animeDownloader";
    private static String defaultPathStatic = Environment.DIRECTORY_DOWNLOADS + "/animeDownloader";

    private final String destinationPathKey = "destinationPath";
    private static String destinationPathKeyStatic = "destinationPath";

    private DateFormat dateFormat;

    public enum DateFormats{
        DMY("DMY"),
        MDY("MDY");
        private String value;

        DateFormats(String value){
            this.value = value;
        }

        public static DateFormats getDateFormats(String source){
            List<DateFormats> formats = Arrays.asList(values());
            for(DateFormats current : formats){
                if(current.getValue().equals(source)) return current;
            }
            return DMY;
        }

        public String getValue(){ return value; }
    }

    private Options(final Activity mActivity, String jsonOptions){
        try {
            JSONObject json = new JSONObject(jsonOptions);
            if(json.has(destinationPathKey)){
                destinationPath = json.getString(destinationPathKey);
            }else{
                destinationPath = defaultPath;
            }
        }catch (final Exception e){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static Options getInstance(Activity mActivity){
        if(options == null){
            // Get the shared preferences File
            SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);

            //Retrieving the value
            String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");

            if(value.equals("")){
                SharedPreferences.Editor editor = sharedPrefs.edit();

                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(destinationPathKeyStatic, defaultPathStatic);

                    editor.putString(Utils.SHARED_PREFS_JSON, jsonObject.toString());
                    editor.apply();

                    value = jsonObject.toString();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            options = new Options(mActivity, value);
        }
        return options;
    }

    public String getDestinationPath(){ return destinationPath; }

    public void setDestinationPath(Activity mActivity, String newPath){
        destinationPath = newPath;

        // Get the shared preferences File
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try {
            JSONObject jsonObject = new JSONObject(value);
            jsonObject.remove(destinationPathKey);
            jsonObject.put(destinationPathKey, newPath);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.remove(Utils.SHARED_PREFS_JSON);
            editor.apply();
            editor.putString(Utils.SHARED_PREFS_JSON, jsonObject.toString());
            editor.apply();
            Log.i("Test", "New jsonPath : " + sharedPrefs.getString(Utils.SHARED_PREFS_JSON, ""));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveTheme(Activity mActivity, String themeName, boolean loadTheme){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try{
            JSONObject jsonObject = new JSONObject(value);
            if(jsonObject.has(Utils.OPTIONS_THEME_KEY)){
                jsonObject.remove(Utils.OPTIONS_THEME_KEY);
            }
            jsonObject.put(Utils.OPTIONS_THEME_KEY, themeName);
            editor.remove(Utils.SHARED_PREFS_JSON);
            editor.apply();
            editor.putString(Utils.SHARED_PREFS_JSON, jsonObject.toString());
            editor.apply();
            if(loadTheme)
                loadTheme(mActivity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadTheme(Activity mActivity){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try{
            String themeName = "";
            JSONObject jsonObject = new JSONObject(value);
            if(jsonObject.has(Utils.OPTIONS_THEME_KEY))
                themeName = jsonObject.getString(Utils.OPTIONS_THEME_KEY);
            else{
                saveTheme(mActivity, "Dark", false);
                themeName = "Dark";
            }
            OptionsActivity.Theme.loadThemeByName(mActivity, themeName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Resources.Theme loadTheme(Activity mActivity, Resources.Theme theme, boolean force){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try{
            String themeName = "";
            JSONObject jsonObject = new JSONObject(value);
            if(jsonObject.has(Utils.OPTIONS_THEME_KEY))
                themeName = jsonObject.getString(Utils.OPTIONS_THEME_KEY);
            else{
                saveTheme(mActivity, "Dark", false);
                themeName = "Dark";
            }
            return OptionsActivity.Theme.loadActivityThemeByName(themeName, theme, force);
        }catch (Exception e){
            e.printStackTrace();
        }
        return theme;
    }

    public String getCurrentTheme(Activity mActivity){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try{
            String themeName = "";
            JSONObject jsonObject = new JSONObject(value);
            if(jsonObject.has(Utils.OPTIONS_THEME_KEY))
                themeName = jsonObject.getString(Utils.OPTIONS_THEME_KEY);
            else{
                saveTheme(mActivity, "Dark", false);
                themeName = "Dark";
            }
            return themeName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Dark";
    }

    public ColorStateList getPrimaryColorStateList(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getPrimaryColorStateList(mActivity.getBaseContext(), theme);
    }
    public int getButtonTextColor(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getButtonTextColor(mActivity.getBaseContext(), theme);
    }

    public int getPrimaryColor(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getPrimaryColor(mActivity.getBaseContext(), theme);
    }

    public int getSecondaryColor(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getSecondaryColor(mActivity.getBaseContext(), theme);
    }

    public ColorStateList getListViewBGColor(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getListViewBG(mActivity.getBaseContext(), theme);
    }

    public OptionsActivity.Theme.ButtonsColor getButtonsColors(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getButtonsColor(mActivity.getBaseContext(), theme);
    }

    public int getThemeColor(Activity mActivity, OptionsActivity.Theme theme){
        return OptionsActivity.Theme.getThemeColor(mActivity, theme);
    }

    public ColorStateList getThemeColorStateList(Activity mActivity, OptionsActivity.Theme theme){
        return OptionsActivity.Theme.getThemeColorStateList(mActivity, theme);
    }

    public int getTheme(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getTheme(theme);
    }

    public int getBottomNicknameInfoColor(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return (int)OptionsActivity.Theme.getBottomInfoColor(mActivity, theme, true);
    }

    public ColorStateList getBottomNicknameInfoColorList(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return (ColorStateList)OptionsActivity.Theme.getBottomInfoColor(mActivity, theme, false);
    }

    public int getBottomVersionInfoColor(Activity mActivity){
        String theme = getCurrentTheme(mActivity);
        return OptionsActivity.Theme.getBottomVersionInfoColor(mActivity, theme);
    }



    public String getTempDir(){ return tempDir; }

    public void saveAnimeToGallery(Activity mActivity, AnimeGalleryInfo animeInfo){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_GALLERY_INFO, "");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Utils.GALLERY_ANIME_NAME, animeInfo.getAnimeName());
            jsonObject.put(Utils.GALLERY_ANIME_EPS, animeInfo.getAnimeEps());
            jsonObject.put(Utils.GALLERY_ANIME_DATE, animeInfo.getDownloadDate());

            if(value.equals("")){
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                editor.putString(Utils.SHARED_PREFS_GALLERY_INFO, jsonArray.toString());
                editor.apply();
                Log.d("JSON", "Now i have : " + sharedPrefs.getString(Utils.SHARED_PREFS_GALLERY_INFO, ""));
            }else{
                JSONArray source = new JSONArray(value);
                boolean found = false;
                for(int i = 0; i < source.length(); i++){
                    JSONObject current = source.getJSONObject(i);
                    if(current.getString(Utils.GALLERY_ANIME_NAME).equals(animeInfo.getAnimeName())){
                        current.remove(Utils.GALLERY_ANIME_DATE);
                        current.put(Utils.GALLERY_ANIME_DATE, animeInfo.getDownloadDate());
                        found = true;
                        break;
                    }
                }

                if(!found){
                    source.put(jsonObject);
                }

                editor.remove(Utils.SHARED_PREFS_GALLERY_INFO);
                editor.apply();
                editor.putString(Utils.SHARED_PREFS_GALLERY_INFO, source.toString());
                editor.apply();

                Log.d("JSON", "Now i have : " + sharedPrefs.getString(Utils.SHARED_PREFS_GALLERY_INFO, ""));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearAnimeGallery(Activity mActivity){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(Utils.SHARED_PREFS_GALLERY_INFO);
        editor.apply();
        Log.d("JSON", "Now i have : " + sharedPrefs.getString(Utils.SHARED_PREFS_GALLERY_INFO, ""));
    }

    public void clearAnimeGallery(Activity mActivity, String animeName){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_GALLERY_INFO, "");
        try{
            JSONArray jsonArray = new JSONArray(value);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString(Utils.GALLERY_ANIME_NAME).equals(animeName)){
                    jsonArray.remove(i);
                    break;
                }
            }

            editor.remove(Utils.SHARED_PREFS_GALLERY_INFO);
            editor.apply();
            editor.putString(Utils.SHARED_PREFS_GALLERY_INFO, jsonArray.toString());
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<AnimeGalleryInfo> getAnimeGallery(Activity mActivity){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_GALLERY_INFO, "");
        if(value.equals("")) return null;

        try {
            JSONArray jsonArray = new JSONArray(value);
            List<AnimeGalleryInfo> anime = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String animeName = jsonObject.getString(Utils.GALLERY_ANIME_NAME);
                String animeEps = jsonObject.getString(Utils.GALLERY_ANIME_EPS);
                String downloadDate = jsonObject.getString(Utils.GALLERY_ANIME_DATE);
                anime.add(new AnimeGalleryInfo(animeName, animeEps, downloadDate));
            }
            return anime;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public DateFormats getDateFormat(Activity mActivity){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try{
            JSONObject jsonObject = new JSONObject(value);
            if(!jsonObject.has(Utils.OPTIONS_DATE_FORMAT_KEY)){
                jsonObject.put(Utils.OPTIONS_DATE_FORMAT_KEY, DateFormats.DMY.getValue());
                editor.remove(Utils.SHARED_PREFS_GALLERY_INFO);
                editor.apply();
                editor.putString(Utils.SHARED_PREFS_GALLERY_INFO, jsonObject.toString());
                editor.apply();
                return DateFormats.DMY;
            }

            String format = jsonObject.getString(Utils.OPTIONS_DATE_FORMAT_KEY);
            return DateFormats.getDateFormats(format);
        }catch (Exception e){
            e.printStackTrace();
        }
        return DateFormats.DMY;
    }

    public void setDateFormat(Activity mActivity, DateFormats format){
        SharedPreferences sharedPrefs = mActivity.getBaseContext().getSharedPreferences(Utils.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String value = sharedPrefs.getString(Utils.SHARED_PREFS_JSON, "");
        try{
            JSONObject jsonObject = new JSONObject(value);
            if(jsonObject.has(Utils.OPTIONS_DATE_FORMAT_KEY)){
                jsonObject.remove(Utils.OPTIONS_DATE_FORMAT_KEY);
            }

            jsonObject.put(Utils.OPTIONS_DATE_FORMAT_KEY, format.getValue());
            editor.remove(Utils.SHARED_PREFS_JSON);
            editor.apply();
            editor.putString(Utils.SHARED_PREFS_JSON, jsonObject.toString());
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
