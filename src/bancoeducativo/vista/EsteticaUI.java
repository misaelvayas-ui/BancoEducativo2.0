package bancoeducativo.vista;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Clase para aplicar estilos y temas consistentes en toda la aplicación
 */
public class EsteticaUI {

    // Paleta de colores principal
    public static final Color COLOR_PRIMARIO = new Color(30, 86, 134);
    public static final Color COLOR_PRIMARIO_HOVER = new Color(40, 100, 155);
    public static final Color COLOR_SECUNDARIO = new Color(46, 139, 87);
    public static final Color COLOR_ACENTO = new Color(220, 20, 60);
    public static final Color COLOR_FONDO = new Color(245, 248, 250);
    public static final Color COLOR_PANEL = new Color(255, 255, 255);
    public static final Color COLOR_TEXTO = new Color(40, 50, 70);
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(108, 117, 125);
    public static final Color COLOR_EXITO = new Color(40, 167, 69);
    public static final Color COLOR_ADVERTENCIA = new Color(255, 193, 7);
    public static final Color COLOR_ERROR = new Color(220, 53, 69);
    public static final Color COLOR_INFO = new Color(23, 162, 184);
    public static final Color COLOR_BORDE = new Color(220, 220, 220);

    // Fuentes
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);

    public static void aplicarTema() {
        try {
            // Intentar aplicar Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si falla, usar el Look and Feel por defecto del sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Configuraciones globales de UI
        UIManager.put("control", new ColorUIResource(250, 250, 250));
        UIManager.put("Panel.background", new ColorUIResource(COLOR_FONDO));
        UIManager.put("Label.font", FUENTE_NORMAL);
        UIManager.put("Button.font", FUENTE_NEGRITA);
        UIManager.put("TextField.font", FUENTE_NORMAL);
        UIManager.put("PasswordField.font", FUENTE_NORMAL);
        UIManager.put("Table.font", FUENTE_NORMAL);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("TableHeader.font", FUENTE_NEGRITA);
        UIManager.put("Button.background", COLOR_PRIMARIO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("TextField.caretForeground", COLOR_TEXTO);
        UIManager.put("TextField.selectionBackground", COLOR_PRIMARIO);
        UIManager.put("TextField.selectionForeground", Color.WHITE);

        // Configurar tooltips
        UIManager.put("ToolTip.background", new ColorUIResource(50, 50, 50));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("ToolTip.font", FUENTE_PEQUENA);
    }

    /**
     * Crea un panel con sombra simulada
     */
    public static JPanel crearPanelConSombra() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    /**
     * Crea un botón estilizado
     */
    public static JButton crearBoton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NEGRITA);
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(150, 40));

        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(fondo);
            }
        });

        return boton;
    }

    /**
     * Crea una etiqueta de título
     */
    public static JLabel crearTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_TITULO);
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    /**
     * Crea una etiqueta de subtítulo
     */
    public static JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_SUBTITULO);
        label.setForeground(COLOR_TEXTO);
        return label;
    }
}

