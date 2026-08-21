package com.example.mymangaapp.mymangaapp.entity;

import com.example.mymangaapp.mymangaapp.enums.GroupJoinRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(
        name = "group_join_request",
        uniqueConstraints = {
                // Ràng buộc để 1 user ko thể gửi nhiều yc pending cho cùng 1 nhóm
                @UniqueConstraint(columnNames = { "transgroup_id", "user_id", "status" })
        }
)
// Khi 1 user yêu cầu xin vào nhóm để leader duyệt
// ta sẽ không cứ thêm luôn vào member của group
// với trạng thái pending vì sẽ làm bẩn, thay vào đó
// ta tạo 1 entity để quản lý trạng thái của từng
// đơn gia nhập
public class GroupJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transgroup_id", nullable = false)
    TransGroup transGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    @Builder.Default
    GroupJoinRequestStatus status = GroupJoinRequestStatus.PENDING;

}
