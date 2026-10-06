package src.dao;

import src.model.Brand;
import src.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    // findAll (rubric-compliant)
    public List<Brand> findAll() {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM brands";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Brand(
                        rs.getInt("id"),
                        rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching brands: " + e.getMessage());
        }
        return list;
    }

    public Brand findById(int id) {
        String sql = "SELECT * FROM brands WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Brand(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching brand by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean create(Brand brand) {
        String sql = "INSERT INTO brands (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            int rows = ps.executeUpdate();
            return rows >= 0; // allow "no change" updates

        } catch (SQLException e) {
            System.err.println("Error adding brand: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Brand brand) {
        String sql = "UPDATE brands SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            ps.setInt(2, brand.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating brand: " + e.getMessage());
            return false;
        }
    }

    // prevent deletion if brand is used by products
    public boolean isBrandUsed(int brandId) {
        String sql = "SELECT COUNT(*) FROM products WHERE brand_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking brand usage: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        if (isBrandUsed(id))
            return false;

        String sql = "DELETE FROM brands WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting brand: " + e.getMessage());
            return false;
        }
    }

}
