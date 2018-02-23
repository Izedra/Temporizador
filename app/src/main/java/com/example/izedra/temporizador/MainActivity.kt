package com.example.izedra.temporizador

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.izedra.temporizador.util.NotificationUtil
import com.example.izedra.temporizador.util.PrefUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, segsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + segsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimeExpireReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(context, nowSeconds)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimeExpireReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(context, 0)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class Estado{
        Parado, Pausado, Corriendo
    }

    private lateinit var temp: CountDownTimer
    private var tempSegs = 0L
    private var tempEst = Estado.Parado

    private var segsRemaining = 0L

    private fun opciones(): Boolean{
        val i = Intent(this, Preferencias::class.java)
        startActivity(i)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "   Temporizador"

        fab_start.setOnClickListener{ view ->
            startTimer()
            tempEst = Estado.Corriendo
            actualizarBotones()
        }

        fab_pause.setOnClickListener{ view ->
            temp.cancel()
            tempEst = Estado.Pausado
            actualizarBotones()
        }

        fab_stop.setOnClickListener { view ->
            temp.cancel()
            pararTemp()
        }
    }

    override fun onResume() {
        super.onResume()

        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
        // Elimiar temporizador en background, ocultar notificaciones
    }

    override fun onPause() {
        super.onPause()

        if (tempEst == Estado.Corriendo){
            temp.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, segsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
            // Lanzar background y ocultar notificaciones
        } else if (tempEst == Estado.Pausado){
            NotificationUtil.showTimerPaused(this)
            //Mostrar notificaciones
        }

        PrefUtil.setPreviousTimerLengthSeconds(tempSegs, this)
        PrefUtil.setSecondsRemaining(segsRemaining, this)
        PrefUtil.setTempEst(tempEst, this)
    }

    private fun initTimer(){
        tempEst = PrefUtil.getTempEst(this)

        if (tempEst == Estado.Parado){
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }

        segsRemaining =
                if (tempEst == Estado.Corriendo || tempEst == Estado.Pausado){
                    PrefUtil.getSecondsRemaining(this)}
                else{
                    tempSegs
                }

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime>0){
            segsRemaining -= nowSeconds - alarmSetTime
        }
        // Cambiar segsRemaining dependiendo de cuando se detiene el temporizador en segundo plano

        if (segsRemaining <= 0 ){
            pararTemp()
        }
        // Resumir Act. cuando lo paramos
        else if (tempEst == Estado.Corriendo){
            startTimer()
        }

        actualizarBotones()
        actualizarCountdownUI()
    }

    private fun pararTemp(){
        tempEst = Estado.Parado
        setNewTimerLength()

        progress_countdown.progress = 0
        progress_countdown2.progress = 0

        PrefUtil.setSecondsRemaining(tempSegs, this)
        segsRemaining = tempSegs

        actualizarBotones()
        actualizarCountdownUI()
    }

    private fun startTimer(){
        tempEst = Estado.Corriendo

        temp = object : CountDownTimer(segsRemaining * 1000, 1000){
            override fun onFinish() = pararTemp()

            override fun onTick(millisUntilFinished: Long){
                segsRemaining = millisUntilFinished / 1000
                actualizarCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val minutos = PrefUtil.getTempMins(this)
        tempSegs = (minutos * 60L)
        progress_countdown.max = tempSegs.toInt()
        progress_countdown2.max = tempSegs.toInt()
    }

    private fun setPreviousTimerLength(){
        tempSegs = PrefUtil.getPreviousTimerLengthSeconds(this)

        progress_countdown.max = tempSegs.toInt()
        progress_countdown2.max = tempSegs.toInt()
    }

    private fun actualizarCountdownUI(){
        val minsToFinish = segsRemaining / 60
        val segsToFinish = segsRemaining - minsToFinish * 60
        val segsStr = segsToFinish.toString()
        textView_countdown.text = "$minsToFinish:${
            if (segsStr.length == 2)
                segsStr
            else "0" + segsStr}"

        progress_countdown.progress = (tempSegs - segsToFinish).toInt()
        progress_countdown2.progress = (tempSegs - segsToFinish).toInt()
    }

    private fun actualizarBotones(){
        when(tempEst){
            Estado.Corriendo ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }

            Estado.Parado ->{
                fab_start.isEnabled = true
                fab_stop.isEnabled = false
                fab_pause.isEnabled = false
            }

            Estado.Pausado ->{
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
                fab_start.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings ->
                opciones()
            else ->  super.onOptionsItemSelected(item)
        }
    }
}
