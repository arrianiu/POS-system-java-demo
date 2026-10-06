package src.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class InventoryCellRenderer extends DefaultTableCellRenderer {

    // Slytherin Color Palette
    private static final Color CARD_BG = new Color(30, 40, 42);
    private static final Color PANEL_BG = new Color(25, 35, 35);
    private static final Color SLYTHERIN_GREEN = new Color(26, 83, 92);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LOW_STOCK_BG = new Color(80, 35, 35);
    private static final Color LOW_STOCK_FG = new Color(255, 120, 120);

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String status = table.getValueAt(row, 5).toString();

        if (status.startsWith("LOW STOCK")) {
            if (isSelected) {
                c.setBackground(new Color(100, 45, 45));
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(LOW_STOCK_BG);
                c.setForeground(LOW_STOCK_FG);
            }
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            if (isSelected) {
                c.setBackground(SLYTHERIN_GREEN);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(row % 2 == 0 ? CARD_BG : PANEL_BG);
                c.setForeground(TEXT_COLOR);
            }
            setFont(getFont().deriveFont(Font.PLAIN));
        }

        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        return c;
    }
}