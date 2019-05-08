package com.example.loginchrome


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class displayOrderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewF = inflater.inflate(R.layout.fragment_display_order, container, false);

        val model = activity?.let { ViewModelProviders.of(it).get(OrderViewModel::class.java) }
        val currentItem = model?.currentOrder

        viewF.apply {
            val orderLocation = findViewById<TextView>(R.id.orderLocationDisp);
            val deliveryLocation = findViewById<TextView>(R.id.deliveryLocationDisp);
            val paymentToReceive = findViewById<TextView>(R.id.paymentDisp)
            val itemOrdered = findViewById<TextView>(R.id.itemOrderDisp)
            val orderId = findViewById<TextView>(R.id.orderIdDisp)
            val customerName = findViewById<TextView>(R.id.customerNameDisp);

            orderLocation.text = currentItem?.fromLocation;
            deliveryLocation.text = currentItem?.toLocation;
            paymentToReceive.text = String.format("\$%.02f", currentItem?.payment);

            val button = findViewById<Button>(R.id.claimOrder);

            button.setOnClickListener {
                currentItem?.carrierEmail = model?.email.toString();
                itemOrdered.text = currentItem?.itemName;
                orderId.text = currentItem?.orderId;
                customerName.text = currentItem?.customerName;
                it.visibility = View.GONE;
                currentItem?.orderState = OrderState.IN_ROUTE;
                model?.adapter?.notifyDataSetChanged();

                model?.db?.collection("orders")?.document(currentItem?.id!!)?.set(currentItem);
            }

            if(currentItem?.carrierEmail.equals(model?.email) || currentItem?.posterEmail.equals(model?.email)) {
                itemOrdered.text = currentItem?.itemName;
                orderId.text = currentItem?.orderId;
                customerName.text = currentItem?.customerName;
                if(currentItem?.posterEmail.equals(model?.email)) {
                    button.setOnClickListener {
                        currentItem?.orderState = OrderState.DELIVERED;
                        model?.db?.collection("orders")?.document(currentItem?.id!!)?.set(currentItem)?.addOnSuccessListener { viewF.findNavController().navigate(R.id.action_displayOrderFragment_to_viewOrdersFragment) };
                        Notification.send(model?.db, currentItem?.posterEmail ?: "", "Your order has been picked up and is in route to be delivered");

                    }
                    if(OrderState.IN_ROUTE == currentItem?.orderState) {
                        button.text = "Mark as Delivered";
                        Notification.send(model?.db, currentItem.carrierEmail, "You have successfully delivered the item with id ".format(currentItem.id));

                    } else {
                        button.text = "Delete Entry"
                    }
                } else {
                    button.visibility = View.GONE;
                }
            }



        }
        // Inflate the layout for this fragment
        return viewF;
    }


}
