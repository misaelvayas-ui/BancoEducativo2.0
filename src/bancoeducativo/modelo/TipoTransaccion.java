package bancoeducativo.modelo;

/**
 * Enumeración que define los tipos de transacciones disponibles
 */
public enum TipoTransaccion {
    DEPOSITO("Depósito"),
    RETIRO("Retiro"),
    TRANSFERENCIA_ENVIADA("Transferencia Enviada"),
    TRANSFERENCIA_RECIBIDA("Transferencia Recibida");
    
    private final String descripcion;
    
    TipoTransaccion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
