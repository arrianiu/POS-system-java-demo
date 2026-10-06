package src.ui;

import src.dao.InventoryDAO;
import src.ui.components.UserInfoPanel;
import src.ui.components.InventoryCellRenderer;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class InventoryReportFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final InventoryDAO inventoryDAO = new InventoryDAO();

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

    public InventoryReportFrame() {
        setTitle("📊 Inventory Report");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // Header
        add(createStyledHeader(), BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        // Table title
        JPanel tableTitlePanel = new JPanel(new BorderLayout());
        tableTitlePanel.setBackground(DARK_BG);

        JLabel tableTitle = new JLabel("📦 Stock Activity Report");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);

        JLabel infoLabel = new JLabel("Select a product to adjust stock levels");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(SLYTHERIN_SILVER);

        tableTitlePanel.add(tableTitle, BorderLayout.WEST);
        tableTitlePanel.add(infoLabel, BorderLayout.EAST);

        mainPanel.add(tableTitlePanel, BorderLayout.NORTH);
        mainPanel.add(createStyledTable(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadInventory();
        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("📊 INVENTORY REPORT");
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
                new String[] {
                        "Product",
                        "Incoming",
                        "Outgoing",
                        "Adjustments",
                        "Current Stock",
                        "Status"
                }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(SLYTHERIN_GREEN.darker());
        table.setSelectionBackground(HOVER_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Style header
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(SLYTHERIN_SILVER);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(new LineBorder(SLYTHERIN_GREEN));

        // Use custom renderer if available
        try {
            table.setDefaultRenderer(Object.class, new InventoryCellRenderer());
        } catch (Exception e) {
            // If custom renderer fails, use default styling
            System.out.println("Using default table renderer");
        }

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(250); // Product
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Incoming
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Outgoing
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Adjustments
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Current Stock
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Status

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(DARK_BG);

        // Left side - legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legendPanel.setBackground(DARK_BG);

        legendPanel.add(createLegendItem("🟢", "Normal Stock", ACCENT_GREEN));
        legendPanel.add(createLegendItem("🟡", "Low Stock", new Color(220, 180, 60)));
        legendPanel.add(createLegendItem("🔴", "Critical Stock", new Color(200, 60, 60)));

        // Right side - buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(DARK_BG);

        JButton btnAdjust = createStyledButton("✏️ Adjust Stock", SLYTHERIN_GREEN);
        JButton btnRefresh = createStyledButton("🔄 Refresh", new Color(100, 120, 180));

        btnAdjust.addActionListener(e -> adjustStock());
        btnRefresh.addActionListener(e -> loadInventory());

        buttonPanel.add(btnAdjust);
        buttonPanel.add(btnRefresh);

        footer.add(legendPanel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    private JPanel createLegendItem(String icon, String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(DARK_BG);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(color);

        item.add(iconLabel);
        item.add(textLabel);

        return item;
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

    private void loadInventory() {
        model.setRowCount(0);

        try {
            for (Object[] row : inventoryDAO.getInventoryActivityReport()) {
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showStyledDialog(
                    "Error loading inventory report.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adjustStock() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showStyledDialog(
                    "Please select a product first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productName = model.getValueAt(row, 0).toString();

        // Create custom dialog for stock adjustment
        JPanel adjustPanel = new JPanel();
        adjustPanel.setLayout(new BoxLayout(adjustPanel, BoxLayout.Y_AXIS));
        adjustPanel.setBackground(PANEL_BG);

        JLabel infoLabel = new JLabel("Enter quantity adjustment for: " + productName);
        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helpLabel = new JLabel("(Use + for increase, - for decrease)");
        helpLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        helpLabel.setForeground(SLYTHERIN_SILVER);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtQty = new JTextField(10);
        txtQty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtQty.setBackground(DARK_BG);
        txtQty.setForeground(TEXT_COLOR);
        txtQty.setCaretColor(SLYTHERIN_GREEN);
        txtQty.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtQty.setMaximumSize(new Dimension(200, 35));
        txtQty.setAlignmentX(Component.LEFT_ALIGNMENT);

        adjustPanel.add(infoLabel);
        adjustPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        adjustPanel.add(helpLabel);
        adjustPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        adjustPanel.add(txtQty);

        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);

        int result = JOptionPane.showConfirmDialog(
                this,
                adjustPanel,
                "Adjust Stock",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return;

        String input = txtQty.getText().trim();
        if (input.isEmpty())
            return;

        int qty;
        try {
            qty = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showStyledDialog("Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (qty == 0) {
            showStyledDialog("Quantity cannot be zero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int productId = inventoryDAO.getProductIdByName(productName);
            inventoryDAO.adjustStock(productId, qty, "ADJUSTMENT");

            showStyledDialog(
                    "Stock adjusted successfully!\n" +
                            (qty > 0 ? "Added: +" : "Removed: ") + Math.abs(qty),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadInventory();

        } catch (Exception e) {
            e.printStackTrace();
            showStyledDialog("Failed to adjust stock.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStyledDialog(String message, String title, int type) {
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}