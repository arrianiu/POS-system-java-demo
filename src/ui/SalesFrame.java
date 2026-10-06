package src.ui;

import src.dao.*;
import src.model.*;
import src.ui.components.UserInfoPanel;
import src.util.FormatUtil;
import src.util.DialogUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class SalesFrame extends JFrame {

    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JButton btnCheckout;

    private JComboBox<String> cmbCategory;
    private JPanel productGrid;

    private final ProductDAO productDAO = new ProductDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private final Map<Integer, String> categoryMap = new HashMap<>();

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

    public SalesFrame() {
        setTitle("⚡ Point of Sale System");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // Header
        add(createStyledHeader(), BorderLayout.NORTH);

        // Main content
        JPanel main = new JPanel(new GridLayout(1, 2, 20, 0));
        main.setBackground(DARK_BG);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(main, BorderLayout.CENTER);

        main.add(buildProductPanel());
        main.add(buildCartPanel());

        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("⚡ POINT OF SALE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SLYTHERIN_GREEN);

        UserInfoPanel userInfo = new UserInfoPanel();
        userInfo.setBackground(PANEL_BG);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    // ================= LEFT: PRODUCTS =================

    private JPanel buildProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_BG);

        // Header with category filter
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(DARK_BG);

        JLabel lblProducts = new JLabel("📦 Products");
        lblProducts.setForeground(TEXT_COLOR);
        lblProducts.setFont(new Font("Segoe UI", Font.BOLD, 20));

        cmbCategory = createStyledComboBox();
        cmbCategory.addItem("All Categories");

        for (Category c : categoryDAO.findAll()) {
            categoryMap.put(c.getId(), c.getName());
            cmbCategory.addItem(c.getName());
        }

        cmbCategory.addActionListener(e -> loadProducts());

        topPanel.add(lblProducts, BorderLayout.WEST);
        topPanel.add(cmbCategory, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Product grid with scroll
        productGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        productGrid.setBackground(DARK_BG);
        productGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(productGrid);
        scroll.setBackground(DARK_BG);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(DARK_BG);
        panel.add(scroll, BorderLayout.CENTER);

        loadProducts();
        return panel;
    }

    private void loadProducts() {
        productGrid.removeAll();
        String selected = (String) cmbCategory.getSelectedItem();

        for (Product p : productDAO.getAllProducts()) {
            if (!"All Categories".equals(selected)) {
                String cat = categoryMap.get(p.getCategoryId());
                if (!selected.equals(cat))
                    continue;
            }

            productGrid.add(createProductCard(p));
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1, true),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(FormatUtil.peso(p.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(ACCENT_GREEN);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int stock = inventoryDAO.getCurrentStock(p.getId());
        JLabel stockLabel = new JLabel("Stock: " + stock);
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockLabel.setForeground(stock > 10 ? SLYTHERIN_SILVER : new Color(220, 100, 100));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(nameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(priceLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(stockLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_COLOR);
                contentPanel.setBackground(HOVER_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN, 2, true),
                        BorderFactory.createEmptyBorder(20, 15, 20, 15)));
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                contentPanel.setBackground(CARD_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN.darker(), 1, true),
                        BorderFactory.createEmptyBorder(20, 15, 20, 15)));
            }

            public void mouseClicked(MouseEvent e) {
                addToCart(p);
            }
        });

        return card;
    }

    // ================= RIGHT: CART =================

    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_BG);

        JLabel lblCart = new JLabel("🛒 Shopping Cart");
        lblCart.setForeground(TEXT_COLOR);
        lblCart.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(lblCart, BorderLayout.NORTH);

        // Cart table
        cartModel = new DefaultTableModel(
                new String[] { "ID", "Product", "Price", "Qty", "Subtotal", "-", "+" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return c >= 5;
            }
        };

        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(40);
        cartTable.setBackground(CARD_BG);
        cartTable.setForeground(TEXT_COLOR);
        cartTable.setGridColor(SLYTHERIN_GREEN.darker());
        cartTable.setSelectionBackground(HOVER_COLOR);
        cartTable.setSelectionForeground(Color.WHITE);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Hide ID column
        cartTable.getColumnModel().getColumn(0).setMinWidth(0);
        cartTable.getColumnModel().getColumn(0).setMaxWidth(0);

        // Style header
        cartTable.getTableHeader().setBackground(TABLE_HEADER);
        cartTable.getTableHeader().setForeground(SLYTHERIN_SILVER);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.getTableHeader().setBorder(new LineBorder(SLYTHERIN_GREEN));

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        cartTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Button columns
        cartTable.getColumn("-").setCellRenderer(new ButtonRenderer("-"));
        cartTable.getColumn("+").setCellRenderer(new ButtonRenderer("+"));
        cartTable.getColumn("-").setCellEditor(new QtyEditor(false));
        cartTable.getColumn("+").setCellEditor(new QtyEditor(true));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with total and checkout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(DARK_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        lblTotal = new JLabel("TOTAL: ₱ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(ACCENT_GREEN);
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCheckout = createStyledButton("💳 CHECKOUT");
        btnCheckout.setMaximumSize(new Dimension(500, 60));
        btnCheckout.addActionListener(e -> showReceiptPreview());
        btnCheckout.setEnabled(false);

        bottomPanel.add(lblTotal);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bottomPanel.add(btnCheckout);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> cmb = new JComboBox<>();
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb.setBackground(CARD_BG);
        cmb.setForeground(TEXT_COLOR);
        cmb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return cmb;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SLYTHERIN_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled())
                    btn.setBackground(HOVER_COLOR);
            }

            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled())
                    btn.setBackground(SLYTHERIN_GREEN);
            }
        });

        return btn;
    }

    // ================= CART LOGIC =================

    private void addToCart(Product p) {
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if ((int) cartModel.getValueAt(i, 0) == p.getId()) {
                int qty = (int) cartModel.getValueAt(i, 3) + 1;
                if (qty > inventoryDAO.getCurrentStock(p.getId())) {
                    DialogUtil.warn("Insufficient stock.");
                    return;
                }

                cartModel.setValueAt(qty, i, 3);
                cartModel.setValueAt(qty * p.getPrice(), i, 4);
                updateTotal();
                return;
            }
        }

        cartModel.addRow(new Object[] {
                p.getId(), p.getName(), FormatUtil.peso(p.getPrice()), 1, FormatUtil.peso(p.getPrice()), "-", "+"
        });

        updateTotal();
    }

    private void updateTotal() {
        double sum = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            Object val = cartModel.getValueAt(i, 4);
            if (val instanceof Double) {
                sum += (Double) val;
            } else if (val instanceof String) {
                String str = (String) val;
                sum += Double.parseDouble(str.replace("₱", "").replace(",", "").trim());
            }
        }

        lblTotal.setText("TOTAL: " + FormatUtil.peso(sum));
        btnCheckout.setEnabled(cartModel.getRowCount() > 0);
    }

    // ================= RECEIPT + CHECKOUT =================

    private void showReceiptPreview() {
        StringBuilder receipt = new StringBuilder("========= RECEIPT =========\n\n");
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            receipt.append(cartModel.getValueAt(i, 1))
                    .append(" x").append(cartModel.getValueAt(i, 3))
                    .append(" = ").append(cartModel.getValueAt(i, 4))
                    .append("\n");
        }

        receipt.append("\n").append(lblTotal.getText());

        if (JOptionPane.showConfirmDialog(
                this, receipt.toString(), "Confirm Checkout",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            checkout();
        }
    }

    private void checkout() {
        Map<Integer, Integer> cart = new HashMap<>();

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int productId = (int) cartModel.getValueAt(i, 0);
            int qty = (int) cartModel.getValueAt(i, 3);
            cart.put(productId, qty);
        }

        boolean success = salesDAO.processTransaction(cart);

        if (success) {
            cartModel.setRowCount(0);
            lblTotal.setText("TOTAL: ₱ 0.00");
            btnCheckout.setEnabled(false);
            DialogUtil.info("Checkout successful!");
            loadProducts(); // Refresh stock counts
        } else {
            DialogUtil.warn("Checkout failed. Please check stock or database.");
        }

        updateTotal();
    }

    // ================= BUTTON HELPERS =================

    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setFocusPainted(false);
            setBackground(SLYTHERIN_GREEN);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorderPainted(false);
        }

        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {
            return this;
        }
    }

    private class QtyEditor extends DefaultCellEditor {
        private final boolean plus;

        public QtyEditor(boolean plus) {
            super(new JCheckBox());
            this.plus = plus;
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int col) {

            JButton btn = new JButton(plus ? "+" : "-");
            btn.setBackground(SLYTHERIN_GREEN);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setBorderPainted(false);

            btn.addActionListener(e -> {
                int qty = (int) cartModel.getValueAt(row, 3);
                qty += plus ? 1 : -1;

                if (qty <= 0) {
                    cartModel.removeRow(row);
                } else {
                    Object priceObj = cartModel.getValueAt(row, 2);
                    double price;
                    if (priceObj instanceof String) {
                        price = Double.parseDouble(((String) priceObj).replace("₱", "").replace(",", "").trim());
                    } else {
                        price = (Double) priceObj;
                    }

                    cartModel.setValueAt(qty, row, 3);
                    cartModel.setValueAt(FormatUtil.peso(qty * price), row, 4);
                }

                updateTotal();
                loadProducts(); // Refresh to show updated stock
                fireEditingStopped();
            });

            return btn;
        }
    }
}