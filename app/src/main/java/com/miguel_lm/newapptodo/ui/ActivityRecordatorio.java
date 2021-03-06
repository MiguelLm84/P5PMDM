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
import android.widget.RadioGroup;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;

import java.sql.Date;

public class ActivityRecordatorio extends AppCompatActivity {

    EditText ed_digitoRecordatorio;
    RadioGroup radioGroupRecordatorio;
    Button bt_aceptar;
    Tarea tareaRecordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tareaRecordar = (Tarea)getIntent().getSerializableExtra("TAREA");

        ed_digitoRecordatorio = findViewById(R.id.ed_digitoRecordatorio);
        radioGroupRecordatorio = findViewById(R.id.radioGroupRecordatorio);
        bt_aceptar = findViewById(R.id.btn_aceptar);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    // Pulsar botón aceptar
    public void crearRecordatorio(View view) {

        int num = ed_digitoRecordatorio.getText().length();

        Intent intent = new Intent();

        if (radioGroupRecordatorio.getCheckedRadioButtonId() == R.id.radioButton_min)
            intent.putExtra("MINUTOS", num);

        else if (radioGroupRecordatorio.getCheckedRadioButtonId() == R.id.radioButton_horas)
            intent.putExtra("HORAS", num);
        else
            intent.putExtra("DIAS", num);

        setResult(RESULT_OK);
        finish();
    }
}