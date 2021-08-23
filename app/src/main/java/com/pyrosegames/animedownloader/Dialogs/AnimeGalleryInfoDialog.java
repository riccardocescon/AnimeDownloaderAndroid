package com.pyrosegames.animedownloader.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pyrosegames.animedownloader.Adapters.ThemeAdapter;
import com.pyrosegames.animedownloader.AnimeGalleryInfo;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.OptionsActivity;
import com.pyrosegames.animedownloader.R;

public class AnimeGalleryInfoDialog extends Dialog {

    private Activity mActivity;
    private AnimeGalleryInfo anime;
    private ClickEvent callback;

    private TextView downloadDate;
    private TextView downloadHours;

    public interface ClickEvent{
        void onDelete(String animeName);
    }

    public AnimeGalleryInfoDialog(@NonNull Context context, Activity mActivity, AnimeGalleryInfo anime, ClickEvent callback){
        super(context);
        this.mActivity = mActivity;
        this.anime = anime;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.anime_gallery_info_dialog);

        Options options = Options.getInstance(mActivity);
        OptionsActivity.Theme.ButtonsColor buttonsColor = options.getButtonsColors(mActivity);

        boolean isDay = false;
        int redColor = options.getSecondaryColor(mActivity);

        if(options.getCurrentTheme(mActivity).equals(OptionsActivity.Theme.Day.getName())){
            isDay = true;
        }

        TextView animeName = findViewById(R.id.gallery_info_dialog_anime_name);
        animeName.setText(anime.getAnimeName());
        animeName.setTextColor(isDay ? redColor : buttonsColor.getPositiveTextColor());

        TextView animeEps = findViewById(R.id.gallery_info_dialog_anime_eps);
        animeEps.setText(anime.getAnimeEps());
        animeEps.setTextColor(isDay ? redColor : buttonsColor.getPositiveTextColor());

        downloadDate = findViewById(R.id.gallery_info_dialog_download_date);
        downloadHours = findViewById(R.id.gallery_info_dialog_download_date_hours);
        downloadDate.setTextColor(isDay ? redColor : buttonsColor.getPositiveTextColor());
        downloadHours.setTextColor(isDay ? redColor : buttonsColor.getPositiveTextColor());

        loadDates();

        Button deleteButton = findViewById(R.id.gallery_info_dialog_delete);
        deleteButton.setBackgroundTintList(buttonsColor.getNegativeButtonColorList());
        deleteButton.setTextColor(buttonsColor.getNegativeTextColor());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleChoiceDialog(mActivity, mActivity, "Warning",
                        "Are you sure you want to delete this anime from your statistics? This action cannot be undone!",
                        "Yes", "No",() -> {
                    options.clearAnimeGallery(mActivity, anime.getAnimeName());
                    callback.onDelete(anime.getAnimeName());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }, () -> {}).show();
            }
        });
    }

    private void loadDates(){
        Options.DateFormats dateFormat = Options.getInstance(mActivity).getDateFormat(mActivity);

        String[] parts = anime.getDate().split("/");
        String day = parts[AnimeGalleryInfo.day];
        String month = parts[AnimeGalleryInfo.month];
        String year = parts[AnimeGalleryInfo.year];
        String hour = parts[AnimeGalleryInfo.hour];
        String minute = parts[AnimeGalleryInfo.minute];
        String second = parts[AnimeGalleryInfo.second];

        switch (dateFormat){
            case DMY:
                downloadDate.setText(day + "/" + month + "/" + year);
                break;

            case MDY:
                downloadDate.setText(month + "/" + day + "/" + year);
                break;
        }
        downloadHours.setText(hour + ":" + minute + ":" + second);
    }

}
