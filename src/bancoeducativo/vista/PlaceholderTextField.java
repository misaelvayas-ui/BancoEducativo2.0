package bancoeducativo.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Campo de texto personalizado con placeholder
 */
public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private boolean showingPlaceholder = false;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(EsteticaUI.FUENTE_NORMAL);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));

        // Añadir listener para efectos de foco
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(EsteticaUI.COLOR_PRIMARIO, 2),
                    new EmptyBorder(7, 9, 7, 9)
                ));
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1),
                    new EmptyBorder(8, 10, 8, 10)
                ));
                repaint();
            }
        });
    }

    public PlaceholderTextField(String placeholder, int columns) {
        this(placeholder);
        setColumns(columns);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty() && !isFocusOwner() && placeholder != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            g2.setFont(getFont().deriveFont(Font.ITALIC));

            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            g2.drawString(placeholder, 10, y);
            g2.dispose();
        }
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        repaint();
    }
}

