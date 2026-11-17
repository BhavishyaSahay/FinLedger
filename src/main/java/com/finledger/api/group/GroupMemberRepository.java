package com.finledger.api.group;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMember.Key> {
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMember> findByGroupId(Long groupId);
    long countByGroupIdAndUserIdIn(Long groupId, List<Long> userIds);
}