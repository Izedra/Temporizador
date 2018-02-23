package com.example.izedra.temporizador.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.izedra.temporizador.MainActivity

/**
 * Created by DAM on 20/02/2018.
 */
class PrefUtil {
    companion object {

        fun getTempMins(context: Context): Int {
            // tiempo en minutos que dura el temporizador

            val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            try{
                var mins = sp.getString("minutos", "1").toInt()
                return mins
            } catch (ex: NumberFormatException){
                return 1
            }
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.izedra.timer.previous_timer_length"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(segs: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, segs)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.izedra.timer.timer_state"

        fun getTempEst(context: Context): MainActivity.Estado{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return MainActivity.Estado.values()[ordinal]
        }

        fun setTempEst(estado: MainActivity.Estado, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = estado.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.izedra.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(segs: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, segs)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.izedra.timer.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(context: Context, time: Long){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}