package com.pyrosegames.animedownloader.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.OptionsActivity;
import com.pyrosegames.animedownloader.R;

import java.util.List;

public class ThemeAdapter extends BaseAdapter {

    private Activity mActivity;
    private Context mContext;
    private List<OptionsActivity.Theme> themes;
    private ClickEvent callback;

    public interface ClickEvent {
        void onThemeSelected(OptionsActivity.Theme selectedTheme);
    }

    public ThemeAdapter(Activity mActivity, Context mContext, List<OptionsActivity.Theme> themes, ClickEvent callback) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        this.themes = themes;
        this.callback = callback;
    }


    @Override
    public int getCount() {
        return themes.size();
    }

    @Override
    public Object getItem(int position) {
        return themes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        ImageView imageView;
        TextView textView;
        LinearLayout background;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.theme_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.theme_item_image);
            holder.textView = view.findViewById(R.id.theme_item_text);
            holder.background = view.findViewById(R.id.theme_item_layout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.imageView.setBackgroundTintList(Options.getInstance(mActivity).getThemeColorStateList(mActivity, themes.get(position)));

        holder.textView.setText(themes.get(position).getName());
        holder.textView.setBackgroundColor(Options.getInstance(mActivity).getPrimaryColor(mActivity));
        holder.textView.setTextColor(Options.getInstance(mActivity).getButtonTextColor(mActivity));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onThemeSelected(themes.get(position));
            }
        });

        return view;
    }
}
