package com.miguel_lm.newapptodo.ui.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.ListenerTareas;

import java.util.List;

public class AdapterTareasFav extends RecyclerView.Adapter<ViewHolderTarea> {

    private List<Tarea> listaTareasFav;
    private final Context context;
    private final ListenerTareas listenerTareas;

    public AdapterTareasFav(final Context context, List<Tarea> listaTareasFav, ListenerTareas listenerTareas) {
        this.context = context;
        this.listaTareasFav = listaTareasFav;
        this.listenerTareas = listenerTareas;
    }

    public void actualizarListado() {
        this.listaTareasFav = TareaLab.get(context).getTareas();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderTarea onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new ViewHolderTarea(v, listenerTareas);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTarea holder, int position) {

        holder.mostrarTarea(listaTareasFav.get(position), context);
    }

    @Override
    public int getItemCount() {

        return listaTareasFav.size();
    }
}
