package com.pyrosegames.animedownloader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pyrosegames.animedownloader.Fragments.Homepage;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_WRITE_EXT_STORAGE = 1;
    private int REQUEST_READ_EXT_STORAGE = 2;

    private FrameLayout mainFragment;

    public enum FragmentType{
        None,
        Homepage,
        Download,
        Gallery,
        Options
    }
    private FragmentType currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Options.getInstance(this).loadTheme(this);

        checkPermissions();
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme = Options.getInstance(this).loadTheme(this, theme, true);
        return theme;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_READ_EXT_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            checkWritePermission();
        }
        if(requestCode == REQUEST_WRITE_EXT_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            LoadHomePage();
        }
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                checkWritePermission();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXT_STORAGE);
            }
        }
    }

    private void checkWritePermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                LoadHomePage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXT_STORAGE);
            }
        }
    }

    private void LoadHomePage(){
        currentScreen = FragmentType.None;
        changeFragment(FragmentType.Homepage);
    }

    public void changeFragment(FragmentType type){
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (type){
            case Homepage:
                ft.replace(R.id.main_activity_fragment, new Homepage());
                currentScreen = FragmentType.Homepage;
                break;
        }
        ft.commit();
    }
}
