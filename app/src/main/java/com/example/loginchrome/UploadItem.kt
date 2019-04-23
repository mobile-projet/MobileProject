package com.example.loginchrome

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore

class UploadItem(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork() : Result {
        val orderItem : Any? = inputData.keyValueMap.get("itemObj");
        val db : FirebaseFirestore? = inputData.keyValueMap.get("database") as FirebaseFirestore?;
        val collection = inputData.getString("collectionName");
        val id = inputData.getString("itemId");

        if(collection != null && db != null && orderItem != null && id != null)
            db.collection(collection).document(id).set(orderItem);
        else {
            return Result.failure();
        }
        return Result.success();
    }

}