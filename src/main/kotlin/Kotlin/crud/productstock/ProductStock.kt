package Kotlin.crud.productstock

import Kotlin.crud.product.Product
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@Entity
@Table(name = "ProductStock")
class ProductStock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id", nullable = false)
    val productStockId: Long? = 0,
    @JoinColumn(name = "product_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    val product: Product,

    @Column(name = "quantity", nullable = false)
    val quantity: Int
) {
    constructor(product: Product, quantity: Int) : this(
        null,
        product = product,
        quantity = quantity
    )

    override fun toString(): String {
        return "ProductStock(productStockId=$productStockId, product=$product, quantity=$quantity)"
    }


}

data class ProductStockDto(val productStockId: Long?, val product: Product, val quantity: Int) {
}

fun ProductStock.toProductStockDto(): ProductStockDto {
    return ProductStockDto(productStockId, product, quantity)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class ProductStockNotFoundException(message: String = "Product stock has not been found") : RuntimeException(message) {

}

@ResponseStatus(HttpStatus.CONFLICT)
class ProductStockQuantityException(message: String = "Product stock have to be equals 0") : RuntimeException(message) {
}

@ResponseStatus(HttpStatus.CONFLICT)
class ProductStockExistsException(message: String = "Product stock already exists") : RuntimeException(message)