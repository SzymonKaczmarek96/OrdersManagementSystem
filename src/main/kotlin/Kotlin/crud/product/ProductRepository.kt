package Kotlin.crud.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: JpaRepository<Product, Long> {

    fun existsByProductName(productName: String): Boolean

    fun findProductByProductName(productName: String): Product?
}