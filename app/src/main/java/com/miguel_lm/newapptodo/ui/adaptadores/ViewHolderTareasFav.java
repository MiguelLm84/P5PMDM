package com.miguel_lm.newapptodo.ui.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.ui.ListenerTareas;

import java.util.ArrayList;
import java.util.List;

public class ViewHolderTareasFav extends RecyclerView.ViewHolder {

    final TextView textViewTarea_Fecha;
    final TextView textViewTarea_Titulo;
    private final CardView cardViewTarea;
    final ImageButton btn_fav,btn_fav_activado, btn_elimimnar;
    final List<Tarea> listaFav;

    private final ListenerTareas listenerTareas;

    public ViewHolderTareasFav(@NonNull View itemView, ListenerTareas listenerTareas) {
        super(itemView);
        this.listenerTareas = listenerTareas;

        textViewTarea_Titulo = itemView.findViewById(R.id.ed_nomTarea);
        textViewTarea_Fecha = itemView.findViewById(R.id.textViewTareaFechaLimite);
        cardViewTarea = itemView.findViewById(R.id.CardViewTarea);
        btn_fav = itemView.findViewById(R.id.btn_Fav_blanco);
        btn_fav_activado = itemView.findViewById(R.id.btn_Fav_amarillo);
        btn_elimimnar = itemView.findViewById(R.id.btn_eliminar);
        listaFav = new ArrayList<>();
    }

    public void mostrarTarea(final Tarea tarea, Context context) {

        textViewTarea_Titulo.setText(tarea.getTitulo());
        textViewTarea_Fecha.setText(tarea.getFechaTexto());

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
