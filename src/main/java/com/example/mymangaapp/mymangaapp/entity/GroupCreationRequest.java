package com.example.mymangaapp.mymangaapp.entity;

import com.example.mymangaapp.mymangaapp.enums.GroupCreationRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@Table(
        name = "group_creation_request",
        uniqueConstraints = {
                // ko cho 1 user gửi nhiều yc tạo nhóm pending cùng lúc
                @UniqueConstraint(columnNames = { "creator_id", "status" })
        }
)
// Đây là bảng chứa các yêu cầu tạo nhóm dịch của user
public class GroupCreationRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    // user yc tạo nhóm
    User creator;

    // Tên nhóm dịch user muốn tạo
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COLLATE utf8mb4_unicode_ci")
    String nameGroup;

    @Column(length = 300)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(15) DEFAULT 'PENDING'")
    @Builder.Default
    GroupCreationRequestStatus status = GroupCreationRequestStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_group_id")
    // nếu đc duyệt thì sẽ có group được tạo ở đây
    TransGroup createdGroup;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    // Admin nào duyệt
    User reviewer;
}
