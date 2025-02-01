package integration

import Kotlin.crud.CrudApplication
import Kotlin.crud.product.Product
import Kotlin.crud.product.ProductNotFoundException
import Kotlin.crud.product.ProductRepository
import Kotlin.crud.productstock.*
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal

@ContextConfiguration
@SpringBootTest(classes = [CrudApplication::class])
class ProductStockServiceIntegrationTest : TestContainer() {

    @Autowired
    lateinit var productStockRepository: ProductStockRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productStockController: ProductStockController

    @BeforeEach
    fun setUp() {
        productStockRepository.deleteAll()
    }

    @Test
    fun `should get product stock list`() {
        productStockRepository.saveAll(createProductsStockListForTest())
        val response = productStockController.getProductStockList();
        assertThat(response.body).isNotNull
        assertThat(response.body).size().isEqualTo(3)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should get empty list`() {
        val customerDtoList = productStockController.getProductStockList();
        assertThat(customerDtoList.body).isEqualTo(emptyList<ProductStockDto>())
    }

    @Test
    fun `should find product stock by product id`() {
        val product = productRepository.save(createSingleProductToFindProductStockByProductId())
        val productStock = ProductStock(product, 150)
        productStockRepository.save(productStock)
        val response = productStockController.findProductStock(product.productId!!);
        assertThat(response.body).isNotNull
        assertThat(response.body?.quantity).isEqualTo(150)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should throw ProductStockNotFoundException when product stock does not exist`() {
        productRepository.save(createSingleProductForTest())
        val productId = productRepository.findAll().firstOrNull()?.productId
        assertThrows<ProductStockNotFoundException> { productStockController.findProductStock(productId!!) }
    }

    @Test
    fun `should create product stock`() {
        val product = productRepository.save(createSingleProductToCreateProductStock())
        val productStockDto = ProductStock(product, 200).toProductStockDto()
        val response = productStockController.createProductStock(productStockDto);
        assertThat(response.body?.quantity).isEqualTo(200)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `createProductStock should throw ProductNotFoundException when product not exists`() {
        productRepository.deleteAll()
        val product = Product(2L, "Product1", BigDecimal(40))
        val productStockDto = ProductStock(product, 200).toProductStockDto()
        assertThrows<ProductNotFoundException> { productStockController.createProductStock(productStockDto) }
    }

    @Test
    fun `createProductStock should throw ProductStockExistsException when product stock exists`() {
        val product = productRepository.save(createSingleProductToCreateProductStock())
        productStockRepository.save(ProductStock(product, 200))
        val productStockDto = ProductStock(product, 200).toProductStockDto()
        assertThrows<ProductStockExistsException> { productStockController.createProductStock(productStockDto) }
    }


    private fun createProductsStockListForTest(): List<ProductStock> {
        val product1 = Product("Product1", BigDecimal(100.00))
        val product2 = Product("Product2", BigDecimal(50.00))
        val product3 = Product("Product3", BigDecimal(30.00))
        productRepository.saveAll(listOf(product1, product2, product3))
        val productStock1 = ProductStock(product1, 50)
        val productStock2 = ProductStock(product2, 100)
        val productStock3 = ProductStock(product3, 150)
        return listOf(productStock1, productStock2, productStock3);
    }

    private fun createSingleProductToFindProductStockByProductId(): Product {
        return Product("Product4", BigDecimal(100.00))
    }

    private fun createSingleProductToCreateProductStock(): Product {
        return Product("Product5", BigDecimal(100.00))
    }

    private fun createSingleProductForTest(): Product {
        return Product("Product6", BigDecimal(100.00))
    }

    private fun createSingleProductStockDtoForTest(): ProductStockDto {
        return ProductStock(createSingleProductForTest(), 200).toProductStockDto()
    }
}