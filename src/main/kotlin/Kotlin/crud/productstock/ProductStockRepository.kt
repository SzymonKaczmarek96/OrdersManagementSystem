package Kotlin.crud.productstock

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductStockRepository : JpaRepository<ProductStock, Long> {

    @Query("SELECT * FROM PRODUCT_STOCK WHERE product_id = :productId ", nativeQuery = true)
    fun findProductStockByProductId(@Param("productId") productId: Long): ProductStock?

    @Query("SELECT EXISTS (SELECT 1 FROM PRODUCT_STOCK WHERE product_id = :productId)", nativeQuery = true)
    fun existsProductStockByProduct(productId: Long): Boolean
}