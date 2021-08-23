package com.pyrosegames.animedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pyrosegames.animedownloader.Adapters.GalleryAdapter;
import com.pyrosegames.animedownloader.Dialogs.AnimeGalleryInfoDialog;
import com.pyrosegames.animedownloader.Dialogs.GallerySettingsDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private List<AnimeGalleryInfo> anime;
    private ListView listView;
    private GalleryAdapter adapter;

    private GallerySettingsDialog.OrderType currentType;
    private GallerySettingsDialog.OrderBy currentBy;

    private TextView noAnimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Options options = Options.getInstance(this);

        currentType = GallerySettingsDialog.OrderType.Name;
        currentBy = GallerySettingsDialog.OrderBy.Increasing;

        listView = findViewById(R.id.gallery_listview);
        listView.setBackgroundTintList(options.getListViewBGColor(this));
        anime = options.getAnimeGallery(this);
        Activity mActivity = this;
        adapter = new GalleryAdapter(this, getBaseContext(), anime, new GalleryAdapter.ClickEvent() {
            @Override
            public void onAnimeClick(AnimeGalleryInfo selectedAnime) {
                new AnimeGalleryInfoDialog(mActivity, mActivity, selectedAnime, new AnimeGalleryInfoDialog.ClickEvent() {
                    @Override
                    public void onDelete(String animeName) {
                        for(AnimeGalleryInfo current : anime){
                            if(animeName.equals(current.getAnimeName())){
                                anime.remove(current);
                                reOrganize(currentType, currentBy);
                                break;
                            }
                        }
                    }
                }).show();
            }
        });
        listView.setAdapter(adapter);
        noAnimeText = findViewById(R.id.gallery_no_anime_text);

        Button settingsButton = findViewById(R.id.gallery_settings_button);
        settingsButton.setBackgroundTintList(options.getPrimaryColorStateList(this));
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GallerySettingsDialog(mActivity, mActivity, currentType, currentBy, new GallerySettingsDialog.DialogEvents() {
                    @Override
                    public void onSaveSettings(GallerySettingsDialog.OrderType orderType, GallerySettingsDialog.OrderBy orderBy) {
                        currentType = orderType;
                        currentBy = orderBy;
                        reOrganize(orderType, orderBy);
                    }

                    @Override
                    public void onDeletedAllAnime() {
                        anime.clear();
                        reOrganize(currentType, currentBy);
                    }
                }).show();
            }
        });

        reOrganize(currentType, currentBy);
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme = Options.getInstance(this).loadTheme(this, theme, true);
        return theme;
    }

    private void reOrganize(GallerySettingsDialog.OrderType orderType, GallerySettingsDialog.OrderBy orderBy){
        noAnimeText.setVisibility(anime == null || anime.size() == 0 ? View.VISIBLE : View.GONE);
        listView.setVisibility(anime == null || anime.size() == 0 ? View.GONE : View.VISIBLE);
        if(anime == null) return;
        switch (orderType){
            case Name:
                orderByName(orderBy);
                break;

            case Date:
                orderByDate(orderBy);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void orderByName(GallerySettingsDialog.OrderBy orderBy){
        Collections.sort(anime, new Comparator<AnimeGalleryInfo>() {
            @Override
            public int compare(AnimeGalleryInfo o1, AnimeGalleryInfo o2) {
                if(orderBy == GallerySettingsDialog.OrderBy.Increasing)
                    return o1.getAnimeName().compareToIgnoreCase(o2.getAnimeName());
                else
                    return -o1.getAnimeName().compareToIgnoreCase(o2.getAnimeName());
            }
        });
    }

    private void orderByDate(GallerySettingsDialog.OrderBy orderBy){

        int first = 1;
        int second = -1;

        if(orderBy.equals(GallerySettingsDialog.OrderBy.Decreasing)){
            first = -1;
            second = 1;
        }

        int finalFirst = first;
        int finalSecond = second;
        Collections.sort(anime, new Comparator<AnimeGalleryInfo>() {
            @Override
            public int compare(AnimeGalleryInfo o1, AnimeGalleryInfo o2) {
                String[] firstDate = o1.getDate().split("/");
                String[] secondDate = o2.getDate().split("/");
                int[] firstIntDate = new int[firstDate.length];
                int[] secondIntDate = new int[secondDate.length];

                for(int i = 0; i < firstDate.length; i++){
                    firstIntDate[i] = Integer.parseInt(firstDate[i]);
                    secondIntDate[i] = Integer.parseInt(secondDate[i]);
                }

                if(firstIntDate[AnimeGalleryInfo.year] < secondIntDate[AnimeGalleryInfo.year])  return finalFirst;
                else if(firstIntDate[AnimeGalleryInfo.year] > secondIntDate[AnimeGalleryInfo.year]) return finalSecond;
                else{
                    if(firstIntDate[AnimeGalleryInfo.month] < secondIntDate[AnimeGalleryInfo.month]) return finalFirst;
                    else if(firstIntDate[AnimeGalleryInfo.month] > secondIntDate[AnimeGalleryInfo.month]) return  finalSecond;
                    else{
                        if(firstIntDate[AnimeGalleryInfo.day] < secondIntDate[AnimeGalleryInfo.day]) return finalFirst;
                        else if(firstIntDate[AnimeGalleryInfo.day] > secondIntDate[AnimeGalleryInfo.day]) return finalSecond;
                        else{
                            if(firstIntDate[AnimeGalleryInfo.hour] < secondIntDate[AnimeGalleryInfo.hour]) return finalFirst;
                            else if(firstIntDate[AnimeGalleryInfo.hour] > secondIntDate[AnimeGalleryInfo.hour]) return finalSecond;
                            else{
                                if(firstIntDate[AnimeGalleryInfo.minute] < secondIntDate[AnimeGalleryInfo.minute]) return finalFirst;
                                else if(firstIntDate[AnimeGalleryInfo.minute] > secondIntDate[AnimeGalleryInfo.minute]) return finalSecond;
                                else{
                                    if(firstIntDate[AnimeGalleryInfo.second] < secondIntDate[AnimeGalleryInfo.second]) return finalFirst;
                                    else return finalSecond;
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
