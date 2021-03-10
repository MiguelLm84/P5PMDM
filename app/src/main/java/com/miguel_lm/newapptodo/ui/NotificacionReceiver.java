package com.miguel_lm.newapptodo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;

public class NotificacionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Tarea tareaRecogidaNotificacion = (Tarea) intent.getParcelableExtra("TAREA");
        boolean accionModificar = intent.getAction().equalsIgnoreCase("MODIFICAR");

        if (accionModificar) {
            Intent intentModificarTarea = new Intent(context, ActivityTarea.class);
            intentModificarTarea.putExtra(ActivityTarea.PARAM_TAREA_EDITAR, tareaRecogidaNotificacion);
            context.startActivity(intentModificarTarea);

        } else {
            TareaLab tareaLab = TareaLab.get(context);
            tareaLab.deleteTarea(tareaRecogidaNotificacion);
        }

        context.startActivity(new Intent(context, MainActivity.class));

        Log.d("app", "prueba");
    }
}
