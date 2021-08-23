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
import android.widget.Button;
import android.widget.ListView;

import com.pyrosegames.animedownloader.Adapters.GallerySettingChoiceAdapter;
import com.pyrosegames.animedownloader.Adapters.ThemeAdapter;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.OptionsActivity;
import com.pyrosegames.animedownloader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GallerySettingsDialog extends Dialog {

    private Activity mActivity;
    private OrderType orderType;
    private OrderBy orderBy;
    private DialogEvents callback;

    public interface DialogEvents{
        void onSaveSettings(OrderType orderType, OrderBy orderBy);
        void onDeletedAllAnime();
    }

    public enum OrderType{
        Name("Name"),
        Date("Date");

        private String value;
        OrderType(String value){
            this.value = value;
        }

        public String getValue(){ return value; }
    }

    public enum OrderBy{
        Increasing("Increasing"),
        Decreasing("Decreasing");

        private String value;
        OrderBy(String value){
            this.value = value;
        }

        public String getValue(){ return value; }
    }

    public GallerySettingsDialog(@NonNull Context context, Activity mActivity, OrderType orderType, OrderBy orderBy, DialogEvents callback) {
        super(context);
        this.mActivity = mActivity;
        this.orderType = orderType;
        this.orderBy = orderBy;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery_settings_item);

        Button orderTypeButton = findViewById(R.id.gallery_settings_order_type_button);
        orderTypeButton.setBackgroundTintList(Options.getInstance(mActivity).getPrimaryColorStateList(mActivity));
        orderTypeButton.setText(orderType.getValue());
        orderTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderType> types = Arrays.asList(OrderType.values());
                List<String> values = new ArrayList<>();
                for(OrderType current : types){
                    values.add(current.getValue());
                }
                new GallerySettingsChoiceDialog(mActivity, mActivity, "Order Type:", values, new GallerySettingChoiceAdapter.ClickEvent() {
                    @Override
                    public void onSettingClicked(int position) {
                        orderType = types.get(position);
                        orderTypeButton.setText(orderType.getValue());
                    }
                }).show();
            }
        });

        Button orderByButton = findViewById(R.id.gallery_settings_order_by_button);
        orderByButton.setBackgroundTintList(Options.getInstance(mActivity).getPrimaryColorStateList(mActivity));
        orderByButton.setText(orderBy.getValue());
        orderByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderBy> types = Arrays.asList(OrderBy.values());
                List<String> values = new ArrayList<>();
                for(OrderBy current : types){
                    values.add(current.getValue());
                }
                new GallerySettingsChoiceDialog(mActivity, mActivity, "Order By:", values, new GallerySettingChoiceAdapter.ClickEvent() {
                    @Override
                    public void onSettingClicked(int position) {
                        orderBy = types.get(position);
                        orderByButton.setText(orderBy.getValue());
                    }
                }).show();
            }
        });

        OptionsActivity.Theme.ButtonsColor buttonsColor = Options.getInstance(mActivity).getButtonsColors(mActivity);

        Button cancelButton = findViewById(R.id.gallery_settings_cancel);
        cancelButton.setBackgroundTintList(buttonsColor.getNegativeButtonColorList());
        cancelButton.setTextColor(buttonsColor.getNegativeTextColor());
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });

        Button confirmButton = findViewById(R.id.gallery_settings_save);
        confirmButton.setBackgroundTintList(buttonsColor.getPositiveButtonColorList());
        confirmButton.setTextColor(buttonsColor.getPositiveTextColor());
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSaveSettings(orderType, orderBy);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });

        Button deleteAllStatistics = findViewById(R.id.gallery_settings_delete_statistics);
        deleteAllStatistics.setBackgroundTintList(buttonsColor.getNegativeButtonColorList());
        deleteAllStatistics.setTextColor(buttonsColor.getNegativeTextColor());
        deleteAllStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleChoiceDialog(mActivity, mActivity, "Warning", "Are you sure you want to delete all your statistics? This action cannot be undone",
                        "Yes", "No", () -> {
                    Options.getInstance(mActivity).clearAnimeGallery(mActivity);
                    callback.onDeletedAllAnime();
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
}
