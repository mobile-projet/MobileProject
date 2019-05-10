package com.example.loginchrome


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BootDeviceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action

        val message = "BootDeviceReceiver onReceive, action is " + action!!

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        Log.d(TAG_BOOT_BROADCAST_RECEIVER, action)

        if (Intent.ACTION_BOOT_COMPLETED == action) {
            //startServiceDirectly(context);

            startServiceByAlarm(context)
        }
    }

    /* Start RunAfterBootService service directly and invoke the service every 10 seconds. */
    private fun startServiceDirectly(context: Context) {
        try {
            while (true) {
                val message = "BootDeviceReceiver onReceive start service directly."

                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                Log.d(TAG_BOOT_BROADCAST_RECEIVER, message)

                // This intent is used to start background service. The same service will be invoked for each invoke in the loop.
                val startServiceIntent = Intent(context, UpdateService::class.java)
                context.startService(startServiceIntent)

                // Current thread will sleep one second.
                Thread.sleep(10000)
            }
        } catch (ex: InterruptedException) {
            Log.e(TAG_BOOT_BROADCAST_RECEIVER, ex.message, ex)
        }

    }

    /* Create an repeat Alarm that will invoke the background service for each execution time.
     * The interval time can be specified by your self.  */
    private fun startServiceByAlarm(context: Context) {
        // Get alarm manager.
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create intent to invoke the background service.
        val intent = Intent(context, UpdateService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val startTime = System.currentTimeMillis()
        val intervalTime = (60 * 1000).toLong()

        val message = "Start service use repeat alarm. "

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        Log.d(TAG_BOOT_BROADCAST_RECEIVER, message)

        // Create repeat alarm.
        //alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent)
    }

    companion object {

        private val TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER"
    }
}