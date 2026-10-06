package src.ui;

import src.db.DatabaseConnection;
import src.util.UserSession;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    // Slytherin Color Palette
    private static final Color DARK_BG = new Color(15, 20, 25);
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_GREEN = new Color(26, 83, 92);
    private static final Color SLYTHERIN_DARK_GREEN = new Color(18, 58, 65);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color ACCENT_GREEN = new Color(34, 139, 34);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color HOVER_COLOR = new Color(30, 100, 110);

    public LoginFrame() {
        setTitle("E-Commerce System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout());

        // Main container with two panels
        JPanel containerPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        containerPanel.setBackground(DARK_BG);

        // LEFT PANEL - Branding with Snake
        JPanel leftPanel = createBrandingPanel();

        // RIGHT PANEL - Login Form
        JPanel rightPanel = createLoginPanel();

        containerPanel.add(leftPanel);
        containerPanel.add(rightPanel);

        add(containerPanel, BorderLayout.CENTER);

        // Enter key support
        txtPass.addActionListener(e -> login());

        setVisible(true);
    }

    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, SLYTHERIN_DARK_GREEN,
                        0, getHeight(), DARK_BG);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw decorative serpent pattern
                drawSerpentPattern(g2d);
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));

        // Snake icon (custom)
        JPanel snakePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw stylized snake
                g2d.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(SLYTHERIN_GREEN);

                // Snake body (curved path)
                Path2D snake = new Path2D.Double();
                snake.moveTo(w * 0.3, h * 0.2);
                snake.curveTo(w * 0.5, h * 0.1, w * 0.7, h * 0.3, w * 0.6, h * 0.5);
                snake.curveTo(w * 0.5, h * 0.7, w * 0.3, h * 0.6, w * 0.4, h * 0.8);
                g2d.draw(snake);

                // Snake head
                g2d.setColor(SLYTHERIN_GREEN.brighter());
                g2d.fillOval((int) (w * 0.25), (int) (h * 0.15), 40, 40);

                // Eye
                g2d.setColor(ACCENT_GREEN);
                g2d.fillOval((int) (w * 0.28), (int) (h * 0.18), 12, 12);
            }
        };
        snakePanel.setOpaque(false);
        snakePanel.setMaximumSize(new Dimension(300, 200));
        snakePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("OOP");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 52));
        titleLabel.setForeground(SLYTHERIN_SILVER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("E-COMMERCE SYSTEM");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(SLYTHERIN_SILVER.darker());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quote
        JLabel quoteLabel = new JLabel(
                "<html><center>\"Ecommerce Testing<br>(java & SQL)\"</center></html>");
        quoteLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        quoteLabel.setForeground(SLYTHERIN_GREEN);
        quoteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(snakePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(quoteLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void drawSerpentPattern(Graphics2D g2d) {
        g2d.setColor(new Color(26, 83, 92, 30));
        g2d.setStroke(new BasicStroke(2f));

        // Draw subtle snake-like curves in background
        for (int i = 0; i < 5; i++) {
            Path2D pattern = new Path2D.Double();
            int yOffset = i * 120;
            pattern.moveTo(0, 50 + yOffset);
            pattern.curveTo(100, 80 + yOffset, 150, 20 + yOffset, 250, 50 + yOffset);
            pattern.curveTo(350, 80 + yOffset, 400, 20 + yOffset, 500, 50 + yOffset);
            g2d.draw(pattern);
        }
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(welcomeLabel, gbc);

        JLabel instructionLabel = new JLabel("Login to your account");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(SLYTHERIN_SILVER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(instructionLabel, gbc);

        // Username
        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userLabel.setForeground(SLYTHERIN_SILVER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(userLabel, gbc);

        txtUser = createStyledTextField();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.ipady = 15;
        panel.add(txtUser, gbc);

        // Password
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        passLabel.setForeground(SLYTHERIN_SILVER);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.ipady = 0;
        panel.add(passLabel, gbc);

        txtPass = createStyledPasswordField();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.ipady = 15;
        panel.add(txtPass, gbc);

        // Forgot password
        JLabel forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(ACCENT_GREEN);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showStyledDialog(
                        "Please contact your administrator to reset your password.",
                        "Password Reset",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setForeground(ACCENT_GREEN.brighter());
            }

            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setForeground(ACCENT_GREEN);
            }
        });
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.ipady = 0;
        panel.add(forgotPasswordLabel, gbc);

        // Login button
        btnLogin = createStyledButton("LOGIN");
        btnLogin.addActionListener(e -> login());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.ipady = 20;
        panel.add(btnLogin, gbc);

        // Footer
        JLabel footerLabel = new JLabel(
                "<html><center>OOP SUBJECT • MySQL • PROJECT<br><small>ARRIANI JENN UNATING</small></center></html>");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(SLYTHERIN_SILVER.darker());
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.ipady = 0;
        panel.add(footerLabel, gbc);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(PANEL_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(SLYTHERIN_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN, 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }

            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(PANEL_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(SLYTHERIN_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN, 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }

            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });

        return field;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SLYTHERIN_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(HOVER_COLOR);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(SLYTHERIN_GREEN);
            }
        });

        return btn;
    }

    private void login() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStyledDialog(
                    "Username and password are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("AUTHENTICATING...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT password, role FROM users WHERE username=? LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                showStyledDialog(
                        "User does not exist.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                txtPass.setText("");
                return;
            }

            String dbPass = rs.getString("password");

            if (!password.equals(dbPass)) {
                showStyledDialog(
                        "Incorrect password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                txtPass.setText("");
                return;
            }

            String role = rs.getString("role");
            UserSession.startSession(username, role);

            showStyledDialog(
                    "Welcome, " + username + " (" + role + ")",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
            new MainMenuFrame().setVisible(true);

        } catch (SQLException e) {
            showStyledDialog(
                    "Database error occurred.",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            btnLogin.setEnabled(true);
            btnLogin.setText("LOGIN");
        }
    }

    private void showStyledDialog(String message, String title, int type) {
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}