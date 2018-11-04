package com.sharelib.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {

    public static void showNotification(Context context, String contentTitle, String contentText, Uri link, int icon, int notifyID) {
        Intent notificationIntent;
        if (link != null) {
            notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setData(link);
        } else {
            notificationIntent = new Intent();
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context);
        notifyBuilder.setContentTitle(contentTitle);
        notifyBuilder.setContentText(contentText);
        notifyBuilder.setSmallIcon(icon);
        notifyBuilder.setContentIntent(contentIntent);
        notifyBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyID, notifyBuilder.build());
    }
}
