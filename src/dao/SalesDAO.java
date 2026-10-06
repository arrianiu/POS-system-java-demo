package src.dao;

import src.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalesDAO {

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ProductDAO productDAO = new ProductDAO();

    // =========================
    // PROCESS MULTI-ITEM SALE
    // cart: productId -> quantity
    // =========================
    public boolean processTransaction(Map<Integer, Integer> cart) {

        if (cart == null || cart.isEmpty())
            return false;

        String insertSaleSql = """
                INSERT INTO sales (sale_date, total)
                VALUES (?, ?)
                """;

        String insertItemSql = """
                INSERT INTO sale_items
                (sale_id, product_id, quantity, price, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            // 1️⃣ CHECK STOCK
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int productId = entry.getKey();
                int qty = entry.getValue();

                int stock = inventoryDAO.getCurrentStock(conn, productId);
                if (stock < qty) {
                    conn.rollback();
                    return false;
                }
            }

            // 2️⃣ CALCULATE TOTAL
            double grandTotal = 0;
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                double price = productDAO.getPriceById(entry.getKey());
                grandTotal += price * entry.getValue();
            }

            // 3️⃣ INSERT SALE
            int saleId;
            try (PreparedStatement ps = conn.prepareStatement(
                    insertSaleSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setDouble(2, grandTotal);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) {
                    conn.rollback();
                    return false;
                }
                saleId = keys.getInt(1);
            }

            // 4️⃣ INSERT ITEMS + DEDUCT STOCK
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql)) {

                for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {

                    int productId = entry.getKey();
                    int qty = entry.getValue();
                    double price = productDAO.getPriceById(productId);
                    double subtotal = price * qty;

                    psItem.setInt(1, saleId);
                    psItem.setInt(2, productId);
                    psItem.setInt(3, qty);
                    psItem.setDouble(4, price);
                    psItem.setDouble(5, subtotal);
                    psItem.addBatch();

                    if (!inventoryDAO.adjustStock(conn, productId, -qty, "SALE")) {
                        conn.rollback();
                        return false;
                    }
                }

                psItem.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // SALES HISTORY
    // =========================
    public List<String[]> getSalesHistory() {

        List<String[]> list = new ArrayList<>();

        String sql = """
                    SELECT
                        s.id AS sale_id,
                        COUNT(si.id) AS items,
                        SUM(si.subtotal) AS total,
                        s.sale_date
                    FROM sales s
                    JOIN sale_items si ON s.id = si.sale_id
                    GROUP BY s.id, s.sale_date
                    ORDER BY s.sale_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                        String.valueOf(rs.getInt("sale_id")),
                        String.valueOf(rs.getInt("items")),
                        String.valueOf(rs.getDouble("total")),
                        rs.getTimestamp("sale_date").toString()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String[]> getShiftSales(LocalDateTime loginTime) {

        List<String[]> list = new ArrayList<>();

        String sql = """
                    SELECT
                        s.id AS sale_id,
                        COUNT(si.id) AS items,
                        s.total,
                        s.sale_date
                    FROM sales s
                    JOIN sale_items si ON s.id = si.sale_id
                    WHERE s.sale_date >= ?
                    GROUP BY s.id
                    ORDER BY s.sale_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(loginTime));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("sale_id"),
                        rs.getString("items"),
                        rs.getString("total"),
                        rs.getTimestamp("sale_date").toLocalDateTime().toLocalTime().toString()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public double getMonthlyRevenue(int year, int month) {

        String sql = """
                SELECT COALESCE(SUM(total), 0)
                FROM sales
                WHERE YEAR(sale_date) = ?
                  AND MONTH(sale_date) = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getYearlyTransactions(int year) {

        String sql = """
                    SELECT COUNT(*)
                    FROM sales
                    WHERE YEAR(sale_date) = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getYearlyRevenue(int year) {

        String sql = """
                SELECT COALESCE(SUM(total), 0)
                FROM sales
                WHERE YEAR(sale_date) = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public String getTopSellingProductByMonth(int year, int month) {

        String sql = """
                SELECT p.name
                FROM sale_items si
                JOIN sales s ON si.sale_id = s.id
                JOIN products p ON si.product_id = p.id
                WHERE YEAR(s.sale_date) = ?
                  AND MONTH(s.sale_date) = ?
                GROUP BY p.id
                ORDER BY SUM(si.quantity) DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public String getTopSellingProductByYear(int year) {

        String sql = """
                SELECT p.name
                FROM sale_items si
                JOIN sales s ON si.sale_id = s.id
                JOIN products p ON si.product_id = p.id
                WHERE YEAR(s.sale_date) = ?
                GROUP BY p.id
                ORDER BY SUM(si.quantity) DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public int getMonthlyTransactions(int month, int year) {

        String sql = """
                    SELECT COUNT(*)
                    FROM sales
                    WHERE MONTH(sale_date) = ?
                      AND YEAR(sale_date) = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getShiftRevenue(LocalDateTime loginTime) {

        String sql = """
                    SELECT COALESCE(SUM(total), 0)
                    FROM sales
                    WHERE sale_date >= ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(loginTime));
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    // =========================
    // GET SALE DETAILS BY SALE ID
    // =========================
    public List<String[]> getSaleDetails(int saleId) {

        List<String[]> list = new ArrayList<>();

        String sql = """
                    SELECT
                        p.name AS product_name,
                        si.quantity,
                        si.price,
                        si.subtotal
                    FROM sale_items si
                    JOIN products p ON si.product_id = p.id
                    WHERE si.sale_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("product_name"),
                        String.valueOf(rs.getInt("quantity")),
                        String.valueOf(rs.getDouble("price")),
                        String.valueOf(rs.getDouble("subtotal"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String[]> getTodaySales() {

        List<String[]> list = new ArrayList<>();

        String sql = """
                SELECT
                    s.id AS sale_id,
                    COUNT(si.id) AS items,
                    s.total,
                    s.sale_date
                FROM sales s
                JOIN sale_items si ON s.id = si.sale_id
                WHERE DATE(s.sale_date) = CURDATE()
                GROUP BY s.id
                ORDER BY s.sale_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("sale_id"),
                        rs.getString("items"),
                        rs.getString("total"),
                        rs.getTimestamp("sale_date").toString()
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalTransactions() {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalItemsSold() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM sale_items";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTodayRevenue() {
        String sql = """
                SELECT COALESCE(SUM(total), 0)
                FROM sales
                WHERE DATE(sale_date) = CURRENT_DATE
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public String getTopSellingProduct() {
        String sql = """
                SELECT p.name
                FROM sale_items si
                JOIN products p ON si.product_id = p.id
                GROUP BY p.id
                ORDER BY SUM(si.quantity) DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    // =========================
    // TOTAL REVENUE
    // =========================
    public double getTotalRevenue() {

        String sql = "SELECT COALESCE(SUM(subtotal), 0) FROM sale_items";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
