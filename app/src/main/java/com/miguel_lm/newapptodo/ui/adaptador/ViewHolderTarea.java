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

import com.google.android.material.snackbar.Snackbar;
import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.ListenerTareas;

public class ViewHolderTarea extends RecyclerView.ViewHolder {

    private TextView tv_Tarea_Fecha, tv_Tarea_Hora;
    private TextView tv_Tarea_Titulo;
    private CardView cardViewTarea;
    private ImageButton btn_fav_no_activado, btn_fav_activado, btn_elimimnar;
    private CheckBox checkBoxSeleccion;
    private ConstraintLayout constraintLayoutTarea;
    private TareaLab tareaLab;
    private TextView textViewPosicion;

    static final String COLOR_SELECCIONADO = "#ffff0000";  //"#E30425";
    static final String COLOR_NO_SELECCIONADO = "#000000";
    static final String COLOR_COMPLETADO = "#AD686868";

    private ListenerTareas listenerTareas;

    public ViewHolderTarea(@NonNull View itemView, ListenerTareas listenerTareas) {
        super(itemView);

        this.listenerTareas = listenerTareas;

        tv_Tarea_Titulo = itemView.findViewById(R.id.tv_tareaNueva);
        tv_Tarea_Fecha = itemView.findViewById(R.id.tv_fechaNueva);
        tv_Tarea_Hora = itemView.findViewById(R.id.tv_horaNueva);
        cardViewTarea = itemView.findViewById(R.id.CardViewTarea);
        btn_fav_no_activado = itemView.findViewById(R.id.btn_Fav_blanco);
        btn_fav_activado = itemView.findViewById(R.id.btn_Fav_amarillo);
        btn_elimimnar = itemView.findViewById(R.id.btn_eliminar);
        checkBoxSeleccion = itemView.findViewById(R.id.checkBoxSeleccion);
        constraintLayoutTarea = itemView.findViewById(R.id.constraintLayoutTarea);
        textViewPosicion = itemView.findViewById(R.id.textViewPosicion);
    }

    public void mostrarTarea(final Tarea tarea, Context context) {

        tv_Tarea_Titulo.setText(tarea.getTitulo());
        tv_Tarea_Fecha.setText(tarea.getFechaTexto());
        tv_Tarea_Hora.setText(tarea.getHoraTexto());
        textViewPosicion.setText("lat: " + String.format("%.4f", tarea.latitud) + ",   long: " + String.format("%.4f", tarea.longitud));

        btn_fav_no_activado.setVisibility(tarea.esFav ? View.INVISIBLE : View.VISIBLE);
        btn_fav_activado.setVisibility(tarea.esFav ? View.VISIBLE : View.INVISIBLE);

        checkBoxSeleccion.setOnCheckedChangeListener(null);
        checkBoxSeleccion.setChecked(tarea.isCompletado());

        tintBackground(tarea);

        btn_fav_no_activado.setOnClickListener(v -> {
            btn_fav_no_activado.setVisibility(View.INVISIBLE);
            btn_fav_activado.setVisibility(View.VISIBLE);

            tarea.setEsFav(true);

            TareaLab.get(context).updateTarea(tarea);
            Toast.makeText(context, "Tarea añadida a Favoritos", Toast.LENGTH_SHORT).show();

            listenerTareas.seleccionarTareasFavAdd(tarea);
        });

        btn_fav_activado.setOnClickListener(v -> {
            btn_fav_activado.setVisibility(View.INVISIBLE);
            btn_fav_no_activado.setVisibility(View.VISIBLE);

            tarea.setEsFav(false);

            TareaLab.get(context).updateTarea(tarea);
            Toast.makeText(context, "Tarea eliminada de Favoritos", Toast.LENGTH_SHORT).show();

            listenerTareas.seleccionarTareasFavRemove(tarea);
        });

        btn_elimimnar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(context);
                builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
                builderEliminar_Confirmar.setMessage("¿Eliminar la tarea '" + tarea.getTitulo() + "'?");
                builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
                builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which) -> {

                    Snackbar snackbar = Snackbar.make(cardViewTarea, R.string.mensaje, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onShown(Snackbar sb) {
                            super.onShown(sb);
                        }

                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);

                            if (event != DISMISS_EVENT_ACTION) {
                                TareaLab.get(context).deleteTarea(tarea);

                                listenerTareas.eliminarTarea(tarea);

                            }
                        }
                    });

                    snackbar.show();

                });

                builderEliminar_Confirmar.create().show();

            }


        });

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
        int color;
        if (tarea.isTareaSeleccionada())
            color = Color.parseColor(COLOR_SELECCIONADO);
        else if (tarea.isCompletado())
            color = Color.parseColor(COLOR_COMPLETADO);
        else
            color = Color.parseColor(COLOR_NO_SELECCIONADO);

        constraintLayoutTarea.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
