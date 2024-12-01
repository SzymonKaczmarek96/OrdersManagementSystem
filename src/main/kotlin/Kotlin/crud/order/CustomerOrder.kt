package Kotlin.crud.order

import Kotlin.crud.customer.Customer
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class CustomerOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="order_id")
    val orderId: Long? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name ="order_items")
    val orderItems: OrderItems,

    @Enumerated(EnumType.STRING)
    @Column(name ="order_status")
    val customerOrderStatus: CustomerOrderStatus = CustomerOrderStatus.NEW,

    @Column(name = "creation_time")
    val orderCreationTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "total_price")
    val orderTotalPrice: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: Customer
) {}