package com.example.loginchrome

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_order.*


class AddOrderFragment : Fragment() {

    var spinner: Spinner? = null;

    private lateinit var viewF: View;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewF = inflater.inflate(R.layout.fragment_add_order, container, false);


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
                Toast.makeText(context, myStrings[p2], Toast.LENGTH_LONG).show()
            }
        }

        return viewF;
    }
}

