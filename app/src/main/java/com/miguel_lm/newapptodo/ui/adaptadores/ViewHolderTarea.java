package com.miguel_lm.newapptodo.ui.adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.ui.ListenerTareas;

import java.util.ArrayList;
import java.util.List;

public class ViewHolderTarea extends RecyclerView.ViewHolder {

    private TextView tv_Tarea_Fecha;
    private TextView tv_Tarea_Titulo;
    private CardView cardViewTarea;
    private ImageButton btn_fav,btn_fav_activado, btn_elimimnar;
    private List<Tarea> listaFav;

    private ListenerTareas listenerTareas;

    public ViewHolderTarea(@NonNull View itemView, ListenerTareas listenerTareas) {
        super(itemView);

        this.listenerTareas = listenerTareas;

        tv_Tarea_Titulo = itemView.findViewById(R.id.tv_tareaNueva);
        tv_Tarea_Fecha = itemView.findViewById(R.id.tv_fechaNueva);
        cardViewTarea = itemView.findViewById(R.id.CardViewTarea);
        btn_fav = itemView.findViewById(R.id.btn_Fav_blanco);
        btn_fav_activado = itemView.findViewById(R.id.btn_Fav_amarillo);
        btn_elimimnar = itemView.findViewById(R.id.btn_eliminar);
        listaFav = new ArrayList<>();
    }

    public void mostrarTarea(final Tarea tarea,Context context) {

        tv_Tarea_Titulo.setText(tarea.getTitulo());
        tv_Tarea_Fecha.setText(tarea.getFechaTexto());

        btn_fav.setOnClickListener(v -> {
            btn_fav.setVisibility(View.INVISIBLE);
            btn_fav_activado.setVisibility(View.VISIBLE);
            listaFav.add(tarea);
        });

        btn_fav_activado.setOnClickListener(v -> {
            btn_fav_activado.setVisibility(View.INVISIBLE);
            btn_fav.setVisibility(View.VISIBLE);
            listaFav.remove(tarea);
        });

        btn_elimimnar.setOnClickListener(v -> {
            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(context);
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setMessage("¿Eliminar los elementos?");
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which) -> {
                listenerTareas.seleccionarTarea(tarea);
                Toast.makeText(context, "Tarea eliminada", Toast.LENGTH_SHORT).show();
            });
            builderEliminar_Confirmar.create().show();
        });

        final String colorSeleccionado = "#ffff0000";
        final String colorNOSeleccionado = "#03DAC5";
        cardViewTarea.setCardBackgroundColor(Color.parseColor(tarea.isTareaSeleccionada() ? colorSeleccionado : colorNOSeleccionado));

        cardViewTarea.setOnClickListener(v -> {

            tarea.setTareaSeleccionada(!tarea.isTareaSeleccionada());
            cardViewTarea.setCardBackgroundColor(Color.parseColor(tarea.isTareaSeleccionada() ? colorSeleccionado : colorNOSeleccionado));

            listenerTareas.seleccionarTarea(tarea);
        });
    }
}
