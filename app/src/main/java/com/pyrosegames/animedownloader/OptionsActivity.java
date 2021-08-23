package com.pyrosegames.animedownloader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.pyrosegames.animedownloader.Adapters.GallerySettingChoiceAdapter;
import com.pyrosegames.animedownloader.Adapters.ThemeAdapter;
import com.pyrosegames.animedownloader.Dialogs.GallerySettingsChoiceDialog;
import com.pyrosegames.animedownloader.Dialogs.ThemeDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionsActivity extends AppCompatActivity{

    private final int destinationFolderPathRequestCode = 9999;

    private Button destFolderText;

    private boolean firstLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Options options = Options.getInstance(this);

        destFolderText = findViewById(R.id.options_destiantion_folder_input);
        destFolderText.setText(options.getDestinationPath());
        destFolderText.setTextColor(options.getButtonTextColor(this));
        destFolderText.setBackgroundTintList(options.getPrimaryColorStateList(this));
        final Activity mActivity = this;
        destFolderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(OptionsActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .build();

                // get path that the user has chosen
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        String newDestinationPath = path + "/animeDownloader";
                        destFolderText.setText(newDestinationPath);
                        Options.getInstance(mActivity).setDestinationPath(mActivity, newDestinationPath);
                    }
                });

                // Show dialog whenever you want by
                chooser.show();
            }
        });

        String currenTheme = Options.getInstance(mActivity).getCurrentTheme(mActivity);
        String currentDateFormat = Options.getInstance(mActivity).getDateFormat(mActivity).getValue();


        Button themeButton = findViewById(R.id.options_theme_input);
        themeButton.setBackgroundTintList(options.getPrimaryColorStateList(this));
        themeButton.setTextColor(options.getButtonTextColor(this));
        themeButton.setText(currenTheme);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Theme> themes = Arrays.asList(Theme.values());
                new ThemeDialog(mActivity, mActivity, themes, new ThemeAdapter.ClickEvent() {
                    @Override
                    public void onThemeSelected(Theme selectedTheme) {
                        options.saveTheme(mActivity, selectedTheme.getName(), false);
                        if(!currenTheme.equals(selectedTheme.getName())){
                            new AlertDialog.Builder(mActivity)
                                    .setTitle("Info")
                                    .setMessage("Please close and then open your app again to avoid any issue with the new App Theme")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent reload = new Intent(getBaseContext(), OptionsActivity.class);
                                            reload.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(reload);
                                        }
                                    })
                                    .show();
                        }
                    }
                }).show();
            }
        });


        Button dateFormatButton = findViewById(R.id.options_date_format_input);
        dateFormatButton.setBackgroundTintList(options.getPrimaryColorStateList(this));
        dateFormatButton.setTextColor(options.getButtonTextColor(this));
        dateFormatButton.setText(currentDateFormat);
        dateFormatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Options.DateFormats> formats = Arrays.asList(Options.DateFormats.values());
                List<String> values = new ArrayList<>();
                for(Options.DateFormats current : formats){
                    values.add(current.getValue());
                }

                new GallerySettingsChoiceDialog(mActivity, mActivity, "Date Format", values, new GallerySettingChoiceAdapter.ClickEvent() {
                    @Override
                    public void onSettingClicked(int position) {
                        Options.DateFormats pickedFormat = formats.get(position);
                        options.setDateFormat(mActivity, pickedFormat);
                        dateFormatButton.setText(pickedFormat.getValue());
                    }
                }).show();
            }
        });
    }


    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme = Options.getInstance(this).loadTheme(this, theme, true);
        return theme;
    }

    public enum Theme{

        Dark("Dark"),
        Day("Day"),
        Fire("Fire"),
        Water("Water");

        private String name;
        Theme(String name){
            this.name = name;
        }

        public String getName(){ return name; }

        public static void loadThemeByName(Activity mActivity, String name){
            Theme selected = getThemeByName(name);
            switch (selected){
                case Dark:
                    mActivity.getBaseContext().setTheme(R.style.AnimeDatkTheme);
                    break;
                case Day:
                    mActivity.getBaseContext().setTheme(R.style.AnimeDayTheme);
                    break;
                case Water:
                    mActivity.getBaseContext().setTheme(R.style.AnimeWaterTheme);
                    break;
                case Fire:
                    mActivity.getBaseContext().setTheme(R.style.AnimeFireTheme);
                    break;
            }
        }

        public static int getTheme(String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return R.style.AnimeDatkTheme;
                case Day:
                    return R.style.AnimeDayTheme;
                case Water:
                    return R.style.AnimeWaterTheme;
                case Fire:
                    return R.style.AnimeFireTheme;
            }
            return -1;
        }

        public static Resources.Theme loadActivityThemeByName(String name, Resources.Theme theme, boolean force){
            Theme selected = getThemeByName(name);
            switch (selected){
                case Dark:
                    theme.applyStyle(R.style.AnimeDatkTheme, force);
                    break;
                case Day:
                    theme.applyStyle(R.style.AnimeDayTheme, force);
                    break;
                case Water:
                    theme.applyStyle(R.style.AnimeWaterTheme, force);
                    break;
                case Fire:
                    theme.applyStyle(R.style.AnimeFireTheme, force);
                    break;
            }
            return theme;
        }

        public static Object getBottomInfoColor(Context mContext, String selectedTheme, boolean integer){
            Theme theme = getThemeByName(selectedTheme);
            switch (theme){
                case Dark:
                    if(integer)
                        return ContextCompat.getColor(mContext, R.color.Dark_nickname);
                    else
                        return ContextCompat.getColorStateList(mContext, R.color.Dark_nickname);
                case Day:
                    if(integer)
                        return ContextCompat.getColor(mContext, R.color.Day_nickname);
                    else
                        return ContextCompat.getColorStateList(mContext, R.color.Day_nickname);
                case Water:
                    if(integer)
                        return ContextCompat.getColor(mContext, R.color.Water_nickname);
                    else
                        return ContextCompat.getColorStateList(mContext, R.color.Water_nickname);
                case Fire:
                    if(integer)
                        return ContextCompat.getColor(mContext, R.color.Fire_nickname);
                    else
                        return ContextCompat.getColorStateList(mContext, R.color.Fire_nickname);
            }
            return -1;
        }

        public static int getBottomVersionInfoColor(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return ContextCompat.getColor(mContext, R.color.Dark_version);
                case Day:
                    return ContextCompat.getColor(mContext, R.color.Day_version);
                case Water:
                    return ContextCompat.getColor(mContext, R.color.Water_version);
                case Fire:
                    return ContextCompat.getColor(mContext, R.color.Fire_version);
            }
            return -1;
        }

        public static int getThemeColor(Context mContext, Theme theme){
            switch (theme){
                case Dark:
                    return ContextCompat.getColor(mContext, R.color.Dark_background);
                case Day:
                    return ContextCompat.getColor(mContext, R.color.Day_background);
                case Water:
                    return ContextCompat.getColor(mContext, R.color.Water_background);
                case Fire:
                    return ContextCompat.getColor(mContext, R.color.Fire_background);
            }
            return -1;
        }

        public static ColorStateList getThemeColorStateList(Context mContext, Theme theme){
            switch (theme){
                case Dark:
                    return ContextCompat.getColorStateList(mContext, R.color.Dark_background);
                case Day:
                    return ContextCompat.getColorStateList(mContext, R.color.Day_background);
                case Water:
                    return ContextCompat.getColorStateList(mContext, R.color.Water_background);
                case Fire:
                    return ContextCompat.getColorStateList(mContext, R.color.Fire_background);
            }
            return null;
        }

        public static ColorStateList getPrimaryColorStateList(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return ContextCompat.getColorStateList(mContext, R.color.Dark_primary);
                case Day:
                    return ContextCompat.getColorStateList(mContext, R.color.Day_primary);
                case Water:
                    return ContextCompat.getColorStateList(mContext, R.color.Water_primary);
                case Fire:
                    return ContextCompat.getColorStateList(mContext, R.color.Fire_primary);
            }
            return null;
        }

        public static int getButtonTextColor(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return ContextCompat.getColor(mContext, R.color.Dark_button_text);
                case Day:
                    return ContextCompat.getColor(mContext, R.color.Day_button_text);
                case Water:
                    return ContextCompat.getColor(mContext, R.color.Water_button_text);
                case Fire:
                    return ContextCompat.getColor(mContext, R.color.Fire_button_text);
            }
            return  -1;
        }

        public static int getPrimaryColor(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return ContextCompat.getColor(mContext, R.color.Dark_primary);
                case Day:
                    return ContextCompat.getColor(mContext, R.color.Day_primary);
                case Water:
                    return ContextCompat.getColor(mContext, R.color.Water_primary);
                case Fire:
                    return ContextCompat.getColor(mContext, R.color.Fire_primary);
            }
            return  -1;
        }

        public static int getSecondaryColor(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return ContextCompat.getColor(mContext, R.color.Dark_secondary_text);
                case Day:
                    return ContextCompat.getColor(mContext, R.color.Day_seconary_text);
                case Water:
                    return ContextCompat.getColor(mContext, R.color.Water_seconary_text);
                case Fire:
                    return ContextCompat.getColor(mContext, R.color.Fire_seconary_text);
            }
            return  -1;
        }

        public static ColorStateList getListViewBG(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return ContextCompat.getColorStateList(mContext, R.color.Dark_dark_gray);
                case Day:
                    return ContextCompat.getColorStateList(mContext, R.color.Day_light_gray);
                case Water:
                    return ContextCompat.getColorStateList(mContext, R.color.Water_dark_light_blue);
                case Fire:
                    return ContextCompat.getColorStateList(mContext, R.color.Fire_dark_red);
            }
            return  null;
        }

        public static class ButtonsColor{
            private int positiveButtonColor, positiveTextColor;
            private int negativeButtonColor, negativeTextColor;

            private ColorStateList positiveButtonColorList, positiveTextColorList;
            private ColorStateList negativeButtonColorList, negativeTextColorList;

            public ButtonsColor(int pb, int pt, int nb, int nt, ColorStateList pbc, ColorStateList ptc, ColorStateList nbc, ColorStateList ntc){
                positiveButtonColor = pb;
                positiveTextColor = pt;
                negativeButtonColor = nb;
                negativeTextColor = nt;

                positiveButtonColorList = pbc;
                positiveTextColorList = ptc;
                negativeButtonColorList = nbc;
                negativeTextColorList = ntc;
            }

            public int getPositiveButtonColor() { return positiveButtonColor; }
            public int getPositiveTextColor() { return positiveTextColor; }
            public int getNegativeButtonColor() { return negativeButtonColor; }
            public int getNegativeTextColor() { return negativeTextColor; }

            public ColorStateList getPositiveButtonColorList() { return positiveButtonColorList; }
            public ColorStateList getPositiveTextColorList() { return positiveTextColorList; }
            public ColorStateList getNegativeButtonColorList() { return negativeButtonColorList; }
            public ColorStateList getNegativeTextColorList() { return negativeTextColorList; }
        }

        public static ButtonsColor getButtonsColor(Context mContext, String themeName){
            Theme selected = getThemeByName(themeName);
            switch (selected){
                case Dark:
                    return new ButtonsColor(
                            ContextCompat.getColor(mContext, R.color.Dark_primary),
                            ContextCompat.getColor(mContext, R.color.white),
                            ContextCompat.getColor(mContext, R.color.Dark_cancel),
                            ContextCompat.getColor(mContext, R.color.white),

                            ContextCompat.getColorStateList(mContext, R.color.Dark_primary),
                            ContextCompat.getColorStateList(mContext, R.color.white),
                            ContextCompat.getColorStateList(mContext, R.color.Dark_cancel),
                            ContextCompat.getColorStateList(mContext, R.color.white)
                    );
                case Day:
                    return new ButtonsColor(
                            ContextCompat.getColor(mContext, R.color.Day_text),
                            ContextCompat.getColor(mContext, R.color.white),
                            ContextCompat.getColor(mContext, R.color.Day_cancel),
                            ContextCompat.getColor(mContext, R.color.white),

                            ContextCompat.getColorStateList(mContext, R.color.Day_text),
                            ContextCompat.getColorStateList(mContext, R.color.white),
                            ContextCompat.getColorStateList(mContext, R.color.Day_cancel),
                            ContextCompat.getColorStateList(mContext, R.color.white)
                    );
                case Water:
                    return new ButtonsColor(
                            ContextCompat.getColor(mContext, R.color.Water_dark_yellow),
                            ContextCompat.getColor(mContext, R.color.black),
                            ContextCompat.getColor(mContext, R.color.Water_cancel),
                            ContextCompat.getColor(mContext, R.color.white),

                            ContextCompat.getColorStateList(mContext, R.color.Water_dark_yellow),
                            ContextCompat.getColorStateList(mContext, R.color.black),
                            ContextCompat.getColorStateList(mContext, R.color.Water_cancel),
                            ContextCompat.getColorStateList(mContext, R.color.white)
                    );
                case Fire:
                    return new ButtonsColor(
                            ContextCompat.getColor(mContext, R.color.Fire_light_orange),
                            ContextCompat.getColor(mContext, R.color.black),
                            ContextCompat.getColor(mContext, R.color.Fire_cancel),
                            ContextCompat.getColor(mContext, R.color.white),

                            ContextCompat.getColorStateList(mContext, R.color.Fire_light_orange),
                            ContextCompat.getColorStateList(mContext, R.color.black),
                            ContextCompat.getColorStateList(mContext, R.color.Fire_cancel),
                            ContextCompat.getColorStateList(mContext, R.color.white)
                    );
            }
            return  null;
        }

        public static Theme getThemeByName(String name){
            for(Theme current : values()){
                if(current.getName().equals(name)) return current;
            }

            //If the given name does not exists returns default Dark Theme
            return Dark;
        }

    }
}
