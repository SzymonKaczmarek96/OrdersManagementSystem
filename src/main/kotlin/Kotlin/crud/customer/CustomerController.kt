package Kotlin.crud.customer

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RequestMapping("/api/customer")
@RestController
class CustomerController(private val customerService: CustomerService) {

    @GetMapping()
    fun getAllCustomers(): ResponseEntity<List<CustomerDto>> {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{email}")
    fun findCustomerByCustomerEmail(@PathVariable email: String): ResponseEntity<CustomerDto> {
        return ResponseEntity.ok(customerService.findCustomerByEmail(email));
    }

    @PostMapping("/create")
    fun createNewCustomer(@RequestBody customerDto: CustomerDto): ResponseEntity<CustomerDto> {
        return ResponseEntity.ok().body(customerService.saveCustomer(customerDto));
    }

    @PutMapping("/update")
    fun updateCustomerInformation(@RequestBody customerDto: CustomerDto)
            : ResponseEntity<CustomerDto> {
        return ResponseEntity.ok().body(customerService.updateCustomerInformation(customerDto));
    }

    @DeleteMapping("/delete")
    fun deleteCustomer(@RequestParam("id") id: Long): ResponseEntity<Void> {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build()
    }


}