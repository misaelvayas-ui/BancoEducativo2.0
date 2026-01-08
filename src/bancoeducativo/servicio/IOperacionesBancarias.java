package bancoeducativo.servicio;

import bancoeducativo.excepciones.SaldoInsuficienteException;
import bancoeducativo.excepciones.UsuarioNoEncontradoException;
import bancoeducativo.modelo.Transaccion;
import java.util.List;

/**
 * Interfaz que define las operaciones básicas de una cuenta bancaria
 */
public interface IOperacionesBancarias {

    /**
     * Realiza un depósito en la cuenta
     * @param idUsuario ID del usuario
     * @param monto Cantidad a depositar
     * @return true si la operación fue exitosa
     */
    boolean depositar(String idUsuario, double monto);

    /**
     * Realiza un retiro de la cuenta
     * @param idUsuario ID del usuario
     * @param monto Cantidad a retirar
     * @return true si la operación fue exitosa
     * @throws SaldoInsuficienteException si no hay fondos suficientes
     */
    boolean retirar(String idUsuario, double monto) throws SaldoInsuficienteException;

    /**
     * Transfiere dinero entre cuentas
     * @param idOrigen ID del usuario origen
     * @param correoDestino Correo del usuario destino
     * @param monto Cantidad a transferir
     * @return true si la operación fue exitosa
     * @throws SaldoInsuficienteException si no hay fondos suficientes
     * @throws UsuarioNoEncontradoException si el usuario destino no existe
     */
    boolean transferir(String idOrigen, String correoDestino, double monto)
            throws SaldoInsuficienteException, UsuarioNoEncontradoException;

    /**
     * Consulta el saldo de una cuenta
     * @param idUsuario ID del usuario
     * @return Saldo actual
     */
    double consultarSaldo(String idUsuario);

    /**
     * Obtiene el historial de transacciones
     * @param idUsuario ID del usuario
     * @return Lista de transacciones
     */
    List<Transaccion> obtenerHistorial(String idUsuario);
}