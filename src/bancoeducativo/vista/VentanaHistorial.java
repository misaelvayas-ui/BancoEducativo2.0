package bancoeducativo.vista;

import bancoeducativo.modelo.Transaccion;
import bancoeducativo.modelo.TipoTransaccion;
import bancoeducativo.modelo.Usuario;
import bancoeducativo.servicio.Banco;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ventana mejorada para visualizar el historial de transacciones
 */
public class VentanaHistorial extends JFrame {
    private Usuario usuario;
    private Banco banco;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> comboFiltro;
    private PlaceholderTextField txtBusqueda;
    private JLabel lblTotal, lblCantidad;
    private List<Transaccion> todasTransacciones;

    public VentanaHistorial(Usuario usuario, Banco banco) {
        this.usuario = usuario;
        this.banco = banco;
        this.todasTransacciones = banco.obtenerHistorial(usuario.getId());

        setTitle("Historial de Transacciones - " + usuario.getNombre());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        inicializarComponentes();
        cargarHistorial();
        actualizarEstadisticas();

        setVisible(true);
    }
    
    private void inicializarComponentes() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(EsteticaUI.COLOR_FONDO);

        // Panel superior - Header
        panelPrincipal.add(crearPanelHeader(), BorderLayout.NORTH);

        // Panel central - Filtros y tabla
        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setBackground(EsteticaUI.COLOR_FONDO);
        panelCentro.setBorder(new EmptyBorder(20, 20, 10, 20));

        panelCentro.add(crearPanelFiltros(), BorderLayout.NORTH);
        panelCentro.add(crearPanelTabla(), BorderLayout.CENTER);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Panel inferior - Estadísticas y botones
        panelPrincipal.add(crearPanelInferior(), BorderLayout.SOUTH);

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
                    0, 0, EsteticaUI.COLOR_INFO,
                    0, getHeight(), EsteticaUI.COLOR_INFO.darker()
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(1000, 90));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitulo = new JLabel("Historial de Movimientos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Consulte todas sus transacciones realizadas");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(230, 240, 250));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblSubtitulo);

        return panel;
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFiltro = new JLabel("Filtrar por tipo:");
        lblFiltro.setFont(EsteticaUI.FUENTE_NEGRITA);

        comboFiltro = new JComboBox<>(new String[]{
            "Todas las transacciones",
            "Depósitos",
            "Retiros",
            "Transferencias Enviadas",
            "Transferencias Recibidas"
        });
        comboFiltro.setFont(EsteticaUI.FUENTE_NORMAL);
        comboFiltro.setPreferredSize(new Dimension(220, 35));
        comboFiltro.addActionListener(e -> aplicarFiltros());

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(EsteticaUI.FUENTE_NEGRITA);

        txtBusqueda = new PlaceholderTextField("Buscar por ID, monto o destino...");
        txtBusqueda.setPreferredSize(new Dimension(280, 35));
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                aplicarFiltros();
            }
        });

        RoundedButton btnLimpiar = new RoundedButton("Limpiar", EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        btnLimpiar.setPreferredSize(new Dimension(100, 35));
        btnLimpiar.addActionListener(e -> limpiarFiltros());

        panel.add(lblFiltro);
        panel.add(comboFiltro);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblBuscar);
        panel.add(txtBusqueda);
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(EsteticaUI.COLOR_BORDE, 1));

        // Crear tabla
        String[] columnas = {"ID Transacción", "Tipo", "Monto", "Fecha", 
                             "Destino/Origen", "Saldo Resultante"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2 || column == 5) return Double.class;
                return String.class;
            }
        };
        
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(32);
        tabla.setFont(EsteticaUI.FUENTE_NORMAL);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(240, 240, 240));
        tabla.setSelectionBackground(new Color(230, 240, 255));
        tabla.setSelectionForeground(EsteticaUI.COLOR_TEXTO);

        // Configurar header
        JTableHeader header = tabla.getTableHeader();
        header.setFont(EsteticaUI.FUENTE_NEGRITA);
        header.setBackground(EsteticaUI.COLOR_FONDO);
        header.setForeground(EsteticaUI.COLOR_TEXTO);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Renderer personalizado para columnas de monto
        DefaultTableCellRenderer montoRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value instanceof Double) {
                    setText("$" + String.format("%.2f", (Double) value));
                    setHorizontalAlignment(SwingConstants.RIGHT);

                    if (!isSelected) {
                        String tipo = (String) table.getValueAt(row, 1);
                        if (tipo.contains("Depósito") || tipo.contains("Recibida")) {
                            setForeground(EsteticaUI.COLOR_EXITO);
                        } else if (tipo.contains("Retiro") || tipo.contains("Enviada")) {
                            setForeground(EsteticaUI.COLOR_ERROR);
                        } else {
                            setForeground(EsteticaUI.COLOR_TEXTO);
                        }
                    }
                }

                return c;
            }
        };

        tabla.getColumnModel().getColumn(2).setCellRenderer(montoRenderer);
        tabla.getColumnModel().getColumn(5).setCellRenderer(montoRenderer);

        // Renderer para el tipo de transacción
        tabla.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected && value != null) {
                    String tipo = value.toString();
                    if (tipo.contains("Depósito") || tipo.contains("Recibida")) {
                        setForeground(EsteticaUI.COLOR_EXITO);
                    } else if (tipo.contains("Retiro") || tipo.contains("Enviada")) {
                        setForeground(EsteticaUI.COLOR_ERROR);
                    } else {
                        setForeground(EsteticaUI.COLOR_TEXTO);
                    }
                }

                return c;
            }
        });

        // Sorter para ordenamiento
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, EsteticaUI.COLOR_BORDE),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Panel izquierdo - Estadísticas
        JPanel panelEstadisticas = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panelEstadisticas.setOpaque(false);

        lblCantidad = new JLabel("Transacciones: 0");
        lblCantidad.setFont(EsteticaUI.FUENTE_NEGRITA);
        lblCantidad.setForeground(EsteticaUI.COLOR_TEXTO);

        lblTotal = new JLabel("Total mostrado: $0.00");
        lblTotal.setFont(EsteticaUI.FUENTE_NEGRITA);
        lblTotal.setForeground(EsteticaUI.COLOR_SECUNDARIO);

        panelEstadisticas.add(lblCantidad);
        panelEstadisticas.add(new JSeparator(SwingConstants.VERTICAL));
        panelEstadisticas.add(lblTotal);

        // Panel derecho - Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setOpaque(false);

        RoundedButton btnActualizar = new RoundedButton("Actualizar", EsteticaUI.COLOR_INFO);
        btnActualizar.setPreferredSize(new Dimension(130, 38));
        btnActualizar.addActionListener(e -> {
            todasTransacciones = banco.obtenerHistorial(usuario.getId());
            cargarHistorial();
            actualizarEstadisticas();
        });

        RoundedButton btnExportar = new RoundedButton("Exportar", EsteticaUI.COLOR_SECUNDARIO);
        btnExportar.setPreferredSize(new Dimension(130, 38));
        btnExportar.addActionListener(e -> exportarHistorial());

        RoundedButton btnCerrar = new RoundedButton("Cerrar", EsteticaUI.COLOR_TEXTO_SECUNDARIO);
        btnCerrar.setPreferredSize(new Dimension(100, 38));
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);

        panel.add(panelEstadisticas, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);

        return panel;
    }
    
    private void cargarHistorial() {
        modeloTabla.setRowCount(0);

        if (todasTransacciones.isEmpty()) {
            return;
        }
        
        for (Transaccion t : todasTransacciones) {
            Object[] fila = new Object[6];
            fila[0] = t.getIdTransaccion();
            fila[1] = t.getTipo().getDescripcion();
            fila[2] = t.getMonto();
            fila[3] = t.getFechaFormateada();
            fila[4] = t.getCuentaDestino() != null ? t.getCuentaDestino() : "-";
            fila[5] = t.getSaldoResultante();

            modeloTabla.addRow(fila);
        }
    }

    private void aplicarFiltros() {
        String filtroTipo = (String) comboFiltro.getSelectedItem();
        String busqueda = txtBusqueda.getText().trim().toLowerCase();

        List<Transaccion> transaccionesFiltradas = todasTransacciones;

        // Filtrar por tipo
        if (!filtroTipo.equals("Todas las transacciones")) {
            transaccionesFiltradas = transaccionesFiltradas.stream()
                .filter(t -> {
                    String tipo = t.getTipo().getDescripcion();
                    switch (filtroTipo) {
                        case "Depósitos":
                            return t.getTipo() == TipoTransaccion.DEPOSITO;
                        case "Retiros":
                            return t.getTipo() == TipoTransaccion.RETIRO;
                        case "Transferencias Enviadas":
                            return t.getTipo() == TipoTransaccion.TRANSFERENCIA_ENVIADA;
                        case "Transferencias Recibidas":
                            return t.getTipo() == TipoTransaccion.TRANSFERENCIA_RECIBIDA;
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
        }

        // Filtrar por búsqueda
        if (!busqueda.isEmpty()) {
            transaccionesFiltradas = transaccionesFiltradas.stream()
                .filter(t -> {
                    String id = t.getIdTransaccion().toLowerCase();
                    String monto = String.format("%.2f", t.getMonto());
                    String destino = t.getCuentaDestino() != null ? t.getCuentaDestino().toLowerCase() : "";

                    return id.contains(busqueda) ||
                           monto.contains(busqueda) ||
                           destino.contains(busqueda);
                })
                .collect(Collectors.toList());
        }

        // Actualizar tabla
        modeloTabla.setRowCount(0);
        for (Transaccion t : transaccionesFiltradas) {
            Object[] fila = new Object[6];
            fila[0] = t.getIdTransaccion();
            fila[1] = t.getTipo().getDescripcion();
            fila[2] = t.getMonto();
            fila[3] = t.getFechaFormateada();
            fila[4] = t.getCuentaDestino() != null ? t.getCuentaDestino() : "-";
            fila[5] = t.getSaldoResultante();
            modeloTabla.addRow(fila);
        }

        actualizarEstadisticas();
    }

    private void limpiarFiltros() {
        comboFiltro.setSelectedIndex(0);
        txtBusqueda.setText("");
        cargarHistorial();
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int cantidad = tabla.getRowCount();
        lblCantidad.setText("Transacciones: " + cantidad);

        double total = 0.0;
        for (int i = 0; i < cantidad; i++) {
            Double monto = (Double) tabla.getValueAt(i, 2);
            total += monto;
        }
        lblTotal.setText("Total mostrado: $" + String.format("%.2f", total));
    }

    private void exportarHistorial() {
        if (tabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay transacciones para exportar",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Simular exportación (en una aplicación real, se exportaría a CSV/PDF)
        StringBuilder sb = new StringBuilder();
        sb.append("HISTORIAL DE TRANSACCIONES\n");
        sb.append("Usuario: ").append(usuario.getNombre()).append("\n");
        sb.append("Correo: ").append(usuario.getCorreo()).append("\n\n");

        for (int i = 0; i < tabla.getRowCount(); i++) {
            sb.append(tabla.getValueAt(i, 0)).append(" | ");
            sb.append(tabla.getValueAt(i, 1)).append(" | ");
            sb.append("$").append(tabla.getValueAt(i, 2)).append(" | ");
            sb.append(tabla.getValueAt(i, 3)).append(" | ");
            sb.append(tabla.getValueAt(i, 4)).append(" | ");
            sb.append("$").append(tabla.getValueAt(i, 5)).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 15px;'>" +
                "<b>[OK] Funcionalidad de Exportación</b><br><br>" +
                "En una aplicación real, aquí se exportarían<br>" +
                "las transacciones a formato CSV o PDF.<br><br>" +
                "Total de transacciones: " + tabla.getRowCount() +
                "</div></html>",
                "Exportar Historial",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
