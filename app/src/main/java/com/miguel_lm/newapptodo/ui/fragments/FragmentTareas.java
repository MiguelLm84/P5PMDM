package com.miguel_lm.newapptodo.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.adaptadores.AdapterTareas;
import com.miguel_lm.newapptodo.ui.ListenerTareas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentTareas extends Fragment implements ListenerTareas {

    Date fechaLimiteSeleccionada;

    /*public static final String TAREA_EDITAR = "TAREA";
    public enum ActivityTareaModo { crear, editar}
    private ActivityTareaModo activityTareaModo;*/

    Tarea tareaEditar;

    List<Tarea> listaTareas;
    AdapterTareas adapterTareas;
    private LinearLayout toolBar;
    Tarea tareaAmodificar;
    List<Tarea> listaTareasSeleccionadas;
    ArrayList<Tarea> listaTareasFinalizadas;
    TareaLab tareaLab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tareas, container, false);

        toolBar = root.findViewById(R.id.toolbar);
        toolBar.setVisibility(View.GONE);
        listaTareasSeleccionadas = new ArrayList<>();

        tareaLab = TareaLab.get(getContext());
        listaTareas = tareaLab.getTareas();

        /*listaTareas = new ArrayList<>();
        listaTareas.add(new Tarea("Ir al taller",new Date()));
        listaTareas.add(new Tarea("Ir al cine",new Date()));
        listaTareas.add(new Tarea("Hacer la compra",new Date()));
        listaTareas.add(new Tarea("Estudiar",new Date()));*/

        RecyclerView recyclerViewTareas = root.findViewById(R.id.recyclerViewTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTareas = new AdapterTareas(getContext(),listaTareas, this);
        recyclerViewTareas.setAdapter(adapterTareas);

        return root;
    }

    @Override
    public void seleccionarTarea(Tarea tarea) {

        if (tarea.isTareaSeleccionada()) {
            listaTareasSeleccionadas.add(tarea);
        } else {
            listaTareasSeleccionadas.remove(tarea);
        }
        toolBar.setVisibility(listaTareasSeleccionadas.isEmpty() ? View.GONE : View.VISIBLE);
    }


    private void crearTarea() {
        crearOModificarTarea(null);
    }

    private void crearOModificarTarea(final Tarea tareaAmodificar) {

        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        final View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_crear_tarea, null);
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

            final DatePickerDialog dpd = new DatePickerDialog(getContext(), (datePicker, year1, monthOfYear, dayOfMonth) -> {
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
                Toast.makeText(getContext(), "Debe escoger una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tareaAmodificar == null) {
                Tarea nuevaTarea = new Tarea(titulo, calendar.getTime());
                tareaLab.get(getContext()).insertTarea(nuevaTarea);
                //todo: método añadir a BD.
                listaTareas.add(nuevaTarea);

                Toast.makeText(getContext(),"tarea añadida a la BD correctamente.",Toast.LENGTH_LONG).show();

                Toast.makeText(getContext(), "Evento añadido.", Toast.LENGTH_SHORT).show();
            }
            else {
                tareaAmodificar.modificar(titulo, calendar.getTime());

                tareaLab.get(getContext()).updateTarea(tareaAmodificar);
                //todo: método modificar a BD.
                Toast.makeText(getContext()," Tarea modificada correctamente en la BD.",Toast.LENGTH_LONG).show();

                Toast.makeText(getContext(), "Evento modificado.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            adapterTareas.notifyDataSetChanged();
        });

        buttonCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void accionEscogerYModificar() {

        AlertDialog.Builder builderDialogEscogerTareas = new AlertDialog.Builder(getContext());
        builderDialogEscogerTareas.setIcon(R.drawable.editartarea2);
        builderDialogEscogerTareas.setTitle("Modificar Tarea");

        final String[] arrayTareasAMostrar = new String[listaTareas.size()];
        for (int i = 0; i < listaTareas.size(); i++) {
            arrayTareasAMostrar[i] = "\n· TAREA: " + listaTareas.get(i).getTitulo() + "\n· FECHA:  " + listaTareas.get(i).getFechaTextoCorta();
        }
        builderDialogEscogerTareas.setSingleChoiceItems(arrayTareasAMostrar, -1, (dialog, posicionElementoSeleccionado) -> tareaAmodificar = listaTareas.get(posicionElementoSeleccionado));
        builderDialogEscogerTareas.setPositiveButton("Modificar", (dialog, i) -> {

            if (tareaAmodificar == null) {
                Toast.makeText(getContext(), "Debe escoger una tarea", Toast.LENGTH_SHORT).show();
            }
            else {
                crearOModificarTarea(tareaAmodificar);
            }
        });
        builderDialogEscogerTareas.setNegativeButton("Cancelar", null);
        builderDialogEscogerTareas.create().show();
    }

    private void accionEscogerYEliminar() {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(getContext());
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

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(getContext());
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
                        tareaLab.get(getContext()).deleteTarea(listaTareas.get(i));
                        //todo: método eliminar en la BD.
                    }
                }
                Toast.makeText(getContext(),"Tareas eliminadas correctamente en la BD.",Toast.LENGTH_LONG).show(); //getApplicationContext()
                Toast.makeText(getContext(), "Tareas eliminadas correctamente.", Toast.LENGTH_SHORT).show();
                this.adapterTareas.notifyDataSetChanged();
            });
            builderEliminar_Confirmar.create().show();
            dialog.dismiss();
        });

        builderEliminar.setNegativeButton("Cancelar",null);
        builderEliminar.create().show();
    }

    public void onClickFABNuevaTarea(View view) {
        crearTarea();
    }

    public void onClickToolbarEliminar(View view) {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(getContext());
        builderEliminar.setIcon(R.drawable.eliminar);
        builderEliminar.setTitle("Eliminar elementos");

        final ArrayList<String> listaTareasAeliminar = new ArrayList<>();
        String[] arrayTareas = new String[listaTareasFinalizadas.size()];
        final boolean[] tareasSeleccionadas = new boolean[listaTareasFinalizadas.size()];
        for (int i = 0; i < listaTareas.size(); i++){
            arrayTareas[i] = "\n· TAREA: " + listaTareasFinalizadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasFinalizadas.get(i).getFechaTextoCorta();
        }
        builderEliminar.setMultiChoiceItems(arrayTareas, tareasSeleccionadas, (dialog, indiceSeleccionado, isChecked) -> {
            tareasSeleccionadas[indiceSeleccionado] = isChecked;
            String tareasParaEliminar = "\n· TAREA: " + listaTareasFinalizadas.get(indiceSeleccionado).getTitulo() + "\n· FECHA:  " + listaTareasFinalizadas.get(indiceSeleccionado).getFechaTextoCorta() + "\n";
            listaTareasAeliminar.add(tareasParaEliminar);
        });

        builderEliminar.setPositiveButton("Borrar", (dialog, which) -> {

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(getContext());
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setTitle("¿Eliminar los elementos?");
            String tareasPorBorra = null;
            for(int i=0;i<listaTareasAeliminar.size();i++){
                tareasPorBorra = listaTareasAeliminar.get(i);
            }
            builderEliminar_Confirmar.setMessage(tareasPorBorra);
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which1) -> {

                for (int i = listaTareasFinalizadas.size() - 1; i >= 0; i--) {
                    if (tareasSeleccionadas[i]) {
                        listaTareasFinalizadas.remove(i);
                        tareaLab.get(getContext()).deleteTarea(listaTareasFinalizadas.get(i));
                        //todo: método elimira en la BD.
                    }
                }
                Toast.makeText(getContext(), "Tareas eliminadas correctamente", Toast.LENGTH_SHORT).show();
                this.adapterTareas.notifyDataSetChanged();
            });
            builderEliminar_Confirmar.create().show();
            dialog.dismiss();
        });

        builderEliminar.setNegativeButton("Cancelar",null);
        builderEliminar.create().show();
    }

    public void onClickToolbarModificar(View view) {

        //todo:implementar el método de modificar de la toolbar.
    }

    public void onClickToolbarSalir(View view) {
        for (Tarea tarea : listaTareasSeleccionadas)
            tarea.setTareaSeleccionada(false);

        listaTareasSeleccionadas.clear();
        toolBar.setVisibility(View.GONE);
        adapterTareas.notifyDataSetChanged();
        Toast.makeText(getContext(), "Salir sin seleccionar", Toast.LENGTH_SHORT).show();
    }

    /*private void mostrarTareasCaducadas(final List<Tarea> listaTareasFinalizadas) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.eliminar);
        builder.setTitle("Tareas Finalizadas");

        String listaTareasParaBorrar = null;
        String[] arrayTareas = new String[listaTareasFinalizadas.size()];
        final boolean[] tareasSeleccionadas = new boolean[listaTareasFinalizadas.size()];
        for (int i = 0; i < listaTareasFinalizadas.size(); i++) {
            arrayTareas[i] = "\n· TAREA: " + listaTareasFinalizadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasFinalizadas.get(i).getFechaTextoCorta();
            listaTareasParaBorrar =  arrayTareas[i];
        }
        builder.setMultiChoiceItems(arrayTareas, tareasSeleccionadas, (dialog, i, isChecked) -> tareasSeleccionadas[i] = isChecked);

        final String finalListaTareasParaBorrar = listaTareasParaBorrar;
        builder.setPositiveButton("Borrar", (dialog, which) -> {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setIcon(R.drawable.eliminar);
            builder1.setTitle("¿Eliminar el elemento?");
            builder1.setMessage(finalListaTareasParaBorrar);

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(getContext());
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setMessage("¿Eliminar los elementos?");
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which1) -> {

                for (int i = listaTareasFinalizadas.size() - 1; i >= 0; i--) {
                    if (tareasSeleccionadas[i]) {
                        listaTareas.remove(listaTareasFinalizadas.get(i));
                        tareaLab.get(getContext()).deleteTarea(listaTareasFinalizadas.get(i));
                        //todo: método eliminar a BD.
                    }
                }
                Toast.makeText(getContext(), "Tareas eliminadas correctamente", Toast.LENGTH_SHORT).show();
                this.adapterTareas.notifyDataSetChanged();
            });
            builderEliminar_Confirmar.create().show();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }*/
}