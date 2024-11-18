package Kotlin.crud.customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/api/customer")
@RestController
class  CustomerController (private val customerService:CustomerService) {


    @GetMapping()
    fun getAllCustomers():ResponseEntity<List<CustomerDto>>{
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{email}")
    fun findCustomerByCustomerEmail(@PathVariable email:String):ResponseEntity<CustomerDto>{
        return ResponseEntity.ok(customerService.findCustomerByEmail(email));
    }

    @PostMapping("/create")
    fun createNewCustomer(@RequestBody customerDto: CustomerDto):ResponseEntity<CustomerDto>{
        return ResponseEntity.ok().body(customerService.saveCustomer(customerDto));
    }


}