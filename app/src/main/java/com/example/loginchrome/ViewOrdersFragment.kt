package com.example.loginchrome

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ViewOrdersFragment : Fragment() {

    private lateinit var viewF: View;

    private var model : OrderViewModel? = null;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewF = inflater.inflate(R.layout.fragment_view_orders, container, false);
        // Inflate the layout for this fragment

        model  = activity?.let{ViewModelProviders.of(it).get(OrderViewModel::class.java)}


        val recyclerView = viewF.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = OrderListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        model?.items?.observeForever {
            adapter.setMovies(it)
        }

        model?.adapter = adapter;

        //model?.addItem(OrderItem(1.0, "yolo", "fromloc", "toLoc", "my name", "", model?.email ?: "Error"));


        val addOrderButton = viewF.findViewById<Button>(R.id.addOrderButton);
        addOrderButton.setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_viewOrdersFragment_to_addOrderFragment);
        }

        return viewF;
    }

    inner class OrderListAdapter():
        RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>(){
        private var orders = emptyList<OrderItem>()

        internal fun setMovies(orders: List<OrderItem>) {
            this.orders = orders
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {

            return orders.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {


            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view, parent, false)
            return OrderViewHolder(v)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {


            //holder.bindItems(movieList[position])

            val item = orders[position];

            holder.view.findViewById<TextView>(R.id.fromTextView).text="From: ${item.fromLocation}"

            holder.view.findViewById<TextView>(R.id.toTextView).text="To: ${item.toLocation}"

            holder.view.findViewById<TextView>(R.id.paymentTextView).text = String.format("Payment: \$%.02f", item.payment);

            val avail =  holder.view.findViewById<TextView>(R.id.availableText);

            when(item.orderState) {
                OrderState.NOT_READY -> {
                    avail.setTextColor(Color.GREEN);
                    avail.text = "AVAILABLE"
                };
                OrderState.IN_ROUTE -> {
                    if(item.carrierEmail.equals(model?.email)) {
                        avail.setTextColor(Color.BLUE);
                        avail.text = "VIEW";
                    } else if(item.posterEmail.equals(model?.email)) {
                        avail.setTextColor(Color.YELLOW)
                        avail.text = "IN ROUTE"
                    } else {
                        avail.setTextColor(Color.RED);
                        avail.text = "TAKEN";
                    }

                }
            }


            holder.itemView.setOnClickListener(){

                if(!avail.text.equals("TAKEN")) {


                    model?.currentOrder = item;

                    Log.e("P", model?.currentOrder?.toString());

                    viewF.findNavController().navigate(
                        R.id.action_viewOrdersFragment_to_displayOrderFragment,
                        bundleOf("moviePos" to position)
                    );

                }


            }

        }


        inner class OrderViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {


                }


            }


        }
    }

}
