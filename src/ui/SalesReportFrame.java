package src.ui;

import src.dao.SalesDAO;
import src.ui.components.UserInfoPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class SalesReportFrame extends JFrame {

    private final SalesDAO salesDAO = new SalesDAO();
    private final NumberFormat pesoFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH"));

    private JComboBox<String> cboPeriod;
    private JComboBox<String> cboValue;

    private JLabel lblRevenue;
    private JLabel lblTransactions;
    private JLabel lblTopProduct;

    // Slytherin Color Palette
    private static final Color DARK_BG = new Color(15, 20, 25);
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_GREEN = new Color(26, 83, 92);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color ACCENT_GREEN = new Color(34, 139, 34);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color HOVER_COLOR = new Color(30, 100, 110);
    private static final Color CARD_BG = new Color(30, 40, 42);

    public SalesReportFrame() {
        setTitle("📈 Sales Report");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // Header
        add(createStyledHeader(), BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        mainPanel.add(createControlPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(createSummaryPanel());

        add(mainPanel, BorderLayout.CENTER);

        // Events
        cboPeriod.addActionListener(e -> populateValues());

        populateValues();
        loadReport();

        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("📈 SALES REPORT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);

        UserInfoPanel userInfo = new UserInfoPanel();
        userInfo.setBackground(PANEL_BG);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(CARD_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN, 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));
        controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel controlTitle = new JLabel("📊 Generate Report");
        controlTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        controlTitle.setForeground(TEXT_COLOR);
        controlTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Select period and timeframe to view sales data");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(SLYTHERIN_SILVER);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Period and Value selectors
        JPanel selectorPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        selectorPanel.setBackground(CARD_BG);
        selectorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        selectorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboPeriod = createStyledComboBox();
        cboPeriod.addItem("Day");
        cboPeriod.addItem("Month");
        cboPeriod.addItem("Year");

        cboValue = createStyledComboBox();

        selectorPanel.add(createFieldPanel("Period", cboPeriod));
        selectorPanel.add(createFieldPanel("Timeframe", cboValue));

        // Generate button
        JButton btnGenerate = createStyledButton("📊 Generate Report", SLYTHERIN_GREEN);
        btnGenerate.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGenerate.setMaximumSize(new Dimension(250, 50));
        btnGenerate.addActionListener(e -> loadReport());

        controlPanel.add(controlTitle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        controlPanel.add(subtitle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlPanel.add(selectorPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlPanel.add(btnGenerate);

        return controlPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(DARK_BG);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel summaryTitle = new JLabel("📋 Report Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        summaryTitle.setForeground(TEXT_COLOR);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Stat cards
        JPanel cardsPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        cardsPanel.setBackground(DARK_BG);
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblRevenue = new JLabel("₱ 0.00");
        lblTransactions = new JLabel("0");
        lblTopProduct = new JLabel("N/A");

        cardsPanel.add(createStatCard("💰", "Total Revenue", lblRevenue, ACCENT_GREEN));
        cardsPanel.add(createStatCard("🧾", "Total Transactions", lblTransactions, SLYTHERIN_GREEN));
        cardsPanel.add(createStatCard("🏆", "Top-Selling Product", lblTopProduct, new Color(220, 180, 60)));

        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(cardsPanel);

        return summaryPanel;
    }

    private JPanel createStatCard(String icon, String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        // Left side - icon and label
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(CARD_BG);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(SLYTHERIN_SILVER);

        leftPanel.add(iconLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        leftPanel.add(labelText);

        // Right side - value
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(valueLabel, BorderLayout.EAST);

        return card;
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(SLYTHERIN_SILVER);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);

        return panel;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> cmb = new JComboBox<>();
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb.setBackground(DARK_BG);
        cmb.setForeground(TEXT_COLOR);
        cmb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return cmb;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        Color hoverColor = HOVER_COLOR;
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

    private void populateValues() {
        cboValue.removeAllItems();
        String period = cboPeriod.getSelectedItem().toString();

        if (period.equals("Day")) {
            cboValue.addItem(LocalDate.now().toString());
        } else if (period.equals("Month")) {
            for (int m = 1; m <= 12; m++) {
                cboValue.addItem(
                        java.time.Month.of(m)
                                .getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH));
            }
        } else {
            int year = LocalDate.now().getYear();
            for (int y = year; y >= year - 5; y--) {
                cboValue.addItem(String.valueOf(y));
            }
        }
    }

    private void loadReport() {
        String period = cboPeriod.getSelectedItem().toString();

        double revenue = 0;
        int transactions = 0;
        String topProduct = "N/A";

        if (period.equals("Day")) {
            revenue = salesDAO.getTodayRevenue();
            transactions = salesDAO.getTotalTransactions();
            topProduct = salesDAO.getTopSellingProduct();

        } else if (period.equals("Month")) {
            int month = cboValue.getSelectedIndex() + 1;
            int year = LocalDate.now().getYear();

            revenue = salesDAO.getMonthlyRevenue(year, month);
            transactions = salesDAO.getMonthlyTransactions(year, month);
            topProduct = salesDAO.getTopSellingProductByMonth(year, month);

        } else if (period.equals("Year")) {
            int year = Integer.parseInt(cboValue.getSelectedItem().toString());

            revenue = salesDAO.getYearlyRevenue(year);
            transactions = salesDAO.getYearlyTransactions(year);
            topProduct = salesDAO.getTopSellingProductByYear(year);

            if (transactions == 0) {
                topProduct = "N/A";
            }
        }

        lblRevenue.setText(pesoFmt.format(revenue));
        lblTransactions.setText(String.valueOf(transactions));
        lblTopProduct.setText(topProduct);
    }
}