package Kotlin.crud.order

import Kotlin.crud.customer.Customer
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "customer_basket")
class CustomerBasket (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_id")
    val basketId: Long? = null,

    @Column(name ="items_in_basket")
    @JdbcTypeCode(SqlTypes.JSON)
    val itemsInBasket: OrderItems,

    @Column(name = "creation_time", nullable = false)
    val basketCreationTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_time", nullable = false)
    val updatedTime: LocalDateTime? = null,

    @Column(name = "total_price")
    val basketTotalPrice: BigDecimal,

    @JoinColumn(name = "customer_id")
    @OneToOne
    val customer: Customer
) {
}

data class CustomerBasketDto(val basketId: Long?,val itemsInBasket: OrderItems,val basketCreationTime: LocalDateTime,
val updatedTime: LocalDateTime?,val basketTotalPrice: BigDecimal,val customer: Customer ){

}

fun CustomerBasketDto.toCustomerBasketDto(): CustomerBasketDto{
    return CustomerBasketDto(basketId,itemsInBasket,basketCreationTime,updatedTime,basketTotalPrice,customer)
}

