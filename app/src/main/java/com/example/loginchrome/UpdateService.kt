package com.example.loginchrome

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class UpdateService: Service() {

    var started = false;

    companion object {
        const val UPDATE_LIST = "updateJob";
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

    fun startListener(model: OrderViewModel?) {
        if (started) {
            return;
        }

        model?.db?.collection("orders")
            ?.addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, firebaseFirestoreException ->
                val items = ArrayList<OrderItem>();

                for (doc in querySnapshot!!) {
                    if (doc.get("itemName") != null) {
                        val item = doc.toObject(OrderItem::class.java);
                        if(item.orderState != OrderState.DELIVERED)
                            items.add(item)
                    }
                }
                model.updateItems(items);
            });

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