package com.miguel_lm.newapptodo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.util.Log;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.fragments.FragmentTareas;
import com.miguel_lm.newapptodo.ui.fragments.FragmentTareasCompletadas;

public class NotificacionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Tarea tareaRecogidaNotificacion = (Tarea) intent.getSerializableExtra("TAREA");
        boolean accionCompletada = intent.getAction().equalsIgnoreCase("COMPLETADA");
        boolean accionEliminar = intent.getAction().equalsIgnoreCase("BORRAR");

        if (accionCompletada) {

            tareaRecogidaNotificacion.completado = true;
            TareaLab tareaLab = TareaLab.get(context);
            tareaLab.updateTarea(tareaRecogidaNotificacion);

        } else if (accionEliminar) {
            TareaLab tareaLab = TareaLab.get(context);
            tareaLab.deleteTarea(tareaRecogidaNotificacion);
        }

        Intent intentActivity = new Intent(context, MainActivity.class);
        intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentActivity);

        Log.d("app", "prueba");
    }
}
