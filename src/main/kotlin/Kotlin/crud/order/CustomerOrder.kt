package Kotlin.crud.order

import Kotlin.crud.customer.Customer
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "customer_order")
class CustomerOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    val orderId: Long? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "order_items")
    val orderItems: OrderItems,

    @Column(name = "order_status")
    val customerOrderStatus: String? = null,

    @Column(name = "creation_time")
    val orderCreationTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "total_price")
    val orderTotalPrice: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: Customer
) {
    fun toDomain(): CustomerOrderStatus {
        return when (customerOrderStatus) {
            "NEW" -> CustomerOrderStatus.NewCustomerOrder(orderId!!, orderItems, orderCreationTime, orderTotalPrice, customer)
            "PAID" -> CustomerOrderStatus.PaidCustomerOrder(orderId!!, orderItems, orderCreationTime, orderTotalPrice, customer)
            "PROCESS" -> CustomerOrderStatus.ProcessCustomerOrder(
                orderId!!,
                orderItems,
                orderCreationTime,
                orderTotalPrice,
                customer
            )

            "SHIPPED" -> CustomerOrderStatus.ShippedCustomerOrder(
                orderId!!,
                orderItems,
                orderCreationTime,
                orderTotalPrice,
                customer
            )

            "DELIVERED" -> CustomerOrderStatus.DeliveredCustomerOrder(
                orderId!!,
                orderItems,
                orderCreationTime,
                orderTotalPrice,
                customer
            )

            "CANCELLED" -> CustomerOrderStatus.CancelledCustomerOrder(
                orderId!!,
                orderItems,
                orderCreationTime,
                orderTotalPrice,
                customer,
                CustomerOrderStatus.NewCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
            )

            "REFUNDED" -> CustomerOrderStatus.RefundedCustomerOrder(
                orderId!!,
                orderItems,
                orderCreationTime,
                orderTotalPrice,
                customer
            )

            "RETURNED" -> CustomerOrderStatus.ReturnedCustomerOrder(
                orderId!!,
                orderItems,
                orderCreationTime,
                orderTotalPrice,
                customer
            )
            else -> throw IllegalArgumentException("Unidentified status")
        }

    }
}

