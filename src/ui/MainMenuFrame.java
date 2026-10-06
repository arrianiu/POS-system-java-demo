package src.ui;

import src.ui.components.UserInfoPanel;
import src.util.UserSession;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MainMenuFrame extends JFrame {

    // Slytherin Color Palette - Elite Edition
    private static final Color DARK_BG = new Color(12, 17, 20);
    private static final Color PANEL_BG = new Color(20, 28, 30);
    private static final Color SLYTHERIN_GREEN = new Color(26, 83, 92);
    private static final Color SLYTHERIN_DARK_GREEN = new Color(15, 50, 55);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color ACCENT_GREEN = new Color(34, 139, 34);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color HOVER_COLOR = new Color(30, 100, 110);
    private static final Color CARD_BG = new Color(25, 35, 38);
    private static final Color CARD_HOVER = new Color(32, 45, 48);

    public MainMenuFrame() {
        setTitle("⚡ Slytherin Command Center");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // ===== TOP HEADER WITH SNAKE PATTERN =====
        add(createEnhancedHeader(), BorderLayout.NORTH);

        // ===== CENTER - MENU CARDS =====
        add(createMenuPanel(), BorderLayout.CENTER);

        // ===== FOOTER WITH SERPENT DIVIDER =====
        add(createEnhancedFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createEnhancedHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, SLYTHERIN_DARK_GREEN,
                        getWidth(), 0, PANEL_BG);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw subtle serpent pattern
                drawHeaderPattern(g2d);
            }
        };

        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)));

        // Left side - Title with snake icon
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        // Custom snake icon panel
        JPanel snakeIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw sleek serpent S
                g2d.setColor(SLYTHERIN_GREEN);
                g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D serpent = new Path2D.Double();
                serpent.moveTo(35, 10);
                serpent.curveTo(10, 15, 8, 35, 20, 40);
                serpent.curveTo(32, 45, 30, 55, 15, 60);
                g2d.draw(serpent);

                // Eye
                g2d.setColor(ACCENT_GREEN);
                g2d.fillOval(18, 38, 6, 6);
            }
        };
        snakeIcon.setOpaque(false);
        snakeIcon.setPreferredSize(new Dimension(50, 70));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("COMMAND CENTER");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(SLYTHERIN_SILVER);

        JLabel subtitleLabel = new JLabel("\"Those who are cunning shall use any means to achieve their ends\"");
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 12));
        subtitleLabel.setForeground(SLYTHERIN_GREEN);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(subtitleLabel);

        titlePanel.add(snakeIcon);
        titlePanel.add(textPanel);

        // Right side - User Info
        UserInfoPanel userInfoPanel = new UserInfoPanel();
        userInfoPanel.setOpaque(false);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(userInfoPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void drawHeaderPattern(Graphics2D g2d) {
        g2d.setColor(new Color(26, 83, 92, 20));
        g2d.setStroke(new BasicStroke(1.5f));

        // Elegant serpent curves
        for (int i = 0; i < 3; i++) {
            Path2D pattern = new Path2D.Double();
            int xOffset = i * 150;
            pattern.moveTo(xOffset, 40);
            pattern.curveTo(xOffset + 50, 20, xOffset + 100, 60, xOffset + 150, 40);
            g2d.draw(pattern);
        }
    }

    private JPanel createMenuPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        String role = UserSession.getRole();

        if ("admin".equalsIgnoreCase(role)) {
            mainPanel.setLayout(new GridLayout(3, 3, 20, 20));

            mainPanel.add(createEliteCard("📦", "Products", "Manage your arsenal", "Inventory Control",
                    e -> new ProductFrame()));
            mainPanel.add(
                    createEliteCard("🧾", "Checkout", "Process transactions", "Point of Sale", e -> new SalesFrame()));
            mainPanel.add(createEliteCard("📊", "Sales History", "Transaction records", "Financial Logs",
                    e -> new SalesHistoryFrame()));

            mainPanel.add(createEliteCard("📈", "Sales Report", "Revenue analysis", "Performance Metrics",
                    e -> new SalesReportFrame()));
            mainPanel.add(createEliteCard("📦", "Product Report", "Inventory insights", "Stock Analytics",
                    e -> new ProductReportFrame()));
            mainPanel.add(createEliteCard("📊", "Inventory", "Stock overview", "Supply Chain",
                    e -> new InventoryReportFrame()));

            mainPanel.add(createEliteCard("📂", "Categories", "Product taxonomy", "Classification",
                    e -> new CategoryFrame()));
            mainPanel.add(createEliteCard("🏷", "Brands", "Brand management", "Label Control", e -> new BrandFrame()));
            mainPanel.add(
                    createEliteCard("🚚", "Suppliers", "Vendor network", "Supply Partners", e -> new SupplierFrame()));

        } else if ("cashier".equalsIgnoreCase(role)) {
            mainPanel.setLayout(new GridLayout(1, 2, 20, 20));

            mainPanel.add(createEliteCard("🧾", "Checkout", "Process sales", "Point of Sale", e -> new SalesFrame()));
            mainPanel.add(createEliteCard("📅", "Today's Sales", "Daily records", "Shift Report",
                    e -> new CashierSalesHistoryFrame()));

        } else {
            showStyledDialog("Unauthorized access detected.", "Security Alert", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        return mainPanel;
    }

    private JPanel createEliteCard(String icon, String title, String description, String badge, ActionListener action) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Subtle gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, getBackground(),
                        0, getHeight(), getBackground().darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Serpent accent line
                g2d.setColor(new Color(26, 83, 92, 100));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine(20, getHeight() - 15, getWidth() - 20, getHeight() - 15);
            }
        };

        card.setLayout(new BorderLayout(15, 15));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon section
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Content section
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(SLYTHERIN_SILVER);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badgeLabel = new JLabel("⬡ " + badge);
        badgeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        badgeLabel.setForeground(ACCENT_GREEN);
        badgeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(badgeLabel);

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(contentPanel, BorderLayout.CENTER);

        card.add(topPanel, BorderLayout.CENTER);

        // Hover effects with smooth transitions
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN, 2),
                        BorderFactory.createEmptyBorder(24, 24, 24, 24)));
                titleLabel.setForeground(SLYTHERIN_GREEN.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)));
                titleLabel.setForeground(TEXT_COLOR);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Flash effect
                card.setBackground(SLYTHERIN_GREEN);
                Timer timer = new Timer(100, evt -> {
                    card.setBackground(CARD_HOVER);
                    action.actionPerformed(null);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        return card;
    }

    private JPanel createEnhancedFooter() {
        JPanel footerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw serpent divider
                g2d.setColor(SLYTHERIN_GREEN);
                g2d.setStroke(new BasicStroke(2f));

                Path2D serpent = new Path2D.Double();
                int y = 2;
                for (int x = 0; x < getWidth(); x += 30) {
                    if (x == 0) {
                        serpent.moveTo(x, y + 10);
                    } else {
                        serpent.curveTo(x - 15, y, x - 15, y + 20, x, y + 10);
                    }
                }
                g2d.draw(serpent);
            }
        };

        footerPanel.setBackground(PANEL_BG);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(18, 40, 18, 40));
        footerPanel.setLayout(new BorderLayout());

        String role = UserSession.getRole();

        JPanel leftFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftFooter.setOpaque(false);

        JLabel statusLabel = new JLabel("⬡ Active Session");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(ACCENT_GREEN);

        JLabel userLabel = new JLabel(UserSession.getUsername() + " • " + role.toUpperCase());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(SLYTHERIN_SILVER);

        leftFooter.add(statusLabel);
        leftFooter.add(new JLabel("|") {
            {
                setForeground(SLYTHERIN_GREEN.darker());
            }
        });
        leftFooter.add(userLabel);

        JPanel rightFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightFooter.setOpaque(false);

        JLabel versionLabel = new JLabel("Unating");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(SLYTHERIN_SILVER.darker());

        JButton btnLogout = createEliteButton("LOGOUT");
        btnLogout.addActionListener(e -> logout());

        rightFooter.add(versionLabel);
        rightFooter.add(btnLogout);

        footerPanel.add(leftFooter, BorderLayout.WEST);
        footerPanel.add(rightFooter, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createEliteButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SLYTHERIN_DARK_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SLYTHERIN_DARK_GREEN);
            }
        });

        return btn;
    }

    private void logout() {
        // Custom styled confirmation
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "End your session and return to the shadows?",
                "⚡ Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.logout();
            dispose();
            new LoginFrame();
        }
    }

    private void showStyledDialog(String message, String title, int type) {
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}