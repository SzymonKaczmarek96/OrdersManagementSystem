package unit

import Kotlin.crud.customer.DataBlankException
import Kotlin.crud.product.*
import Kotlin.crud.productstock.ProductStock
import Kotlin.crud.productstock.ProductStockNotFoundException
import Kotlin.crud.productstock.ProductStockQuantityException
import Kotlin.crud.productstock.ProductStockRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import kotlin.collections.ArrayList

@ExtendWith(MockKExtension::class)
class ProductServiceTest {

    @MockK
    lateinit var productRepository: ProductRepository

    @MockK
    lateinit var productStockRepository: ProductStockRepository

    @InjectMockKs
    lateinit var productService: ProductService


    @Test
    fun `should find all products`(){
        every { productRepository.findAll() }.returns(createProductsListForTest())
        val productsDtoFound = productService.getAllProducts();
        assertThat(productsDtoFound).hasSize(3)
        assertEquals(listOf("Product1","Product2","Product3"),productsDtoFound.stream()
            .map{productDto -> productDto.productName}.toList())
    }

    @Test
    fun `should find empty list when repository has not records`(){
        every { productRepository.findAll() }.returns(emptyList())
        val productsDtoFound = productService.getAllProducts();
        assertThat(productsDtoFound).hasSize(0)
        assertEquals(productsDtoFound, listOf<ProductDto>())
    }

    @Test
    fun `should find product by product name`(){
        every { productRepository.findAll() }.returns(createProductsListForTest())
        every { productRepository.findProductByProductName("Product1") }returns(createSingleProduct())
        val productDtoFoundByProductName = productService.getProductByProductName("Product1");
        assertThat(productDtoFoundByProductName).isEqualTo(ProductDto(1L,"Product1", BigDecimal(100)))
    }

    @Test
    fun `should throw ProductNotFoundException when product has not been found`(){
        every { productRepository.findProductByProductName("Product1") }returns(null)
        assertThrows<ProductNotFoundException> {productService.getProductByProductName("Product1")}
    }

    @Test
    fun `should save product`(){
        val productDtoForCreation = createProductDtoWithCorrectData()
        every { productRepository.existsByProductName("Product1") }.returns(false)
        every { productRepository.save(any(Product::class)) }.returns(createSingleProduct())
        val savedProduct = productService.saveProduct(productDtoForCreation)
        assertThat(savedProduct.productId).isEqualTo(1L)
        assertThat(savedProduct.productName).isEqualTo("Product1")
        assertThat(savedProduct.productPrice).isEqualTo(BigDecimal(100))
    }

    @Test
    fun `should throw ProductExistsException when saving an existing product`(){
        val productDtoForCreation = createProductDtoWithCorrectData()
        every { productRepository.existsByProductName("Product1") }.returns(true)
        assertThrows<ProductExistsException> {productService.saveProduct(productDtoForCreation)}
    }

    @Test
    fun `should throw DataBlankException when product name is empty`(){
        val productDtoForCreation = createProductDtoWithoutProductName()
        every { productRepository.existsByProductName(" ") }.returns(false)
        assertThrows<DataBlankException> {productService.saveProduct(productDtoForCreation)}
    }

    @Test
    fun `should throw DataBlankException when price is equals zero`(){
        val productDtoForCreation = createProductDtoWithZeroPrice()
        every { productRepository.existsByProductName("Product1") }.returns(false)
        assertThrows<DataBlankException> {productService.saveProduct(productDtoForCreation)}
    }

    @Test
    fun `should update product information`(){
        val productName = "Product1"
        val productInformation = createProductToUpdateProductInformation()
        every { productRepository.findProductByProductName(productName)}.returns(createSingleProduct())
        every { productRepository.save(any(Product::class)) }.returns(Product(productInformation.productId
            ,productInformation.productName,productInformation.productPrice))
        val productUpdated = productService.updateProductInformation("Product1",productInformation)
        assertThat(productUpdated.productName).isEqualTo("Product2")
        assertThat(productUpdated.productPrice).isEqualTo(BigDecimal(200.00))
    }

    @Test
    fun `should throw ProductNotExistsException when updating non existing product`(){
        every { productRepository.findProductByProductName("Product1") }.returns(null)
        assertThrows<ProductNotFoundException>{productService.updateProductInformation("Product1"
        ,createProductToUpdateProductInformation())}
    }

    @Test
    fun `should just update product name when updating product price is less than zero`(){
        val product = createSingleProduct()
        val productInformation = createProductDtoWithZeroPrice()
        every { productRepository.findProductByProductName("Product1") }.returns(product)
        every { productRepository.save(any(Product::class)) }.returns(Product(product.productId
            ,productInformation.productName,productInformation.productPrice))
        val productUpdated = productService.updateProductInformation("Product1",createProductDtoWithZeroPrice())
        assertThat(productUpdated.productName).isEqualTo("Product1")
        assertThat(productUpdated.productPrice).isEqualTo(BigDecimal(100.00))
    }

    @Test
    fun `should just update product price when updating product name is blank`(){
        val product = createSingleProductWithBiggerPrice()
        val productInformation = createProductDtoWithoutProductName()
        every { productRepository.findProductByProductName("Product1") }.returns(product)
        every { productRepository.save(any(Product::class)) }.returns(Product(product.productId
            ,productInformation.productName,productInformation.productPrice))
        val productUpdated = productService.updateProductInformation("Product1",createProductDtoWithZeroPrice())
        assertThat(productUpdated.productName).isEqualTo("Product1")
        assertThat(productUpdated.productPrice).isEqualTo(BigDecimal(1000))
    }

    @Test
    fun `should delete product`(){
        val product = createSingleProduct()
        val productStock = createProductStockWithZeroQuantity()
        every { productRepository.findByIdOrNull(1L) }.returns(product)
        every { productStockRepository.findProductStockByProductId(1) } returns(productStock)
        every { productStockRepository.delete(productStock) } just Runs
        every { productRepository.delete(product) } just Runs

        productService.deleteProduct(1L)

        verify { productStockRepository.findProductStockByProductId(1L)}
        verify { productRepository.findByIdOrNull(1L) }
        verify { productRepository.delete(product) }
    }

    @Test
    fun `should throw ProductNotFoundException when deleting product is not found`(){
        every { productRepository.findByIdOrNull(1L) }.returns(null)
        assertThrows<ProductNotFoundException>{productService.deleteProduct(1L)}
    }

    @Test
    fun `should throw ProductStockNotFoundException when deleting stock is not found`(){
        val product = createSingleProduct()
        every { productRepository.findByIdOrNull(1L) }.returns(product)
        every { productStockRepository.findProductStockByProductId(1L) }returns (null)
        assertThrows<ProductStockNotFoundException>{productService.deleteProduct(1L)}
    }


    @Test
    fun `should throw ProductStockQuantityException when deleting product has stock greater than zero`(){
        val product = createSingleProduct()
        val productStock = createProductStockWithFiftyQuantity()
        every { productRepository.findByIdOrNull(1L) }.returns(product)
        every { productStockRepository.findProductStockByProductId(1) } returns(productStock)
        assertThrows<ProductStockQuantityException>{productService.deleteProduct(1L)}
    }


    private fun createSingleProduct(): Product {
        return Product(1L,"Product1", BigDecimal(100.00))
    }

    private fun createSingleProductWithBiggerPrice(): Product{
        return Product(1L,"Product1",BigDecimal(1000))
    }

    private fun createProductDtoWithCorrectData(): ProductDto{
        return ProductDto(1L,"Product1", BigDecimal(100.00))
    }

    private fun createProductDtoWithoutProductName(): ProductDto{
        return ProductDto(1L," ",BigDecimal(100))
    }

    private fun createProductDtoWithZeroPrice(): ProductDto {
        return ProductDto(1L,"Product1",BigDecimal(0))
    }

    private fun createProductToUpdateProductInformation():ProductDto{
        return ProductDto(1L, "Product2", BigDecimal(200.00))
    }

    private fun createProductStockWithFiftyQuantity(): ProductStock {
        val productForProductStock = createSingleProduct()
        return ProductStock(1L,productForProductStock,50)
    }
    private fun createProductStockWithZeroQuantity(): ProductStock {
        val productForProductStock = Product(1L,"Product1", BigDecimal(100.00))
        return ProductStock(1L,productForProductStock,0)
    }

    private fun createProductsListForTest():List<Product>{
        val product1 = Product(1L,"Product1", BigDecimal(100.00))
        val product2 = Product(2L,"Product2", BigDecimal(50.00))
        val product3 = Product(3L,"Product3", BigDecimal(30.00))
        val productsList = ArrayList<Product>()
        productsList.add(product1)
        productsList.add(product2)
        productsList.add(product3)
        return productsList;
    }


}