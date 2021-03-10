package com.miguel_lm.newapptodo.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.miguel_lm.newapptodo.ControlTareas;
import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityTarea extends AppCompatActivity {

    private EditText editTextTareaTitulo;
    private TextView textViewTareaFechaLimite, textViewTareaHoraLimite;
    TextView tv_tituloNuevaTarea;
    private Date fechaLimiteSeleccionada;
    private Date horaLimiteSeleccionada;
    public static final String PARAM_TAREA_EDITAR = "param_tarea_editar";
    private TextView textViewTareaPosicion;

    EditText ed_digitoRecordatorio;
    RadioGroup radioGroupRecordatorio;
    //RadioButton radioMin, radioHoras, radioDias;
    AlertDialog dialog;

    int alarmID = 1;

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

    // Datos del recordatorio
    int horasRecordatorio, diasRecordatorio, minutosRecordatorio;

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
        textViewTareaHoraLimite = findViewById(R.id.textViewTareaHoraLimite);
        tv_tituloNuevaTarea = findViewById(R.id.tv_tituloNuevaTarea);
        textViewTareaPosicion = findViewById(R.id.textViewTareaPosicion);

        tareaEditar = (Tarea) getIntent().getSerializableExtra(PARAM_TAREA_EDITAR);

        activityTareaModo = tareaEditar == null ? ActivityTareaModo.crear : ActivityTareaModo.editar;

        if (activityTareaModo == ActivityTareaModo.editar) {
            fechaLimiteSeleccionada = tareaEditar.fechaLimite;
            horaLimiteSeleccionada = tareaEditar.horaLimite;
            mostrarTarea();
            mostrarHora();
            tv_tituloNuevaTarea.setText("Modificar Tarea");
            //Toast.makeText(getApplicationContext(), "Modificar tarea existente.", Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            fechaLimiteSeleccionada = new Date();
            horaLimiteSeleccionada = new Date();
            tv_tituloNuevaTarea.setText("Nueva Tarea");
            //Toast.makeText(getApplicationContext(), "Crear una nueva tarea.", Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        mostrarFecha();
        mostrarHora();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        textViewTareaPosicion.setText("");
        pedirPosicionUsuario();
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
        textViewTareaHoraLimite.setText(tareaEditar.getHoraTexto());
    }

    private void mostrarFecha() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", new Locale("es", "ES"));
        textViewTareaFechaLimite.setText(formatoFecha.format(fechaLimiteSeleccionada));
    }

    private void mostrarHora() {
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatoHora.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        textViewTareaHoraLimite.setText(formatoHora.format(horaLimiteSeleccionada));
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

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar en blanco", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activityTareaModo == ActivityTareaModo.crear) {

            Tarea nuevaTarea = new Tarea(titulo, fechaLimiteSeleccionada, horaLimiteSeleccionada, posicionUsuario != null ? posicionUsuario.getLatitude() : 0, posicionUsuario != null ? posicionUsuario.getLongitude() : 0);
            long idNuevaTarea = tareaLab.insertTarea(nuevaTarea);
            nuevaTarea.setmId((int) idNuevaTarea);

            ControlTareas.getInstance().tareaActual = nuevaTarea;

            generarNotificacionProgramada();

            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {


            tareaEditar.setTareaSeleccionada(false);
            tareaEditar.modificar(titulo, fechaLimiteSeleccionada, horaLimiteSeleccionada, posicionUsuario != null ? posicionUsuario.getLatitude() : 0, posicionUsuario != null ? posicionUsuario.getLongitude() : 0);

            ControlTareas.getInstance().tareaActual = tareaEditar;

            generarNotificacionProgramada();

            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onShown(Snackbar sb) {
                    super.onShown(sb);
                }

                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);

                    if (event != DISMISS_EVENT_ACTION) {

                        // Entrará aquí si NO se pulsa el botón undo
                        tareaLab.updateTarea(tareaEditar);

                    }


                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
            snackbar.show();

            //createNotification(tareaEditar.mId);
        }


    }



    ///////////////////////////////////////////
    // UBICACIÓN
    ///////////////////////////////////////////


    public void seleccionarPosicionEnMapa(View view) {

        Intent intent = new Intent(this, ActivityEscogerPosicion.class);
        intent.putExtra("POSICION", posicionUsuario);
        startActivityForResult(intent, 1234);  // TODO: usar constante

        textViewTareaPosicion.setVisibility(View.VISIBLE);
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



    //////////////////////////////////////////
    // MODIFICAR FECHA Y HORA
    //////////////////////////////////////////

    public void cambiarFecha(View view) {

        final Calendar calendarFecha = Calendar.getInstance();
        calendarFecha.setTime(fechaLimiteSeleccionada);


        int day = calendarFecha.get(Calendar.DAY_OF_MONTH);
        int month = calendarFecha.get(Calendar.MONTH);
        int year = calendarFecha.get(Calendar.YEAR);

        final DatePickerDialog dpd = new DatePickerDialog(this, (datePicker, year1, monthOfYear, dayOfMonth) -> {
            calendarFecha.set(Calendar.YEAR, year1);
            calendarFecha.set(Calendar.MONTH, monthOfYear);
            calendarFecha.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            fechaLimiteSeleccionada = calendarFecha.getTime();

            mostrarFecha();
        }, year, month, day);
        dpd.show();
    }

    public void cambiarHora(View view) {

        final Calendar calendarHora = Calendar.getInstance();
        calendarHora.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        calendarHora.setTime(horaLimiteSeleccionada);

        int hour = calendarHora.get(Calendar.HOUR_OF_DAY);
        int minutes = calendarHora.get(Calendar.MINUTE);

        final TimePickerDialog tpd = new TimePickerDialog(ActivityTarea.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hora, int minutos) {

                calendarHora.set(Calendar.HOUR_OF_DAY, hora);
                calendarHora.set(Calendar.MINUTE, minutos);

                horaLimiteSeleccionada = calendarHora.getTime();

                mostrarHora();
            }
        }, hour, minutes, true);
        tpd.show();
    }

    ///////////////////////////////////////
    // DIÁLOGO DE RECORDATORIO
    ///////////////////////////////////////

    public void dialogoRecordatorio(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTarea.this);
        final View dialogLayout = LayoutInflater.from(ActivityTarea.this).inflate(R.layout.dialog_recordatorio, null);
        builder.setView(dialogLayout);
        dialog = builder.create();

        ed_digitoRecordatorio = dialogLayout.findViewById(R.id.ed_digitoRecordatorio);
        radioGroupRecordatorio = dialogLayout.findViewById(R.id.radioGroupRecordatorio);

        dialog.show();

    }

    /** Salir del diálogo de recordatorio */
    public void cancelarDialogoRecordatorio(View view) {

        dialog.dismiss();
    }


    /** Salir del diálogo de recordatorio */
    public void aceptarDialogoRecordatorio(View view) {

        int num = ed_digitoRecordatorio.getText().length();

        minutosRecordatorio = 0;
        horasRecordatorio = 0;
        diasRecordatorio = 0;
        if (radioGroupRecordatorio.getCheckedRadioButtonId() == R.id.radioButton_min)
            minutosRecordatorio = num;
        else if (radioGroupRecordatorio.getCheckedRadioButtonId() == R.id.radioButton_horas)
            horasRecordatorio = num;
        else
            diasRecordatorio = num;

        dialog.dismiss();

    }

    ///////////////////////////////////////
    // MOSTRAR NOTIFICACIÓN
    ///////////////////////////////////////


    public void generarNotificacionProgramada() {

        Tarea tarea = ControlTareas.getInstance().tareaActual;

        Calendar calendarFecha = Calendar.getInstance();
        calendarFecha.setTime(tarea.fechaLimite);

        Calendar calendarHora = Calendar.getInstance();
        calendarHora.setTime(tarea.horaLimite);

        Calendar calendarAlarma = Calendar.getInstance();
        calendarAlarma.set(Calendar.DAY_OF_MONTH, calendarFecha.get(Calendar.DAY_OF_MONTH));
        calendarAlarma.set(Calendar.MONTH, calendarFecha.get(Calendar.MONTH));
        calendarAlarma.set(Calendar.YEAR, calendarFecha.get(Calendar.YEAR));
        calendarAlarma.set(Calendar.HOUR_OF_DAY, calendarHora.get(Calendar.HOUR_OF_DAY));
        calendarAlarma.set(Calendar.MINUTE, calendarHora.get(Calendar.MINUTE));

        if (diasRecordatorio != 0)
            calendarAlarma.add(Calendar.DAY_OF_YEAR, -diasRecordatorio);
        else if (horasRecordatorio != 0)
            calendarAlarma.add(Calendar.HOUR_OF_DAY, -horasRecordatorio);
        else
            calendarAlarma.add(Calendar.MINUTE, -minutosRecordatorio);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarma.getTimeInMillis(), pendingIntent);
    }

}