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
// bỏ ràng buộc unique (creator_id, status)
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
    // Bỏ unique và ko đc null
    @Column(length = 50, nullable = false)
    String nameGroup;

    @Column(length = 300)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(15) DEFAULT 'PENDING'")
    @Builder.Default
    GroupCreationRequestStatus status = GroupCreationRequestStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_group_id")
    // nếu đc duyệt thì sẽ có group được tạo ở đây
    TransGroup createdGroup;

    // 1 admin có thể duyệt nhiều yêu cầu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    // Admin nào duyệt hoặc từ chối
    User reviewer;
}
