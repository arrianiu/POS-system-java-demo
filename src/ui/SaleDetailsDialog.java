package src.ui;

import src.dao.SalesDAO;
import src.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SaleDetailsDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private final SalesDAO salesDAO = new SalesDAO();

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

    public SaleDetailsDialog(JFrame parent, int saleId) {
        super(parent, "Sale Details", true);

        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // Header
        add(createStyledHeader(saleId), BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        // Table title
        JLabel tableTitle = new JLabel("🧾 Items Purchased");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(TEXT_COLOR);

        mainPanel.add(tableTitle, BorderLayout.NORTH);
        mainPanel.add(createStyledTable(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadDetails(saleId);
        setVisible(true);
    }

    private JPanel createStyledHeader(int saleId) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel titleLabel = new JLabel("🧾 SALE DETAILS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel saleIdLabel = new JLabel("Transaction ID: #" + saleId);
        saleIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saleIdLabel.setForeground(SLYTHERIN_SILVER);
        saleIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createRigidArea(new Dimension(0, 5)));
        header.add(saleIdLabel);

        return header;
    }

    private JScrollPane createStyledTable() {
        model = new DefaultTableModel(
                new String[] { "Product", "Qty", "Price", "Subtotal" }, 0) {
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

        // Center align Qty column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Right align Price and Subtotal columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300); // Product
        table.getColumnModel().getColumn(1).setPreferredWidth(80); // Qty
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Price
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Subtotal

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(DARK_BG);

        // Total panel
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setBackground(PANEL_BG);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN, 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel totalLabel = new JLabel("TOTAL AMOUNT");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalLabel.setForeground(SLYTHERIN_SILVER);
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel totalValue = new JLabel("₱ 0.00");
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalValue.setForeground(ACCENT_GREEN);
        totalValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalPanel.add(totalLabel);
        totalPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        totalPanel.add(totalValue);

        // Store reference for updating
        totalPanel.putClientProperty("totalLabel", totalValue);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(DARK_BG);

        JButton btnPrint = createStyledButton("🖨️ Print Receipt", SLYTHERIN_GREEN);
        JButton btnClose = createStyledButton("Close", SLYTHERIN_SILVER.darker());

        btnPrint.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Print feature coming soon!",
                    "Print",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);

        JPanel footerContainer = new JPanel(new BorderLayout(0, 15));
        footerContainer.setBackground(DARK_BG);
        footerContainer.add(totalPanel, BorderLayout.NORTH);
        footerContainer.add(buttonPanel, BorderLayout.SOUTH);

        return footerContainer;
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

    private void loadDetails(int saleId) {
        model.setRowCount(0);
        List<String[]> items = salesDAO.getSaleDetails(saleId);

        double total = 0;

        for (String[] row : items) {
            double subtotal = Double.parseDouble(row[3]);
            total += subtotal;

            model.addRow(new Object[] {
                    row[0], // product
                    Integer.parseInt(row[1]), // qty
                    FormatUtil.peso(Double.parseDouble(row[2])), // price
                    FormatUtil.peso(subtotal) // subtotal
            });
        }

        // Update total in footer
        updateTotal(total);
    }

    private void updateTotal(double total) {
        Container contentPane = getContentPane();
        if (contentPane.getComponentCount() > 1) {
            Component mainComp = contentPane.getComponent(1);
            if (mainComp instanceof JPanel) {
                JPanel mainPanel = (JPanel) mainComp;
                if (mainPanel.getComponentCount() > 2) {
                    Component footerComp = mainPanel.getComponent(2);
                    if (footerComp instanceof JPanel) {
                        JPanel footerContainer = (JPanel) footerComp;
                        if (footerContainer.getComponentCount() > 0) {
                            Component totalPanelComp = footerContainer.getComponent(0);
                            if (totalPanelComp instanceof JPanel) {
                                JPanel totalPanel = (JPanel) totalPanelComp;
                                JLabel totalLabel = (JLabel) totalPanel.getClientProperty("totalLabel");
                                if (totalLabel != null) {
                                    totalLabel.setText(FormatUtil.peso(total));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}