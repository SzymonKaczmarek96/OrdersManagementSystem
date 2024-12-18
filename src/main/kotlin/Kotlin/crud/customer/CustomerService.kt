package Kotlin.crud.customer

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import javax.xml.crypto.Data

@Service
class CustomerService(private val customerRepository: CustomerRepository) {


    fun getAllCustomers(): List<CustomerDto> {
        return customerRepository.findAll().map { customer -> customer.toCustomerDto() }.toList()
    }

    fun findCustomerByEmail(customerEmail: String): CustomerDto {
        val customer = customerRepository.findCustomerByEmail(customerEmail) ?: throw CustomerNotFoundException()
        return customer.toCustomerDto()
    }

    @Transactional
    fun saveCustomer(customerDto: CustomerDto): CustomerDto {
        customerRepository.existsByEmail(customerDto.email).takeIf { it }?.let { throw EmailExistsException() }
        val customer = customerRepository.save(createCustomer(customerDto))
        return customer.toCustomerDto()
    }

    @Transactional
    fun updateCustomerInformation(customerDto: CustomerDto): CustomerDto {
        val customer = customerRepository.findByIdOrNull(customerDto.id) ?: throw CustomerNotFoundException()
        customer.updateWith(customerDto)
        customerRepository.save(customer)
        return customer.toCustomerDto()
    }


    fun deleteCustomer(id: Long) {
        val customer = customerRepository.findByIdOrNull(id) ?: throw CustomerNotFoundException()
        customerRepository.delete(customer)
    }


    private fun createCustomer(customerDto: CustomerDto): Customer {
        validateResponseData(customerDto)
        return Customer(
            firstName = customerDto.firstName,
            lastName = customerDto.lastName,
            email = customerDto.email,
            address = customerDto.address
        )
    }


    private fun validateResponseData(customerDto: CustomerDto) {
        require(
            customerDto.firstName.isNotBlank()
                    && customerDto.lastName.isNotBlank()
                    && customerDto.email.isNotBlank()
                    && customerDto.lastName.isNotBlank()
        ) {
            throw DataBlankException();
        }
    }


}