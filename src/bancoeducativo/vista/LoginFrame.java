// 🔴 SOLO SE MODIFICA ESTÉTICA Y LAYOUT – LÓGICA INTACTA
package bancoeducativo.vista;

import bancoeducativo.controlador.Validador;
import bancoeducativo.modelo.Usuario;
import bancoeducativo.servicio.ArchivoManager;
import bancoeducativo.servicio.Banco;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private PlaceholderTextField txtCorreo;
    private JPasswordField txtContraseña;
    private JLabel lblValidacionCorreo;
    private JLabel lblValidacionPassword;
    private Banco banco;
    private RoundedButton btnLogin;

    public LoginFrame() {
        setTitle("Banco Educativo - Inicio de Sesión");
        setSize(550, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        banco = new Banco();
        ArchivoManager.validarIntegridad();
        banco.setUsuarios(ArchivoManager.cargarUsuarios());
        banco.cargarTransacciones(ArchivoManager.cargarTransacciones());

        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(EsteticaUI.COLOR_FONDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(450, 550));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        card.add(crearHeader());
        card.add(crearFormulario());
        card.add(crearAcciones());

        root.add(card, gbc);
        add(root);
    }

    // ================= HEADER =================
    private JPanel crearHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(450, 130));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(EsteticaUI.COLOR_PRIMARIO);

        JLabel icono = new JLabel("\uD83C\uDFE6");
        icono.setFont(obtenerFuenteEmoji(36));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("BANCO EDUCATIVO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Bienvenido a su banca en línea");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(220, 230, 245));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icono);
        panel.add(Box.createVerticalStrut(8));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitulo);

        return panel;
    }

    private Font obtenerFuenteEmoji(int size) {
        Font emoji = new Font("Segoe UI Emoji", Font.PLAIN, size);
        if (!emoji.canDisplay('\uD83C')) {
            emoji = new Font("Dialog", Font.PLAIN, size);
        }
        return emoji;
    }

    // ================= FORMULARIO =================
    private JPanel crearFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        panel.add(crearCampoCorreo());
        panel.add(Box.createVerticalStrut(18));
        panel.add(crearCampoPassword());
        panel.add(Box.createVerticalStrut(12));

        JLabel forgot = new JLabel("<html><u>¿Olvidaste tu contraseña?</u></html>");
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgot.setForeground(EsteticaUI.COLOR_PRIMARIO);
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mostrarDialogoRecuperacion();
            }
        });

        panel.add(forgot);
        return panel;
    }

    private JPanel crearCampoCorreo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Correo electrónico");
        lbl.setFont(EsteticaUI.FUENTE_NEGRITA);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtCorreo = new PlaceholderTextField("ejemplo@correo.com");
        txtCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                validarCorreoEnTiempoReal();
            }
        });

        lblValidacionCorreo = new JLabel(" ");
        lblValidacionCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblValidacionCorreo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtCorreo);
        panel.add(lblValidacionCorreo);
        return panel;
    }

    private JPanel crearCampoPassword() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Contraseña");
        lbl.setFont(EsteticaUI.FUENTE_NEGRITA);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtContraseña = new JPasswordField();
        txtContraseña.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtContraseña.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContraseña.addActionListener(e -> autenticar());

        lblValidacionPassword = new JLabel(" ");
        lblValidacionPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblValidacionPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtContraseña);
        panel.add(lblValidacionPassword);
        return panel;
    }

    // ================= ACCIONES =================
    private JPanel crearAcciones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 45, 35, 45));

        btnLogin = new RoundedButton("Iniciar Sesión", EsteticaUI.COLOR_PRIMARIO);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnLogin.addActionListener(e -> autenticar());

        RoundedButton btnRegistro = new RoundedButton("Crear Cuenta Nueva", EsteticaUI.COLOR_SECUNDARIO);
        btnRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRegistro.addActionListener(e -> abrirRegistro());

        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnRegistro);

        return panel;
    }

    // ================= LÓGICA ORIGINAL =================

    private void validarCorreoEnTiempoReal() {
        String correo = txtCorreo.getText().trim();
        if (!correo.isEmpty() && !Validador.validarCorreo(correo)) {
            lblValidacionCorreo.setText("⚠ Formato de correo inválido");
            lblValidacionCorreo.setForeground(EsteticaUI.COLOR_ADVERTENCIA);
        } else {
            lblValidacionCorreo.setText(" ");
        }
    }

    private void mostrarDialogoRecuperacion() {
        JOptionPane.showMessageDialog(this,
                "Contacte a su instructor.\nSistema educativo.",
                "Recuperación",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void autenticar() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtContraseña.getPassword());

        lblValidacionCorreo.setText(" ");
        lblValidacionPassword.setText(" ");

        if (correo.isEmpty() || pass.isEmpty()) {
            if (correo.isEmpty()) lblValidacionCorreo.setText("⚠ Correo requerido");
            if (pass.isEmpty()) lblValidacionPassword.setText("⚠ Contraseña requerida");
            return;
        }

        if (!Validador.validarCorreo(correo)) {
            lblValidacionCorreo.setText("⚠ " + Validador.getMensajeErrorCorreo());
            return;
        }

        Usuario usuario = banco.autenticar(correo, pass);
        if (usuario == null) {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtContraseña.setText("");
        } else {
            dispose();
            new VentanaPrincipal_NEW(usuario, banco);
        }
    }

    private void abrirRegistro() {
        dispose();
        new RegistroFrame(banco);
    }
}
