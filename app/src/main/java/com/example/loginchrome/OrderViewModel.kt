package com.example.loginchrome

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class OrderViewModel(application: Application): AndroidViewModel(application), Serializable {

    val database = FirebaseDatabase.getInstance();

    var db = UpdateService.db;

    var firebaseUser : FirebaseUser? = null;


    val items: MutableLiveData<List<OrderItem>> = MutableLiveData();

    var userName: String = "Not Signed In";
    var email : String = "Not Signed In";
    var picture: String = "Not Signed In";

    var currentOrder : OrderItem? = null;

    var adapter : RecyclerView.Adapter<ViewOrdersFragment.OrderListAdapter.OrderViewHolder>? = null;

    var selectedFrom: String = "All"

    var filterMyOrders: Boolean = false
    var filterMyCarries: Boolean = false

    fun addItem(item: OrderItem) {
        items.postValue(items.value?.plusElement(item) ?: listOf(item));
    }

    fun updateItems(item: List<OrderItem>) {
        items.postValue(item)
    }





}