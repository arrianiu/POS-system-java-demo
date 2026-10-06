package src.ui;

import src.dao.SalesDAO;
import src.ui.components.UserInfoPanel;
import src.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SalesHistoryFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final SalesDAO salesDAO = new SalesDAO();
    private JPanel statsPanel;

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

    public SalesHistoryFrame() {
        setTitle("📊 Sales History");
        setSize(1100, 700);
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

        mainPanel.add(createStatsPanel(), BorderLayout.NORTH);

        // Table title
        JPanel tableTitlePanel = new JPanel(new BorderLayout());
        tableTitlePanel.setBackground(DARK_BG);

        JLabel tableTitle = new JLabel("📋 Transaction History");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);

        JLabel infoLabel = new JLabel("Click 'View' to see transaction details");
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

        loadSales();
        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("📊 SALES HISTORY");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);

        UserInfoPanel userInfo = new UserInfoPanel();
        userInfo.setBackground(PANEL_BG);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(DARK_BG);

        JPanel totalSalesCard = createStatCard("💰", "Total Sales", "₱ 0.00", ACCENT_GREEN);
        JPanel transactionsCard = createStatCard("🧾", "Transactions", "0", SLYTHERIN_GREEN);
        JPanel avgSaleCard = createStatCard("📊", "Average Sale", "₱ 0.00", new Color(100, 120, 180));

        statsPanel.add(totalSalesCard);
        statsPanel.add(transactionsCard);
        statsPanel.add(avgSaleCard);

        return statsPanel;
    }

    private JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(SLYTHERIN_SILVER);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueText.setForeground(accentColor);
        valueText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(labelText);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueText);

        card.putClientProperty("valueLabel", valueText);

        return card;
    }

    private JScrollPane createStyledTable() {
        model = new DefaultTableModel(
                new String[] { "Sale ID", "Items", "Total", "Date", "View" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 4; // only View button
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

        // Center align all columns except View button
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Sale ID
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Items
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Total
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Date

        // View button
        table.getColumn("View").setCellRenderer(new ViewButtonRenderer());
        table.getColumn("View").setCellEditor(new ViewButtonEditor());

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Sale ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80); // Items
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Total
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Date
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // View

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

        btnRefresh.addActionListener(e -> loadSales());
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

    private void loadSales() {
        model.setRowCount(0);
        List<String[]> sales = salesDAO.getSalesHistory();

        double totalSales = 0;
        int transactionCount = 0;

        for (String[] row : sales) {
            double total = Double.parseDouble(row[2]);
            totalSales += total;
            transactionCount++;

            model.addRow(new Object[] {
                    row[0], // sale_id
                    row[1], // item count
                    FormatUtil.peso(total), // total
                    row[3], // date
                    "View"
            });
        }

        updateStats(totalSales, transactionCount);
    }

    private void updateStats(double totalSales, int transactionCount) {
        // Update Total Sales
        JPanel card1 = (JPanel) statsPanel.getComponent(0);
        JLabel value1 = (JLabel) card1.getClientProperty("valueLabel");
        if (value1 != null) {
            value1.setText(FormatUtil.peso(totalSales));
        }

        // Update Transactions
        JPanel card2 = (JPanel) statsPanel.getComponent(1);
        JLabel value2 = (JLabel) card2.getClientProperty("valueLabel");
        if (value2 != null) {
            value2.setText(String.valueOf(transactionCount));
        }

        // Update Average Sale
        JPanel card3 = (JPanel) statsPanel.getComponent(2);
        JLabel value3 = (JLabel) card3.getClientProperty("valueLabel");
        if (value3 != null) {
            double avgSale = transactionCount > 0 ? totalSales / transactionCount : 0;
            value3.setText(FormatUtil.peso(avgSale));
        }
    }

    // =========================
    // VIEW BUTTONS
    // =========================
    private class ViewButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ViewButtonRenderer() {
            setText("👁️ View");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setBackground(SLYTHERIN_GREEN);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean s, boolean f, int r, int c) {
            return this;
        }
    }

    private class ViewButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton("👁️ View");
        private int saleId;

        public ViewButtonEditor() {
            super(new JCheckBox());
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setBackground(SLYTHERIN_GREEN);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                new SaleDetailsDialog(SalesHistoryFrame.this, saleId);
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean s, int row, int col) {
            saleId = Integer.parseInt(table.getValueAt(row, 0).toString());
            return button;
        }
    }
}