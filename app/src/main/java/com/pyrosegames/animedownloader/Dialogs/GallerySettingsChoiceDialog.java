package com.pyrosegames.animedownloader.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pyrosegames.animedownloader.Adapters.GallerySettingChoiceAdapter;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GallerySettingsChoiceDialog extends Dialog {

    private Activity mActivity;
    private Context mContext;
    private String title;
    private List<String> items;
    private GallerySettingChoiceAdapter.ClickEvent callback;

    public GallerySettingsChoiceDialog(@NonNull Context context, Activity mActivity, String title, List<String> items, GallerySettingChoiceAdapter.ClickEvent callback) {
        super(context);
        this.mActivity = mActivity;
        this.mContext = context;
        this.title = title;
        this.items = items;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery_settings_choice_dialog);

        TextView title = findViewById(R.id.gallery_settings_title);
        title.setText(this.title);

        ListView listView = findViewById(R.id.gallery_settings_listview);
        GallerySettingChoiceAdapter adapter = new GallerySettingChoiceAdapter(mContext, items, new GallerySettingChoiceAdapter.ClickEvent() {
            @Override
            public void onSettingClicked(int position) {
                callback.onSettingClicked(position);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });
        listView.setAdapter(adapter);

    }
}
