package src.dao;

import src.model.Supplier;
import src.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    // ===== FIND ALL =====
    public List<Supplier> findAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("id"),
                        rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
        }
        return list;
    }

    // ===== FIND BY ID =====
    public Supplier findById(int id) {
        String sql = "SELECT * FROM suppliers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching supplier by ID: " + e.getMessage());
        }
        return null;
    }

    // ===== CREATE =====
    public boolean create(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getName());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            return false;
        }
    }

    // ===== UPDATE =====
    // ===== UPDATE =====
    public boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getName());
            ps.setInt(2, supplier.getId()); // Make sure the ID is set correctly
            int rows = ps.executeUpdate();
            return rows > 0; // Allow "no change" updates
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    // ===== CHECK USAGE =====
    // ===== CHECK USAGE =====
    public boolean isSupplierUsed(int supplierId) {
        String sql = "SELECT COUNT(*) FROM products WHERE supplier_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if the supplier is linked to products
            }
        } catch (SQLException e) {
            System.err.println("Error checking supplier usage: " + e.getMessage());
        }
        return false; // Allow deletion if no products are linked to this supplier
    }

    // ===== DELETE =====
    public boolean delete(int id) {
        if (isSupplierUsed(id))
            return false;

        String sql = "DELETE FROM suppliers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }
}
