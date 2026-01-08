package bancoeducativo.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación sin fondos suficientes
 */
public class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
