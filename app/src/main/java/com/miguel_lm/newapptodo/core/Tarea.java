package com.miguel_lm.newapptodo.core;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "Tarea")
public class Tarea implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int mId;

    @ColumnInfo(name="titulo")
    public String titulo;

    @ColumnInfo(name="fechaCreacion")
    public Date fechaCreacion;

    /*@ColumnInfo(name="esFav")
    public boolean esFav;

    @ColumnInfo(name="completado")
    public boolean completado;*/

    @ColumnInfo(name="fechaLimite")
    public Date fechaLimite;

    /** Indica si la tarea se ha seleccionado en el listado para trabajar con lotes de tareas */
    private boolean tareaSeleccionada;

    public Tarea(String titulo, /*boolean esFav, boolean completado,*/ Date fechaLimite) {
        this.titulo = titulo;
        this.fechaCreacion = new Date();
        //this.esFav = esFav;
        //this.completado = completado;
        this.fechaLimite = fechaLimite;
        tareaSeleccionada = false;
        mId = 0;
    }

    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /*public boolean isEsFav() {
        return esFav;
    }

    public void setEsFav(boolean esFav) {
        this.esFav = esFav;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }*/

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Date getFechaCreacion() {

        return fechaCreacion;
    }

    public String getFechaTexto() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy ", Locale.getDefault());
        return formatoFecha.format(fechaCreacion);
    }

    public String getFechaTextoCorta() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy ", Locale.getDefault());
        return formatoFecha.format(fechaCreacion);
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String toStringTarea(){

        return "\n· TITULO: "+getTitulo()+"\n\n· FECHA: "+getFechaTexto();
    }

    public void modificar(String titulo, Date fecha) {

        this.titulo = titulo;
        this.fechaCreacion = fecha;
    }

    public boolean isTareaSeleccionada() {
        return tareaSeleccionada;
    }

    public void setTareaSeleccionada(boolean tareaSeleccionada) {
        this.tareaSeleccionada = tareaSeleccionada;
    }
}