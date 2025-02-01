package Kotlin.crud.productstock

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/product_stock")
class ProductStockController(val productStockService: ProductStockService) {

    @GetMapping
    fun getProductStockList(): ResponseEntity<List<ProductStockDto>> {
        return ResponseEntity.ok(productStockService.getProductStockList())
    }

    @GetMapping("/{productId}")
    fun findProductStock(@PathVariable productId: Long): ResponseEntity<ProductStockDto> {
        return ResponseEntity.ok(productStockService.findProductStockByProductId(productId))
    }

    @PostMapping("/create")
    fun createProductStock(@RequestBody productStockDto: ProductStockDto): ResponseEntity<ProductStockDto> {
        return ResponseEntity.ok().body(productStockService.createProductStock(productStockDto))
    }
}