package com.miguel_lm.newapptodo.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.ActivityTarea;
import com.miguel_lm.newapptodo.ui.ListenerTareas;
import com.miguel_lm.newapptodo.ui.MainActivity;
import com.miguel_lm.newapptodo.ui.adaptador.AdapterTareas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentTareas extends Fragment implements ListenerTareas {

    public static FragmentTareas FragmentTareasInstance;

    AdapterTareas adapterTareas;
    private LinearLayout toolBar;
    List<Tarea> listaTareasSeleccionadas;
    TareaLab tareaLab;
    List<Tarea> listaTareas;
    private ImageView imageButtonModificarTarea;
    Tarea tareaAmodificar;
    private static final int REQUEST_EDITAR_TAREA = 1222;
    Snackbar snackbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tareas, container, false);

        FragmentTareasInstance = this;
        
        toolBar = root.findViewById(R.id.toolbar);
        toolBar.setVisibility(View.GONE);

        listaTareasSeleccionadas = new ArrayList<>();

        ImageView imageButtonEliminarTarea = root.findViewById(R.id.btn_delete);
        ImageView imageButtonSalirToolbar = root.findViewById(R.id.btn_salir);
        imageButtonModificarTarea = root.findViewById(R.id.btn_modificar);

        snackbar = Snackbar.make(root.findViewById(R.id.CoordinatorLayoutFragTareas), R.string.mensaje, Snackbar.LENGTH_SHORT);

        tareaLab = TareaLab.get(getContext());
        listaTareas = tareaLab.getTareasNoCaducadas();
        Collections.sort(listaTareas);

        RecyclerView recyclerViewTareas = root.findViewById(R.id.recyclerViewTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTareas = new AdapterTareas(getContext(), listaTareas, this);
        recyclerViewTareas.setAdapter(adapterTareas);

        imageButtonSalirToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToolbarSalir();
            }
        });

        imageButtonModificarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToolbarModificar();
            }
        });

        imageButtonEliminarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToolbarEliminar();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDITAR_TAREA   &&   resultCode == RESULT_OK)
            refrescarListado();
    }

    public void refrescarListado() {

        listaTareasSeleccionadas.clear();
        mostrarToolbar();
        adapterTareas.actualizarListado(TareaLab.get(getContext()).getTareasNoCaducadas());
    }

    @Override
    public void seleccionarTarea(Tarea tarea) {

        tareaAmodificar = tarea;

        if (tarea.isTareaSeleccionada()) {
            listaTareasSeleccionadas.add(tarea);
        } else {
            listaTareasSeleccionadas.remove(tarea);
        }

        mostrarToolbar();
    }

    private void mostrarToolbar() {
        toolBar.setVisibility(listaTareasSeleccionadas.isEmpty() ? View.GONE : View.VISIBLE);

        imageButtonModificarTarea.setVisibility(listaTareasSeleccionadas.size() == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {

        refrescarListado();
    }

    @Override
    public void seleccionarTareasFavAdd(Tarea tarea) {

        refrescarListado();

    }

    @Override
    public void seleccionarTareasFavRemove(Tarea tarea) {

    }

    @Override
    public void completarTarea(Tarea tarea, boolean completada) {

        if (completada)
            refrescarListado();
    }

    public void onClickToolbarEliminar() {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(getContext());
        builderEliminar.setIcon(R.drawable.eliminar__1_);
        builderEliminar.setTitle("Eliminar elementos");

        String[] arrayTareas = new String[listaTareasSeleccionadas.size()];
        final boolean[] tareasSeleccionadasParaBorrar = new boolean[listaTareasSeleccionadas.size()];
        for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
            arrayTareas[i] = "\n· TAREA: " + listaTareasSeleccionadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasSeleccionadas.get(i).getFechaTextoCorta();
        }
        builderEliminar.setMultiChoiceItems(arrayTareas, tareasSeleccionadasParaBorrar, (dialog, indiceSeleccionado, isChecked) -> {
            tareasSeleccionadasParaBorrar[indiceSeleccionado] = isChecked;
        });

        builderEliminar.setPositiveButton("Borrar", (dialog, which) -> {

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(getContext());
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setTitle("¿Eliminar los elementos?");
            String textoNombresTareas = "";

            ArrayList<String> listaTareasAeliminar = new ArrayList<>();
            for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
                if (tareasSeleccionadasParaBorrar[i]) {
                    String tareasParaEliminar = "\n· TAREA: " + listaTareasSeleccionadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasSeleccionadas.get(i).getFechaTextoCorta() + "\n";
                    listaTareasAeliminar.add(tareasParaEliminar);

                } else {
                    Toast.makeText(getContext(), "Debe seleccionar una tarea para eliminarla", Toast.LENGTH_SHORT).show();
                    onClickToolbarSalir();
                    return;
                }
            }

            for (int i = 0; i < listaTareasAeliminar.size(); i++) {
                textoNombresTareas += listaTareasAeliminar.get(i);
            }

            for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
                if (tareasSeleccionadasParaBorrar[i]) {
                    Tarea tareaAborrar = listaTareasSeleccionadas.get(i);
                    tareaLab.get(getContext()).deleteTarea(listaTareasSeleccionadas.get(i));
                    refrescarListado();
                    snackbar.setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listaTareasSeleccionadas.add(tareaAborrar);
                            tareaLab.get(getContext()).insertTarea(tareaAborrar);
                            //refrescarListado();
                            listaTareasSeleccionadas.clear();
                            adapterTareas.actualizarListado(TareaLab.get(getContext()).getTareasNoCaducadas());
                            onClickToolbarSalir();
                        }
                    });
                    snackbar.show();
                    onClickToolbarSalir();
                    return;
                }
            }
            listaTareasSeleccionadas.clear();

            builderEliminar_Confirmar.create().show();
            dialog.dismiss();
        });

        builderEliminar.setNegativeButton("Cancelar", null);
        builderEliminar.create().show();
    }

    public void onClickToolbarModificar() {

        Intent intentNuevaTarea = new Intent(getContext(), ActivityTarea.class);
        intentNuevaTarea.putExtra(ActivityTarea.PARAM_TAREA_EDITAR, tareaAmodificar);
        startActivityForResult(intentNuevaTarea, REQUEST_EDITAR_TAREA);
    }

    public void onClickToolbarSalir() {
        for (Tarea tarea : listaTareasSeleccionadas)
            tarea.setTareaSeleccionada(false);

        listaTareasSeleccionadas.clear();
        toolBar.setVisibility(View.GONE);
        adapterTareas.notifyDataSetChanged();
        //Toast.makeText(getContext(), "Salir sin seleccionar", Toast.LENGTH_SHORT).show();
    }

    public void ordenarPorFechas(Tarea tarea){

        TareaLab tareaLab = TareaLab.get(getContext());
        List<Tarea> listaDeTareas = tareaLab.getTareas();

        Collections.sort(listaDeTareas);

        //todo: IMPORTANTE, cambiar color de fondo a los que estén en la lista de fav y al mismo tiempo estén completadas.
    }
}