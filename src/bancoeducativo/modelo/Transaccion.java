package bancoeducativo.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa una transacción bancaria
 * Registra todos los detalles de cada operación realizada
 */
public class Transaccion {
    private String idTransaccion;
    private TipoTransaccion tipo;
    private double monto;
    private LocalDateTime fecha;
    private String cuentaOrigen;
    private String cuentaDestino;
    private double saldoResultante;
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public Transaccion(String idTransaccion, TipoTransaccion tipo, double monto,
                       String cuentaOrigen, String cuentaDestino, double saldoResultante) {
        this.idTransaccion = idTransaccion;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.saldoResultante = saldoResultante;
    }
    
    // Constructor para cargar desde archivo
    public Transaccion(String idTransaccion, TipoTransaccion tipo, double monto,
                       LocalDateTime fecha, String cuentaOrigen, String cuentaDestino, 
                       double saldoResultante) {
        this.idTransaccion = idTransaccion;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.saldoResultante = saldoResultante;
    }
    
    public String getIdTransaccion() {
        return idTransaccion;
    }
    
    public TipoTransaccion getTipo() {
        return tipo;
    }
    
    public double getMonto() {
        return monto;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public String getCuentaOrigen() {
        return cuentaOrigen;
    }
    
    public String getCuentaDestino() {
        return cuentaDestino;
    }
    
    public double getSaldoResultante() {
        return saldoResultante;
    }
    
    public String getFechaFormateada() {
        return fecha.format(FORMATTER);
    }
    
    /**
     * Obtiene los detalles completos de la transacción
     */
    public String obtenerDetalles() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(idTransaccion);
        sb.append("\nTipo: ").append(tipo.getDescripcion());
        sb.append("\nMonto: $").append(String.format("%.2f", monto));
        sb.append("\nFecha: ").append(getFechaFormateada());
        
        if (cuentaDestino != null && !cuentaDestino.isEmpty()) {
            sb.append("\nDestino: ").append(cuentaDestino);
        }
        
        sb.append("\nSaldo Resultante: $").append(String.format("%.2f", saldoResultante));
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return idTransaccion + ";" + tipo.name() + ";" + monto + ";" + 
               fecha.toString() + ";" + cuentaOrigen + ";" + 
               (cuentaDestino != null ? cuentaDestino : "") + ";" + saldoResultante;
    }
    
    /**
     * Crea una transacción desde una línea de texto
     */
    public static Transaccion fromString(String linea) {
        String[] partes = linea.split(";");
        return new Transaccion(
            partes[0],
            TipoTransaccion.valueOf(partes[1]),
            Double.parseDouble(partes[2]),
            LocalDateTime.parse(partes[3]),
            partes[4],
            partes.length > 5 ? partes[5] : null,
            Double.parseDouble(partes[6])
        );
    }
}
