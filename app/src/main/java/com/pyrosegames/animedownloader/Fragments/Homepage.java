package com.pyrosegames.animedownloader.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyrosegames.animedownloader.ADownlaodActivity;
import com.pyrosegames.animedownloader.AnimeGalleryInfo;
import com.pyrosegames.animedownloader.GalleryActivity;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.OptionsActivity;
import com.pyrosegames.animedownloader.R;

import java.util.Calendar;
import java.util.Date;

public class Homepage extends Fragment {

    public Homepage(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        Options options = Options.getInstance(getActivity());

        Button downloadButton = view.findViewById(R.id.homepage_download_anime);
        downloadButton.setTextColor(options.getButtonTextColor(getActivity()));
        downloadButton.setBackgroundTintList(options.getPrimaryColorStateList(getActivity()));
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent downloadActivity = new Intent(getContext(), ADownlaodActivity.class);
                startActivity(downloadActivity);
            }
        });

        Button galleryButton = view.findViewById(R.id.homepage_gallery_anime);
        galleryButton.setBackgroundTintList(options.getPrimaryColorStateList(getActivity()));
        galleryButton.setTextColor(options.getButtonTextColor(getActivity()));
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent downloadActivity = new Intent(getContext(), GalleryActivity.class);
                startActivity(downloadActivity);
            }
        });

        Button optionsButton = view.findViewById(R.id.homepage_options);
        optionsButton.setTextColor(options.getButtonTextColor(getActivity()));
        optionsButton.setBackgroundTintList(options.getPrimaryColorStateList(getActivity()));
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optionsActivity = new Intent(getContext(), OptionsActivity.class);
                startActivity(optionsActivity);
            }
        });

        Button uploadButton = view.findViewById(R.id.test_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                AnimeGalleryInfo anime = new AnimeGalleryInfo("Test", "12", currentTime.toString());
                options.saveAnimeToGallery(getActivity(), anime);
            }
        });

        Button clearButton = view.findViewById(R.id.test_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.clearAnimeGallery(getActivity());
            }
        });


        TextView authorName = view.findViewById(R.id.homepage_author);
        authorName.setTextColor(options.getBottomNicknameInfoColor(getActivity()));

        ImageView instagramIcon = view.findViewById(R.id.homepage_instagram_icon);
        instagramIcon.setImageTintList(options.getBottomNicknameInfoColorList(getActivity()));

        TextView instagramName = view.findViewById(R.id.homepage_instagram_name);
        instagramName.setTextColor(options.getBottomNicknameInfoColor(getActivity()));

        LinearLayout instagramLayout = view.findViewById(R.id.homepage_instagram);
        instagramLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/riccardocescon/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/")));
                }
            }
        });

        ImageView githubIcon = view.findViewById(R.id.homepage_github_icon);
        githubIcon.setImageTintList(options.getBottomNicknameInfoColorList(getActivity()));

        TextView githubName = view.findViewById(R.id.homepage_github_name);
        githubName.setTextColor(options.getBottomNicknameInfoColor(getActivity()));

        LinearLayout githubLayout = view.findViewById(R.id.homepage_github);
        githubLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/riccardocescon");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/")));
                }
            }
        });

        TextView appVersion = view.findViewById(R.id.homepage_app_version);
        appVersion.setTextColor(options.getBottomVersionInfoColor(getActivity()));

        return view;
    }
}
