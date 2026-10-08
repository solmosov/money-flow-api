create unique index uk_categories_user_type_name_active
    on categories (user_id, type, name)
    where deleted_at is null