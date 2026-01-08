package bancoeducativo.excepciones;
/**
 * Excepción lanzada cuando las credenciales de autenticación son incorrectas
 */
public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
