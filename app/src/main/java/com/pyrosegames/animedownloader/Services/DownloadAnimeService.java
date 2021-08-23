package com.pyrosegames.animedownloader.Services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.pyrosegames.animedownloader.MainActivity;
import com.pyrosegames.animedownloader.Options;
import com.pyrosegames.animedownloader.R;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadAnimeService extends Service {
    private static final String TAG = "DownloadAnimeService";

    private boolean isRunning  = false;

    private String animeNameString;
    private List<String> urls = new ArrayList<>();
    private int startEp, endEp;
    private String destinationPath;
    private NotificationCompat.Builder builder;
    private String tempDir;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        urls = Arrays.asList(intent.getStringArrayExtra("urls"));
        startEp = intent.getIntExtra("startEp", 1);
        endEp = intent.getIntExtra("endEp", 12);
        destinationPath = intent.getStringExtra("destinationPath");
        animeNameString = intent.getStringExtra("animeNameString");
        tempDir = intent.getStringExtra("tempDir");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                File currentAnimeDir = new File(destinationPath, animeNameString);

                boolean manuallyRedirect = false;

                int counter = 0;
                for(String url : urls) {
                    counter++;
                    if(counter < startEp)
                        continue;
                    Log.i(TAG, "I'm downloading ep : \" + finalCounter1 + \" / \" + endEp + \" of \" + animeNameString + \"\\nDownloaded : 0%");
                    final String title = "Downloading " + animeNameString;

                    try {
                        YoutubeDL.getInstance().init(getApplicationContext()/*mActivity.getApplication()*/);

                        YoutubeDLRequest request = new YoutubeDLRequest(url);
                        request.addOption("-f", "best");
                        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        //redirect from internal storage to sd card :
                        /*if(!currentAnimeDir.getAbsolutePath().contains("emulated")){
                            manuallyRedirect = true;
                            request.addOption("-o", tempDir + "/" + animeNameString + "_" + counter + ".mp4");
                        }else {
                            request.addOption("-o", currentAnimeDir.getAbsolutePath() + "/" + animeNameString + "_" + counter + ".mp4");
                        }*/
                        request.addOption("-o", currentAnimeDir.getAbsolutePath() + "/" + animeNameString + "_" + counter + ".mp4");
                        final int finalCounter = counter;
                        boolean finalManuallyRedirect = manuallyRedirect;
                        YoutubeDL.getInstance().execute(request, new DownloadProgressCallback() {
                            @Override
                            public void onProgressUpdate(final float progress, final long etaInSeconds) {
                                Log.d("URL_", progress + "% (ETA " + etaInSeconds + " seconds)");

                                String text = "I'm downloading ep : " + finalCounter + " / " + endEp + " of " + animeNameString + "\nDownloaded : " + progress + "% (ETA " + etaInSeconds + " seconds)";
                                Log.i(TAG, text);
                                showStatusBarIcon(title, text, true);

                                Intent broadCastNotification = new Intent();
                                broadCastNotification.setAction("downloadInfo");
                                broadCastNotification.putExtra("info_title", title);
                                broadCastNotification.putExtra("info_text", text);
                                broadCastNotification.putExtra("info_ended", false);
                                sendBroadcast(broadCastNotification);

                                if(finalCounter == endEp && etaInSeconds < 2){
                                    Intent broadCastEndNotification = new Intent();
                                    broadCastEndNotification.setAction("downloadInfo");
                                    broadCastEndNotification.putExtra("info_ended", true);
                                    broadCastEndNotification.putExtra("info_error", "none");
                                    broadCastEndNotification.putExtra("info_redirect", finalManuallyRedirect);
                                    sendBroadcast(broadCastEndNotification);
                                    showStatusBarIcon("Download Completed", "All episodes have been downlaoded successfully", false);
                                }
                            }
                        });

                    } catch (YoutubeDLException | InterruptedException e) {
                        Log.d("URL_", "failed to initialize youtubedl-android", e);
                        Intent broadCastEndNotification = new Intent();
                        broadCastEndNotification.setAction("downloadInfo");
                        broadCastEndNotification.putExtra("info_ended", true);
                        broadCastEndNotification.putExtra("info_error", "I can't download this anime, sorry master : " + e.getMessage());
                        sendBroadcast(broadCastEndNotification);
                        Log.i(TAG, "I can't download this anime, sorry master : " + e.getMessage());

                        //Stop service once it finishes its task
                        stopSelf();
                        return;
                    }
                }

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }



    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    public void showStatusBarIcon(String title, String text, boolean ongoing){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(DownloadAnimeService.this, "My Notification");
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setOngoing(ongoing);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(DownloadAnimeService.this);
        managerCompat.notify(1, builder.build());
    }
}
