package com.miguel_lm.newapptodo.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.miguel_lm.newapptodo.ui.adaptador.AdapterTareas;

import java.util.ArrayList;
import java.util.List;

public class FragmentTareasCompletadas extends Fragment implements ListenerTareas {

    public static FragmentTareasCompletadas FragmentTareasInstanceCompletadas;

    AdapterTareas adapterTareasCompletadas;
    LinearLayout toolBar;
    List<Tarea> listaTareasSeleccionadas;
    TareaLab tareaLab;

    private ImageView imageButtonModificarTarea;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentTareasCompletadas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTareasCompletadas newInstance(String param1, String param2) {
        FragmentTareasCompletadas fragment = new FragmentTareasCompletadas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FragmentTareasInstanceCompletadas = this;

        View root = inflater.inflate(R.layout.fragment_tareas_completadas, container, false);

        toolBar = root.findViewById(R.id.toolbar3);
        toolBar.setVisibility(View.GONE);

        ImageView imageButtonEliminarTarea = root.findViewById(R.id.btn_delete);
        ImageView imageButtonSalirToolbar = root.findViewById(R.id.btn_salir);
        imageButtonModificarTarea = root.findViewById(R.id.btn_modificar);

        tareaLab = TareaLab.get(getContext());
        List<Tarea> listaTareasCompletadas = tareaLab.getTareasCaducadas();

        RecyclerView recyclerViewTareasCaducadas = root.findViewById(R.id.recyclerViewTareasCaducadas);
        recyclerViewTareasCaducadas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTareasCompletadas = new AdapterTareas(getContext(), listaTareasCompletadas, this);
        recyclerViewTareasCaducadas.setAdapter(adapterTareasCompletadas);

        listaTareasSeleccionadas = new ArrayList<>();

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

    public void refrescarListado() {

        adapterTareasCompletadas.actualizarListado(TareaLab.get(getContext()).getTareasCaducadas());
    }

    ////////////////////////////////////////////////////////////////
    // LISTENER VIEW HOLDER TAREA
    ////////////////////////////////////////////////////////////////

    @Override
    public void seleccionarTarea(Tarea tarea) {
        if (tarea.isTareaSeleccionada()) {
            listaTareasSeleccionadas.add(tarea);
        } else {
            listaTareasSeleccionadas.remove(tarea);
        }

        toolBar.setVisibility(listaTareasSeleccionadas.isEmpty() ? View.GONE : View.VISIBLE);
        imageButtonModificarTarea.setVisibility(listaTareasSeleccionadas.size() == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {
        refrescarListado();
    }

    @Override
    public void seleccionarTareasFavAdd(Tarea tarea) {

    }

    @Override
    public void seleccionarTareasFavRemove(Tarea tarea) {

    }

    @Override
    public void completarTarea(Tarea tarea, boolean completada) {

        if (!completada)
            refrescarListado();
    }

    ////////////////////////////////////////////////////////////////
    // TOOLBAR
    ////////////////////////////////////////////////////////////////

    public void onClickToolbarEliminar() {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(getContext());
        builderEliminar.setIcon(R.drawable.eliminar);
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
            String textoNombresTareas = null;

            // Generar array con los nombres de las tareas a borrar
            ArrayList<String> listaTareasAeliminar = new ArrayList<>();
            for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
                if (tareasSeleccionadasParaBorrar[i]) {
                    String tareasParaEliminar = "\n· TAREA: " + listaTareasSeleccionadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasSeleccionadas.get(i).getFechaTextoCorta() + "\n";
                    listaTareasAeliminar.add(tareasParaEliminar);
                }
            }

            // Y convertir el array de nombres en un solo string
            for (int i = 0; i < listaTareasAeliminar.size(); i++) {
                textoNombresTareas = listaTareasAeliminar.get(i) + ", ";
            }

            // Configurar el dialog
            builderEliminar_Confirmar.setMessage(textoNombresTareas);
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which1) -> {

                // Recorrer las tareas a borrar y eliminarlas de la BD
                for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
                    if (tareasSeleccionadasParaBorrar[i]) {
                        tareaLab.get(getContext()).deleteTarea(listaTareasSeleccionadas.get(i));
                    }
                }
                listaTareasSeleccionadas.clear();


                Toast.makeText(getContext(), "Tareas eliminadas correctamente", Toast.LENGTH_SHORT).show();

                refrescarListado();
            });
            builderEliminar_Confirmar.create().show();
            dialog.dismiss();
        });

        builderEliminar.setNegativeButton("Cancelar", null);
        builderEliminar.create().show();
    }


    public void onClickToolbarModificar() {

        //todo:implementar el método de modificar de la toolbar.
    }

    public void onClickToolbarSalir() {

        for (Tarea tarea : listaTareasSeleccionadas)
            tarea.setTareaSeleccionada(false);

        listaTareasSeleccionadas.clear();
        toolBar.setVisibility(View.GONE);
        adapterTareasCompletadas.notifyDataSetChanged();
        Toast.makeText(getContext(), "Salir sin seleccionar", Toast.LENGTH_SHORT).show();
    }
}