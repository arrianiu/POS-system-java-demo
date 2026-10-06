package src.dao;

import src.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    // =========================
    // LOG INVENTORY MOVEMENT
    // =========================
    private boolean logMovement(
            Connection conn,
            int productId,
            int changeQty,
            String action) throws SQLException {

        String sql = """
                INSERT INTO inventory_logs
                (product_id, change_qty, action, log_date)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, changeQty);
            ps.setString(3, action);
            ps.setTimestamp(4,
                    Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // ADJUST STOCK SAFELY
    // =========================
    public boolean adjustStock(
            Connection conn,
            int productId,
            int qtyChange,
            String action) throws SQLException {

        String updateSql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, qtyChange);
            ps.setInt(2, productId);

            if (ps.executeUpdate() == 0)
                return false;
        }

        return logMovement(conn, productId, qtyChange, action);
    }

    // =========================
    // CHECK CURRENT STOCK
    // =========================
    // =========================
    // CHECK CURRENT STOCK
    // =========================
    public int getCurrentStock(Connection conn, int productId) throws SQLException {
        String sql = "SELECT quantity FROM products WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    public int getProductIdByName(String name) {

        String sql = "SELECT id FROM products WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean adjustStock(int productId, int qtyChange, String action) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            boolean success = adjustStock(conn, productId, qtyChange, action);

            conn.commit();
            return success;
        }
    }

    public int getCurrentStock(int productId) {
        String sql = "SELECT quantity FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // =========================
    // GET INVENTORY HISTORY
    // =========================
    public List<String[]> getInventoryLogs() {

        List<String[]> logs = new ArrayList<>();

        String sql = """
                SELECT p.name,
                       l.change_qty,
                       l.action,
                       l.log_date
                FROM inventory_logs l
                JOIN products p ON l.product_id = p.id
                ORDER BY l.log_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new String[] {
                        rs.getString("name"),
                        String.valueOf(rs.getInt("change_qty")),
                        rs.getString("action"),
                        rs.getTimestamp("log_date").toString()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public List<Object[]> getInventoryActivityReport() {

        List<Object[]> list = new ArrayList<>();

        String sql = """
                    SELECT
                        p.name,
                        COALESCE(SUM(CASE WHEN il.change_qty > 0 THEN il.change_qty END), 0) AS incoming,
                        COALESCE(SUM(CASE WHEN il.change_qty < 0 THEN ABS(il.change_qty) END), 0) AS outgoing,
                        COALESCE(SUM(CASE WHEN il.action = 'ADJUSTMENT' THEN 1 END), 0) AS adjustments,
                        p.quantity
                    FROM products p
                    LEFT JOIN inventory_logs il ON p.id = il.product_id
                    GROUP BY p.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int stock = rs.getInt("quantity");

                list.add(new Object[] {
                        rs.getString("name"),
                        rs.getInt("incoming"),
                        rs.getInt("outgoing"),
                        rs.getInt("adjustments"),
                        stock,
                        stock <= 10 ? "LOW STOCK ⚠" : "OK"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
