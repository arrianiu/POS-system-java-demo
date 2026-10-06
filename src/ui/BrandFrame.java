package src.ui;

import src.dao.BrandDAO;
import src.model.Brand;
import src.ui.components.UserInfoPanel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;

public class BrandFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName;

    private final BrandDAO brandDAO = new BrandDAO();

    // Slytherin Color Palette
    private static final Color DARK_BG = new Color(15, 20, 25);
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_GREEN = new Color(26, 83, 92);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color ACCENT_GREEN = new Color(34, 139, 34);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color HOVER_COLOR = new Color(30, 100, 110);
    private static final Color CARD_BG = new Color(30, 40, 42);
    private static final Color TABLE_HEADER = new Color(20, 30, 32);

    public BrandFrame() {
        setTitle("🏷️ Brand Management");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // Header
        add(createStyledHeader(), BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(DARK_BG);

        JLabel tableTitle = new JLabel("📋 Brands List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(createStyledTable(), BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(createFormPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                txtName.setText(model.getValueAt(table.getSelectedRow(), 1).toString());
            }
        });

        loadBrands();
        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("🏷️ BRAND MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);

        UserInfoPanel userInfo = new UserInfoPanel();
        userInfo.setBackground(PANEL_BG);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JScrollPane createStyledTable() {
        model = new DefaultTableModel(
                new String[] { "ID", "Brand Name" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(SLYTHERIN_GREEN.darker());
        table.setSelectionBackground(HOVER_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Style header
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(SLYTHERIN_SILVER);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBorder(new LineBorder(SLYTHERIN_GREEN));

        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        return scrollPane;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN, 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        // Title
        JLabel formTitle = new JLabel("✏️ Brand Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TEXT_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Input field
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(PANEL_BG);
        inputPanel.setMaximumSize(new Dimension(650, 45));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblName = new JLabel("Brand Name:");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblName.setForeground(SLYTHERIN_SILVER);
        lblName.setPreferredSize(new Dimension(130, 45));

        txtName = createStyledTextField();

        inputPanel.add(lblName, BorderLayout.WEST);
        inputPanel.add(txtName, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(PANEL_BG);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAdd = createStyledButton("➕ Add", ACCENT_GREEN);
        JButton btnUpdate = createStyledButton("✏️ Update", SLYTHERIN_GREEN);
        JButton btnDelete = createStyledButton("🗑️ Delete", new Color(180, 60, 60));

        btnAdd.addActionListener(e -> addBrand());
        btnUpdate.addActionListener(e -> updateBrand());
        btnDelete.addActionListener(e -> deleteBrand());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        // Assemble form
        formPanel.add(formTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(inputPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(buttonPanel);

        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(DARK_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(SLYTHERIN_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN, 2),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }

            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
        });

        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Color hoverColor = bgColor.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private void loadBrands() {
        model.setRowCount(0);
        for (Brand b : brandDAO.findAll()) {
            model.addRow(new Object[] { b.getId(), b.getName() });
        }
    }

    private void addBrand() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            showStyledDialog("Brand name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Brand b = new Brand();
        b.setName(name);

        if (brandDAO.create(b)) {
            showStyledDialog("Brand added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtName.setText("");
            loadBrands();
        }
    }

    private void updateBrand() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showStyledDialog("Please select a brand to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String name = txtName.getText().trim();

        if (name.isEmpty()) {
            showStyledDialog("Brand name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Updating brand ID = " + id);

        Brand b = new Brand(id, name);

        if (!brandDAO.update(b)) {
            showStyledDialog(
                    "Update failed.\nID not found in database.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        showStyledDialog("Brand updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        txtName.setText("");
        table.clearSelection();
        loadBrands();
    }

    private void deleteBrand() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showStyledDialog("Please select a brand to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        System.out.println("Deleting brand ID = " + id);

        if (brandDAO.isBrandUsed(id)) {
            showStyledDialog(
                    "Cannot delete brand.\nStill linked to products.",
                    "Delete Blocked",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!brandDAO.delete(id)) {
            showStyledDialog(
                    "Delete failed.\nID not found in database.",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        showStyledDialog("Brand deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        txtName.setText("");
        table.clearSelection();
        loadBrands();
    }

    private void showStyledDialog(String message, String title, int type) {
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}