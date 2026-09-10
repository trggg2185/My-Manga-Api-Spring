MỘT HỆ THỐNG BACKEND MANGA

* DB
    - user(id, transgroup_id(nullable), username(unique), password, email, facebook, discord, bio, created_at, updated_at) 
    - role(id, name, description, created_at, updated_at)
    - permission(id, name, description, created_at, updated_at)
    - manga(id, name, slug, authors_name, genres, status(completed, ongoing, onhold), description, owner_transgroup_id, created_at, updated_at)
    - category(id, name, description, created_at, updated_at)
    - trans_group(id, leader_id, name, description, status(pending, approved, rejected, deleted), created_at, updated_at)
    - chapter(id, manga_id, chapter_index, title, view, created_at, updated_at)
    - page(id, chapter_id, page_number, image_url, created_at, updated_at)
    - user_roles(user_id, role_id)
    - role_permissions(role_id, permission_id)
    - manga_transgroups(manga_id, transgroup_id)
    - invalidated_token(id, expiration_time, created_at, updated_at)
    - group_join_request(id, status, transgroup_id, user_id, created_at, updated_at)

* Công nghệ sử dụng
    - Spring Boot
    - Spring JPA
    - Spring Security
    - Redis
    - Amazon Web Service SDK S3 (R2 Object Storage)