package com.pyrosegames.animedownloader.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyrosegames.animedownloader.AnimeGalleryInfo;
import com.pyrosegames.animedownloader.GalleryActivity;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.R;

import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    private Activity mActivity;
    private Context mContext;
    private List<AnimeGalleryInfo> anime;
    private ClickEvent callback;

    public interface ClickEvent{
        void onAnimeClick(AnimeGalleryInfo selectedAnime);
    }

    public GalleryAdapter(Activity mActivity, Context mContext, List<AnimeGalleryInfo> anime, ClickEvent callback){
        this.mActivity = mActivity;
        this.mContext = mContext;
        this.anime = anime;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        if(anime == null) return 0;
        return anime.size();
    }

    @Override
    public Object getItem(int position) {
        return anime.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        TextView nameField;
        LinearLayout layout;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.gallery_adapter_item, parent, false);
            holder = new ViewHolder();
            holder.nameField = view.findViewById(R.id.gallery_anime_name);
            holder.layout = view.findViewById(R.id.gallery_anime_name_layout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        if(anime == null) return view;

        holder.nameField.setText(anime.get(position).getAnimeName());
        holder.nameField.setTextColor(Options.getInstance(mActivity).getPrimaryColorStateList(mActivity));
        holder.nameField.setBackgroundTintList(Options.getInstance(mActivity).getListViewBGColor(mActivity));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAnimeClick(anime.get(position));
            }
        });

        return view;
    }
}
