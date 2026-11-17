package com.finledger.api.payment;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.razorpayOrderId = :orderId")
    Optional<Payment> lockByOrderId(String orderId);

    Optional<Payment> findFirstByExpenseIdAndUserIdAndStatus(Long expenseId, Long userId, Payment.Status status);
}