package com.miguel_lm.newapptodo.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;

import java.sql.Date;

public class ActivityRecordatorio extends AppCompatActivity {

    EditText ed_digitoRecordatorio;
    RadioButton rb_min, rb_horas, rb_dias;
    Button bt_aceptar;
    String tituloTareaRecordatorio, fechaTareaRecordatorio, latitudTareaRecordatorio, longitudTareaRecordatorio;
    Tarea tareaParaBorrar;

    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tituloTareaRecordatorio = getIntent().getExtras().getParcelable("TITULO");
        fechaTareaRecordatorio = getIntent().getExtras().getParcelable("FECHA");
        latitudTareaRecordatorio = getIntent().getExtras().getParcelable("LATITUD");
        longitudTareaRecordatorio = getIntent().getExtras().getParcelable("LONGITUD");

        ed_digitoRecordatorio = findViewById(R.id.ed_digitoRecordatorio);
        rb_min = findViewById(R.id.radioButton_min);
        rb_horas = findViewById(R.id.radioButton_horas);
        rb_dias = findViewById(R.id.radioButton_dias);
        bt_aceptar = findViewById(R.id.btn_aceptar);
    }

    public void crearRecordatorio(View view) {

        enviarNotificacion();
    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification(){

        Intent intent = new Intent(this, ActivityTarea.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Date date = Date.valueOf(fechaTareaRecordatorio);
        double lat = Double.parseDouble(latitudTareaRecordatorio);
        double lon = Double.parseDouble(longitudTareaRecordatorio);
        tareaParaBorrar = new Tarea(tituloTareaRecordatorio,date,lat,lon);

        NotificationCompat.Action modificar = new NotificationCompat.Action.Builder(R.drawable.ic_baseline_3p_24, "Modificar", pendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_message_24);
        builder.setContentTitle("Recordatorio Tarea");
        builder.setContentText(tituloTareaRecordatorio + "\n" + fechaTareaRecordatorio);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setCategory(NotificationCompat.CATEGORY_REMINDER);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLights(Color.MAGENTA,1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.addAction(modificar);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Action borrar = new NotificationCompat.Action.Builder(R.drawable.ic_baseline_3p_24, "Borrar", pendingIntent2).build();

        builder.addAction(borrar);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID,builder.build());
    }

    public void enviarNotificacion(){

        int num = ed_digitoRecordatorio.getText().length();

        if(num != 0 && rb_min.isChecked()){
            createNotificationChannel();
            createNotification();
        }
        if(num != 0 && rb_horas.isChecked()){
            createNotificationChannel();
            createNotification();
        }
        if(num != 0 && rb_dias.isChecked()){
            createNotificationChannel();
            createNotification();
        }
    }
}