package Kotlin.crud.product

import jakarta.persistence.*
import java.math.BigDecimal
@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val productId: Long,

    @Column(name = "product_name", nullable = false)
    val productName:String,

    @Column(name = "price", nullable = false)
    val productPrice: BigDecimal,
) {}