package com.miguel_lm.newapptodo.ui;


import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.miguel_lm.newapptodo.ControlTareas;
import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;

import java.util.List;

public class NotificationService extends IntentService {

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    Notification notification;


    public NotificationService(String name) {
        super(name);
    }

    public NotificationService() {
        super("SERVICE");
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(Intent intent2) {
        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.app_name);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, MainActivity.class);
        Resources res = this.getResources();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        String message = getString(R.string.new_notification);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);


        Intent intentModificar = new Intent(this, NotificacionReceiver.class);
        intentModificar.setAction("COMPLETADA");
        intentModificar.putExtra("TAREA", ControlTareas.getInstance().tareaActual);
        PendingIntent pendingIntentModificar = PendingIntent.getBroadcast(this, iUniqueId, intentModificar, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentBorrar = new Intent(this, NotificacionReceiver.class);
        intentBorrar.setAction("BORRAR");
        intentBorrar.putExtra("TAREA", ControlTareas.getInstance().tareaActual);
        PendingIntent pendingIntentBorrar = PendingIntent.getBroadcast(this, iUniqueId, intentBorrar, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int NOTIFY_ID = ControlTareas.getInstance().tareaActual.mId;
            String id = "" + ControlTareas.getInstance().tareaActual.mId;
            String title = "" + ControlTareas.getInstance().tareaActual.mId;
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notifManager == null) {
                notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle("" + ControlTareas.getInstance().tareaActual.getTitulo()).setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.ic_baseline_message_24)
                    .setContentText(ControlTareas.getInstance().tareaActual.toStringTareaNotificacion())
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_baseline_message_24))
                    .setColor(Color.BLUE)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .addAction(R.drawable.ic_cheque, "Completada", pendingIntentModificar)
                    .addAction(R.drawable.ic_eliminar__1_, "Eliminar", pendingIntentBorrar)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);

            startForeground(1, notification);

        } else {
            pendingIntent = PendingIntent.getActivity(context, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_baseline_message_24)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_baseline_message_24))
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.app_name))
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText(message).build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
