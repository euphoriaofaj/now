-- user_profile всем пользователям, у кого его ещё нет
INSERT INTO user_profiles (user_id, is_free_call_made, created_at)
SELECT u.id, FALSE, NOW(6)
FROM users u
LEFT JOIN user_profiles up ON up.user_id = u.id
WHERE up.user_id IS NULL;

-- translator_profile только тем, у кого есть роль ROLE_TRANSLATOR, и профиля ещё нет
INSERT INTO translator_profiles (user_id, created_at)
SELECT u.id, NOW(6)
FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON r.id = ur.role_id AND r.role_name = 'ROLE_TRANSLATOR'
LEFT JOIN translator_profiles tp ON tp.user_id = u.id
WHERE tp.user_id IS NULL;
