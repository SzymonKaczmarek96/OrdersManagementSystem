package Kotlin.crud.order

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.math.BigDecimal

class OrderItems(
    val itemList: Set<OrderItem>
)  : Serializable

class OrderItem(
    @JsonProperty("product_id")
    val productId:Long,

    @JsonProperty("product_name")
    val productName:String,

    @JsonProperty("price")
    val price: BigDecimal,

    @JsonProperty("quantity")
    val quantity: Int,
): Serializable{}