MỘT HỆ THỐNG SPRING BACKEND MANGA ĐƠN GIẢN

* DB
    - user(id, transgroup_id(nullable), username(unique), password, email, member_since, facebook, discord, bio) 
    - role(id, name, description)
    - permission(id, name, description)
    - manga(id, name, authors_name, genres, status(completed, ongoing, onhold), description, published_date)
    - trans_group(id, leader_id, name, founded_date, description, status(pending, approved, rejected, deleted))
    - chapter(id, manga_id, name, title, view, published_date, updated_date)
    - page(id, chapter_id, page_number, image_url)
    - user_roles(user_id, role_id)
    - role_permissions(role_id, permission_id)
    - manga_transgroups(manga_id, transgroup_id)
    - invalidated_token(id, expiration_time)
    - group_join_request(id, status, transgroup_id, user_id)