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

/**
 * Ventana de registro de nuevos usuarios - Versión mejorada
 */
public class RegistroFrame extends JFrame {
    private PlaceholderTextField txtNombre, txtCorreo, txtSaldoInicial;
    private JPasswordField txtPass, txtPass2;
    private JLabel lblValidacionNombre, lblValidacionCorreo, lblValidacionPass, lblValidacionPass2;
    private JProgressBar barFortalezaPass;
    private JLabel lblFortalezaPass;
    private Banco banco;

    public RegistroFrame(Banco banco) {
        this.banco = banco;

        setTitle("Banco Educativo - Registro de Nuevo Usuario");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();
        setVisible(true);
    }

    private void inicializarComponentes() {
        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, EsteticaUI.COLOR_FONDO,
                    0, getHeight(), new Color(220, 240, 230)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Panel contenedor con fondo blanco
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 40, 20, 40),
            BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1, true)
        ));

        // Panel superior - Header
        JPanel panelHeader = crearPanelHeader();
        cardPanel.add(panelHeader, BorderLayout.NORTH);

        // Panel central - Formulario
        JPanel panelFormulario = crearPanelFormulario();
        JScrollPane scrollPane = new JScrollPane(panelFormulario);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelBotones = crearPanelBotones();
        cardPanel.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(cardPanel);
        add(panelPrincipal);
    }

    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, EsteticaUI.COLOR_SECUNDARIO,
                    0, getHeight(), EsteticaUI.COLOR_SECUNDARIO.darker()
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(520, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Crear Nueva Cuenta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Complete el formulario para registrarse");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(220, 245, 230));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblSubtitulo);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));
        panel.setBackground(Color.WHITE);

        // Nombre completo
        panel.add(crearLabel("Nombre completo *"));
        txtNombre = new PlaceholderTextField("Ingrese su nombre completo");
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarNombreEnTiempoReal();
            }
        });
        panel.add(txtNombre);
        lblValidacionNombre = crearLabelValidacion();
        panel.add(lblValidacionNombre);
        panel.add(Box.createVerticalStrut(12));

        // Correo electrónico
        panel.add(crearLabel("Correo electrónico *"));
        txtCorreo = new PlaceholderTextField("ejemplo@correo.com");
        txtCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtCorreo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarCorreoEnTiempoReal();
            }
        });
        panel.add(txtCorreo);
        lblValidacionCorreo = crearLabelValidacion();
        panel.add(lblValidacionCorreo);
        panel.add(Box.createVerticalStrut(12));

        // Contraseña
        panel.add(crearLabel("Contraseña *"));
        txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPass.setFont(EsteticaUI.FUENTE_NORMAL);
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        txtPass.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarPasswordEnTiempoReal();
                verificarCoincidenciaPasswords();
            }
        });
        panel.add(txtPass);

        // Barra de fortaleza de contraseña
        barFortalezaPass = new JProgressBar(0, 100);
        barFortalezaPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        barFortalezaPass.setStringPainted(false);
        panel.add(Box.createVerticalStrut(5));
        panel.add(barFortalezaPass);

        lblFortalezaPass = new JLabel(" ");
        lblFortalezaPass.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        panel.add(lblFortalezaPass);

        lblValidacionPass = crearLabelValidacion();
        panel.add(lblValidacionPass);
        panel.add(Box.createVerticalStrut(12));

        // Confirmar contraseña
        panel.add(crearLabel("Confirmar contraseña *"));
        txtPass2 = new JPasswordField();
        txtPass2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPass2.setFont(EsteticaUI.FUENTE_NORMAL);
        txtPass2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        txtPass2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                verificarCoincidenciaPasswords();
            }
        });
        panel.add(txtPass2);
        lblValidacionPass2 = crearLabelValidacion();
        panel.add(lblValidacionPass2);
        panel.add(Box.createVerticalStrut(12));

        // Saldo inicial
        panel.add(crearLabel("Saldo inicial (opcional)"));
        txtSaldoInicial = new PlaceholderTextField("0.00");
        txtSaldoInicial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(txtSaldoInicial);
        panel.add(Box.createVerticalStrut(8));

        JLabel lblAyuda = new JLabel("Puede ingresar un saldo inicial o dejarlo en 0");
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyuda.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        panel.add(lblAyuda);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(EsteticaUI.FUENTE_NEGRITA);
        label.setForeground(EsteticaUI.COLOR_TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel crearLabelValidacion() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void validarNombreEnTiempoReal() {
        String nombre = txtNombre.getText().trim();
        if (!nombre.isEmpty()) {
            if (Validador.validarNombre(nombre)) {
                lblValidacionNombre.setText("✓ Nombre válido");
                lblValidacionNombre.setForeground(EsteticaUI.COLOR_EXITO);
            } else {
                lblValidacionNombre.setText("⚠ " + Validador.getMensajeErrorNombre());
                lblValidacionNombre.setForeground(EsteticaUI.COLOR_ERROR);
            }
        } else {
            lblValidacionNombre.setText(" ");
        }
    }

    private void validarCorreoEnTiempoReal() {
        String correo = txtCorreo.getText().trim();
        if (!correo.isEmpty()) {
            if (Validador.validarCorreo(correo)) {
                if (banco.existeCorreo(correo)) {
                    lblValidacionCorreo.setText("⚠ Este correo ya está registrado");
                    lblValidacionCorreo.setForeground(EsteticaUI.COLOR_ADVERTENCIA);
                } else {
                    lblValidacionCorreo.setText("OK - Correo disponible");
                    lblValidacionCorreo.setForeground(EsteticaUI.COLOR_EXITO);
                }
            } else {
                lblValidacionCorreo.setText("⚠ Formato de correo inválido");
                lblValidacionCorreo.setForeground(EsteticaUI.COLOR_ERROR);
            }
        } else {
            lblValidacionCorreo.setText(" ");
        }
    }

    private void validarPasswordEnTiempoReal() {
        String pass = new String(txtPass.getPassword());
        if (!pass.isEmpty()) {
            int fortaleza = calcularFortalezaPassword(pass);
            barFortalezaPass.setValue(fortaleza);

            if (fortaleza < 33) {
                barFortalezaPass.setForeground(EsteticaUI.COLOR_ERROR);
                lblFortalezaPass.setText("Débil");
                lblFortalezaPass.setForeground(EsteticaUI.COLOR_ERROR);
            } else if (fortaleza < 66) {
                barFortalezaPass.setForeground(EsteticaUI.COLOR_ADVERTENCIA);
                lblFortalezaPass.setText("Media");
                lblFortalezaPass.setForeground(EsteticaUI.COLOR_ADVERTENCIA);
            } else {
                barFortalezaPass.setForeground(EsteticaUI.COLOR_EXITO);
                lblFortalezaPass.setText("Fuerte");
                lblFortalezaPass.setForeground(EsteticaUI.COLOR_EXITO);
            }

            if (Validador.validarContrasena(pass)) {
                lblValidacionPass.setText("OK - Contraseña válida");
                lblValidacionPass.setForeground(EsteticaUI.COLOR_EXITO);
            } else {
                lblValidacionPass.setText("⚠ " + Validador.getMensajeErrorContrasena());
                lblValidacionPass.setForeground(EsteticaUI.COLOR_ERROR);
            }
        } else {
            barFortalezaPass.setValue(0);
            lblFortalezaPass.setText(" ");
            lblValidacionPass.setText(" ");
        }
    }

    private void verificarCoincidenciaPasswords() {
        String pass1 = new String(txtPass.getPassword());
        String pass2 = new String(txtPass2.getPassword());

        if (!pass2.isEmpty()) {
            if (pass1.equals(pass2)) {
                lblValidacionPass2.setText("OK - Las contraseñas coinciden");
                lblValidacionPass2.setForeground(EsteticaUI.COLOR_EXITO);
            } else {
                lblValidacionPass2.setText("⚠ Las contraseñas no coinciden");
                lblValidacionPass2.setForeground(EsteticaUI.COLOR_ERROR);
            }
        } else {
            lblValidacionPass2.setText(" ");
        }
    }

    private int calcularFortalezaPassword(String pass) {
        int fortaleza = 0;

        if (pass.length() >= 8) fortaleza += 25;
        if (pass.length() >= 12) fortaleza += 15;
        if (pass.matches(".*[a-z].*")) fortaleza += 15;
        if (pass.matches(".*[A-Z].*")) fortaleza += 15;
        if (pass.matches(".*[0-9].*")) fortaleza += 15;
        if (pass.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) fortaleza += 15;

        return Math.min(fortaleza, 100);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 20, 15));

        RoundedButton btnRegistrar = new RoundedButton("Registrar Cuenta", EsteticaUI.COLOR_EXITO);
        btnRegistrar.setPreferredSize(new Dimension(180, 45));
        btnRegistrar.addActionListener(e -> registrar());

        RoundedButton btnCancelar = new RoundedButton("Cancelar", EsteticaUI.COLOR_ERROR);
        btnCancelar.setPreferredSize(new Dimension(180, 45));
        btnCancelar.addActionListener(e -> cancelar());

        panel.add(btnRegistrar);
        panel.add(btnCancelar);

        return panel;
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String pass1 = new String(txtPass.getPassword());
        String pass2 = new String(txtPass2.getPassword());
        String saldoStr = txtSaldoInicial.getText().trim();

        // Validar campos vacíos
        if (nombre.isEmpty() || correo.isEmpty() || pass1.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                    "<b>Campos Incompletos</b><br><br>" +
                    "Por favor complete todos los campos obligatorios (*)" +
                    "</div></html>",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar nombre
        if (!Validador.validarNombre(nombre)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorNombre(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar correo
        if (!Validador.validarCorreo(correo)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorCorreo(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar contraseña
        if (!Validador.validarContrasena(pass1)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorContrasena(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar coincidencia de contraseñas
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                    "<b>Error en Contraseñas</b><br><br>" +
                    "Las contraseñas no coinciden.<br>" +
                    "Por favor, verifíquelas e intente nuevamente." +
                    "</div></html>",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtPass.setText("");
            txtPass2.setText("");
            return;
        }

        // Validar si el correo ya existe
        if (banco.existeCorreo(correo)) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                    "<b>Correo Ya Registrado</b><br><br>" +
                    "El correo <b>" + correo + "</b> ya está registrado.<br>" +
                    "Por favor utilice otro correo electrónico." +
                    "</div></html>",
                    "Correo Existente",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar saldo inicial
        double saldoInicial = 0.0;
        if (!saldoStr.isEmpty() && !saldoStr.equals("0.00")) {
            try {
                saldoInicial = Double.parseDouble(saldoStr);
                if (saldoInicial < 0) {
                    JOptionPane.showMessageDialog(this,
                            "El saldo inicial no puede ser negativo",
                            "Error de Validación",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (saldoInicial > 1000000) {
                    JOptionPane.showMessageDialog(this,
                            "El saldo inicial no puede superar $1,000,000.00",
                            "Error de Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "El saldo inicial debe ser un número válido",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Crear nuevo usuario
        String id = "U" + System.currentTimeMillis();
        Usuario nuevo = new Usuario(id, nombre, correo, pass1, saldoInicial);
        banco.registrarUsuarios(nuevo);
        ArchivoManager.guardarUsuarios(banco.getClientes());

        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 15px;'>" +
                "<b style='color: #28a745; font-size: 16px;'>¡Registro Exitoso!</b><br><br>" +
                "<table style='text-align: left; margin: auto;'>" +
                "<tr><td><b>Nombre:</b></td><td>" + nombre + "</td></tr>" +
                "<tr><td><b>Correo:</b></td><td>" + correo + "</td></tr>" +
                "<tr><td><b>Saldo inicial:</b></td><td>$" + String.format("%.2f", saldoInicial) + "</td></tr>" +
                "</table><br>" +
                "<span style='font-size: 12px; color: gray;'>" +
                "Ahora puede iniciar sesión con sus credenciales" +
                "</span>" +
                "</div></html>",
                "Registro Completado",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new LoginFrame();
    }

    private void cancelar() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                "¿Está seguro que desea cancelar el registro?<br><br>" +
                "Se perderán todos los datos ingresados." +
                "</div></html>",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}

