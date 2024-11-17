package Kotlin.crud.customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomerService (@Autowired val customerRepository:CustomerRepository) {

    fun getAllCustomers(): List<CustomerDto> {
        return customerRepository.findAll().map{customer -> customer.toCustomerDto()}.toList();
    }

}