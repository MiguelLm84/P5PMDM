package com.miguel_lm.newapptodo.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.adaptadores.AdapterTareas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private long tiempoParaSalir = 0;
    List<Tarea> listaTareas;
    AdapterTareas adapterTareas;
    LinearLayout toolBar;
    Tarea tareaAmodificar;
    List<Tarea> listaTareasSeleccionadas;
    ArrayList<Tarea> listaTareasFinalizadas;
    TareaLab tareaLab;
    Navigation navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.clipboard);*/
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_favoritas, R.id.navigation_tareas,  R.id.navigation_caducadas)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //TareaLab tareaLab = TareaLab.get(this);

     //   List<Tarea> lista = tareaLab.getTareas();
       // Log.d("TAREAS", "num tareas " + lista);
        //Log.d("TAREAS", "TAREA1 " + lista.get(0));
    }

    public void botonNuevaTareaClick(View view) {

        Intent intentNuevaTarea = new Intent(this, ActivityTarea.class);
        startActivity(intentNuevaTarea);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.accionCrearTarea) {
            crearTarea();
        } else if (item.getItemId() == R.id.accionModificar) {
            accionEscogerYModificar();
        } else if (item.getItemId() == R.id.accionEliminar) {
            accionEscogerYEliminar();
        }
        return super.onOptionsItemSelected(item);
    }

    private void crearTarea() {
        crearOModificarTarea(null);
    }

    private void crearOModificarTarea(final Tarea tareaAmodificar) {

        AlertDialog.Builder build = new AlertDialog.Builder(this);
        final View dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_crear_tarea, null);
        build.setView(dialogLayout);
        final AlertDialog dialog = build.create();

        final EditText edTxtTarea = dialogLayout.findViewById(R.id.edTxt_tarea);
        final TextView tvFecha = dialogLayout.findViewById(R.id.tv_fecha);
        final Button buttonAceptar = dialogLayout.findViewById(R.id.btn_aceptar);
        final Button buttonCancelar = dialogLayout.findViewById(R.id.btn_cancelar);

        final Calendar calendar = Calendar.getInstance();

        if (tareaAmodificar != null) {
            edTxtTarea.setText(tareaAmodificar.getTitulo());
            tvFecha.setText(tareaAmodificar.getFechaTexto());
        }

        tvFecha.setOnClickListener(v -> {

            if (tareaAmodificar != null) {
                calendar.setTime(tareaAmodificar.getFechaLimite());
            }

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            final DatePickerDialog dpd = new DatePickerDialog(this, (datePicker, year1, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", new Locale("es","ES"));
                tvFecha.setText(formatoFecha.format(calendar.getTime()));
            }, year, month, day);
            dpd.show();
        });

        buttonAceptar.setOnClickListener(v -> {

            String titulo = edTxtTarea.getText().toString();

            if(edTxtTarea.getText().toString().length()<=0){
                Toast.makeText(this, "Debe escoger una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tareaAmodificar == null) {
                Tarea nuevaTarea = new Tarea(titulo, calendar.getTime());
                tareaLab.get(this).insertTarea(nuevaTarea);
                //todo: método añadir a BD.
                listaTareas.add(nuevaTarea);

                Toast.makeText(this,"tarea añadida a la BD correctamente.",Toast.LENGTH_LONG).show();

                Toast.makeText(this, "Evento añadido.", Toast.LENGTH_SHORT).show();
            }
            else {
                tareaAmodificar.modificar(titulo, calendar.getTime());

                tareaLab.get(this).updateTarea(tareaAmodificar);
                //todo: método modificar a BD.
                Toast.makeText(this," Tarea modificada correctamente en la BD.",Toast.LENGTH_LONG).show();

                Toast.makeText(this, "Evento modificado.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            adapterTareas.notifyDataSetChanged();
        });

        buttonCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void accionEscogerYModificar() {

        AlertDialog.Builder builderDialogEscogerTareas = new AlertDialog.Builder(this);
        builderDialogEscogerTareas.setIcon(R.drawable.editartarea2);
        builderDialogEscogerTareas.setTitle("Modificar Tarea");

        final String[] arrayTareasAMostrar = new String[listaTareas.size()];
        for (int i = 0; i < listaTareas.size(); i++) {
            arrayTareasAMostrar[i] = "\n· TAREA: " + listaTareas.get(i).getTitulo() + "\n· FECHA:  " + listaTareas.get(i).getFechaTextoCorta();
        }
        builderDialogEscogerTareas.setSingleChoiceItems(arrayTareasAMostrar, -1, (dialog, posicionElementoSeleccionado) -> tareaAmodificar = listaTareas.get(posicionElementoSeleccionado));
        builderDialogEscogerTareas.setPositiveButton("Modificar", (dialog, i) -> {

            if (tareaAmodificar == null) {
                Toast.makeText(getApplicationContext(), "Debe escoger una tarea", Toast.LENGTH_SHORT).show();
            }
            else {
                crearOModificarTarea(tareaAmodificar);
            }
        });
        builderDialogEscogerTareas.setNegativeButton("Cancelar", null);
        builderDialogEscogerTareas.create().show();
    }

    private void accionEscogerYEliminar() {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(this);
        builderEliminar.setIcon(R.drawable.eliminar);
        builderEliminar.setTitle("Eliminar elementos");

        final ArrayList<String> listaTareasAeliminar = new ArrayList<>();
        String[] arrayTareas = new String[listaTareas.size()];
        final boolean[] tareasSeleccionadas = new boolean[listaTareas.size()];
        for (int i = 0; i < listaTareas.size(); i++){
            arrayTareas[i] = "\n· TAREA: " + listaTareas.get(i).getTitulo() + "\n· FECHA:  " + listaTareas.get(i).getFechaTextoCorta();
        }
        builderEliminar.setMultiChoiceItems(arrayTareas, tareasSeleccionadas, (dialog, indiceSeleccionado, isChecked) -> {
            tareasSeleccionadas[indiceSeleccionado] = isChecked;
            String tareasParaEliminar = "\n· TAREA: " + listaTareas.get(indiceSeleccionado).getTitulo() + "\n· FECHA:  " + listaTareas.get(indiceSeleccionado).getFechaTextoCorta() + "\n";
            listaTareasAeliminar.add(tareasParaEliminar);
        });

        builderEliminar.setPositiveButton("Borrar", (dialog, which) -> {

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(this);
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setTitle("¿Eliminar los elementos?");
            String tareasPorBorra = null;
            for(int i=0;i<listaTareasAeliminar.size();i++){
                tareasPorBorra = listaTareasAeliminar.get(i);
            }
            builderEliminar_Confirmar.setMessage(tareasPorBorra);
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which1) -> {

                for (int i = listaTareas.size() - 1; i >= 0; i--) {
                    if (tareasSeleccionadas[i]) {
                        listaTareas.remove(i);
                        tareaLab.get(this).deleteTarea(listaTareas.get(i));
                        //todo: método eliminar en la BD.
                    }
                }
                Toast.makeText(getApplicationContext(),"Tareas eliminadas correctamente en la BD.",Toast.LENGTH_LONG).show(); //getApplicationContext()
                Toast.makeText(getApplicationContext(), "Tareas eliminadas correctamente.", Toast.LENGTH_SHORT).show();
                this.adapterTareas.notifyDataSetChanged();
            });
            builderEliminar_Confirmar.create().show();
            dialog.dismiss();
        });

        builderEliminar.setNegativeButton("Cancelar",null);
        builderEliminar.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this,R.id.nav_host_fragment).navigateUp();
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
        }
    }
}