package Kotlin.crud.customer

import org.springframework.stereotype.Service

@Service
class CustomerService (private val customerRepository:CustomerRepository) {


    fun getAllCustomers(): List<CustomerDto> {
        return customerRepository.findAll().map{customer -> customer.toCustomerDto()}.toList();
    }

    fun saveCustomer(customerDto:CustomerDto): CustomerDto {
        customerRepository.existsByEmail(customerDto.email).takeIf { it }?.let {
            throw EmailExistsException();
        }
        val customer = customerRepository.save(createCustomer(customerDto))
       return customer.toCustomerDto();
    }

    fun findCustomerByEmail(customerEmail: String): CustomerDto {
        val customer = customerRepository.findCustomerByEmail(customerEmail)?: throw CustomerNotFoundException();
        return customer.toCustomerDto();
    }


   private fun createCustomer(customerDto: CustomerDto):Customer{
       validateResponseData(customerDto)
        return Customer(
            firstName = customerDto.firstName,
            lastName = customerDto.lastName,
            email = customerDto.email,
            address = customerDto.address
        )
   }

    private fun validateResponseData(customerDto: CustomerDto){
        require(customerDto.firstName.isNotBlank()
                && customerDto.lastName.isNotBlank()
                && customerDto.email.isNotBlank()
                && customerDto.lastName.isNotBlank()) {
            "Customer data cannot contain empty fields"
        }
    }


}