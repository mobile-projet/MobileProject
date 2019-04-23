package com.example.loginchrome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class OrderViewModel(application: Application): AndroidViewModel(application) {

    val database = FirebaseDatabase.getInstance();

    var firebaseUser : FirebaseUser? = null;


    val items: MutableLiveData<List<OrderItem>> = MutableLiveData();

    var userName: String = "Not Signed In";
    var email : String = "Not Signed In";
    var picture: String = "Not Signed In";

    var currentOrder : OrderItem? = null;

    var adapter : RecyclerView.Adapter<ViewOrdersFragment.OrderListAdapter.OrderViewHolder>? = null;

    var selectedFrom: String = ""

    var filterMyOrders: Boolean = false
    var filterMyCarries: Boolean = false

    fun addItem(item: OrderItem) {
        items.postValue(items.value?.plusElement(item) ?: listOf(item));
    }





}