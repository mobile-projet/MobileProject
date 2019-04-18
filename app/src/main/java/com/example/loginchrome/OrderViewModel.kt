package com.example.loginchrome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class OrderViewModel(application: Application): AndroidViewModel(application) {


    val items: MutableLiveData<List<OrderItem>> = MutableLiveData();

    var userName: String = "Not Signed In";
    var email : String = "Not Signed In";
    var picture: String = "Not Signed In";

    var currentOrder : OrderItem? = null;

    var adapter : RecyclerView.Adapter<ViewOrdersFragment.OrderListAdapter.OrderViewHolder>? = null;

    var selectedFrom: String = ""

    fun addItem(item: OrderItem) {
        items.postValue(items.value?.plusElement(item) ?: listOf(item));
    }





}