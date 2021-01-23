package com.miguel_lm.newapptodo.ui.adaptador;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.ListenerTareas;

public class ViewHolderTarea extends RecyclerView.ViewHolder {

    private TextView tv_Tarea_Fecha;
    private TextView tv_Tarea_Titulo;
    private CardView cardViewTarea;
    private ImageButton btn_fav_no_activado,btn_fav_activado, btn_elimimnar;
    private CheckBox checkBoxSeleccion;
    private ConstraintLayout constraintLayoutTarea;

    static final String COLOR_SELECCIONADO = "#ffff0000";
    static final String COLOR_NO_SELECCIONADO = "#000000";  //"#FF0000FF"


    private ListenerTareas listenerTareas;

    public ViewHolderTarea(@NonNull View itemView, ListenerTareas listenerTareas) {
        super(itemView);

        this.listenerTareas = listenerTareas;

        tv_Tarea_Titulo = itemView.findViewById(R.id.tv_tareaNueva);
        tv_Tarea_Fecha = itemView.findViewById(R.id.tv_fechaNueva);
        cardViewTarea = itemView.findViewById(R.id.CardViewTarea);
        btn_fav_no_activado = itemView.findViewById(R.id.btn_Fav_blanco);
        btn_fav_activado = itemView.findViewById(R.id.btn_Fav_amarillo);
        btn_elimimnar = itemView.findViewById(R.id.btn_eliminar);
        checkBoxSeleccion = itemView.findViewById(R.id.checkBoxSeleccion);
        constraintLayoutTarea = itemView.findViewById(R.id.constraintLayoutTarea);
    }

    public void mostrarTarea(final Tarea tarea, Context context) {

        // Mostrar datos de la tarea
        tv_Tarea_Titulo.setText(tarea.getTitulo());
        tv_Tarea_Fecha.setText(tarea.getFechaTexto());

        // Indicar si es favorita o no
        btn_fav_no_activado.setVisibility(tarea.esFav ? View.INVISIBLE : View.VISIBLE);
        btn_fav_activado.setVisibility(tarea.esFav ? View.VISIBLE : View.INVISIBLE);

        // Indicar si está completada
        checkBoxSeleccion.setOnCheckedChangeListener(null);
        checkBoxSeleccion.setChecked(tarea.isCompletado());

        // Mostrar tarea como seleccionada
        tintBackground(tarea);


        //////////////////////////////////////////////////////////////////////////////////

        // Detectar marcar como favorito
        // Se marca como fav y se actualiza la BD
        // Se usa el listener para notificar al fragment y resto de fragments que hay que repintar listados
        btn_fav_no_activado.setOnClickListener(v -> {
            btn_fav_no_activado.setVisibility(View.INVISIBLE);
            btn_fav_activado.setVisibility(View.VISIBLE);

            tarea.setEsFav(true);
            TareaLab.get(context).updateTarea(tarea);
            Toast.makeText(context, "Tarea añadida a Favoritos", Toast.LENGTH_SHORT).show();

            listenerTareas.seleccionarTareasFavAdd(tarea);
        });

        // Detectar quitar como favorito
        // Se desmarca como fav y se actualiza la BD
        // Se usa el listener para notificar al fragment y resto de fragments que hay que repintar listados
        btn_fav_activado.setOnClickListener(v -> {
            btn_fav_activado.setVisibility(View.INVISIBLE);
            btn_fav_no_activado.setVisibility(View.VISIBLE);

            tarea.setEsFav(false);
            TareaLab.get(context).updateTarea(tarea);
            Toast.makeText(context, "Tarea eliminada de Favoritos", Toast.LENGTH_SHORT).show();

            listenerTareas.seleccionarTareasFavRemove(tarea);
        });

        // Eliminar una tarea
        btn_elimimnar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(context);
                builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
                builderEliminar_Confirmar.setMessage("¿Eliminar la tarea '" + tarea.getTitulo() + "'?");
                builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
                builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which) -> {
                    TareaLab.get(context).deleteTarea(tarea);
                    Toast.makeText(context, "Tarea eliminada", Toast.LENGTH_SHORT).show();

                    listenerTareas.eliminarTarea(tarea);
                });
                builderEliminar_Confirmar.create().show();

            }
        });

        // Recoger tarea seleccionada
        checkBoxSeleccion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                tarea.setCompletado(isChecked);
                TareaLab.get(context).updateTarea(tarea);
                Toast.makeText(context, isChecked ? "Tarea COMPLETADA" : "Tarea no completada", Toast.LENGTH_SHORT).show();

                listenerTareas.completarTarea(tarea, isChecked);
            }
        });

        cardViewTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tarea.setTareaSeleccionada(!tarea.isTareaSeleccionada());
                tintBackground(tarea);
                listenerTareas.seleccionarTarea(tarea);
            }
        });
    }

    private void tintBackground(Tarea tarea) {
        int color = Color.parseColor(tarea.isTareaSeleccionada() ? COLOR_SELECCIONADO : COLOR_NO_SELECCIONADO);
        constraintLayoutTarea.setBackgroundTintList(ColorStateList.valueOf(color));
    }

}
