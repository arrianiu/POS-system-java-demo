package src.ui;

import src.dao.SalesDAO;
import src.util.UserSession;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

public class CashierSalesHistoryFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final SalesDAO salesDAO = new SalesDAO();
    private JLabel lblStats;

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

    public CashierSalesHistoryFrame() {
        setTitle("📅 Today's Sales");
        setSize(900, 650);
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
        mainPanel.add(createStyledTable(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadTodaySales();
        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PANEL_BG);

        JLabel titleLabel = new JLabel("📅 TODAY'S SALES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel shiftLabel = new JLabel("Shift started: " +
                UserSession.getLoginTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")));
        shiftLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shiftLabel.setForeground(SLYTHERIN_SILVER);
        shiftLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(shiftLabel);

        JLabel userLabel = new JLabel("Cashier: " + UserSession.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(TEXT_COLOR);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(DARK_BG);

        lblStats = new JLabel("Loading...");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStats.setForeground(TEXT_COLOR);

        // Create stat cards
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

        // Store reference to update later
        card.putClientProperty("valueLabel", valueText);

        return card;
    }

    private JScrollPane createStyledTable() {
        model = new DefaultTableModel(
                new String[] { "Sale ID", "Items", "Total", "Time" }, 0) {
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
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Style header
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(SLYTHERIN_SILVER);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(new LineBorder(SLYTHERIN_GREEN));

        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Right align total
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setBackground(DARK_BG);

        JButton btnRefresh = createStyledButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> loadTodaySales());

        JButton btnClose = createStyledButton("Close");
        btnClose.setBackground(SLYTHERIN_SILVER.darker());
        btnClose.addActionListener(e -> dispose());

        footer.add(btnRefresh);
        footer.add(btnClose);

        return footer;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SLYTHERIN_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Color hoverColor = btn.getBackground().brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(SLYTHERIN_GREEN);
            }
        });

        return btn;
    }

    private void loadTodaySales() {
        model.setRowCount(0);

        double totalSales = 0;
        int transactionCount = 0;

        for (String[] row : salesDAO.getShiftSales(UserSession.getLoginTime())) {
            model.addRow(new Object[] {
                    row[0], // sale_id
                    row[1], // item count
                    row[2], // total
                    row[3] // time
            });

            // Calculate stats
            try {
                String totalStr = row[2].replace("₱", "").replace(",", "").trim();
                totalSales += Double.parseDouble(totalStr);
                transactionCount++;
            } catch (Exception e) {
                // Skip if parsing fails
            }
        }

        // Update stat cards
        updateStatCards(totalSales, transactionCount);
    }

    private JPanel statsPanel;

    private void updateStatCards(double totalSales, int transactionCount) {
        // Update Total Sales
        JPanel card1 = (JPanel) statsPanel.getComponent(0);
        JLabel value1 = (JLabel) card1.getClientProperty("valueLabel");
        if (value1 != null) {
            value1.setText(String.format("₱ %.2f", totalSales));
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
            value3.setText(String.format("₱ %.2f", avgSale));
        }
    }
}