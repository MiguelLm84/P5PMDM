package com.miguel_lm.newapptodo.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    public enum ActivityTareaModo { crear, editar}
    private ActivityTareaModo activityTareaModo;
    private long tiempoParaSalir = 0;
    TareaLab tareaLab;
    Tarea tareaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.to_do__2_);

        tareaLab = TareaLab.get(this);
        listaTareas = tareaLab.getTareas();

        editTextTareaTitulo = findViewById(R.id.ed_nomTarea);
        textViewTareaFechaLimite = findViewById(R.id.textViewTareaFechaLimite);
        tv_tituloNuevaTarea = findViewById(R.id.tv_tituloNuevaTarea);

        tareaEditar = (Tarea) getIntent().getSerializableExtra(PARAM_TAREA_EDITAR);

        activityTareaModo = tareaEditar == null ? ActivityTareaModo.crear : ActivityTareaModo.editar;

        if (activityTareaModo == ActivityTareaModo.editar) {
            fechaLimiteSeleccionada = tareaEditar.fechaLimite;
            mostrarTarea();
            tv_tituloNuevaTarea.setText("Modificar tarea");
        }
        else {
            fechaLimiteSeleccionada = new Date();
            tv_tituloNuevaTarea.setText("Nueva tarea");
        }
        mostrarFecha();
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
            Toast.makeText(this, "Título está en blanco", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activityTareaModo == ActivityTareaModo.crear) {

            Tarea nuevaTarea = new Tarea(titulo, fechaLimiteSeleccionada);
            tareaLab.insertTarea(nuevaTarea);
            listaTareas.add(nuevaTarea);

            Toast.makeText(getApplicationContext(),"tarea añadida a la BD correctamente.",Toast.LENGTH_LONG).show();

            Toast.makeText(this, "Evento añadido.", Toast.LENGTH_SHORT).show();
        }
        else {

            tareaEditar.setTareaSeleccionada(false);
            tareaEditar.modificar(titulo, fechaLimiteSeleccionada);
            tareaLab.updateTarea(tareaEditar);
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
}