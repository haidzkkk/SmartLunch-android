package com.fpoly.smartlunch.data.model

data class Topping(
    val _id: String,
    val name: String,
    val price: Double,
    val productId: String,
    val isActive: Boolean
){
    private var _quantity: Int = 0
    val quantity: Int
        get() {
            return _quantity
        }

    fun plusQuantity(){
        _quantity += 1
    }

    fun minusQuantity(){
        if (_quantity > 0){
            _quantity -= 1
        }
    }

    companion object{
        fun toTopping(toppingCart: ToppingCart): Topping{
            return Topping(
                _id = toppingCart._id._id,
                name = toppingCart._id.name,
                price = toppingCart._id.price,
                productId = toppingCart._id.productId,
                isActive = toppingCart._id.isActive
            ).apply {
                _quantity = toppingCart._quantity
            }
        }

        fun toTopping(toppingOrder: ToppingOrder): Topping{
            return Topping(
                _id = toppingOrder._id,
                name = toppingOrder.name,
                price = toppingOrder.price,
                productId = toppingOrder.productId,
                isActive = true
            ).apply {
                _quantity = toppingOrder._quantity
            }
        }
    }

}