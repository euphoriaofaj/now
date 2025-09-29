package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class V2__seed_roles_and_admin extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        var conn = context.getConnection();

        Long adminRoleId = upsertRole(conn, "ROLE_ADMIN");
        Long userRoleId = upsertRole(conn, "ROLE_USER");
        Long trRoleId = upsertRole(conn, "ROLE_TRANSLATOR");

        String phone = "01012345673";
        String rawPwd = "123456";
        String hash = new BCryptPasswordEncoder().encode(rawPwd);

        Long adminId = ensureUser(conn, phone, hash);

        linkUserRole(conn, adminId, adminRoleId);
        linkUserRole(conn, adminId, userRoleId);
        linkUserRole(conn, adminId, trRoleId);
    }

    private Long upsertRole(java.sql.Connection c, String roleName) throws Exception {
        try (var ps = c.prepareStatement("select id from roles where role_name=?")) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        try (var ins = c.prepareStatement(
                "insert into roles (role_name, created_at) values (?, NOW(6))",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, roleName);
            ins.executeUpdate();
            try (ResultSet rs = ins.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalStateException("Cannot upsert role: " + roleName);
    }

    private Long ensureUser(java.sql.Connection c, String phone, String pwdHash) throws Exception {
        try (var ps = c.prepareStatement("select id from users where phone_number=? limit 1")) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        try (var ins = c.prepareStatement(
                "insert into users (phone_number, password, is_active, created_at) values (?, ?, true, NOW(6))",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, phone);
            ins.setString(2, pwdHash);
            ins.executeUpdate();
            try (ResultSet rs = ins.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalStateException("Cannot create admin user");
    }

    private void linkUserRole(java.sql.Connection c, Long userId, Long roleId) throws Exception {
        try (var ps = c.prepareStatement("select 1 from user_roles where user_id=? and role_id=?")) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return;
            }
        }
        try (var ins = c.prepareStatement("insert into user_roles (user_id, role_id) values (?, ?)")) {
            ins.setLong(1, userId);
            ins.setLong(2, roleId);
            ins.executeUpdate();
        }
    }
}
