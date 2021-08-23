package com.pyrosegames.animedownloader.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import com.pyrosegames.animedownloader.Adapters.ThemeAdapter;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.OptionsActivity;
import com.pyrosegames.animedownloader.R;

import java.util.List;

public class ThemeDialog extends Dialog {

    private Activity mActivity;
    private List<OptionsActivity.Theme> themes;

    private ThemeAdapter.ClickEvent callback;

    public ThemeDialog(@NonNull Context context, Activity mActivity, List<OptionsActivity.Theme> themes, ThemeAdapter.ClickEvent callback){
        super(context);
        this.mActivity = mActivity;
        this.themes = themes;
        this.callback = callback;
    }

    public ThemeDialog(@NonNull Context context) {
        super(context);
    }

    public ThemeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ThemeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_theme_dialog);

        ListView listView = findViewById(R.id.theme_listview);
        listView.setBackgroundTintList(Options.getInstance(mActivity).getPrimaryColorStateList(mActivity));
        ThemeAdapter adapter = new ThemeAdapter(mActivity, getContext(), themes, new ThemeAdapter.ClickEvent() {
            @Override
            public void onThemeSelected(OptionsActivity.Theme selectedTheme) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
                callback.onThemeSelected(selectedTheme);
            }
        });
        listView.setAdapter(adapter);

    }
}
