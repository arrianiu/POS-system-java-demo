package src.ui;

import src.dao.*;
import src.model.*;
import src.util.FormatUtil;
import src.util.DialogUtil;
import src.util.ValidationUtil;
import src.ui.components.UserInfoPanel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ProductFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtName, txtUnit, txtQty;
    private JTextField txtCost, txtMarkup, txtPrice;
    private JTextField txtSearchName;

    private JComboBox<Category> cmbCategory, cbCategory;
    private JComboBox<Brand> cmbBrand, cbBrand;
    private JComboBox<Supplier> cmbSupplier, cbSupplier;

    private JLabel lblDate;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    private final Map<Integer, Category> categoryMap = new HashMap<>();
    private final Map<Integer, Brand> brandMap = new HashMap<>();
    private final Map<Integer, Supplier> supplierMap = new HashMap<>();

    // Slytherin Color Palette - Soft Theme
    private static final Color DARK_BG = new Color(15, 20, 25);
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_GREEN = new Color(26, 83, 92);
    private static final Color SLYTHERIN_SILVER = new Color(170, 183, 184);
    private static final Color ACCENT_GREEN = new Color(34, 139, 34);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color CARD_BG = new Color(30, 40, 42);
    private static final Color TABLE_HEADER = new Color(20, 30, 32);

    public ProductFrame() {
        setTitle("Product Management");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(DARK_BG);

        // Header
        add(createStyledHeader(), BorderLayout.NORTH);

        // Main content with proper layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        // Top: Search Panel
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);

        // Center: Table (primary focus)
        mainPanel.add(createStyledTable(), BorderLayout.CENTER);

        // Bottom: Form Panel
        mainPanel.add(createFormPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Initialize
        loadDropdowns();
        loadSearchDropdowns();
        loadProducts();

        setVisible(true);
    }

    private JPanel createStyledHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SLYTHERIN_GREEN),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel titleLabel = new JLabel("⬡ Product Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        UserInfoPanel userInfo = new UserInfoPanel(true);
        userInfo.setBackground(PANEL_BG);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(CARD_BG);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel searchTitle = new JLabel("Search Products");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchTitle.setForeground(SLYTHERIN_SILVER);
        searchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields row
        JPanel fieldsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        fieldsPanel.setBackground(CARD_BG);
        fieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSearchName = createStyledTextField();
        cbCategory = createStyledComboBox();
        cbBrand = createStyledComboBox();
        cbSupplier = createStyledComboBox();

        fieldsPanel.add(createFieldPanel("Product Name", txtSearchName, CARD_BG));
        fieldsPanel.add(createFieldPanel("Category", cbCategory, CARD_BG));
        fieldsPanel.add(createFieldPanel("Brand", cbBrand, CARD_BG));
        fieldsPanel.add(createFieldPanel("Supplier", cbSupplier, CARD_BG));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSearch = createStyledButton("Search", SLYTHERIN_GREEN);
        JButton btnClear = createStyledButton("Clear", SLYTHERIN_GREEN.darker());

        btnSearch.addActionListener(e -> searchProducts());
        btnClear.addActionListener(e -> {
            txtSearchName.setText("");
            cbCategory.setSelectedIndex(0);
            cbBrand.setSelectedIndex(0);
            cbSupplier.setSelectedIndex(0);
            loadProducts();
        });

        buttonPanel.add(btnSearch);
        buttonPanel.add(btnClear);

        searchPanel.add(searchTitle);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        searchPanel.add(fieldsPanel);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        searchPanel.add(buttonPanel);

        return searchPanel;
    }

    private JScrollPane createStyledTable() {
        model = new DefaultTableModel(
                new String[] {
                        "ID", "Name", "Category", "Brand", "Supplier",
                        "Unit", "Cost Price", "Markup (%)", "Price",
                        "Quantity", "Date Added"
                }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(SLYTHERIN_GREEN.darker());
        table.setSelectionBackground(SLYTHERIN_GREEN);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(SLYTHERIN_SILVER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new LineBorder(SLYTHERIN_GREEN));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Custom cell renderer for alternating rows
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : PANEL_BG);
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(SLYTHERIN_GREEN);
                    c.setForeground(Color.WHITE);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Special formatter for cost price column
        table.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                                column);

                        if (value instanceof Number) {
                            setText(FormatUtil.peso(((Number) value).doubleValue()));
                        }

                        if (!isSelected) {
                            c.setBackground(row % 2 == 0 ? CARD_BG : PANEL_BG);
                            c.setForeground(ACCENT_GREEN);
                        } else {
                            c.setBackground(SLYTHERIN_GREEN);
                            c.setForeground(Color.WHITE);
                        }

                        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                        return c;
                    }
                });

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedRow() >= 0) {
                    loadSelectedProduct();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new LineBorder(SLYTHERIN_GREEN, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        return scrollPane;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN, 1),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));

        JLabel formTitle = new JLabel("Product Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(SLYTHERIN_SILVER);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Initialize fields
        txtName = createStyledTextField();
        txtUnit = createStyledTextField();
        txtQty = createStyledTextField();
        txtCost = createStyledTextField();
        txtMarkup = createStyledTextField();
        txtPrice = createStyledTextField();
        txtPrice.setEditable(false);
        txtPrice.setBackground(DARK_BG);

        cmbCategory = createStyledComboBox();
        cmbBrand = createStyledComboBox();
        cmbSupplier = createStyledComboBox();

        lblDate = new JLabel(LocalDate.now().toString());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(ACCENT_GREEN);

        // Single row with all fields
        JPanel fieldsRow = new JPanel(new GridLayout(1, 9, 10, 0));
        fieldsRow.setBackground(PANEL_BG);
        fieldsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        fieldsRow.add(createFieldPanel("Name", txtName, PANEL_BG));
        fieldsRow.add(createFieldPanel("Category", cmbCategory, PANEL_BG));
        fieldsRow.add(createFieldPanel("Brand", cmbBrand, PANEL_BG));
        fieldsRow.add(createFieldPanel("Supplier", cmbSupplier, PANEL_BG));
        fieldsRow.add(createFieldPanel("Unit", txtUnit, PANEL_BG));
        fieldsRow.add(createFieldPanel("Qty", txtQty, PANEL_BG));
        fieldsRow.add(createFieldPanel("Cost", txtCost, PANEL_BG));
        fieldsRow.add(createFieldPanel("Markup %", txtMarkup, PANEL_BG));
        fieldsRow.add(createFieldPanel("Price", txtPrice, PANEL_BG));

        // Bottom row with date and buttons
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setBackground(PANEL_BG);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(PANEL_BG);
        JLabel dateLbl = new JLabel("Date: ");
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(SLYTHERIN_SILVER);
        datePanel.add(dateLbl);
        datePanel.add(lblDate);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(PANEL_BG);

        JButton btnAdd = createStyledButton("Add Product", ACCENT_GREEN);
        JButton btnUpdate = createStyledButton("Update", SLYTHERIN_GREEN);
        JButton btnDelete = createStyledButton("Delete", new Color(180, 60, 60));

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        bottomRow.add(datePanel, BorderLayout.WEST);
        bottomRow.add(buttonPanel, BorderLayout.EAST);

        // Auto-compute selling price
        KeyAdapter compute = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                computeSellingPrice();
            }
        };
        txtCost.addKeyListener(compute);
        txtMarkup.addKeyListener(compute);

        // Assemble form
        formPanel.add(formTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(fieldsRow);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(bottomRow);

        return formPanel;
    }

    private JPanel createFieldPanel(String label, JComponent field, Color bgColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(SLYTHERIN_SILVER);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(CARD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(SLYTHERIN_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });

        return field;
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> cmb = new JComboBox<>();
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb.setBackground(CARD_BG);
        cmb.setForeground(TEXT_COLOR);
        cmb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SLYTHERIN_GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return cmb;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        Color hoverColor = bgColor.brighter();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // ================= ORIGINAL LOGIC (UNCHANGED) =================

    private void loadDropdowns() {
        cmbCategory.removeAllItems();
        cmbBrand.removeAllItems();
        cmbSupplier.removeAllItems();

        for (Category c : categoryDAO.findAll()) {
            categoryMap.put(c.getId(), c);
            cmbCategory.addItem(c);
        }
        for (Brand b : brandDAO.findAll()) {
            brandMap.put(b.getId(), b);
            cmbBrand.addItem(b);
        }
        for (Supplier s : supplierDAO.findAll()) {
            supplierMap.put(s.getId(), s);
            cmbSupplier.addItem(s);
        }
    }

    private void loadSearchDropdowns() {
        cbCategory.addItem(null);
        cbBrand.addItem(null);
        cbSupplier.addItem(null);
        categoryDAO.findAll().forEach(cbCategory::addItem);
        brandDAO.findAll().forEach(cbBrand::addItem);
        supplierDAO.findAll().forEach(cbSupplier::addItem);
    }

    private void loadProducts() {
        model.setRowCount(0);
        for (Product p : productDAO.getAllProducts()) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    categoryMap.get(p.getCategoryId()).getName(),
                    brandMap.get(p.getBrandId()).getName(),
                    supplierMap.get(p.getSupplierId()).getName(),
                    p.getUnit(),
                    p.getCostPrice(),
                    p.getMarkup(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getDateAdded()
            });
        }
    }

    private void searchProducts() {
        model.setRowCount(0);
        Category c = (Category) cbCategory.getSelectedItem();
        Brand b = (Brand) cbBrand.getSelectedItem();
        Supplier s = (Supplier) cbSupplier.getSelectedItem();

        for (Product p : productDAO.search(
                txtSearchName.getText(),
                c == null ? null : c.getId(),
                b == null ? null : b.getId(),
                s == null ? null : s.getId())) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    categoryMap.get(p.getCategoryId()).getName(),
                    brandMap.get(p.getBrandId()).getName(),
                    supplierMap.get(p.getSupplierId()).getName(),
                    p.getUnit(),
                    p.getCostPrice(),
                    p.getMarkup(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getDateAdded()
            });
        }
    }

    private void computeSellingPrice() {
        if (!ValidationUtil.isPositiveDouble(txtCost.getText()) ||
                !ValidationUtil.isPositiveDouble(txtMarkup.getText()))
            return;
        double cost = Double.parseDouble(txtCost.getText());
        double markup = Double.parseDouble(txtMarkup.getText());
        txtPrice.setText(String.format("%.2f", cost + (cost * markup / 100)));
    }

    private void addProduct() {
        if (!validateForm())
            return;
        if (productDAO.create(buildProduct(false))) {
            DialogUtil.info("Product added!");
            loadProducts();
            clearForm();
        }
    }

    private void updateProduct() {
        if (table.getSelectedRow() < 0)
            return;
        if (productDAO.update(buildProduct(true))) {
            DialogUtil.info("Product updated!");
            loadProducts();
            clearForm();
        }
    }

    private void deleteProduct() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        productDAO.delete((int) model.getValueAt(r, 0));
        loadProducts();
        clearForm();
    }

    private void loadSelectedProduct() {
        int id = (int) model.getValueAt(table.getSelectedRow(), 0);
        Product p = productDAO.findById(id);

        txtName.setText(p.getName());
        txtUnit.setText(p.getUnit());
        txtQty.setText(String.valueOf(p.getQuantity()));
        txtCost.setText(String.valueOf(p.getCostPrice()));
        txtMarkup.setText(String.valueOf(p.getMarkup()));
        txtPrice.setText(String.valueOf(p.getPrice()));
        lblDate.setText(p.getDateAdded().toString());

        selectCombo(cmbCategory, p.getCategoryId());
        selectCombo(cmbBrand, p.getBrandId());
        selectCombo(cmbSupplier, p.getSupplierId());
    }

    private <T> void selectCombo(JComboBox<T> box, int id) {
        for (int i = 0; i < box.getItemCount(); i++) {
            Object o = box.getItemAt(i);
            if (o instanceof Category && ((Category) o).getId() == id ||
                    o instanceof Brand && ((Brand) o).getId() == id ||
                    o instanceof Supplier && ((Supplier) o).getId() == id) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }

    private Product buildProduct(boolean withId) {
        Product p = new Product();
        if (withId)
            p.setId((int) model.getValueAt(table.getSelectedRow(), 0));
        p.setName(txtName.getText().trim());
        p.setUnit(txtUnit.getText().trim());
        p.setQuantity(Integer.parseInt(txtQty.getText()));
        p.setCostPrice(Double.parseDouble(txtCost.getText()));
        p.setMarkup(Double.parseDouble(txtMarkup.getText()));
        p.setPrice(Double.parseDouble(txtPrice.getText()));
        p.setDateAdded(LocalDate.parse(lblDate.getText()));
        p.setCategoryId(((Category) cmbCategory.getSelectedItem()).getId());
        p.setBrandId(((Brand) cmbBrand.getSelectedItem()).getId());
        p.setSupplierId(((Supplier) cmbSupplier.getSelectedItem()).getId());
        return p;
    }

    private boolean validateForm() {
        return !ValidationUtil.isBlank(txtName.getText()) &&
                ValidationUtil.isPositiveInt(txtQty.getText()) &&
                ValidationUtil.isPositiveDouble(txtCost.getText()) &&
                ValidationUtil.isPositiveDouble(txtMarkup.getText());
    }

    private void clearForm() {
        txtName.setText("");
        txtUnit.setText("");
        txtQty.setText("");
        txtCost.setText("");
        txtMarkup.setText("");
        txtPrice.setText("");
        lblDate.setText(LocalDate.now().toString());
        table.clearSelection();
    }
}