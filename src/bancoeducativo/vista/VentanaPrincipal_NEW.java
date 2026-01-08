package bancoeducativo.vista;

import bancoeducativo.controlador.Validador;
import bancoeducativo.excepciones.SaldoInsuficienteException;
import bancoeducativo.excepciones.UsuarioNoEncontradoException;
import bancoeducativo.modelo.Usuario;
import bancoeducativo.servicio.Banco;
import bancoeducativo.servicio.ArchivoManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal mejorada con dashboard moderno
 */
public class VentanaPrincipal_NEW extends JFrame {

    private Usuario usuarioActual;
    private Banco banco;
    private JLabel lblSaldo, lblNombreUsuario;
    private JPanel panelCentral;
    private int botonSeleccionado = -1;

    public VentanaPrincipal_NEW(Usuario usuario, Banco banco) {
        this.usuarioActual = usuario;
        this.banco = banco;

        setTitle("Banco Educativo | Banca en Línea");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(900, 600));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

        inicializarComponentes();
        mostrarDashboard();
        setVisible(true);
    }

    private void inicializarComponentes() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(EsteticaUI.COLOR_FONDO);

        root.add(crearBarraSuperior(), BorderLayout.NORTH);
        root.add(crearMenuLateral(), BorderLayout.WEST);

        panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(EsteticaUI.COLOR_FONDO);
        root.add(panelCentral, BorderLayout.CENTER);

        add(root);
    }

    // ================= BARRA SUPERIOR MEJORADA =================
    private JPanel crearBarraSuperior() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, EsteticaUI.COLOR_PRIMARIO,
                    getWidth(), 0, EsteticaUI.COLOR_PRIMARIO.darker()
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(1100, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Logo y título
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierdo.setOpaque(false);

        JLabel lblBanco = new JLabel("BANCO EDUCATIVO");
        lblBanco.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBanco.setForeground(Color.WHITE);

        panelIzquierdo.add(lblBanco);

        // Info usuario
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDerecho.setOpaque(false);

        lblNombreUsuario = new JLabel(usuarioActual.getNombre());
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreUsuario.setForeground(Color.WHITE);

        RoundedButton btnCerrarSesion = new RoundedButton("Cerrar Sesión", EsteticaUI.COLOR_ERROR);
        btnCerrarSesion.setPreferredSize(new Dimension(140, 35));
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        panelDerecho.add(lblNombreUsuario);
        panelDerecho.add(btnCerrarSesion);

        panel.add(panelIzquierdo, BorderLayout.WEST);
        panel.add(panelDerecho, BorderLayout.EAST);

        return panel;
    }

    // ================= MENÚ LATERAL MEJORADO =================
    private JPanel crearMenuLateral() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, EsteticaUI.COLOR_BORDE),
            BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));

        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMenu.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblMenu);
        panel.add(Box.createVerticalStrut(15));

        panel.add(crearBotonMenu("Dashboard", 0, e -> mostrarDashboard()));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Depositar", 1, e -> realizarDeposito()));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Retirar", 2, e -> realizarRetiro()));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Transferir", 3, e -> realizarTransferencia()));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Historial", 4, e -> verHistorial()));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Mi Cuenta", 5, e -> verInformacionCuenta()));

        panel.add(Box.createVerticalGlue());

        // Panel de ayuda
        JPanel panelAyuda = new JPanel();
        panelAyuda.setLayout(new BoxLayout(panelAyuda, BoxLayout.Y_AXIS));
        panelAyuda.setBackground(new Color(240, 248, 255));
        panelAyuda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EsteticaUI.COLOR_INFO, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panelAyuda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel lblAyuda = new JLabel("Ayuda");
        lblAyuda.setFont(EsteticaUI.FUENTE_NEGRITA);
        lblAyuda.setForeground(EsteticaUI.COLOR_INFO);

        JLabel lblTextoAyuda = new JLabel("<html><div style='font-size:10px;'>Sistema educativo para aprender operaciones bancarias básicas</div></html>");
        lblTextoAyuda.setFont(EsteticaUI.FUENTE_PEQUENA);
        lblTextoAyuda.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);

        panelAyuda.add(lblAyuda);
        panelAyuda.add(Box.createVerticalStrut(5));
        panelAyuda.add(lblTextoAyuda);

        panel.add(panelAyuda);

        return panel;
    }

    private JButton crearBotonMenu(String texto, int indice, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Estilo inicial
        btn.setForeground(EsteticaUI.COLOR_TEXTO);
        btn.setBackground(Color.WHITE);

        btn.addActionListener(e -> {
            botonSeleccionado = indice;
            actualizarEstilosBotones();
            accion.actionPerformed(e);
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (botonSeleccionado != indice) {
                    btn.setOpaque(true);
                    btn.setBackground(new Color(245, 248, 250));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (botonSeleccionado != indice) {
                    btn.setOpaque(false);
                    btn.setBackground(Color.WHITE);
                }
            }
        });

        return btn;
    }

    private void actualizarEstilosBotones() {
        // Implementar si es necesario
    }

    // ================= DASHBOARD =================
    private void mostrarDashboard() {
        panelCentral.removeAll();

        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBackground(EsteticaUI.COLOR_FONDO);
        dashboard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Título de bienvenida
        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuarioActual.getNombre());
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBienvenida.setForeground(EsteticaUI.COLOR_TEXTO);
        lblBienvenida.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Resumen de su cuenta bancaria");
        lblSubtitulo.setFont(EsteticaUI.FUENTE_NORMAL);
        lblSubtitulo.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboard.add(lblBienvenida);
        dashboard.add(Box.createVerticalStrut(5));
        dashboard.add(lblSubtitulo);
        dashboard.add(Box.createVerticalStrut(25));

        // Tarjetas de información
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 3, 20, 0));
        panelTarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panelTarjetas.setOpaque(false);
        panelTarjetas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTarjetas.add(crearTarjetaSaldo());
        panelTarjetas.add(crearTarjetaTransacciones());
        panelTarjetas.add(crearTarjetaAccesoRapido());

        dashboard.add(panelTarjetas);
        dashboard.add(Box.createVerticalStrut(30));

        // Acciones rápidas
        dashboard.add(crearPanelAccionesRapidas());

        panelCentral.add(dashboard, BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    private JPanel crearTarjetaSaldo() {
        JPanel tarjeta = crearTarjetaBase();
        tarjeta.setBackground(new Color(230, 245, 255));

        JLabel lblTitulo = new JLabel("Saldo Disponible");
        lblTitulo.setFont(EsteticaUI.FUENTE_NEGRITA);
        lblTitulo.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblSaldo = new JLabel("$" + String.format("%.2f", usuarioActual.getSaldo()));
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblSaldo.setForeground(EsteticaUI.COLOR_PRIMARIO);
        lblSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblSaldo);
        tarjeta.add(Box.createVerticalGlue());

        return tarjeta;
    }

    private JPanel crearTarjetaTransacciones() {
        JPanel tarjeta = crearTarjetaBase();
        tarjeta.setBackground(new Color(240, 255, 240));

        JLabel lblTitulo = new JLabel("Transacciones");
        lblTitulo.setFont(EsteticaUI.FUENTE_NEGRITA);
        lblTitulo.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        int numTransacciones = banco.obtenerHistorial(usuarioActual.getId()).size();
        JLabel lblCantidad = new JLabel(String.valueOf(numTransacciones));
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblCantidad.setForeground(EsteticaUI.COLOR_SECUNDARIO);
        lblCantidad.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblCantidad);
        tarjeta.add(Box.createVerticalGlue());

        return tarjeta;
    }

    private JPanel crearTarjetaAccesoRapido() {
        JPanel tarjeta = crearTarjetaBase();
        tarjeta.setBackground(new Color(255, 245, 235));

        JLabel lblTitulo = new JLabel("Acceso Rápido");
        lblTitulo.setFont(EsteticaUI.FUENTE_NEGRITA);
        lblTitulo.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTexto = new JLabel("Disponible 24/7");
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTexto.setForeground(EsteticaUI.COLOR_ADVERTENCIA.darker());
        lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblTexto);
        tarjeta.add(Box.createVerticalGlue());

        return tarjeta;
    }

    private JPanel crearTarjetaBase() {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Centrar componentes
        tarjeta.setAlignmentY(Component.CENTER_ALIGNMENT);

        return tarjeta;
    }

    private JPanel crearPanelAccionesRapidas() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(crearBotonAccionRapida("Depositar", "Agregar fondos", EsteticaUI.COLOR_EXITO, e -> realizarDeposito()));
        panel.add(crearBotonAccionRapida("Retirar", "Extraer dinero", EsteticaUI.COLOR_ADVERTENCIA, e -> realizarRetiro()));
        panel.add(crearBotonAccionRapida("Transferir", "Enviar dinero", EsteticaUI.COLOR_PRIMARIO, e -> realizarTransferencia()));
        panel.add(crearBotonAccionRapida("Historial", "Ver movimientos", EsteticaUI.COLOR_INFO, e -> verHistorial()));
        panel.add(crearBotonAccionRapida("Saldo", "Consultar saldo", EsteticaUI.COLOR_SECUNDARIO, e -> consultarSaldo()));
        panel.add(crearBotonAccionRapida("Mi Cuenta", "Ver información", new Color(138, 43, 226), e -> verInformacionCuenta()));

        return panel;
    }

    private JPanel crearBotonAccionRapida(String titulo, String descripcion, Color color, java.awt.event.ActionListener accion) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(color);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(EsteticaUI.FUENTE_PEQUENA);
        lblDesc.setForeground(EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblDesc);
        panel.add(Box.createVerticalGlue());

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accion.actionPerformed(null);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(248, 250, 252));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
            }
        });

        return panel;
    }

    // ================= OPERACIONES BANCARIAS =================
    private void realizarDeposito() {
        String input = JOptionPane.showInputDialog(this,
                "Ingrese el monto a depositar:",
                "Realizar Depósito",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        if (!Validador.validarMonto(input)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorMonto(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double monto = Double.parseDouble(input);

        if (banco.depositar(usuarioActual.getId(), monto)) {
            actualizarSaldo();
            ArchivoManager.guardarUsuarios(banco.getClientes());
            ArchivoManager.guardarTransacciones(banco);

            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 15px;'>" +
                    "<b style='color: #28a745; font-size: 16px;'>✓ Depósito Exitoso</b><br><br>" +
                    "<table style='text-align: left; margin: auto;'>" +
                    "<tr><td><b>Monto depositado:</b></td><td>$" + String.format("%.2f", monto) + "</td></tr>" +
                    "<tr><td><b>Nuevo saldo:</b></td><td>$" + String.format("%.2f", usuarioActual.getSaldo()) + "</td></tr>" +
                    "</table>" +
                    "</div></html>",
                    "Operación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            mostrarDashboard(); // Actualizar dashboard
        }
    }

    private void realizarRetiro() {
        String input = JOptionPane.showInputDialog(this,
                "<html><div style='padding: 5px;'>" +
                "Saldo disponible: <b>$" + String.format("%.2f", usuarioActual.getSaldo()) + "</b><br><br>" +
                "Ingrese el monto a retirar:" +
                "</div></html>",
                "Realizar Retiro",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        if (!Validador.validarMonto(input)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorMonto(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double monto = Double.parseDouble(input);

        try {
            if (banco.retirar(usuarioActual.getId(), monto)) {
                actualizarSaldo();
                ArchivoManager.guardarUsuarios(banco.getClientes());
                ArchivoManager.guardarTransacciones(banco);

                JOptionPane.showMessageDialog(this,
                        "<html><div style='text-align: center; padding: 15px;'>" +
                        "<b style='color: #28a745; font-size: 16px;'>✓ Retiro Exitoso</b><br><br>" +
                        "<table style='text-align: left; margin: auto;'>" +
                        "<tr><td><b>Monto retirado:</b></td><td>$" + String.format("%.2f", monto) + "</td></tr>" +
                        "<tr><td><b>Nuevo saldo:</b></td><td>$" + String.format("%.2f", usuarioActual.getSaldo()) + "</td></tr>" +
                        "</table>" +
                        "</div></html>",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                mostrarDashboard(); // Actualizar dashboard
            }
        } catch (SaldoInsuficienteException e) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                    "<b>[!] Saldo Insuficiente</b><br><br>" +
                    e.getMessage() +
                    "</div></html>",
                    "Error en la Operación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void realizarTransferencia() {
        String correoDestino = JOptionPane.showInputDialog(this,
                "Ingrese el correo del destinatario:",
                "Realizar Transferencia",
                JOptionPane.QUESTION_MESSAGE);

        if (correoDestino == null || correoDestino.trim().isEmpty()) return;

        if (!Validador.validarCorreo(correoDestino)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorCorreo(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (correoDestino.equals(usuarioActual.getCorreo())) {
            JOptionPane.showMessageDialog(this,
                    "No puede transferirse dinero a sí mismo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "<html><div style='padding: 5px;'>" +
                "Saldo disponible: <b>$" + String.format("%.2f", usuarioActual.getSaldo()) + "</b><br>" +
                "Destinatario: <b>" + correoDestino + "</b><br><br>" +
                "Ingrese el monto a transferir:" +
                "</div></html>",
                "Realizar Transferencia",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        if (!Validador.validarMonto(input)) {
            JOptionPane.showMessageDialog(this,
                    Validador.getMensajeErrorMonto(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double monto = Double.parseDouble(input);

        try {
            if (banco.transferir(usuarioActual.getId(), correoDestino, monto)) {
                actualizarSaldo();
                ArchivoManager.guardarUsuarios(banco.getClientes());
                ArchivoManager.guardarTransacciones(banco);

                JOptionPane.showMessageDialog(this,
                        "<html><div style='text-align: center; padding: 15px;'>" +
                        "<b style='color: #28a745; font-size: 16px;'>[OK] Transferencia Exitosa</b><br><br>" +
                        "<table style='text-align: left; margin: auto;'>" +
                        "<tr><td><b>Destinatario:</b></td><td>" + correoDestino + "</td></tr>" +
                        "<tr><td><b>Monto:</b></td><td>$" + String.format("%.2f", monto) + "</td></tr>" +
                        "<tr><td><b>Nuevo saldo:</b></td><td>$" + String.format("%.2f", usuarioActual.getSaldo()) + "</td></tr>" +
                        "</table>" +
                        "</div></html>",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                mostrarDashboard(); // Actualizar dashboard
            }
        } catch (SaldoInsuficienteException e) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                    "<b>[!] Saldo Insuficiente</b><br><br>" +
                    e.getMessage() +
                    "</div></html>",
                    "Error en la Operación",
                    JOptionPane.WARNING_MESSAGE);
        } catch (UsuarioNoEncontradoException e) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                    "<b>[!] Usuario No Encontrado</b><br><br>" +
                    "El usuario con correo <b>" + correoDestino + "</b> no existe" +
                    "</div></html>",
                    "Error en la Operación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verHistorial() {
        new VentanaHistorial(usuarioActual, banco);
    }

    private void consultarSaldo() {
        double saldo = banco.consultarSaldo(usuarioActual.getId());
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 15px;'>" +
                "<b style='font-size: 16px;'>Consulta de Saldo</b><br><br>" +
                "Su saldo actual es:<br>" +
                "<span style='font-size: 24px; color: #1E5686; font-weight: bold;'>$" +
                String.format("%.2f", saldo) + "</span>" +
                "</div></html>",
                "Consulta de Saldo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void verInformacionCuenta() {
        JOptionPane.showMessageDialog(this,
                "<html><div style='padding: 15px;'>" +
                "<b style='font-size: 16px;'>Información de la Cuenta</b><br><br>" +
                "<table style='border-spacing: 10px 5px;'>" +
                "<tr><td><b>ID Usuario:</b></td><td>" + usuarioActual.getId() + "</td></tr>" +
                "<tr><td><b>Nombre:</b></td><td>" + usuarioActual.getNombre() + "</td></tr>" +
                "<tr><td><b>Correo:</b></td><td>" + usuarioActual.getCorreo() + "</td></tr>" +
                "<tr><td><b>Saldo Actual:</b></td><td style='color: #2E8B57; font-weight: bold;'>$" +
                String.format("%.2f", usuarioActual.getSaldo()) + "</td></tr>" +
                "<tr><td><b>Transacciones:</b></td><td>" +
                banco.obtenerHistorial(usuarioActual.getId()).size() + "</td></tr>" +
                "</table>" +
                "</div></html>",
                "Información de Cuenta",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarSaldo() {
        if (lblSaldo != null) {
            lblSaldo.setText("$" + String.format("%.2f", usuarioActual.getSaldo()));
        }
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                "¿Está seguro que desea cerrar sesión?<br><br>" +
                "<span style='font-size: 11px; color: gray;'>Todos los cambios han sido guardados</span>" +
                "</div></html>",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            ArchivoManager.guardarUsuarios(banco.getClientes());
            ArchivoManager.guardarTransacciones(banco);
            dispose();
            new LoginFrame();
        }
    }
}

