import Kotlin.crud.customer.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.collections.ArrayList

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerService: CustomerService

    @Test
    fun shouldGetAllCustomers(){
        //given
        every { customerRepository.findAll()} returns (ArrayList<Customer>(saveCustomersWithDifferentDescriptionForTest()))
        //when
        val customerList = customerService.getAllCustomers();
        //then
        assertThat(customerList).hasSize(4)
        assertEquals(listOf("Szymon","Anna","Jan","Ewa"),saveCustomersWithDifferentDescriptionForTest()
            .map{it.toCustomerDto().firstName})
    }

    @Test
    fun shouldGetEmptyList(){
        //given
        every { customerRepository.findAll() }returns(listOf())
        //when
        val customerList = customerService.getAllCustomers();
        //
        assertThat(customerList).isEmpty();
    }

        @Test
        fun shouldSaveCustomer(){
            //given
            every { customerRepository.existsByEmail("szymonpawlak@o2.pl") } returns false
            every { customerRepository.save(any<Customer>()) } returns saveCustomerWithCorrectData()
            //when
            val customer = customerService.saveCustomer(saveCustomerWithCorrectData().toCustomerDto());
            //then
            assertEquals("Szymon",customer.firstName)
            assertEquals("Pawlak",customer.lastName)
            assertEquals("szymonpawlak@o2.pl",customer.email)
            assertEquals("123 Main St, Warsaw, Poland",customer.address)
        }

        @Test
        fun shouldThrowEmailExistsException_whenEmailExists(){
            every { customerRepository.existsByEmail("szymonpawlak@o2.pl") } returns true
            assertThrows<EmailExistsException> { val customer = customerService.saveCustomer(saveCustomerWithCorrectData().toCustomerDto());  }
        }
    
        @Test
        fun shouldThrowIllegalArgumentException_WhenAnyDataIsBlank(){
            //given
            every { customerRepository.existsByEmail("szymonpawlak96@o2.pl") } returns false
            every { customerRepository.save(any<Customer>()) } returns saveCustomerWithBlankData()
            //when //then
            assertThrows<IllegalArgumentException> {customerService.saveCustomer(saveCustomerWithBlankData().toCustomerDto())}
        }

        @Test
        fun shouldFindCustomerByEmail(){
            //given
            every { customerRepository.findAll() }.returns(saveCustomersWithDifferentDescriptionForTest())
            every { customerRepository.findCustomerByEmail("szymonpawlak@o2.pl") }returns(saveCustomerWithCorrectData());
            //when then
            val foundCustomer = customerService.findCustomerByEmail("szymonpawlak@o2.pl");
            //
            assertEquals("Szymon",foundCustomer.firstName)
            assertEquals("Pawlak",foundCustomer.lastName)
            assertEquals("szymonpawlak@o2.pl",foundCustomer.email)
            assertEquals("123 Main St, Warsaw, Poland",foundCustomer.address)
        }

        @Test
        fun shouldThrowEmailNotExistsException_whenCustomerWithIntroducedEmailNotExists(){
            every { customerRepository.findAll() }.returns(saveCustomersWithDifferentDescriptionForTest())
            every { customerRepository.findCustomerByEmail("szymonpawlak@o2.pl") }returns(null)
            assertThrows<CustomerNotFoundException> { customerService.findCustomerByEmail("szymonpawlak@o2.pl"); }
        }
    



    private fun saveCustomersWithDifferentDescriptionForTest():List<Customer>{
        return mutableListOf(
            Customer(1L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland"),
            Customer(2L, "Anna", "Nowak", "annanowak@o2.pl", "456 Green Ave, Krakow, Poland"),
            Customer(3L, "Jan", "Kowalski", "jankowalski@o2.pl", "789 Elm St, Gdansk, Poland"),
            Customer(4L, "Ewa", "Wiśniewska", "ewawisniewska@o2.pl", "101 Oak Blvd, Poznan, Poland")
        )
    }

    private fun saveCustomerWithCorrectData():Customer{
       return Customer(1L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland")
    }

    private fun saveCustomerWithBlankData():Customer{
        return Customer(2L, " ", "Pawlak", "szymonpawlak96@o2.pl", "123 Main St, Warsaw, Poland")
    }

}