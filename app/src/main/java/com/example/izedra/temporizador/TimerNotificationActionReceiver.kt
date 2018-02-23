package com.example.izedra.temporizador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceGroup
import com.example.izedra.temporizador.util.NotificationUtil
import com.example.izedra.temporizador.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            Constantes.ACTION_STOP ->{
                MainActivity.removeAlarm(context)
                PrefUtil.setTempEst(MainActivity.Estado.Parado, context)
                NotificationUtil.hideTimerNotification(context)
            }

            Constantes.ACTION_PAUSE -> {
                var segsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = MainActivity.nowSeconds

                segsRemaining -= nowSeconds - alarmSetTime
                PrefUtil.setSecondsRemaining(segsRemaining, context)

                MainActivity.removeAlarm(context)
                PrefUtil.setTempEst(MainActivity.Estado.Pausado, context)
                NotificationUtil.showTimerPaused(context)
            }

            Constantes.ACTION_RESUME -> {
                val segsRemaining = PrefUtil.getSecondsRemaining(context)
                val wakeUpTime = MainActivity.setAlarm(context, MainActivity.nowSeconds, segsRemaining)
                PrefUtil.setTempEst(MainActivity.Estado.Corriendo, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }

            Constantes.ACTION_START -> {
                val minsRemaining = PrefUtil.getTempMins(context)
                val segsRemaining = minsRemaining * 60L
                val wakeUpTime = MainActivity.setAlarm(context, MainActivity.nowSeconds, segsRemaining)
                PrefUtil.setTempEst(MainActivity.Estado.Corriendo, context)
                PrefUtil.setSecondsRemaining(segsRemaining, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
