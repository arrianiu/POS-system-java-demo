package src.ui;

import src.dao.SupplierDAO;
import src.model.Supplier;
import src.ui.components.UserInfoPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class SupplierFrame extends JFrame {

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

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName;

    private final SupplierDAO supplierDAO = new SupplierDAO();

    public SupplierFrame() {

        setTitle("Supplier Management");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        // Set frame background
        getContentPane().setBackground(DARK_BG);

        add(new UserInfoPanel(true), BorderLayout.NORTH);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DARK_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("⬡ Supplier Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[] { "ID", "Supplier Name" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(25, 0, 0, 0)));

        // Input section
        JPanel inputSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        inputSection.setBackground(PANEL_BG);

        JLabel lblName = new JLabel("Supplier Name");
        lblName.setForeground(SLYTHERIN_SILVER);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(PANEL_BG);
        inputWrapper.add(lblName, BorderLayout.NORTH);

        txtName = new JTextField(30);
        styleTextField(txtName);
        inputWrapper.add(Box.createVerticalStrut(8));
        inputWrapper.add(txtName, BorderLayout.SOUTH);

        inputSection.add(inputWrapper);

        // Button section
        JPanel buttonSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonSection.setBackground(PANEL_BG);
        buttonSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton btnAdd = createStyledButton("Add", true);
        JButton btnUpdate = createStyledButton("Update", false);
        JButton btnDelete = createStyledButton("Delete", false);

        buttonSection.add(btnAdd);
        buttonSection.add(btnUpdate);
        buttonSection.add(btnDelete);

        formPanel.add(inputSection, BorderLayout.NORTH);
        formPanel.add(buttonSection, BorderLayout.SOUTH);

        mainPanel.add(formPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addSupplier());
        btnUpdate.addActionListener(e -> updateSupplier());
        btnDelete.addActionListener(e -> deleteSupplier());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                txtName.setText(model.getValueAt(table.getSelectedRow(), 1).toString());
            }
        });

        loadSuppliers();
        setVisible(true);
    }

    private void styleTable() {
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setGridColor(SLYTHERIN_GREEN);
        table.setSelectionBackground(SLYTHERIN_GREEN);
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(SLYTHERIN_SILVER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SLYTHERIN_GREEN));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setReorderingAllowed(false);

        // Custom cell renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : PANEL_BG);
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(SLYTHERIN_GREEN);
                    c.setForeground(TEXT_COLOR);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        };

        // ID column - center aligned
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : PANEL_BG);
                    c.setForeground(SLYTHERIN_SILVER);
                } else {
                    c.setBackground(SLYTHERIN_GREEN);
                    c.setForeground(TEXT_COLOR);
                }

                return c;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(0).setMaxWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(700);
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(CARD_BG);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(ACCENT_GREEN);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SLYTHERIN_GREEN, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        // Focus border
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_GREEN, 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SLYTHERIN_GREEN, 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setBackground(ACCENT_GREEN);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
            button.setBorderPainted(false);
        } else {
            button.setBackground(SLYTHERIN_GREEN);
            button.setForeground(TEXT_COLOR);
            button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
            button.setBorderPainted(false);
        }

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalBg = button.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(new Color(28, 119, 28));
                } else {
                    button.setBackground(HOVER_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBg);
            }
        });

        return button;
    }

    private void loadSuppliers() {
        model.setRowCount(0);
        for (Supplier s : supplierDAO.findAll()) {
            model.addRow(new Object[] { s.getId(), s.getName() });
        }
    }

    private void addSupplier() {
        if (txtName.getText().trim().isEmpty())
            return;

        Supplier s = new Supplier();
        s.setName(txtName.getText().trim());

        if (supplierDAO.create(s)) {
            loadSuppliers();
            txtName.setText("");
            showCustomDialog("Supplier added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateSupplier() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        Supplier s = new Supplier(
                Integer.parseInt(model.getValueAt(row, 0).toString()),
                txtName.getText().trim());

        if (supplierDAO.update(s)) {
            loadSuppliers();
            txtName.setText("");
            table.clearSelection();
            showCustomDialog("Supplier updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        if (supplierDAO.isSupplierUsed(id)) {
            showCustomDialog(
                    "Cannot delete supplier. Still linked to products.",
                    "Delete Blocked",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (supplierDAO.delete(id)) {
            loadSuppliers();
            txtName.setText("");
            table.clearSelection();
            showCustomDialog("Supplier deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCustomDialog(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

}