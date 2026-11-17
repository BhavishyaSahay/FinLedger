package com.finledger.api.group;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "group_members")
@IdClass(GroupMember.Key.class)
@Getter @Setter @NoArgsConstructor
public class GroupMember {
    @Id @Column(name = "group_id")
    private Long groupId;

    @Id @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    public GroupMember(Long groupId, Long userId, Role role) {
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
    }

    public enum Role { ADMIN, MEMBER }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Key implements Serializable {
        private Long groupId;
        private Long userId;
    }
}