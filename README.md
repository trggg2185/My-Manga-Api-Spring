MỘT HỆ THỐNG SPRING BACKEND MANGA ĐƠN GIẢN

* DB
    - user(id, transgroup_id(nullable), username(unique), password, email, member_since, facebook, discord, bio) 
    - role(id, name, description)
    - permission(id, name, description)
    - manga(id, name(unique), authors_name, genres, status(completed, ongoing, onhold), description, view, published_date)
    - trans_group(id, leader_id, name)
    - chapter(id, manga_id, name, title, published_date)
    - page(id, chapter_id, page_number, image_url)
    - user_role(user_id, role_id)
    - role_permission(role_id, permission_id)
    - manga_transgroup(manga_id, transgroup_id)