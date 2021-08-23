package com.pyrosegames.animedownloader.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pyrosegames.animedownloader.Dialogs.GallerySettingsChoiceDialog;
import com.pyrosegames.animedownloader.R;

import java.util.List;

public class GallerySettingChoiceAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> items;
    private ClickEvent callback;

    public interface ClickEvent{
        void onSettingClicked(int position);
    }

    public GallerySettingChoiceAdapter(Context mContext, List<String> items, ClickEvent callback){
        this.mContext = mContext;
        this.items = items;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        TextView item;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.gallery_settings_choice_adapter_item, parent, false);
            holder = new ViewHolder();
            holder.item = view.findViewById(R.id.gallery_settings_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.item.setText(items.get(position));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSettingClicked(position);
            }
        });

        return view;
    }
}
