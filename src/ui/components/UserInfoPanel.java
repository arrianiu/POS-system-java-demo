/* package src.ui.components;

import src.ui.LoginFrame;
import src.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class UserInfoPanel extends JPanel {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    // Slytherin Color Palette
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LOGOUT_RED = new Color(180, 60, 60);
    private static final Color LOGOUT_HOVER = new Color(200, 80, 80);

    public UserInfoPanel() {

        setLayout(new BorderLayout(15, 0));
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        String shiftTime = UserSession.getLoginTime() != null
                ? UserSession.getLoginTime().format(TIME_FMT)
                : "--:--";

        JLabel lblUser = new JLabel(
                "⬡ " + UserSession.getUsername()
                        + " | " + UserSession.getRole().toUpperCase()
                        + " | Shift: " + shiftTime);

        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(SLYTHERIN_SILVER);

        JButton btnLogout = createLogoutButton();

        add(lblUser, BorderLayout.WEST);
        add(btnLogout, BorderLayout.EAST);
    }

    private JButton createLogoutButton() {
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(LOGOUT_RED);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(LOGOUT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(LOGOUT_RED);
            }
        });

        btnLogout.addActionListener(e -> logout());

        return btnLogout;
    }

    private void logout() {

        // Custom styled dialog
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?\nThis will end your shift.",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        // End session
        UserSession.logout();

        // Close all windows
        for (Window w : Window.getWindows()) {
            w.dispose();
        }

        // Back to login
        new LoginFrame();
    }
} */

package src.ui.components;

import src.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class UserInfoPanel extends JPanel {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    // Slytherin Color Palette
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color BACK_BUTTON_BG = new Color(40, 60, 65);
    private static final Color BACK_BUTTON_HOVER = new Color(50, 80, 85);

    public UserInfoPanel() {
        this(false);
    }

    public UserInfoPanel(boolean showBackButton) {
        setLayout(new BorderLayout(15, 0));
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        String shiftTime = UserSession.getLoginTime() != null
                ? UserSession.getLoginTime().format(TIME_FMT)
                : "--:--";

        JLabel lblUser = new JLabel(
                "⬡ " + UserSession.getUsername()
                        + " | " + UserSession.getRole().toUpperCase()
                        + " | Shift: " + shiftTime);

        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(SLYTHERIN_SILVER);

        add(lblUser, BorderLayout.WEST);

        if (showBackButton) {
            JButton btnBack = createBackButton();
            add(btnBack, BorderLayout.EAST);
        }
    }

    private JButton createBackButton() {
        JButton btnBack = new JButton("← Back to Menu");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(BACK_BUTTON_BG);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnBack.setBackground(BACK_BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnBack.setBackground(BACK_BUTTON_BG);
            }
        });

        btnBack.addActionListener(e -> goBackToMainMenu());

        return btnBack;
    }

    private void goBackToMainMenu() {
        // Close current window
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}