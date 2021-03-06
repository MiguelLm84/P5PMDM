package com.miguel_lm.newapptodo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityTarea extends AppCompatActivity {

    private EditText editTextTareaTitulo;
    private TextView textViewTareaFechaLimite;
    private TextView tv_tituloNuevaTarea;
    private Date fechaLimiteSeleccionada;
    public static final String PARAM_TAREA_EDITAR = "param_tarea_editar";
    private TextView textViewTareaPosicion;
    private Button bt_Aceptar, bt_Cancel;

    public enum ActivityTareaModo {crear, editar}

    private ActivityTareaModo activityTareaModo;
    private long tiempoParaSalir = 0;
    TareaLab tareaLab;
    Tarea tareaEditar;
    List<Tarea> listaTareas;
    private FusedLocationProviderClient fusedLocationClient;
    private Location posicionUsuario;
    Snackbar snackbar, snackbarUbicacion;

    int PERMISSION_ID = 44;

    private static final int REQUEST_RECORDATORIO = 5566;

    // Datos del recordatorio
    int horas, dias, minutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.to_do__2_);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        snackbar = Snackbar.make(findViewById(R.id.CoordinatorLayoutActivityTarea), R.string.mensaje, Snackbar.LENGTH_LONG);
        snackbarUbicacion = Snackbar.make(findViewById(R.id.CoordinatorLayoutActivityTarea), "Pulse para deshacer cambios", Snackbar.LENGTH_LONG);

        tareaLab = TareaLab.get(this);
        listaTareas = tareaLab.getTareas();

        editTextTareaTitulo = findViewById(R.id.ed_nomTarea);
        textViewTareaFechaLimite = findViewById(R.id.textViewTareaFechaLimite);
        tv_tituloNuevaTarea = findViewById(R.id.tv_tituloNuevaTarea);
        textViewTareaPosicion = findViewById(R.id.textViewTareaPosicion);

        tareaEditar = (Tarea) getIntent().getSerializableExtra(PARAM_TAREA_EDITAR);

        activityTareaModo = tareaEditar == null ? ActivityTareaModo.crear : ActivityTareaModo.editar;

        if (activityTareaModo == ActivityTareaModo.editar) {
            fechaLimiteSeleccionada = tareaEditar.fechaLimite;
            mostrarTarea();
            tv_tituloNuevaTarea.setText("Modificar Tarea");
            //Toast.makeText(getApplicationContext(), "Modificar tarea existente.", Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            fechaLimiteSeleccionada = new Date();
            tv_tituloNuevaTarea.setText("Nueva Tarea");
            //Toast.makeText(getApplicationContext(), "Crear una nueva tarea.", Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        mostrarFecha();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        textViewTareaPosicion.setText("");
        pedirPosicionUsuario();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234 && resultCode == RESULT_OK) {
            LatLng posicionMapa = (LatLng) data.getParcelableExtra("POSICION");  // todo: parar a constante
            posicionUsuario.setLatitude(posicionMapa.latitude);
            posicionUsuario.setLongitude(posicionMapa.longitude);

            mostrarPosicion();

        } else if (requestCode == 5566 && resultCode == RESULT_OK) {

            minutos = data.getIntExtra("MINUTOS", 0);
            horas = data.getIntExtra("HORAS", 0);
            dias = data.getIntExtra("DIAS", 0);
        }
    }

    private void mostrarPosicion() {

        textViewTareaPosicion.setText("lat: " + String.format("%.4f", posicionUsuario.getLatitude()) + ", long: " + String.format("%.4f", posicionUsuario.getLongitude()));
    }

    private void pedirPosicionUsuario() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        } else
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                posicionUsuario = location;
                                mostrarPosicion();
                            }
                        }
                    });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pedirPosicionUsuario();
            }
    }

    private void mostrarTarea() {

        editTextTareaTitulo.setText(tareaEditar.getTitulo());
        textViewTareaFechaLimite.setText(tareaEditar.getFechaTexto());
    }

    private void mostrarFecha() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", new Locale("es", "ES"));
        textViewTareaFechaLimite.setText(formatoFecha.format(fechaLimiteSeleccionada));
    }

    public void cambiarFecha(View view) {

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaLimiteSeleccionada);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        final DatePickerDialog dpd = new DatePickerDialog(this, (datePicker, year1, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            fechaLimiteSeleccionada = calendar.getTime();

            mostrarFecha();
        }, year, month, day);
        dpd.show();
    }

    public void buttonCancelarClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Botón para modificar o crear nueva tarea
     * IMPORTANTE: aquí se llama a crearNotificacion()
     * Se hace aquí porque hasta entonces no se sabe el id de la tarea (si se está creando), que es el dato que necesita guardar la notificación
     */
    public void buttonOkClick(View view) {

        String titulo = editTextTareaTitulo.getText().toString();
        String fecha = textViewTareaFechaLimite.getText().toString();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar en blanco", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activityTareaModo == ActivityTareaModo.crear) {

            Tarea nuevaTarea = new Tarea(titulo, fechaLimiteSeleccionada, posicionUsuario != null ? posicionUsuario.getLatitude() : 0, posicionUsuario != null ? posicionUsuario.getLongitude() : 0);
            long idNuevaTarea = tareaLab.insertTarea(nuevaTarea);

            createNotificationChannel();
            //createNotification(idNuevaTarea);
            createNotification(nuevaTarea.mId);

            //Toast.makeText(getApplicationContext(),"Tarea añadida a la BD correctamente.",Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Evento añadido.", Toast.LENGTH_SHORT).show();
        } else {

            tareaEditar.setTareaSeleccionada(false);
            tareaEditar.modificar(titulo, fechaLimiteSeleccionada);
            tareaLab.updateTarea(tareaEditar);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tareaLab.get(ActivityTarea.this).insertTarea(tareaEditar);
                }
            });
            snackbar.show();
            //Toast.makeText(getApplicationContext(),"Tarea modificada correctamente.",Toast.LENGTH_SHORT).show();
            createNotificationChannel();
            createNotification(tareaEditar.mId);
        }

        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {

        long tiempo = System.currentTimeMillis();
        if (tiempo - tiempoParaSalir > 3000) {
            tiempoParaSalir = tiempo;
            Toast.makeText(this, "Presione de nuevo 'Atrás' si desea salir",
                    Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void seleccionarPosicionEnMapa(View view) {

        Intent intent = new Intent(this, ActivityEscogerPosicion.class);
        intent.putExtra("POSICION", posicionUsuario);
        startActivityForResult(intent, 1234);  // TODO: usar constante

        //todo: Borrar todo lo del snackbar de aquí para bajo hasta la línea 292.
        /*textViewTareaPosicion.setVisibility(View.VISIBLE);

        if (textViewTareaPosicion.getText() != null) {
            snackbarUbicacion.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textViewTareaPosicion.setText("");
                    textViewTareaPosicion.setVisibility(View.GONE);
                }
            });
            snackbarUbicacion.show();
        } else {
            textViewTareaPosicion.setText("");
        }*/
    }

    public void IntroducirRecordatorio(View view) {

        Intent intent = new Intent(this, ActivityRecordatorio.class);
        intent.putExtra("TAREA", tareaEditar);

        startActivityForResult(intent, REQUEST_RECORDATORIO);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void anhadirUbicacionActual(View view) {

        textViewTareaPosicion.setVisibility(View.VISIBLE);
        pedirPosicionUsuario();
        mostrarPosicion();

        snackbarUbicacion.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTareaPosicion.setText("");
                textViewTareaPosicion.setVisibility(View.GONE);
            }
        });
        snackbarUbicacion.show();
    }


    ///////////////////////////////////
    // ENVIAR NOTIFICAIÓN
    ///////////////////////////////////


    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification(long idTarea) {

        // todo: falta usar, dias, horas o minutos para programar la notificación


        // Acción modificar
        Intent intentModificar = new Intent(this, ActivityTarea.class);
        intentModificar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentModificar.getExtras().putLong("id_tarea_modificar", idTarea);
        PendingIntent pendingIntentModificar = PendingIntent.getActivity(this, 1, intentModificar, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action modificar = new NotificationCompat.Action.Builder(R.drawable.ic_baseline_3p_24, "Modificar", pendingIntentModificar).build();

        // Acción borrar
        Intent intentBorrar = new Intent(this, ActivityTarea.class);
        intentBorrar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntentBorrar = PendingIntent.getActivity(this, 0, intentBorrar, 0);
        NotificationCompat.Action borrar = new NotificationCompat.Action.Builder(R.drawable.ic_baseline_3p_24, "Borrar", pendingIntentBorrar).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_message_24);
        builder.setContentTitle("Recordatorio Tarea");
        //builder.setContentText(tituloTareaRecordatorio + "\n" + fechaTareaRecordatorio);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setCategory(NotificationCompat.CATEGORY_REMINDER);
        builder.setContentIntent(pendingIntentModificar);
        builder.setAutoCancel(true);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Añadir acciones
        builder.addAction(modificar);
        builder.addAction(borrar);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }

}