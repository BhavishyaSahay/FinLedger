package com.finledger.api.group;

import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface GroupRepository extends JpaRepository<ExpenseGroup, Long> {
    @Query("select g from ExpenseGroup g where g.id in " +
            "(select m.groupId from GroupMember m where m.userId = :userId) order by g.createdAt desc")
    List<ExpenseGroup> findAllForUser(Long userId);
}