package com.example.loginchrome

data class OrderItem(

    val payment: Double, //how much is the dude gonna pay for delivery
    val itemName: String,
    val fromLocation: String,
    val toLocation: String,
    val customerName: String,
    val orderId: String?, //sometimes tapingo ppl require you to have order confirmation before you can get your food
    val orderState: OrderState = OrderState.NOT_READY


)

enum class OrderState {
    NOT_READY,
    NOT_PICKED_UP,
    IN_ROUTE,
    DELIVERED
}