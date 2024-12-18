package unit

import Kotlin.crud.product.*
import Kotlin.crud.productstock.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class ProductStockServiceTest {

    @MockK
    lateinit var productStockRepository: ProductStockRepository

    @MockK
    lateinit var productRepository: ProductRepository

    @InjectMockKs
    lateinit var productStockService: ProductStockService


    @Test
    fun `should get product stock list`() {
        every { productStockRepository.findAll() }.returns(createProductsStockListForTest())
        val productStockDtoList = productStockService.getProductStockList();
        assertThat(productStockDtoList.size).isEqualTo(3)
        assertThat(
            listOf(
                50,
                100,
                150
            )
        ).isEqualTo(createProductsStockListForTest().map { productStock -> productStock.quantity })
    }

    @Test
    fun `should get empty list`() {
        every { productStockRepository.findAll() }.returns(emptyList())
        val productStockDtoList = productStockService.getProductStockList();
        assertThat(productStockDtoList).isEqualTo(emptyList<ProductStockDto>())
    }

    @Test
    fun `should find product stock by product id`() {
        every { productStockRepository.findProductStockByProductId(1L) }.returns(createProductStockWithCorrectData())
        val productStockDto = productStockService.findProductStockByProductId(1L)
        assertThat(productStockDto.productStockId)
        assertThat(productStockDto.product.toProductDto().productName).isEqualTo("Product1")
        assertThat(productStockDto.quantity).isEqualTo(50)
    }

    @Test
    fun `should throw ProductStockNotFoundException when attempting find product stock not exists`() {
        every { productStockRepository.findProductStockByProductId(1L) }.returns(null)
        assertThrows<ProductStockNotFoundException> { productStockService.findProductStockByProductId(1L) }
    }

    @Test
    fun `should create product stock`() {
        val productStockDtoForTest = createProductStockWithCorrectData().toProductStockDto()
        every { productRepository.findProductByProductName(productStockDtoForTest.product.toProductDto().productName) }
            .returns(createProductForTest())
        every { productStockRepository.existsProductStockByProduct(1L) }.returns(false)
        every { productStockRepository.save(any(ProductStock::class)) }.returns(createProductStockWithCorrectData())
        val createdProductStock = productStockService.createProductStock(productStockDtoForTest);
        assertThat(createdProductStock.product.toProductDto().productName).isEqualTo("Product1")
        assertThat(createdProductStock.quantity).isEqualTo(50)
    }

    @Test
    fun `should throw ProductNotFoundException when attempting create product has not exists`() {
        every { productRepository.findProductByProductName("Product1") }
            .returns(null)
        assertThrows<ProductNotFoundException> {
            productStockService
                .createProductStock(createProductStockWithCorrectData().toProductStockDto())
        }
    }

    @Test
    fun `should throw ProductStockExistsException when attempting create product stock has exists`() {
        every { productRepository.findProductByProductName("Product1") }
            .returns(createProductForTest())
        every { productStockRepository.existsProductStockByProduct(1L) }.returns(true)
        assertThrows<ProductStockExistsException> {
            productStockService
                .createProductStock(createProductStockWithCorrectData().toProductStockDto())
        }
    }


    private fun createProductStockWithCorrectData(): ProductStock {
        val product1 = Product(1L, "Product1", BigDecimal(100.00))
        return ProductStock(1L, product1, 50);
    }

    private fun createProductForTest(): Product {
        return Product(1L, "Product1", BigDecimal(100.00))
    }


    private fun createProductsStockListForTest(): List<ProductStock> {
        val product1 = Product(1L, "Product1", BigDecimal(100.00))
        val product2 = Product(2L, "Product2", BigDecimal(50.00))
        val product3 = Product(3L, "Product3", BigDecimal(30.00))
        val productStock1 = ProductStock(1L, product1, 50)
        val productStock2 = ProductStock(2L, product2, 100)
        val productStock3 = ProductStock(3L, product3, 150)
        val productsStockList = ArrayList<ProductStock>()
        productsStockList.add(productStock1)
        productsStockList.add(productStock2)
        productsStockList.add(productStock3)
        return productsStockList;
    }

}