package util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.Color;
import java.awt.Font;

public class UIStyle {
    public static final Color PAGE_BACKGROUND = new Color(243, 247, 252);
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color PRIMARY = new Color(33, 97, 140);
    public static final Color SECONDARY = new Color(234, 242, 248);
    public static final Color TEXT_DARK = new Color(38, 50, 56);
    public static final Color BORDER = new Color(204, 214, 223);

    private UIStyle() {
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createCardBorder());
        return panel;
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        );
    }

    public static Border createInnerPadding() {
        return BorderFactory.createEmptyBorder(8, 8, 8, 8);
    }

    public static void styleLabel(JComponent component, boolean heading) {
        component.setForeground(TEXT_DARK);
        component.setFont(new Font("Segoe UI", heading ? Font.BOLD : Font.PLAIN, heading ? 24 : 14));
    }

    public static void styleField(JComponent component) {
        component.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        component.setBorder(new CompoundBorder(BorderFactory.createLineBorder(BORDER), createInnerPadding()));
        component.setBackground(Color.WHITE);
        component.setForeground(TEXT_DARK);
        if (component instanceof JTextArea) {
            ((JTextArea) component).setLineWrap(true);
            ((JTextArea) component).setWrapStyleWord(true);
        }
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setOpaque(true);
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(SECONDARY);
        button.setForeground(TEXT_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(9, 16, 9, 16)));
        button.setOpaque(true);
    }

    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(SECONDARY);
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.setRowHeight(26);
        scrollPane.setBorder(createCardBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
    }
}
