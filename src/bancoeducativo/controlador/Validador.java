package bancoeducativo.controlador;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para validar datos de entrada
 */
public class Validador {
    
    // Patrón para validar correo electrónico
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Valida el formato de un correo electrónico
     */
    public static boolean validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(correo).matches();
    }
    
    /**
     * Valida que la contraseña cumpla los requisitos mínimos
     */
    public static boolean validarContrasena(String contrasena) {
        if (contrasena == null) {
            return false;
        }
        // Mínimo 6 caracteres, al menos una letra y un número
        return contrasena.length() >= 6 && 
               contrasena.matches(".*[a-zA-Z].*") && 
               contrasena.matches(".*[0-9].*");
    }
    
    /**
     * Valida que el nombre no esté vacío y tenga longitud adecuada
     */
    public static boolean validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return nombre.trim().length() >= 3;
    }
    
    /**
     * Valida que un monto sea positivo y válido
     */
    public static boolean validarMonto(double monto) {
        return monto > 0 && monto < 1000000; // Límite máximo de 1 millón
    }
    
    /**
     * Valida que un monto sea positivo y válido (versión String)
     */
    public static boolean validarMonto(String montoStr) {
        try {
            double monto = Double.parseDouble(montoStr);
            return validarMonto(monto);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Obtiene mensaje de error para validación de correo
     */
    public static String getMensajeErrorCorreo() {
        return "El correo electrónico no tiene un formato válido";
    }
    
    /**
     * Obtiene mensaje de error para validación de contraseña
     */
    public static String getMensajeErrorContrasena() {
        return "La contraseña debe tener al menos 6 caracteres, \n" +
               "incluyendo letras y números";
    }
    
    /**
     * Obtiene mensaje de error para validación de nombre
     */
    public static String getMensajeErrorNombre() {
        return "El nombre debe tener al menos 3 caracteres";
    }
    
    /**
     * Obtiene mensaje de error para validación de monto
     */
    public static String getMensajeErrorMonto() {
        return "El monto debe ser un número positivo válido";
    }
}
