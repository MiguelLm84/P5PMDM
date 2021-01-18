package com.miguel_lm.newapptodo.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.ListenerTareas;
import com.miguel_lm.newapptodo.ui.adaptadores.AdapterTareasFav;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentTareasFav extends Fragment implements ListenerTareas {

    Tarea tareaEditar;

    List<Tarea> listaTareasFav;
    AdapterTareasFav adapterTareasFav;
    LinearLayout toolBar;
    Tarea tareaAmodificar;
    List<Tarea> listaTareasSeleccionadas;
    ArrayList<Tarea> listaTareasFinalizadas;
    TareaLab tareaLab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tareas_fav, container, false);

        //toolBar = root.findViewById(R.id.toolbar);
        /*tareaLab = TareaLab.get(getContext());
        listaTareasFav = tareaLab.getTareas();*/

        listaTareasFav = new ArrayList<>();
        Tarea t1 =new Tarea("Ir al taller",new Date());
        Tarea t2 =new Tarea("Ir al cine",new Date());
        listaTareasFav.add(t1);
        listaTareasFav.add(t2);

        /*if(this.getArguments() != null){
            String texto = getArguments().getString("Titulo");
            String fecha = getArguments().getString("Fecha");
            Date fechaTexto;
            try {
                XSimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
                fechaTexto = formatoDeFecha.parse(fecha);
                Tarea tareaFav = new Tarea(texto,fechaTexto);
                listaTareasFav.add(tareaFav);*/

        RecyclerView recyclerViewTareas = root.findViewById(R.id.recyclerViewTareasFav);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTareasFav = new AdapterTareasFav(getContext(),listaTareasFav, this);
        recyclerViewTareas.setAdapter(adapterTareasFav);
            /*} catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

        //toolBar.setVisibility(View.GONE);
        listaTareasSeleccionadas = new ArrayList<>();

        return root;
    }

    @Override
    public void seleccionarTarea(Tarea tarea) {

     if (tarea.isTareaSeleccionada()) {
        listaTareasSeleccionadas.add(tarea);
    } else {
        listaTareasSeleccionadas.remove(tarea);
    }
    //toolBar.setVisibility(listaTareasSeleccionadas.isEmpty() ? View.GONE : View.VISIBLE);
}

    public void onClickToolbarEliminar(View view) {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(getContext());
        builderEliminar.setIcon(R.drawable.eliminar);
        builderEliminar.setTitle("Eliminar elementos");

        final ArrayList<String> listaTareasAeliminar = new ArrayList<>();
        String[] arrayTareas = new String[listaTareasFinalizadas.size()];
        final boolean[] tareasSeleccionadas = new boolean[listaTareasFinalizadas.size()];
        for (int i = 0; i < listaTareasFav.size(); i++){
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
                this.adapterTareasFav.notifyDataSetChanged();
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
        adapterTareasFav.notifyDataSetChanged();
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
                        listaTareasFav.remove(listaTareasFinalizadas.get(i));
                        tareaLab.get(getContext()).deleteTarea(listaTareasFinalizadas.get(i));
                        //todo: método eliminar a BD.
                    }
                }
                Toast.makeText(getContext(), "Tareas eliminadas correctamente", Toast.LENGTH_SHORT).show();
                this.adapterTareasFav.notifyDataSetChanged();
            });
            builderEliminar_Confirmar.create().show();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }*/
}