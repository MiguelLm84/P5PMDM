package com.miguel_lm.newapptodo.ui;

 import android.Manifest;
 import android.app.AlertDialog;
 import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
import android.widget.TextView;
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
    List<Tarea> listaTareas;
    public static final String PARAM_TAREA_EDITAR = "param_tarea_editar";
    private TextView textViewTareaPosicion;
    private Button bt_Aceptar, bt_Cancel;

    public enum ActivityTareaModo {crear, editar}

    private ActivityTareaModo activityTareaModo;
    private long tiempoParaSalir = 0;
    TareaLab tareaLab;
    Tarea tareaEditar;

    private FusedLocationProviderClient fusedLocationClient;
    private Location posicionUsuario;
    Snackbar snackbar, snackbarUbicacion;

    int PERMISSION_ID = 44;


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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234   &&   resultCode == RESULT_OK) {
            LatLng posicionMapa = (LatLng) data.getParcelableExtra("POSICION");  // todo: parar a constante
            posicionUsuario.setLatitude(posicionMapa.latitude);
            posicionUsuario.setLongitude(posicionMapa.longitude);

            mostrarPosicion();
        }
    }

    private void mostrarPosicion() {

        textViewTareaPosicion.setText("lat: " + posicionUsuario.getLatitude() + ", long: " + posicionUsuario.getLongitude());

    }

    private void pedirPosicionUsuario() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }
        else
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
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", new Locale("es","ES"));
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

    public void buttonOkClick(View view) {

        String titulo = editTextTareaTitulo.getText().toString();
        String fecha = textViewTareaFechaLimite.getText().toString();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar en blanco", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activityTareaModo == ActivityTareaModo.crear) {

            Tarea nuevaTarea = new Tarea(titulo, fechaLimiteSeleccionada, posicionUsuario != null ? posicionUsuario.getLatitude() : 0, posicionUsuario != null ? posicionUsuario.getLongitude() : 0);
            tareaLab.insertTarea(nuevaTarea);
            listaTareas.add(nuevaTarea);

            //Toast.makeText(getApplicationContext(),"Tarea añadida a la BD correctamente.",Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Evento añadido.", Toast.LENGTH_SHORT).show();
        }
        else {

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
        }

        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed(){

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

        textViewTareaPosicion.setVisibility(View.VISIBLE);

        /*snackbarUbicacion.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTareaPosicion.setText("");
                textViewTareaPosicion.setVisibility(View.GONE);
            }
        });
        snackbarUbicacion.show();*/

        if(textViewTareaPosicion.getText() != null){
            snackbarUbicacion.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textViewTareaPosicion.setText("");
                    textViewTareaPosicion.setVisibility(View.GONE);
                }
            });
            snackbarUbicacion.show();
        } else{
            textViewTareaPosicion.setText("");
        }
    }

    public void IntroducirRecordatorio(View view){

        String tituloTarea = editTextTareaTitulo.getText().toString();
        String fechaTarea = textViewTareaFechaLimite.getText().toString();
        String LatitudTarea = String.valueOf(posicionUsuario.getLatitude());
        String LongitudTarea = String.valueOf(posicionUsuario.getLongitude());
        Intent intent = new Intent(this, ActivityRecordatorio.class);

        intent.putExtra("TITULO", tituloTarea);
        intent.putExtra("FECHA", fechaTarea);
        intent.putExtra("LATITUD", LatitudTarea);
        intent.putExtra("LONGITUD", LongitudTarea);

        startActivityForResult(intent, 1234);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void anhadirUbicacionActual(View view){

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
}