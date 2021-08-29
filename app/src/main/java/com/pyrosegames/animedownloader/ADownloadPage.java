package com.pyrosegames.animedownloader;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.textfield.TextInputLayout;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.R;
import com.pyrosegames.animedownloader.Services.DownloadAnimeService;
import com.pyrosegames.animedownloader.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ir.androidexception.filepicker.utility.Util.requestPermission;

public class ADownloadPage extends Fragment {

    public interface OnSelectAnime{
        void onAnimeNameClick();
    }

    private Activity mActivity;
    private OnSelectAnime callback;
    private String animeName;
    private int startEp, endEp;
    private ProgressDialog dialog;
    private List<String> urls;
    private List<String> effectiveUrls;
    private Button downloadButton;
    private Receiver receiver;

    private EditText fromEp, toEp;

    private int REQUEST_CODE = 2;

    public ADownloadPage(String animeName, int startEp, int endEp, List<String> urls, OnSelectAnime callback){
        this.animeName = animeName;
        this.startEp = startEp;
        this.endEp = endEp;
        this.urls = urls;
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_download_page, container, false);

        Button destinationFolder = view.findViewById(R.id.adownload_folder_name_input);
        destinationFolder.setText(Options.getInstance(getActivity()).getDestinationPath());
        destinationFolder.setTextColor(Options.getInstance(getActivity()).getButtonTextColor(getActivity()));
        destinationFolder.setBackgroundTintList(Options.getInstance(getActivity()).getPrimaryColorStateList(getActivity()));
        view.findViewById(R.id.adownload_folder_name_input).setOnClickListener(currentView ->{
            StorageChooser chooser = new StorageChooser.Builder()
                    .withActivity(getActivity())
                    .withFragmentManager(getActivity().getFragmentManager())
                    .withMemoryBar(true)
                    .allowCustomPath(true)
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .build();

            // get path that the user has chosen
            chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                @Override
                public void onSelect(String path) {
                    String newDestinationPath = path + "/animeDownloader";
                    destinationFolder.setText(newDestinationPath);
                    Options.getInstance(getActivity()).setDestinationPath(getActivity(), newDestinationPath);
                }
            });

            // Show dialog whenever you want by
            chooser.show();
        });

        mActivity = getActivity();

        Button animeName = view.findViewById(R.id.adownload_anime_anime_name);
        animeName.setText(this.animeName);
        animeName.setTextColor(Options.getInstance(getActivity()).getButtonTextColor(getActivity()));
        animeName.setBackgroundTintList(Options.getInstance(getActivity()).getPrimaryColorStateList(getActivity()));
        animeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Anime Saturn
                callback.onAnimeNameClick();
            }
        });

        fromEp = view.findViewById(R.id.adownload_anime_info_from_ep_input);
        fromEp.setText(String.valueOf(startEp));
        fromEp.setTextColor(Options.getInstance(getActivity()).getSecondaryColor(getActivity()));

        toEp = view.findViewById(R.id.adownload_anime_info_to_ep_input);
        toEp.setText(String.valueOf(endEp));
        toEp.setTextColor(Options.getInstance(getActivity()).getSecondaryColor(getActivity()));

        OptionsActivity.Theme.ButtonsColor buttonsColor = Options.getInstance(getActivity()).getButtonsColors(getActivity());

        downloadButton = view.findViewById(R.id.adownload_anime_download_button);
        downloadButton.setBackgroundTintList(Options.getInstance(getActivity()).getPrimaryColorStateList(getActivity()));
        downloadButton.setTextColor(Options.getInstance(getActivity()).getButtonTextColor(getActivity()));
        if(urls == null || urls.size() == 0) {
            downloadButton.setVisibility(View.INVISIBLE);
            ((view.findViewById(R.id.adownload_anime_info_from_ep_text))).setVisibility(View.INVISIBLE);
            ((view.findViewById(R.id.adownload_anime_info_to_ep_text))).setVisibility(View.INVISIBLE);
        }else {
            downloadButton.setVisibility(View.VISIBLE);
            ((view.findViewById(R.id.adownload_anime_info_from_ep_text))).setVisibility(View.VISIBLE);
            ((view.findViewById(R.id.adownload_anime_info_to_ep_text))).setVisibility(View.VISIBLE);
        }
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAnime();
            }
        });

        receiver = new Receiver(mActivity, this);
        IntentFilter filter = new IntentFilter("downloadInfo");
        mActivity.registerReceiver(receiver, filter);

        return view;
    }

    private void downloadAnime(){

        int realFromEp = Integer.parseInt(fromEp.getText().toString());
        int toFromEp = Integer.parseInt(toEp.getText().toString());
        if(realFromEp < 1) realFromEp = 1;
        if(toFromEp > urls.size()) toFromEp = urls.size();

        fromEp.setText(String.valueOf(realFromEp));
        toEp.setText(String.valueOf(toFromEp));

        effectiveUrls = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++){
            if(i > realFromEp - 1 && i < toFromEp - 1){
                effectiveUrls.add(urls.get(i));
            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                getActivity().runOnUiThread(() ->{
                    Toast.makeText(getContext(), "Permission needed", Toast.LENGTH_SHORT).show();
                });
            } else {
                requestPermission(getActivity());
            }
        } else {
            int finalRealFromEp = realFromEp;
            int finalToFromEp = toFromEp;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startDownloadService(finalRealFromEp, finalToFromEp);
                }
            });
        }
    }

    private void startDownloadService(int firstEp, int lastEp){
        getActivity().runOnUiThread(() -> {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("I'm downloading ep : 0 of " + animeName);
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        });

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        Intent intent = new Intent(getContext(), DownloadAnimeService.class);
        String[] urlsArray = new String[urls.size()];
        for(int i = 0; i < urls.size(); i++){
            String currentUrl = urls.get(i);
            urlsArray[i] = currentUrl;
        }
        intent.putExtra("urls", urlsArray);
        intent.putExtra("startEp", firstEp);
        intent.putExtra("endEp", lastEp);
        intent.putExtra("destinationPath", Options.getInstance(getActivity()).getDestinationPath());
        intent.putExtra("animeNameString", animeName);
        intent.putExtra("tempDir", Options.getInstance(getActivity()).getTempDir());
        getActivity().startService(intent);
    }

    private void stopDownloading(){
        Objects.requireNonNull(mActivity).runOnUiThread(() ->{
            mActivity.unregisterReceiver(receiver);
            dialog.dismiss();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

                int realFromEp = Integer.parseInt(fromEp.getText().toString());
                int toFromEp = Integer.parseInt(toEp.getText().toString());
                if(realFromEp < 1) realFromEp = 1;
                if(toFromEp > urls.size()) toFromEp = urls.size();

                startDownloadService(realFromEp, toFromEp);
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText( getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return;
        }
    }

    private class Receiver extends BroadcastReceiver {

        private Activity mActivityReceiver;
        private ADownloadPage parent;
        private boolean lastRequest = false;

        public Receiver(Activity mActivity, ADownloadPage parent){
            mActivityReceiver = mActivity;
            this.parent = parent;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean ended = intent.getBooleanExtra("info_ended", false);
            if(ended){
                final String errorMessage = intent.getStringExtra("info_error");
                mActivityReceiver.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog != null)
                            dialog.dismiss();
                        if(!errorMessage.equals("none")){
                            if(errorMessage.contains("[Errno 13] Permission denied")){
                                Toast.makeText(mActivityReceiver.getBaseContext(), "I am not able to download to our SD Card yet, please change the default location to your internal storage", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(mActivityReceiver.getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }else{
                            saveToGallery();
                        }
                    }
                });
                return;
            }
            final String title = intent.getStringExtra("info_title");
            final String text = intent.getStringExtra("info_text");
            if(text == null) return;
            mActivityReceiver.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(dialog == null){
                        dialog = new ProgressDialog(mActivityReceiver);
                        dialog.setTitle(title);
                        dialog.setMessage(text);
                        dialog.setCancelable(false);
                        dialog.setInverseBackgroundForced(true);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mActivityReceiver.stopService(new Intent(mActivityReceiver.getBaseContext(), DownloadAnimeService.class));
                                parent.stopDownloading();
                                lastRequest = true;
                            }
                        });
                        dialog.show();
                    }else{
                        dialog.setMessage(text);
                    }
                }
            });
        }
    }

    private void saveToGallery(){
        //Options.getInstance(getActivity())
    }
}
