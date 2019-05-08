package com.example.loginchrome

import com.google.firebase.firestore.FirebaseFirestore

data class Notification(var id : String, val emailTo: String, val message: String) {
    companion object {
        fun send(db: FirebaseFirestore?, to: String, msg : String){

            val notif = Notification("", to, msg);

            notif.id = Integer.toHexString(notif.hashCode());

            db?.collection("notifications")?.document(notif.id)?.set(notif);


        }
    }
}
