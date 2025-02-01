package Kotlin.crud.product

import Kotlin.crud.customer.Customer
import jakarta.persistence.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import updateIf
import java.math.BigDecimal
@Entity
@Table(name = "product")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val productId: Long? = null,

    @Column(name = "product_name", nullable = false, unique = true)
    private var productName:String,

    @Column(name = "product_price", nullable = false)
    private var productPrice: BigDecimal,

) {

    constructor(productName: String,productPrice: BigDecimal):this(
        null,
        productName = productName,
        productPrice = productPrice,
        )
    fun toProductDto(): ProductDto{
        return ProductDto(productId,productName,productPrice)
    }

    fun updateWith(productDto: ProductDto){
        updateIf(productDto.productName.isNotBlank()){this.productName = productDto.productName}
        updateIf(productDto.productPrice > BigDecimal(0)){this.productPrice = productDto.productPrice}
    }

    override fun toString(): String {
        return "Product(productId=$productId, productName='$productName', productPrice=$productPrice)"
    }


}


data class ProductDto(val productId: Long?,val productName: String,val productPrice: BigDecimal){

}

@ResponseStatus(HttpStatus.NOT_FOUND)
class ProductNotFoundException(message:String = "Product has not been found"): RuntimeException(message){

}
@ResponseStatus(HttpStatus.CONFLICT)
class ProductExistsException(message: String = "Product exists in data base"):RuntimeException(message)



