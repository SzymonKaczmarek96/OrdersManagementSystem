package Kotlin.crud.order

import Kotlin.crud.customer.Customer
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class CustomerBasket (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_id")
    val basketId: Long? = null,

    @Column(name ="items_in_basket")
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

