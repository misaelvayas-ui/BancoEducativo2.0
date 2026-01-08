package bancoeducativo.vista;

import bancoeducativo.controlador.Validador;
import bancoeducativo.excepciones.SaldoInsuficienteException;
import bancoeducativo.excepciones.UsuarioNoEncontradoException;
import bancoeducativo.modelo.Usuario;
import bancoeducativo.servicio.Banco;
import bancoeducativo.servicio.ArchivoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal del sistema bancario educativo
 * Interfaz estilo banca real (SIN perder funcionalidad)
 */
public class VentanaPrincipal extends JFrame {

    private Usuario usuarioActual;
    private Banco banco;
    private JLabel lblSaldo;

    public VentanaPrincipal(Usuario usuario, Banco banco) {
        this.usuarioActual = usuario;
        this.banco = banco;

        setTitle("Banco Educativo | Banca en Línea");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

        inicializarComponentes();
        actualizarSaldo();
        setVisible(true);
    }

    private void inicializarComponentes() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));

        root.add(crearBarraSuperior(), BorderLayout.NORTH);
        root.add(crearMenuLateral(), BorderLayout.WEST);
        root.add(crearPanelCentral(), BorderLayout.CENTER);

        add(root);
    }

    // ================= BARRA SUPERIOR =================
    private JPanel crearBarraSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 86, 134));
        panel.setPreferredSize(new Dimension(900, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblBanco = new JLabel("BANCO EDUCATIVO");
        lblBanco.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBanco.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombre());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsuario.setForeground(Color.WHITE);

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setFocusPainted(false);
        btnSalir.addActionListener(e -> cerrarSesion());

        panel.add(lblBanco, BorderLayout.WEST);
        panel.add(lblUsuario, BorderLayout.CENTER);
        panel.add(btnSalir, BorderLayout.EAST);

        return panel;
    }

    // ================= MENÚ LATERAL =================
    private JPanel crearMenuLateral() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 247, 250));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        panel.add(crearBotonMenu("Depositar", e -> realizarDeposito()));
        panel.add(crearBotonMenu("Retirar", e -> realizarRetiro()));
        panel.add(crearBotonMenu("Transferir", e -> realizarTransferencia()));
        panel.add(crearBotonMenu("Ver Historial", e -> verHistorial()));
        panel.add(crearBotonMenu("Consultar Saldo", e -> consultarSaldo()));
        panel.add(crearBotonMenu("Información de Cuenta", e -> verInformacionCuenta()));

        return panel;
    }

    private JButton crearBotonMenu(String texto, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.addActionListener(accion);
        return btn;
    }

    // ================= PANEL CENTRAL =================
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel lblBienvenida = new JLabel("Bienvenido a su banca en línea");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBienvenida.setForeground(new Color(40, 50, 70));

        lblSaldo = new JLabel();
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblSaldo.setForeground(new Color(46, 139, 87));

        JLabel lblInfo = new JLabel("Seleccione una operación desde el menú lateral.");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setForeground(Color.GRAY);

        panel.add(lblBienvenida);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblSaldo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblInfo);

        return panel;
    }

    // ================= MÉTODOS FUNCIONALES (COPIA EXACTA DEL ORIGINAL) =================

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
                    "Depósito realizado exitosamente\n" +
                            "Monto: $" + String.format("%.2f", monto) + "\n" +
                            "Nuevo saldo: $" + String.format("%.2f", usuarioActual.getSaldo()),
                    "Operación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void realizarRetiro() {
        String input = JOptionPane.showInputDialog(this,
                "Saldo disponible: $" + String.format("%.2f", usuarioActual.getSaldo()) + "\n" +
                        "Ingrese el monto a retirar:",
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
                        "Retiro realizado exitosamente\n" +
                                "Monto: $" + String.format("%.2f", monto) + "\n" +
                                "Nuevo saldo: $" + String.format("%.2f", usuarioActual.getSaldo()),
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SaldoInsuficienteException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Saldo Insuficiente",
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
                "Saldo disponible: $" + String.format("%.2f", usuarioActual.getSaldo()) + "\n" +
                        "Ingrese el monto a transferir:",
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
                        "Transferencia realizada exitosamente\n" +
                                "Destinatario: " + correoDestino + "\n" +
                                "Monto: $" + String.format("%.2f", monto) + "\n" +
                                "Nuevo saldo: $" + String.format("%.2f", usuarioActual.getSaldo()),
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SaldoInsuficienteException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Saldo Insuficiente",
                    JOptionPane.WARNING_MESSAGE);
        } catch (UsuarioNoEncontradoException e) {
            JOptionPane.showMessageDialog(this,
                    "El usuario con correo " + correoDestino + " no existe",
                    "Usuario No Encontrado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verHistorial() {
        new VentanaHistorial(usuarioActual, banco);
    }

    private void consultarSaldo() {
        double saldo = banco.consultarSaldo(usuarioActual.getId());
        JOptionPane.showMessageDialog(this,
                "Su saldo actual es:\n$" + String.format("%.2f", saldo),
                "Consulta de Saldo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void verInformacionCuenta() {
        String info = "Información de la Cuenta\n\n" +
                "ID de Usuario: " + usuarioActual.getId() + "\n" +
                "Nombre: " + usuarioActual.getNombre() + "\n" +
                "Correo: " + usuarioActual.getCorreo() + "\n" +
                "Saldo Actual: $" + String.format("%.2f", usuarioActual.getSaldo());

        JOptionPane.showMessageDialog(this,
                info,
                "Información de Cuenta",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarSaldo() {
        lblSaldo.setText("Saldo disponible: $" +
                String.format("%.2f", usuarioActual.getSaldo()));
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            ArchivoManager.guardarUsuarios(banco.getClientes());
            ArchivoManager.guardarTransacciones(banco);
            dispose();
            new LoginFrame();
        }
    }
}
