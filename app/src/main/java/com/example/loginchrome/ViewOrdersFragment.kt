package com.example.loginchrome

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update


class ViewOrdersFragment : Fragment() {


    var isInitialized = false;
    var isBound = false;
    var updateService : UpdateService? = null;

    private lateinit var viewF: View;

    private var model : OrderViewModel? = null
    private lateinit var fromSpinner: Spinner

    var startServiceIntent: Intent? = null
    var completionReceiver: UpdatedListReceiver? = null;



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewF = inflater.inflate(R.layout.fragment_view_orders, container, false);
        // Inflate the layout for this fragment

        model  = activity?.let{ViewModelProviders.of(it).get(OrderViewModel::class.java)}


        fromSpinner = viewF.findViewById(R.id.fromSpinner)

        ArrayAdapter.createFromResource(viewF.context, R.array.diningHalls, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            fromSpinner.adapter = adapter
        }

        fromSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                model?.selectedFrom = fromSpinner.selectedItem.toString();
                model?.items?.postValue(model?.items?.value ?: listOf<OrderItem>())
            }
        }


        val typeSpinner : Spinner = viewF.findViewById(R.id.typeSpinner);

        ArrayAdapter.createFromResource(viewF.context, R.array.filterType, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            typeSpinner.adapter = adapter
        }

        typeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (typeSpinner.selectedItem.toString().equals("My Carries")){
                    model?.filterMyCarries = true
                    model?.filterMyOrders = false
                } else if (typeSpinner.selectedItem.toString().equals("My Orders")){
                    model?.filterMyCarries = false
                    model?.filterMyOrders = true
                } else {
                    model?.filterMyCarries = false
                    model?.filterMyOrders = false
                }
                model?.items?.postValue(model?.items?.value ?: listOf<OrderItem>());

            }
        }

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

        if(savedInstanceState != null) {
            isInitialized = savedInstanceState.getBoolean(INITIALIZE_STATUS);

        }
        startServiceIntent = Intent(context, UpdateService::class.java);
        if(!isInitialized) {
            viewF.context.startService(startServiceIntent);
            isInitialized = true;
        }

        completionReceiver = UpdatedListReceiver(this, model);

        return viewF;
    }

    inner class OrderListAdapter():
        RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>(){
        private var orders = emptyList<OrderItem>()

        internal fun setMovies(orders: List<OrderItem>) {
            // filter by from Location
            this.orders = orders.filter {
                if((model?.selectedFrom?.equals("All") ?: true))
                    true
                else
                    it.fromLocation.equals(model?.selectedFrom)
            }

            this.orders = this.orders.filter {
                if(it.orderState == OrderState.IN_ROUTE) {
                    if (!it.carrierEmail.equals(model?.email) && !it.posterEmail.equals(model?.email)) {
                        false
                    } else true
                } else true
            }

            // filter by my orders or my carries
            if (model?.filterMyCarries ?: false){
                this.orders = this.orders.filter {
                    it.carrierEmail.equals(model?.email)
                }
            } else if (model?.filterMyOrders ?: false) {
                this.orders = this.orders.filter {
                    it.posterEmail.equals(model?.email)
                }
            }
            //this.orders = orders;

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

    override fun onPause() {
        super.onPause()
        if (isBound) {
            context?.unbindService(serviceConnection)
            isBound = false
        }

        context?.unregisterReceiver(completionReceiver)


    }

    override fun onResume() {
        super.onResume()
        if (isInitialized && !isBound) {
            Log.e("YOLO", "bound...");
            context?.bindService(startServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        context?.registerReceiver(completionReceiver, IntentFilter(UpdateService.UPDATE_LIST))
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(INITIALIZE_STATUS, isInitialized)
        super.onSaveInstanceState(outState)
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.e("YOLO", "BOUND");

            val binder = iBinder as UpdateService.MyBinder
            updateService = binder.getService()
            isBound = true

            Log.e("YOLO", "BOUND");

            updateService?.callUpdate(model);
            updateService?.startListener(model);
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            updateService = null
            isBound = false
        }
    }

    companion object {
        const val INITIALIZE_STATUS = "intialization status"
        const val CURR_IMAGE = "current image";
    }

}
