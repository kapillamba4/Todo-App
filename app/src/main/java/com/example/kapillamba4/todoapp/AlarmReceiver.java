package com.example.kapillamba4.todoapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by kapil on 7/10/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private String title;
    private String content;

    public int generateUniqueID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        //Toast.makeText(context, "OnRecieve", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(2000);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.todo_icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);

        notificationManager.notify(generateUniqueID(), mBuilder.build());
    }
}
