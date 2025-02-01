package Kotlin.crud.product

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/product")
@RestController
class ProductController(val productService: ProductService) {

    @GetMapping
    fun getAllProductsList():ResponseEntity<List<ProductDto>>{
        return ResponseEntity.ok(productService.getAllProducts())
    }

    @GetMapping("/{productName}")
    fun findProductByProductName(@PathVariable productName:String): ResponseEntity<ProductDto>{
        return ResponseEntity.ok(productService.getProductByProductName(productName))
    }

    @PostMapping("/create")
    fun createProduct(@RequestBody productDto: ProductDto):ResponseEntity<ProductDto>{
        return ResponseEntity.ok().body(productService.saveProduct(productDto))
    }

    @PutMapping("/update/{productName}")
    fun updateProductInformation(@PathVariable productName: String,@RequestBody productDto: ProductDto):ResponseEntity<ProductDto>{
        return ResponseEntity.ok().body(productService.updateProductInformation(productName,productDto))
    }

    @DeleteMapping("/delete")
    fun deleteProduct(@RequestParam(name = "id") productId:Long):ResponseEntity<Void>{
        productService.deleteProduct(productId)
        return ResponseEntity.noContent().build()
    }
}