package com.pyrosegames.animedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class ADownlaodActivity extends AppCompatActivity {

    public enum FrameType{
        DownloadPage,
        AnimeSaturn
    }

    public FrameType currentScreen;
    private List<String> allUrls = new ArrayList<>();
    private String animeName;
    private int startEp, endEp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_downlaod);

        animeName = "Nothing...";
        startEp = 1;
        endEp = 12;

        changeFragment(FrameType.DownloadPage);

    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme = Options.getInstance(this).loadTheme(this, theme, true);
        return theme;
    }

    public void changeFragment(FrameType type){
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (type){
            case DownloadPage:
                ft.replace(R.id.adownload_fragment, new ADownloadPage(animeName, startEp, endEp, allUrls, new ADownloadPage.OnSelectAnime() {
                    @Override
                    public void onAnimeNameClick() {
                        changeFragment(FrameType.AnimeSaturn);
                    }
                }));
                break;

            case AnimeSaturn:
                ft.replace(R.id.adownload_fragment, new AAnimeSaturnWebPage(new AAnimeSaturnWebPage.OnWebEnded() {
                    @Override
                    public void onAnimeSelected(String _animeName, List<String> urls) {
                        allUrls = urls;
                        animeName = _animeName;
                        endEp = urls.size();
                        changeFragment(FrameType.DownloadPage);
                    }
                }));
                break;
        }
        ft.commit();
    }
}
