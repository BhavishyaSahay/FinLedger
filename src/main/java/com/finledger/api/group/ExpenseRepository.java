package com.finledger.api.group;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);
}