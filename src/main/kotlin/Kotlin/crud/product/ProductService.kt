package Kotlin.crud.product


import Kotlin.crud.customer.DataBlankException
import Kotlin.crud.productstock.ProductStockNotFoundException
import Kotlin.crud.productstock.ProductStockQuantityException
import Kotlin.crud.productstock.ProductStockRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProductService (val productRepository: ProductRepository, val productStockRepository: ProductStockRepository) {

    fun getAllProducts(): List<ProductDto> {
        return productRepository.findAll().map { product -> product.toProductDto() }.toList()
    }

    fun getProductByProductName(productName: String): ProductDto {
        val product = productRepository.findProductByProductName(productName) ?: throw ProductNotFoundException()
        return product.toProductDto()
    }

    @Transactional
    fun saveProduct(productDto: ProductDto): ProductDto {
        if (productRepository.existsByProductName(productDto.productName)) {
            throw ProductExistsException()
        }
        val product = productRepository.save(createProduct(productDto))
        return product.toProductDto()
    }
    @Transactional
    fun updateProductInformation(productName: String,productDto: ProductDto): ProductDto {
        val product = productRepository.findProductByProductName(productName) ?:throw ProductNotFoundException()
        product.updateWith(productDto)
        productRepository.save(product)
        return product.toProductDto()
    }

    fun deleteProduct(productId: Long){
        val product = productRepository.findByIdOrNull(productId) ?:throw ProductNotFoundException()
        validateProductStock(productId)
        productRepository.delete(product)
    }

    private fun createProduct(productDto: ProductDto): Product{
        validateResponseData(productDto)
        return Product(
            productName = productDto.productName,
            productPrice = productDto.productPrice)
    }

    private fun validateResponseData(productDto: ProductDto) {
        require(
            productDto.productName.isNotBlank()
                    && productDto.productPrice > BigDecimal(0)
        ) {
            throw DataBlankException();
        }
    }

    private fun validateProductStock(productId: Long){
        val productStock = productStockRepository.findProductStockByProductId(productId)
            ?: throw ProductStockNotFoundException()
        if(productStock.quantity > 0){
            throw ProductStockQuantityException()
        }
        productStockRepository.delete(productStock);
    }
}
