package com.example.mymangaapp.mymangaapp.enums;

public enum TransGroupStatus {
    PENDING, // đang đợi đc admin duyệt
    APPROVED, // đã đc duyệt
    REJECTED, // từ chối
    DELETED // Trạng thái soft delete
}
