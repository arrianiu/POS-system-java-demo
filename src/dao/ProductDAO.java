package src.dao;

import src.model.Product;
import src.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ===== FIND ALL =====
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getPriceById(int productId) {

        String sql = "SELECT price FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0; // fallback
    }

    // ===== FIND BY ID =====
    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapProduct(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== CREATE =====
    public boolean create(Product p) {
        String sql = """
                    INSERT INTO products
                    (name, category_id, brand_id, supplier_id, unit,
                     cost_price, markup, price, quantity, date_added)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setProductParams(ps, p);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE =====
    public boolean update(Product p) {
        String sql = """
                    UPDATE products SET
                    name = ?, category_id = ?, brand_id = ?, supplier_id = ?,
                    unit = ?, cost_price = ?, markup = ?, price = ?,
                    quantity = ?, date_added = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setProductParams(ps, p);
            ps.setInt(11, p.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== DELETE =====
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getProductReport() {

        List<Object[]> list = new ArrayList<>();

        String sql = """
                    SELECT
                        p.name,
                        c.name AS category,
                        b.name AS brand,
                        s.name AS supplier,
                        p.unit,
                        p.cost_price,
                        p.markup,
                        p.price,
                        p.quantity,
                        (p.cost_price * p.quantity) AS stock_value,
                        p.date_added
                    FROM products p
                    JOIN categories c ON p.category_id = c.id
                    JOIN brands b ON p.brand_id = b.id
                    JOIN suppliers s ON p.supplier_id = s.id
                    ORDER BY p.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("brand"),
                        rs.getString("supplier"),
                        rs.getString("unit"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("markup"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDouble("stock_value"),
                        rs.getDate("date_added")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== SEARCH =====
    public List<Product> search(String name, Integer categoryId, Integer brandId, Integer supplierId) {

        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");

        if (name != null && !name.isBlank())
            sql.append(" AND name LIKE ?");
        if (categoryId != null)
            sql.append(" AND category_id = ?");
        if (brandId != null)
            sql.append(" AND brand_id = ?");
        if (supplierId != null)
            sql.append(" AND supplier_id = ?");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;

            if (name != null && !name.isBlank())
                ps.setString(i++, "%" + name + "%");
            if (categoryId != null)
                ps.setInt(i++, categoryId);
            if (brandId != null)
                ps.setInt(i++, brandId);
            if (supplierId != null)
                ps.setInt(i++, supplierId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== HELPER METHODS =====
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setBrandId(rs.getInt("brand_id"));
        p.setSupplierId(rs.getInt("supplier_id"));
        p.setUnit(rs.getString("unit"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setMarkup(rs.getDouble("markup"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setDateAdded(rs.getDate("date_added").toLocalDate());
        return p;
    }

    private void setProductParams(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1, p.getName());
        ps.setInt(2, p.getCategoryId());
        ps.setInt(3, p.getBrandId());
        ps.setInt(4, p.getSupplierId());
        ps.setString(5, p.getUnit());
        ps.setDouble(6, p.getCostPrice());
        ps.setDouble(7, p.getMarkup());
        ps.setDouble(8, p.getPrice());
        ps.setInt(9, p.getQuantity());
        ps.setDate(10, Date.valueOf(
                p.getDateAdded() != null ? p.getDateAdded() : LocalDate.now()));
    }
}
