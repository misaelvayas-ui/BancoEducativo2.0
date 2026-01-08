package bancoeducativo.excepciones;

/**
 * Excepción lanzada cuando se busca un usuario que no existe en el sistema
 */
public class UsuarioNoEncontradoException extends Exception {
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
