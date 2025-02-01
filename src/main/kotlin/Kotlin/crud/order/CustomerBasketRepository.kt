package Kotlin.crud.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerBasketRepository: JpaRepository<CustomerBasket,Long> {
}