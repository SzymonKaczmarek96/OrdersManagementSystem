package Kotlin.crud.customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/api/customer")
@RestController
class CustomerController () {

    @Autowired
    lateinit var customerService:CustomerService

    @GetMapping()
    fun getAllCustomer():ResponseEntity<List<CustomerDto>>{
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


}