package com.example.loginchrome

import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Math.*

data class Notification(var id : String, val emailTo: String, val message: String) {
    companion object {
        fun send(db: FirebaseFirestore?, to: String, msg : String){

            val notif = Notification("", to, msg);

            notif.id = Integer.toHexString(notif.hashCode());

            db?.collection("notifications")?.document(notif.id)?.set(notif);


        }

        fun distanceDeg(lat1:Double, lon1:Double, lat2:Double, lon2:Double):Double{
			return distanceRad(lat1*PI/180,lon1*PI/180,lat2*PI/180,lon2*PI/180)
		}

		fun distanceRad(lat1:Double, lon1:Double, lat2:Double, lon2:Double):Double{
			return 6728.0*Math.ceil(Math.sqrt(2-2*cos(lat1)*cos(lat2)*cos(lon1-lon2) - 2*sin(lat1)*sin(lat2)));
		}
    }
}
