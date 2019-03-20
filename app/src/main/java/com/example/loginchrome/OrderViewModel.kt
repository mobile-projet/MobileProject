package com.example.loginchrome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class OrderViewModel(application: Application): AndroidViewModel(application) {


    val items: MutableLiveData<List<OrderItem>> = MutableLiveData();

    var userName: String? = null;
    var email : String? = null;





}