package com.example.loginchrome

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class UpdateService: Service() {

    var started = false;

    companion object {
        const val UPDATE_LIST = "updateJob";
        var isRunning = false;
        var db : FirebaseFirestore? = null;
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder;
    }
    private val iBinder = MyBinder()

    inner class MyBinder: Binder() {
        fun getService() : UpdateService {
            return this@UpdateService;
        }
    }

    var notificationEvent : ListenerRegistration? = null;
    var updateEvent : ListenerRegistration? = null;

    override fun onCreate() {
        super.onCreate()
        Log.e("YOLO", "STARTING THE SERVICE");
        isRunning = true;
        if(db == null)
            db = FirebaseFirestore.getInstance();

        val account = GoogleSignIn.getLastSignedInAccount(applicationContext);

        if (account != null) {
            notificationEvent?.remove();
            notificationEvent = db?.collection("notifications")?.whereEqualTo("emailTo", account.email)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    Log.e("yolo", "received notif");

                    querySnapshot?.forEach { t ->
                        val notif = t.toObject(Notification::class.java)

                        // Create an explicit intent for an Activity in your app
                        val intent = Intent(applicationContext, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

                        var builder = NotificationCompat.Builder(this, "YOLO")
                            .setSmallIcon(R.drawable.navigation_empty_icon)
                            .setContentTitle("Notification")
                            .setContentText(notif.message)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val name = getString(R.string.channel_name)
                            val descriptionText = getString(R.string.channel_description)
                            val importance = NotificationManager.IMPORTANCE_HIGH
                            val channel = NotificationChannel("YOLO", name, importance).apply {
                                description = descriptionText
                            }
                            // Register the channel with the system
                            val notificationManager: NotificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.createNotificationChannel(channel)

                            with(NotificationManagerCompat.from(applicationContext)) {
                                // notificationId is a unique int for each notification that you must define
                                notify(notif.hashCode(), builder.build())
                                Log.e("YOLO", "SENDING NOTIFICATION");
                                db?.collection("notifications")?.document(notif.id)?.delete();
                            }
                        }
                    }
                }

        }
    }

    fun startListener(model: OrderViewModel?) {
        if (started) {
            return;
        }

        model?.db = db;

        updateEvent?.remove();
        updateEvent = model?.db?.collection("orders")
            ?.addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, firebaseFirestoreException ->
                val items = ArrayList<OrderItem>();

                for (doc in querySnapshot!!) {
                    if (doc.get("itemName") != null) {
                        val item = doc.toObject(OrderItem::class.java);
                        if(item.orderState != OrderState.DELIVERED)
                            items.add(item)
                    }
                }
                model?.updateItems(items);
            });


        if(notificationEvent == null) {
            notificationEvent = model?.db?.collection("notifications")?.whereEqualTo("emailTo", model?.email)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                querySnapshot?.forEach { t ->
                    val notif = t.toObject(Notification::class.java)

                    // Create an explicit intent for an Activity in your app
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

                    var builder = NotificationCompat.Builder(this, "YOLO")
                        .setSmallIcon(R.drawable.navigation_empty_icon)
                        .setContentTitle("Notification")
                        .setContentText(notif.message)
                        .setContentIntent(pendingIntent).setVibrate(LongArray(0))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val name = getString(R.string.channel_name)
                        val descriptionText = getString(R.string.channel_description)
                        val importance = NotificationManager.IMPORTANCE_HIGH
                        val channel = NotificationChannel("YOLO", name, importance).apply {
                            description = descriptionText
                        }
                        // Register the channel with the system
                        val notificationManager: NotificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)

                        with(NotificationManagerCompat.from(applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(notif.hashCode(), builder.build())
                            Log.e("YOLO", "SENDING NOTIFICATION");
                            db?.collection("notifications")?.document(notif.id)?.delete();

                        }
                    }
                }

            }
        }



    }
    
    fun callUpdate(model: OrderViewModel?) {
        Log.e("yolo", "calling update")
        model?.db?.collection("orders")?.get()?.
            addOnSuccessListener {
                val items = ArrayList<OrderItem>();
                for(doc in it!!) {
                    if(doc.get("itemName") != null) {
                        val item = doc.toObject(OrderItem::class.java);
                        if(item.orderState != OrderState.DELIVERED)
                            items.add(item)
                    }
                }

                model.updateItems(items);

            }
    }


}