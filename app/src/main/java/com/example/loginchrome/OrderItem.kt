package com.example.loginchrome

data class OrderItem(
    val payment: Double, //how much is the dude gonna pay for delivery
    val itemName: String,
    val fromLocation: String,
    val toLocation: String,
    val customerName: String,
    val orderId: String?, //sometimes tapingo ppl require you to have order confirmation before you can get your food
    val posterEmail: String,
    var orderState: OrderState = OrderState.NOT_READY,
    var carrierEmail: String = "None",//will be updated when they claim the item- EMAIL OF CARRIER
    var id: String = "ERROR" //MUST BE SET AFTER CREATUIB!!!!!

){
    constructor() : this(1.0, "None",
        "None", "None", "None",
        "None", "None", OrderState.NOT_READY,
        "None", "ERROR")

}

enum class OrderState {
    NOT_READY,
    NOT_PICKED_UP,
    IN_ROUTE,
    DELIVERED
}