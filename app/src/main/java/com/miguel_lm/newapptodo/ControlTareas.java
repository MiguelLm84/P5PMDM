package com.miguel_lm.newapptodo;

import com.miguel_lm.newapptodo.core.Tarea;

public class ControlTareas {

    // Singleton
    private static ControlTareas instance = new ControlTareas();
    public static ControlTareas getInstance() { return instance; }

    public Tarea tareaActual;
}
