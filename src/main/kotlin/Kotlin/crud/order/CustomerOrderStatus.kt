package Kotlin.crud.order

import Kotlin.crud.customer.Customer
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class CustomerOrderStatus(
    val orderId: Long,
    val orderItems: OrderItems,
    val orderCreationTime: LocalDateTime,
    val orderTotalPrice: BigDecimal,
    val customer: Customer
) {

    class NewCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        orderCreationTime: LocalDateTime = LocalDateTime.now(),
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) : CustomerOrderStatus(orderId, orderItems, orderCreationTime, orderTotalPrice, customer) {
        fun pay() = PaidCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
        fun cancel() = CancelledCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer, this)
    }

    class PaidCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        orderCreationTime: LocalDateTime,
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) :
        CustomerOrderStatus(orderId, orderItems, orderCreationTime, orderTotalPrice, customer) {
        fun process() = ProcessCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
        fun cancel() = CancelledCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer, this)
    }

    class ProcessCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        orderCreationTime: LocalDateTime,
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) :
        CustomerOrderStatus(orderId, orderItems, orderCreationTime, orderTotalPrice, customer) {
        fun ship() = ShippedCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
    }

    class ShippedCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        orderCreationTime: LocalDateTime,
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) :
        CustomerOrderStatus(orderId, orderItems, orderCreationTime, orderTotalPrice, customer) {
        fun deliver() = DeliveredCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
    }

    class DeliveredCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        orderCreationTime: LocalDateTime,
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) :
        CustomerOrderStatus(orderId, orderItems, orderCreationTime, orderTotalPrice, customer) {
        fun returnOrder() = ReturnedCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
    }


    class ReturnedCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        orderCreationTime: LocalDateTime,
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) :
        CustomerOrderStatus(orderId, orderItems, orderCreationTime, orderTotalPrice, customer) {
        fun refund() = RefundedCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
    }

    class CancelledCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        cancelledOrderTime: LocalDateTime = LocalDateTime.now(),
        orderTotalPrice: BigDecimal,
        customer: Customer,
        private val previousStatus: CustomerOrderStatus
    ) :
        CustomerOrderStatus(orderId, orderItems, cancelledOrderTime, orderTotalPrice, customer) {
        fun refundIfStatusWasPaid(): CustomerOrderStatus {
            return when (previousStatus) {
                is PaidCustomerOrder -> {
                    RefundedCustomerOrder(orderId, orderItems, orderCreationTime, orderTotalPrice, customer)
                }

                else -> {
                    return this
                }
            }
        }
    }

    class RefundedCustomerOrder(
        orderId: Long,
        orderItems: OrderItems,
        cancelledOrderTime: LocalDateTime = LocalDateTime.now(),
        orderTotalPrice: BigDecimal,
        customer: Customer
    ) :
        CustomerOrderStatus(orderId, orderItems, cancelledOrderTime, orderTotalPrice, customer) {

    }

    fun toEntity() = CustomerOrder(
        orderId = this.orderId,
        orderItems = this.orderItems,
        orderCreationTime = this.orderCreationTime,
        orderTotalPrice = this.orderTotalPrice,
        customer = this.customer,
        customerOrderStatus = when (this) {
            is NewCustomerOrder -> "NEW"
            is PaidCustomerOrder -> "PAID"
            is ProcessCustomerOrder -> "PROCESS"
            is ShippedCustomerOrder -> "SHIPPED"
            is DeliveredCustomerOrder -> "DELIVERED"
            is CancelledCustomerOrder -> "CANCELLED"
            is ReturnedCustomerOrder -> "RETURNED"
            is RefundedCustomerOrder -> "REFUNDED"
        }
    )

}



