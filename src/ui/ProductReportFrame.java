package src.ui;

import src.dao.ProductDAO;
import src.ui.components.UserInfoPanel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductReportFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private final ProductDAO productDAO = new ProductDAO();
    private final NumberFormat pesoFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH"));

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

    public ProductReportFrame() {
        setTitle("📦 Product Report");
        setSize(1400, 750);
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

        // Stats panel
        mainPanel.add(createStatsPanel(), BorderLayout.NORTH);

        // Table title
        JPanel tableTitlePanel = new JPanel(new BorderLayout());
        tableTitlePanel.setBackground(DARK_BG);

        JLabel tableTitle = new JLabel("📋 Detailed Product Report");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);

        JLabel infoLabel = new JLabel("Complete product inventory with pricing and stock values");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(SLYTHERIN_SILVER);

        tableTitlePanel.add(tableTitle, BorderLayout.WEST);
        tableTitlePanel.add(infoLabel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(DARK_BG);
        centerPanel.add(tableTitlePanel, BorderLayout.NORTH);
        centerPanel.add(createStyledTable(), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadProducts();
        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("📦 PRODUCT REPORT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);

        UserInfoPanel userInfo = new UserInfoPanel();
        userInfo.setBackground(PANEL_BG);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(DARK_BG);

        JPanel totalProductsCard = createStatCard("📦", "Total Products", "0", SLYTHERIN_GREEN);
        JPanel totalValueCard = createStatCard("💰", "Total Inventory Value", "₱ 0.00", ACCENT_GREEN);
        JPanel lowStockCard = createStatCard("⚠️", "Low Stock Items", "0", new Color(220, 180, 60));
        JPanel categoriesCard = createStatCard("📂", "Categories", "0", new Color(100, 120, 180));

        statsPanel.add(totalProductsCard);
        statsPanel.add(totalValueCard);
        statsPanel.add(lowStockCard);
        statsPanel.add(categoriesCard);

        return statsPanel;
    }

    private JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelText.setForeground(SLYTHERIN_SILVER);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueText.setForeground(accentColor);
        valueText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(labelText);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueText);

        card.putClientProperty("valueLabel", valueText);

        return card;
    }

    private JScrollPane createStyledTable() {
        model = new DefaultTableModel(
                new String[] {
                        "Product",
                        "Category",
                        "Brand",
                        "Supplier",
                        "Unit",
                        "Cost",
                        "Markup (%)",
                        "Price",
                        "Stock",
                        "Stock Value",
                        "Date Added"
                }, 0) {
            @Override
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
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style header
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(SLYTHERIN_SILVER);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(new LineBorder(SLYTHERIN_GREEN));

        // Right align numeric columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Cost
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // Markup
        table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer); // Price
        table.getColumnModel().getColumn(8).setCellRenderer(rightRenderer); // Stock

        // Stock Value with green color
        DefaultTableCellRenderer valueRenderer = new DefaultTableCellRenderer();
        valueRenderer.setHorizontalAlignment(JLabel.RIGHT);
        valueRenderer.setForeground(ACCENT_GREEN);
        table.getColumnModel().getColumn(9).setCellRenderer(valueRenderer);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Product
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Category
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Brand
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Supplier
        table.getColumnModel().getColumn(4).setPreferredWidth(60); // Unit
        table.getColumnModel().getColumn(5).setPreferredWidth(90); // Cost
        table.getColumnModel().getColumn(6).setPreferredWidth(80); // Markup
        table.getColumnModel().getColumn(7).setPreferredWidth(90); // Price
        table.getColumnModel().getColumn(8).setPreferredWidth(60); // Stock
        table.getColumnModel().getColumn(9).setPreferredWidth(120); // Stock Value
        table.getColumnModel().getColumn(10).setPreferredWidth(100); // Date

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setBackground(DARK_BG);

        JButton btnRefresh = createStyledButton("🔄 Refresh", SLYTHERIN_GREEN);
        JButton btnExport = createStyledButton("📥 Export", new Color(100, 120, 180));
        JButton btnClose = createStyledButton("Close", SLYTHERIN_SILVER.darker());

        btnRefresh.addActionListener(e -> loadProducts());
        btnExport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Export feature coming soon!",
                    "Export",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        btnClose.addActionListener(e -> dispose());

        footer.add(btnRefresh);
        footer.add(btnExport);
        footer.add(btnClose);

        return footer;
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

    private void loadProducts() {
        model.setRowCount(0);

        int totalProducts = 0;
        double totalValue = 0;
        int lowStockCount = 0;
        java.util.Set<String> categories = new java.util.HashSet<>();

        for (Object[] row : productDAO.getProductReport()) {
            model.addRow(new Object[] {
                    row[0], // Product
                    row[1], // Category
                    row[2], // Brand
                    row[3], // Supplier
                    row[4], // Unit
                    pesoFmt.format(((Number) row[5]).doubleValue()), // Cost
                    ((Number) row[6]).doubleValue() + "%", // Markup
                    pesoFmt.format(((Number) row[7]).doubleValue()), // Price
                    row[8], // Stock
                    pesoFmt.format(((Number) row[9]).doubleValue()), // Stock Value
                    row[10] // Date Added
            });

            // Calculate stats
            totalProducts++;
            totalValue += ((Number) row[9]).doubleValue();
            categories.add(row[1].toString());

            int stock = ((Number) row[8]).intValue();
            if (stock < 10) {
                lowStockCount++;
            }
        }

        updateStats(totalProducts, totalValue, lowStockCount, categories.size());
    }

    private void updateStats(int totalProducts, double totalValue, int lowStock, int categories) {
        Container statsPanel = ((JPanel) ((JPanel) getContentPane().getComponent(1)).getComponent(0));

        if (statsPanel instanceof JPanel) {
            JPanel stats = (JPanel) statsPanel;

            // Update Total Products
            JPanel card1 = (JPanel) stats.getComponent(0);
            JLabel value1 = (JLabel) card1.getClientProperty("valueLabel");
            if (value1 != null)
                value1.setText(String.valueOf(totalProducts));

            // Update Total Value
            JPanel card2 = (JPanel) stats.getComponent(1);
            JLabel value2 = (JLabel) card2.getClientProperty("valueLabel");
            if (value2 != null)
                value2.setText(pesoFmt.format(totalValue));

            // Update Low Stock
            JPanel card3 = (JPanel) stats.getComponent(2);
            JLabel value3 = (JLabel) card3.getClientProperty("valueLabel");
            if (value3 != null)
                value3.setText(String.valueOf(lowStock));

            // Update Categories
            JPanel card4 = (JPanel) stats.getComponent(3);
            JLabel value4 = (JLabel) card4.getClientProperty("valueLabel");
            if (value4 != null)
                value4.setText(String.valueOf(categories));
        }
    }
}