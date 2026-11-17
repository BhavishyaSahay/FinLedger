package com.finledger.api.group;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, ExpenseSplit.Key> {
    List<ExpenseSplit> findByExpenseId(Long expenseId);
    List<ExpenseSplit> findByExpenseIdIn(Collection<Long> expenseIds);

    // Lock one share row while paying it, so two phones can't pay the same share at once
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ExpenseSplit s where s.expenseId = :expenseId and s.userId = :userId")
    Optional<ExpenseSplit> lockOne(Long expenseId, Long userId);

    long countByExpenseIdAndStatus(Long expenseId, ExpenseSplit.Status status);
}