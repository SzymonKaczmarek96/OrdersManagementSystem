package Kotlin.crud.productstock

import Kotlin.crud.product.ProductNotFoundException
import Kotlin.crud.product.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ProductStockService(
    val productStockRepository: ProductStockRepository,
    val productRepository: ProductRepository
) {

    fun getProductStockList(): List<ProductStockDto> {
        return productStockRepository.findAll().map { productStock -> productStock.toProductStockDto() }.toList()
    }

    fun findProductStockByProductId(productId: Long): ProductStockDto {
        return productStockRepository.findProductStockByProductId(productId)?.toProductStockDto()
            ?: throw ProductStockNotFoundException()
    }

    @Transactional
    fun createProductStock(productStockDto: ProductStockDto): ProductStockDto {
        val product = productRepository.findProductByProductName(productStockDto.product.toProductDto().productName)
            ?: throw ProductNotFoundException()
        if (productStockRepository.existsProductStockByProduct(productStockDto.product.productId!!)) {
            throw ProductStockExistsException()
        }
        val productStock = ProductStock(product, productStockDto.quantity)
        productStockRepository.save(productStock)
        return productStock.toProductStockDto()
    }

}