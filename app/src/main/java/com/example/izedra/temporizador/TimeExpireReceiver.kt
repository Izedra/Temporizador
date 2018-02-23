package com.example.izedra.temporizador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.izedra.temporizador.util.PrefUtil

class TimeExpireReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Mostrar notificaciones

        PrefUtil.setTempEst(MainActivity.Estado.Parado, context)
        PrefUtil.setAlarmSetTime(context, 0)
    }
}
