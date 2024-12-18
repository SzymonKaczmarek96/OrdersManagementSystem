package integration

import Kotlin.crud.CrudApplication
import Kotlin.crud.customer.DataBlankException
import Kotlin.crud.product.*
import Kotlin.crud.productstock.ProductStock
import Kotlin.crud.productstock.ProductStockNotFoundException
import Kotlin.crud.productstock.ProductStockQuantityException
import Kotlin.crud.productstock.ProductStockRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal
import kotlin.streams.toList

@ContextConfiguration
@SpringBootTest(classes = [CrudApplication::class])
class ProductServiceIntegrationTest : TestContainer() {

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productController: ProductController

    @Autowired
    lateinit var productStockRepository: ProductStockRepository

    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()
        productStockRepository.deleteAll()
    }

    @Test
    fun `should get product list`() {
        productRepository.saveAll(createProductsListForTest())
        val response = productController.getAllProductsList()
        assertThat(response.body).isNotNull
        assertThat(response.body).size().isEqualTo(2)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should get empty list`() {
        val customerDtoList = productController.getAllProductsList().body;
        assertThat(customerDtoList).isEmpty()
    }

    @Test
    fun `should get product by product name`() {
        productRepository.saveAll(createProductsListForTest())
        val productName = "Product1"
        val response = productController.findProductByProductName(productName);
        assertThat(response.body?.productName).isEqualTo("Product1")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should throw ProductNotFoundException when product not existing in repository`() {
        productRepository.saveAll(createProductsListForTest())
        assertThrows<ProductNotFoundException> { productController.findProductByProductName(" ") }
    }

    @Test
    fun `should create new product`() {
        val productDto = createProductDtoWithCorrectData();
        val response = productController.createProduct(productDto)
        assertThat(response.body?.productName).isEqualTo(productDto.productName)
        assertThat(response.body?.productPrice).isEqualTo(productDto.productPrice)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should throw ProductExistsException when attempting to create a product that already exists in the database`() {
        productRepository.save(createSingleProduct())
        val productDto = createProductDtoWithCorrectData();
        assertThrows<ProductExistsException> { productController.createProduct(productDto) }
    }

    @Test
    fun `should throw DataBlankException when attempting to create product with blank product name`() {
        val productDto = createProductDtoWithoutProductName();
        assertThrows<DataBlankException> { productController.createProduct(productDto) }
    }

    @Test
    fun `should throw DataBlankException when attempting to create product with zero price`() {
        val productDto = createProductDtoWithZeroPrice()
        assertThrows<DataBlankException> { productController.createProduct(productDto) }
    }


    @Test
    fun `should update product information`() {
        productRepository.save(createSingleProduct())
        val productName = "Product1"
        val productDto = createProductToUpdateProductInformation()
        val response = productController.updateProductInformation(productName, productDto)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.productName).isEqualTo(productDto.productName)
        assertThat(response.body?.productPrice).isEqualTo(productDto.productPrice)
    }

    @Test
    fun `should throw ProductNotFoundException when attempting to update for not existing product`() {
        val productName = "Product1"
        val productDto = createProductToUpdateProductInformation()
        assertThrows<ProductNotFoundException> { productController.updateProductInformation(productName, productDto) }
    }

    @Test
    fun `should not update product information when attempting to update product with blank product name`() {
        productRepository.save(createSingleProduct())
        val productName = "Product1"
        val productDto = createProductDtoWithoutProductName()
        val response = productController.updateProductInformation(productName, productDto)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.productName).isEqualTo("Product1")
        assertThat(response.body?.productPrice).isEqualTo(createSingleProduct().toProductDto().productPrice)
    }

    @Test
    fun `should not update product information when attempting to update product with zero price`() {
        val product = createSingleProduct();
        productRepository.save(product)
        val productName = "Product1"
        val productDto = createProductDtoWithZeroPrice()
        val response = productController.updateProductInformation(productName, productDto)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.productName).isEqualTo("Product1")
        assertThat(response.body?.productPrice.toString()).isEqualTo("100.00")
    }


    @Test
    fun `should delete product`() {
        productRepository.saveAll(createProductsListForTest())
        val productId = productRepository.findAll().firstOrNull()?.productId
        productStockRepository.save(
            ProductStock(
                6L, Product(
                    productId, "Product8", BigDecimal(50)
                ), 0
            )
        )
        val response = productController.deleteProduct(productId!!)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    fun `should throw ProductNotFoundExceptions when attempting delete product has not found`() {
        assertThrows<ProductNotFoundException> { productController.deleteProduct(1L) }
    }

    @Test
    fun `should throw ProductStockNotFoundException when deleting stock is not found`() {
        productRepository.saveAll(createProductsListForTest())
        val productId = productRepository.findAll().firstOrNull()?.productId
        assertThrows<ProductStockNotFoundException> { productController.deleteProduct(productId!!) }
    }

    @Test
    @Transactional
    fun `should throw ProductStockQuantityException when deleting product has stock greater than zero`() {
        val product = Product("Product10", BigDecimal(100.00))
        productRepository.save(product)
        val productId = product.productId
        productStockRepository.save(ProductStock(10L, product, 50))
        assertNotNull(productId)
        assertThrows<ProductStockQuantityException> {
            productController.deleteProduct(productId!!)
        }
    }


    private fun createSingleProduct(): Product {
        return Product(1L, "Product1", BigDecimal(100.00))
    }

    private fun createSingleProductWithBiggerPrice(): Product {
        return Product(1L, "Product1", BigDecimal(1000))
    }

    private fun createProductDtoWithCorrectData(): ProductDto {
        return ProductDto(1L, "Product1", BigDecimal(100.00))
    }

    private fun createProductDtoWithoutProductName(): ProductDto {
        return ProductDto(1L, " ", BigDecimal(100))
    }

    private fun createProductDtoWithZeroPrice(): ProductDto {
        return ProductDto(1L, "Product1", BigDecimal(0))
    }

    private fun createProductToUpdateProductInformation(): ProductDto {
        return ProductDto(1L, "Product2", BigDecimal(200.00))
    }

    private fun createProductStockWithFiftyQuantity(): ProductStock {
        val productForProductStock = Product("Product1", BigDecimal(50))
        return ProductStock(1L, productForProductStock, 50)
    }

    private fun createProductStockWithZeroQuantity(): ProductStock {
        val productForProductStock = Product(40L, "Product1", BigDecimal(100.00))
        return ProductStock(1L, productForProductStock, 0)
    }

    private fun createProductsListForTest(): List<Product> {
        val product1 = Product(1L, "Product1", BigDecimal(100.00))
        val product2 = Product(2L, "Product2", BigDecimal(50.00))
        val product3 = Product(3L, "Product3", BigDecimal(30.00))
        val productsList = ArrayList<Product>()
        productsList.add(product1)
        productsList.add(product2)
        productsList.add(product3)
        return productsList;
    }
}