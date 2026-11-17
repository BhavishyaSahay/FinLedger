package com.finledger.api.group;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "expense_splits")
@IdClass(ExpenseSplit.Key.class)
@Getter @Setter @NoArgsConstructor
public class ExpenseSplit {
    @Id @Column(name = "expense_id")
    private Long expenseId;

    @Id @Column(name = "user_id")
    private Long userId;

    @Column(name = "amount_owed", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountOwed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "paid_via")
    private PaidVia paidVia;

    @Column(name = "paid_at")
    private Instant paidAt;

    private String note;

    public ExpenseSplit(Long expenseId, Long userId, BigDecimal amountOwed) {
        this.expenseId = expenseId; this.userId = userId; this.amountOwed = amountOwed;
    }

    public void markPaid(PaidVia via, String note) {
        this.status = Status.PAID;
        this.paidVia = via;
        this.paidAt = Instant.now();
        this.note = note;
    }

    public enum Status { PENDING, PAID }
    public enum PaidVia { SELF, RAZORPAY, CASH }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Key implements Serializable {
        private Long expenseId;
        private Long userId;
    }
}