package bancoeducativo.servicio;

import bancoeducativo.modelo.Transaccion;
import bancoeducativo.modelo.Usuario;

import java.io.*;
import java.util.*;

/**
 * Gestor de persistencia de datos en archivos
 * Maneja lectura y escritura de usuarios y transacciones
 */
public class ArchivoManager {
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final String ARCHIVO_TRANSACCIONES = "transacciones.txt";
    
    /**
     * Guarda la lista de usuarios en archivo
     */
    public static void guardarUsuarios(List<Usuario> usuarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (Usuario c : usuarios) {
                bw.write(c.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Carga la lista de usuarios desde archivo
     */
    public static List<Usuario> cargarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return lista;
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length >= 5) {
                    Usuario c = new Usuario(p[0], p[1], p[2], p[3], 
                                           Double.parseDouble(p[4]));
                    lista.add(c);
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando usuarios: " + e.getMessage());
        }
        return lista;
    }
    
    /**
     * Guarda todas las transacciones del banco
     */
    public static void guardarTransacciones(Banco banco) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_TRANSACCIONES))) {
            Map<String, List<Transaccion>> todasTransacciones = 
                banco.obtenerTodasTransacciones();
            
            for (Map.Entry<String, List<Transaccion>> entry : todasTransacciones.entrySet()) {
                String idUsuario = entry.getKey();
                List<Transaccion> transacciones = entry.getValue();
                
                for (Transaccion t : transacciones) {
                    // Formato: idUsuario;transaccion_toString
                    bw.write(idUsuario + ";" + t.toString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando transacciones: " + e.getMessage());
        }
    }
    
    /**
     * Carga todas las transacciones desde archivo
     */
    public static Map<String, List<Transaccion>> cargarTransacciones() {
        Map<String, List<Transaccion>> transacciones = new HashMap<>();
        File archivo = new File(ARCHIVO_TRANSACCIONES);
        if (!archivo.exists()) return transacciones;
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_TRANSACCIONES))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";", 2); // Dividir solo en 2 partes
                if (partes.length >= 2) {
                    String idUsuario = partes[0];
                    String datosTransaccion = partes[1];
                    
                    Transaccion t = Transaccion.fromString(datosTransaccion);
                    
                    transacciones.computeIfAbsent(idUsuario, k -> new ArrayList<>()).add(t);
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando transacciones: " + e.getMessage());
        }
        
        return transacciones;
    }
    
    /**
     * Valida la integridad de los archivos
     */
    public static boolean validarIntegridad() {
        File archivoUsuarios = new File(ARCHIVO_USUARIOS);
        File archivoTransacciones = new File(ARCHIVO_TRANSACCIONES);
        
        // Si no existen, crear archivos vacíos
        if (!archivoUsuarios.exists()) {
            try {
                archivoUsuarios.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creando archivo de usuarios: " + e.getMessage());
                return false;
            }
        }
        
        if (!archivoTransacciones.exists()) {
            try {
                archivoTransacciones.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creando archivo de transacciones: " + e.getMessage());
                return false;
            }
        }
        
        return archivoUsuarios.canRead() && archivoUsuarios.canWrite() &&
               archivoTransacciones.canRead() && archivoTransacciones.canWrite();
    }
}
