package com.miguel_lm.newapptodo.core;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Entity(tableName = "Tarea")
public class Tarea implements Serializable, Comparable<Tarea> {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int mId;

    @ColumnInfo(name = "titulo")
    public String titulo;

    @ColumnInfo(name = "fechaCreacion")
    public Date fechaCreacion;

    @ColumnInfo(name = "esFav")
    public boolean esFav;

    @ColumnInfo(name = "completado")
    public boolean completado;

    @ColumnInfo(name = "fechaLimite")
    public Date fechaLimite;

    @ColumnInfo(name = "horaLimite")
    public Date horaLimite;

    @ColumnInfo(name = "latitud")
    public double latitud;

    @ColumnInfo(name = "longitud")
    public double longitud;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    private boolean tareaSeleccionada;

    public Tarea(String titulo, Date fechaLimite, Date horaLimite, double latitud, double longitud) {
        this.titulo = titulo;
        this.fechaCreacion = new Date();
        this.esFav = false;
        this.completado = false;
        this.fechaLimite = fechaLimite;
        this.horaLimite = horaLimite;
        this.latitud = latitud;
        this.longitud = longitud;
        tareaSeleccionada = false;
        mId = 0;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isEsFav() {
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
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public Date getHoraLimite() {
        return horaLimite;
    }

    public void setHoraLimite(Date horaLimite) {
        this.horaLimite = horaLimite;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFechaTexto() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", Locale.getDefault());
        return formatoFecha.format(fechaLimite);
    }

    public String getFechaTextoCorta() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy ", Locale.getDefault());
        return formatoFecha.format(fechaLimite);
    }

    public String getHoraTexto() {
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatoHora.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        return formatoHora.format(horaLimite);
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String toStringTarea() {
        return "\n· TITULO: " + getTitulo() + "\n\n· FECHA: " + getFechaTexto();
    }

    public void modificar(String titulo, Date fecha, Date hora, double latitud, double longitud) {
        this.titulo = titulo;
        this.fechaLimite = fecha;
        this.horaLimite = hora;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public boolean isTareaSeleccionada() {
        return tareaSeleccionada;
    }

    public void setTareaSeleccionada(boolean tareaSeleccionada) {
        this.tareaSeleccionada = tareaSeleccionada;
    }

    @Override
    public int compareTo(Tarea tarea) {

        if (tarea.fechaLimite.compareTo(fechaLimite) < 0) {
            return -1;
        } else if (tarea.fechaLimite.compareTo(fechaLimite) < 0) {
            return 0;
        } else {
            return 1;
        }
    }
}