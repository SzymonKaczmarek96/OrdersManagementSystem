import Kotlin.crud.customer.*
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
import java.util.*
import kotlin.collections.ArrayList

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerService: CustomerService

    @Test
    fun `should get all customers`() {
        //given
        every { customerRepository.findAll() } returns (ArrayList<Customer>(saveCustomersWithDifferentDescriptionForTest()))
        //when
        val customerList = customerService.getAllCustomers();
        //then
        assertThat(customerList).hasSize(4)
        assertEquals(
            listOf("Szymon", "Anna", "Jan", "Ewa"), saveCustomersWithDifferentDescriptionForTest()
                .map { it.toCustomerDto().firstName })
    }

    @Test
    fun `should get empty list`() {
        //given
        every { customerRepository.findAll() } returns (listOf())
        //when
        val customerList = customerService.getAllCustomers();
        //
        assertThat(customerList).isEmpty();
    }

    @Test
    fun `should save customer`() {
        //given
        every { customerRepository.existsByEmail("szymonpawlak@o2.pl") } returns false
        every { customerRepository.save(any<Customer>()) }.returns(saveCustomerWithCorrectData())
        //when
        val customer = customerService.saveCustomer(saveCustomerWithCorrectData().toCustomerDto());
        //then
        assertEquals("Szymon", customer.firstName)
        assertEquals("Pawlak", customer.lastName)
        assertEquals("szymonpawlak@o2.pl", customer.email)
        assertEquals("123 Main St, Warsaw, Poland", customer.address)
    }

    @Test
    fun `should throw EmailExistsException when email exists`() {
        every { customerRepository.existsByEmail("szymonpawlak@o2.pl") } returns true
        assertThrows<EmailExistsException> {
            val customer = customerService.saveCustomer(saveCustomerWithCorrectData().toCustomerDto());
        }
    }

    @Test
    fun `should throw DataBlankException when any data is blank`() {
        //given
        every { customerRepository.existsByEmail("szymonpawlak96@o2.pl") } returns false
        every { customerRepository.save(any<Customer>()) } returns saveCustomerWithBlankData()
        //when //then
        assertThrows<DataBlankException> { customerService.saveCustomer(saveCustomerWithBlankData().toCustomerDto()) }
    }

    @Test
    fun `should find customer by email`() {
        //given
        every { customerRepository.findAll() }.returns(saveCustomersWithDifferentDescriptionForTest())
        every { customerRepository.findCustomerByEmail("szymonpawlak@o2.pl") } returns (saveCustomerWithCorrectData());
        //when then
        val foundCustomer = customerService.findCustomerByEmail("szymonpawlak@o2.pl");
        //
        assertEquals("Szymon", foundCustomer.firstName)
        assertEquals("Pawlak", foundCustomer.lastName)
        assertEquals("szymonpawlak@o2.pl", foundCustomer.email)
        assertEquals("123 Main St, Warsaw, Poland", foundCustomer.address)
    }


    @Test
    fun `should throw EmailNotExistsException when customer with introduced email does not exist`() {
        every { customerRepository.findAll() }.returns(saveCustomersWithDifferentDescriptionForTest())
        every { customerRepository.findCustomerByEmail("szymonpawlak@o2.pl") } returns (null)
        assertThrows<CustomerNotFoundException> { customerService.findCustomerByEmail("szymonpawlak@o2.pl"); }
    }

    @Test
    fun `should update customer information`() {
        every { customerRepository.findById(1L) }.returns(Optional.of(saveCustomerWithCorrectData()))
        every { customerRepository.save(any(Customer::class)) }.returns(saveCustomerWithCorrectData());
        //when
        val updatedCustomer = customerService
            .updateCustomerInformation(CustomerDto(1L, "Andrzej", "Nowak", "", "100 Oak Blvd, Poznan, Poland"));
        //then
        assertEquals("Andrzej", updatedCustomer.firstName)
        assertEquals("Nowak", updatedCustomer.lastName)
        assertEquals("100 Oak Blvd, Poznan, Poland", updatedCustomer.address)
    }

    @Test
    fun `should throw CustomerNotFoundException when customer not exists`() {
        every { customerRepository.findById(1L) } returns (Optional.empty())
        assertThrows<CustomerNotFoundException> {
            customerService
                .updateCustomerInformation(CustomerDto(1L, "Andrzej", "Nowak", "", "100 Oak Blvd, Poznan, Poland"));
        }
    }


    @Test
    fun `should delete customer`() {
        val customer = saveCustomerWithCorrectData();
        every { customerRepository.findByIdOrNull(1L) } returns customer
        every { customerRepository.delete(customer) } just Runs
        customerService.deleteCustomer(1L)
        verify { customerRepository.delete(customer) }
    }

    @Test
    fun `should throw CustomerNotExistsException when customer not exists during delete customer`() {
        every { customerRepository.findByIdOrNull(1L) } returns null
        assertThrows<CustomerNotFoundException> { customerService.deleteCustomer(1L) }
    }


    private fun saveCustomersWithDifferentDescriptionForTest(): List<Customer> {
        return mutableListOf(
            Customer(1L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland"),
            Customer(2L, "Anna", "Nowak", "annanowak@o2.pl", "456 Green Ave, Krakow, Poland"),
            Customer(3L, "Jan", "Kowalski", "jankowalski@o2.pl", "789 Elm St, Gdansk, Poland"),
            Customer(4L, "Ewa", "Wiśniewska", "ewawisniewska@o2.pl", "101 Oak Blvd, Poznan, Poland")
        )
    }

    private fun saveCustomerWithCorrectData(): Customer {
        return Customer(1L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland")
    }

    private fun saveCustomerWithBlankData(): Customer {
        return Customer(2L, " ", "Pawlak", "szymonpawlak96@o2.pl", "123 Main St, Warsaw, Poland")
    }


}

