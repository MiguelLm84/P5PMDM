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

    // Controles
    private EditText editTextTareaTitulo;
    //private CheckBox checkBoxTareaFav;
    //private CheckBox checkBoxTareaCompletada;
    private TextView textViewTareaFechaLimite;
    //private Button btn_aceptar, btn_cancelar;

    // Guardar la fecha en un Date
    private Date fechaLimiteSeleccionada;

    List<Tarea> listaTareas;
    public enum ActivityTareaModo { crear, editar}
    private ActivityTareaModo activityTareaModo;
    private long tiempoParaSalir = 0;
    TareaLab tareaLab;
    Tarea tareaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.to_do__2_);

        tareaLab = TareaLab.get(this);
        listaTareas = tareaLab.getTareas();

        // Controles
        editTextTareaTitulo = findViewById(R.id.ed_nomTarea);
        //checkBoxTareaFav = findViewById(R.id.checkBoxTareaFav);
        //checkBoxTareaCompletada = findViewById(R.id.checkBoxTareaCompletada);
        textViewTareaFechaLimite = findViewById(R.id.textViewTareaFechaLimite);

        // Recoger la tarea a editar
        // Si no existe, se está creando una nueva
        activityTareaModo = tareaEditar == null ? ActivityTareaModo.crear : ActivityTareaModo.editar;

        // Si hay una tarea que editar, se muestra
        if (activityTareaModo == ActivityTareaModo.editar) {
            fechaLimiteSeleccionada = tareaEditar.fechaLimite;
            mostrarTarea();
        }
        else {
            fechaLimiteSeleccionada = new Date();
        }
        mostrarFecha();
    }

    private void mostrarTarea() {

        editTextTareaTitulo.setText(tareaEditar.getTitulo());
        //checkBoxTareaCompletada.setChecked(tareaEditar.completado);
        //checkBoxTareaFav.setChecked(tareaEditar.esFav);
        textViewTareaFechaLimite.setText(tareaEditar.getFechaTexto());
    }

    private void mostrarFecha() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", new Locale("es","ES"));
        textViewTareaFechaLimite.setText(formatoFecha.format(fechaLimiteSeleccionada));
    }

    /////////////////////////////////////////
    // EVENTOS
    /////////////////////////////////////////

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

            // Recoger en el date la fecha límite indicada
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

    /** Guarda la tarea nueva o existente en la BD
     * Primero recoge los datos y comprueba que son correctos*/
    public void buttonOkClick(View view) {

        // Recoger datos y crear tarea
        String titulo = editTextTareaTitulo.getText().toString();
        String fecha = textViewTareaFechaLimite.getText().toString();
        //boolean estaCompletada = checkBoxTareaCompletada.isChecked();
        //boolean esFavorita = checkBoxTareaFav.isChecked();

        // Comprobar los datos
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Título está en blanco", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear la nueva tarea si estamos en modo crear
        //TareaLab tareaLab = TareaLab.get(this);
        if (activityTareaModo == ActivityTareaModo.crear) {

            // Crear una tarea y guardarla en la BD
            Tarea nuevaTarea = new Tarea(titulo, /*esFavorita, estaCompletada,*/ fechaLimiteSeleccionada);
            tareaLab.insertTarea(nuevaTarea);
            listaTareas.add(nuevaTarea);

            Toast.makeText(getApplicationContext(),"tarea añadida a la BD correctamente.",Toast.LENGTH_LONG).show();

            Toast.makeText(this, "Evento añadido.", Toast.LENGTH_SHORT).show();
        }
        else {

            // Modificar la tarea y actualizarla en la BD
            tareaEditar.modificar(titulo, fechaLimiteSeleccionada);
            tareaLab.updateTarea(tareaEditar);
        }

        // Cerrar el activity devolviendo ok
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