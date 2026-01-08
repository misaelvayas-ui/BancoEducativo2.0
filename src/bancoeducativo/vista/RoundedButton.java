package bancoeducativo.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Botón personalizado con bordes redondeados y efectos visuales
 */
public class RoundedButton extends JButton {

    private boolean hover = false;
    private boolean pressed = false;
    private Color colorBase;
    private Color colorHover;
    private int cornerRadius = 20;

    public RoundedButton(String text) {
        super(text);
        this.colorBase = EsteticaUI.COLOR_SECUNDARIO;
        this.colorHover = EsteticaUI.COLOR_SECUNDARIO.brighter();

        inicializar();
    }

    public RoundedButton(String text, Color color) {
        super(text);
        this.colorBase = color;
        this.colorHover = color.brighter();

        inicializar();
    }

    private void inicializar() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(EsteticaUI.FUENTE_NEGRITA);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 45));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Sombra
        if (!pressed) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 4, w - 2, h - 2, cornerRadius, cornerRadius);
        }

        // Fondo con gradiente
        Color color1 = pressed ? colorBase.darker() : (hover ? colorHover : colorBase);
        Color color2 = color1.darker();

        GradientPaint gradient = new GradientPaint(
            0, 0, color1,
            0, h, color2
        );

        g2.setPaint(gradient);
        g2.fillRoundRect(0, pressed ? 2 : 0, w - 2, h - 4, cornerRadius, cornerRadius);

        // Borde sutil
        g2.setColor(new Color(0, 0, 0, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, pressed ? 2 : 0, w - 3, h - 5, cornerRadius, cornerRadius);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No pintar borde por defecto
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setColorBase(Color color) {
        this.colorBase = color;
        this.colorHover = color.brighter();
        repaint();
    }
}

