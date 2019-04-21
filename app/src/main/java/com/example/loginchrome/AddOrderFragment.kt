package com.example.loginchrome

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_add_order.*
import android.app.Activity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService




class AddOrderFragment : Fragment() {

    var spinner: Spinner? = null;

    private lateinit var viewF: View;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewF = inflater.inflate(R.layout.fragment_add_order, container, false);

        val model = activity?.let { ViewModelProviders.of(it).get(OrderViewModel::class.java) }


        spinner = viewF.findViewById<Spinner>(R.id.place);
        val myStrings = arrayOf("ABP Squires", "D2", "Deets", "DX", "Hokie Grill", "Owens", "Turner", "West End")
        spinner?.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item, myStrings
        )
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                p0: AdapterView<*>?, p1: View?, p2: Int, p3:
                Long
            ) {
                //Toast.makeText(context, myStrings[p2], Toast.LENGTH_LONG).show()
            }
        }

        viewF.apply {
            val nameBox = findViewById<EditText>(R.id.editText1)
            val orderName = findViewById<EditText>(R.id.editText2)
            val orderFrom = findViewById<Spinner>(R.id.place)
            val tapingoOrderId = findViewById<EditText>(R.id.editText4)
            val locationToDropOff = findViewById<EditText>(R.id.editText5);


            val ref = model?.database?.getReference("message")
            findViewById<Button>(R.id.addToOrder).setOnClickListener{

                /*if(nameBox.text.isBlank() || orderName.text.isBlank() || tapingoOrderId.text.isBlank() || locationToDropOff.text.isBlank()) {
                    Toast.makeText(context, "Please fill out the required fields", Toast.LENGTH_SHORT);
                } else {*/
                    val orderItem = OrderItem(1.0, orderName.text.toString(), orderFrom.selectedItem.toString(), locationToDropOff.text.toString(), nameBox.text.toString(),  tapingoOrderId.text.toString(), model?.email ?: "Error");
                    model?.addItem(orderItem);

                    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
                    imm.hideSoftInputFromWindow(viewF.getWindowToken(), 0)

                    if(ref != null) {
                        Log.e("yolo", "NOT NULL");
                    }
                    ref?.setValue(orderItem);

                    viewF.findNavController().navigate(R.id.action_addOrderFragment_to_viewOrdersFragment);


                //}



            }
        };

        return viewF;
    }
}

