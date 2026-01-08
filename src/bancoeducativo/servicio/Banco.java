package bancoeducativo.servicio;

import bancoeducativo.excepciones.SaldoInsuficienteException;
import bancoeducativo.excepciones.UsuarioNoEncontradoException;
import bancoeducativo.modelo.CuentaBancaria;
import bancoeducativo.modelo.Transaccion;
import bancoeducativo.modelo.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal que gestiona todas las operaciones del banco
 * Implementa la interfaz IOperacionesBancarias
 */
public class Banco implements IOperacionesBancarias {
    private List<Usuario> usuarios;
    private Map<String, CuentaBancaria> cuentas; // idUsuario -> CuentaBancaria
    
    public Banco() {
        usuarios = new ArrayList<>();
        cuentas = new HashMap<>();
    }
    
    public void setUsuarios(List<Usuario> lista) {
        usuarios = lista;
        // Crear cuentas bancarias para cada usuario
        for (Usuario u : usuarios) {
            CuentaBancaria cuenta = new CuentaBancaria(u.getId(), u, u.getSaldo());
            cuentas.put(u.getId(), cuenta);
        }
    }
    
    public void registrarUsuarios(Usuario c) {
        usuarios.add(c);
        // Crear cuenta bancaria para el nuevo usuario
        CuentaBancaria cuenta = new CuentaBancaria(c.getId(), c, c.getSaldo());
        cuentas.put(c.getId(), cuenta);
    }
    
    public Usuario autenticar(String correo, String contrasena) {
        for (Usuario c : usuarios) {
            if (c.getCorreo().equals(correo) &&
                    c.getContrasena().equals(contrasena)) {
                return c;
            }
        }
        return null;
    }
    
    public boolean existeCorreo(String correo) {
        for (Usuario c : usuarios) {
            if (c.getCorreo().equals(correo)) return true;
        }
        return false;
    }
    
    public Usuario buscarPorCorreo(String correo) throws UsuarioNoEncontradoException {
        for (Usuario u : usuarios) {
            if (u.getCorreo().equals(correo)) {
                return u;
            }
        }
        throw new UsuarioNoEncontradoException("Usuario no encontrado: " + correo);
    }
    
    public List<Usuario> getClientes() {
        return usuarios;
    }
    
    public CuentaBancaria getCuenta(String idUsuario) {
        return cuentas.get(idUsuario);
    }
    
    // Implementación de IOperacionesBancarias
    
    @Override
    public boolean depositar(String idUsuario, double monto) {
        CuentaBancaria cuenta = cuentas.get(idUsuario);
        if (cuenta == null) return false;
        
        return cuenta.depositar(monto);
    }
    
    @Override
    public boolean retirar(String idUsuario, double monto) throws SaldoInsuficienteException {
        CuentaBancaria cuenta = cuentas.get(idUsuario);
        if (cuenta == null) return false;
        
        return cuenta.retirar(monto);
    }
    
    @Override
    public boolean transferir(String idOrigen, String correoDestino, double monto) 
            throws SaldoInsuficienteException, UsuarioNoEncontradoException
    {
        CuentaBancaria cuentaOrigen = cuentas.get(idOrigen);
        if (cuentaOrigen == null) return false;
        
        Usuario usuarioDestino = buscarPorCorreo(correoDestino);
        CuentaBancaria cuentaDestino = cuentas.get(usuarioDestino.getId());
        
        if (cuentaDestino == null) {
            throw new UsuarioNoEncontradoException("Cuenta destino no encontrada");
        }
        
        return cuentaOrigen.transferir(cuentaDestino, monto);
    }
    
    @Override
    public double consultarSaldo(String idUsuario) {
        CuentaBancaria cuenta = cuentas.get(idUsuario);
        if (cuenta == null) return 0.0;
        
        return cuenta.consultarSaldo();
    }
    
    @Override
    public List<Transaccion> obtenerHistorial(String idUsuario) {
        CuentaBancaria cuenta = cuentas.get(idUsuario);
        if (cuenta == null) return new ArrayList<>();
        
        return cuenta.obtenerHistorial();
    }
    
    /**
     * Obtiene todas las transacciones de todos los usuarios
     * Usado para guardar en archivo
     */
    public Map<String, List<Transaccion>> obtenerTodasTransacciones() {
        Map<String, List<Transaccion>> todasTransacciones = new HashMap<>();
        
        for (Map.Entry<String, CuentaBancaria> entry : cuentas.entrySet()) {
            todasTransacciones.put(entry.getKey(), entry.getValue().obtenerHistorial());
        }
        
        return todasTransacciones;
    }
    
    /**
     * Carga las transacciones desde el archivo
     */
    public void cargarTransacciones(Map<String, List<Transaccion>> transacciones) {
        for (Map.Entry<String, List<Transaccion>> entry : transacciones.entrySet()) {
            String idUsuario = entry.getKey();
            List<Transaccion> lista = entry.getValue();
            
            CuentaBancaria cuenta = cuentas.get(idUsuario);
            if (cuenta != null) {
                for (Transaccion t : lista) {
                    cuenta.agregarTransaccion(t);
                }
            }
        }
    }
}
