package bancoeducativo.modelo;

import bancoeducativo.excepciones.SaldoInsuficienteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona las operaciones financieras de una cuenta bancaria
 */
public class CuentaBancaria {
    private String numeroCuenta;
    private Usuario titular;
    private double saldo;
    private LocalDateTime fechaCreacion;
    private List<Transaccion> historialTransacciones;
    
    public CuentaBancaria(String numeroCuenta, Usuario titular, double saldoInicial) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldoInicial;
        this.fechaCreacion = LocalDateTime.now();
        this.historialTransacciones = new ArrayList<>();
    }
    
    /**
     * Realiza un depósito en la cuenta
     * @param monto Cantidad a depositar
     * @return true si la operación fue exitosa
     */
    public boolean depositar(double monto) {
        if (monto <= 0) {
            return false;
        }
        
        saldo += monto;
        titular.setSaldo(saldo);
        
        // Registrar transacción
        String idTransaccion = generarIdTransaccion();
        Transaccion transaccion = new Transaccion(
            idTransaccion,
            TipoTransaccion.DEPOSITO,
            monto,
            numeroCuenta,
            null,
            saldo
        );
        historialTransacciones.add(transaccion);
        
        return true;
    }
    
    /**
     * Realiza un retiro de la cuenta
     * @param monto Cantidad a retirar
     * @return true si la operación fue exitosa
     * @throws SaldoInsuficienteException si no hay fondos suficientes
     */
    public boolean retirar(double monto) throws SaldoInsuficienteException {
        if (monto <= 0) {
            return false;
        }
        
        if (!validarSaldo(monto)) {
            throw new SaldoInsuficienteException(
                "Saldo insuficiente. Disponible: $" + String.format("%.2f", saldo)
            );
        }
        
        saldo -= monto;
        titular.setSaldo(saldo);
        
        // Registrar transacción
        String idTransaccion = generarIdTransaccion();
        Transaccion transaccion = new Transaccion(
            idTransaccion,
            TipoTransaccion.RETIRO,
            monto,
            numeroCuenta,
            null,
            saldo
        );
        historialTransacciones.add(transaccion);
        
        return true;
    }
    
    /**
     * Transfiere dinero a otra cuenta
     * @param cuentaDestino Cuenta que recibirá el dinero
     * @param monto Cantidad a transferir
     * @return true si la operación fue exitosa
     * @throws SaldoInsuficienteException si no hay fondos suficientes
     */
    public boolean transferir(CuentaBancaria cuentaDestino, double monto) 
            throws SaldoInsuficienteException {
        if (monto <= 0 || cuentaDestino == null) {
            return false;
        }
        
        if (!validarSaldo(monto)) {
            throw new SaldoInsuficienteException(
                "Saldo insuficiente para transferir. Disponible: $" + 
                String.format("%.2f", saldo)
            );
        }
        
        // Retirar de cuenta origen
        saldo -= monto;
        titular.setSaldo(saldo);
        
        // Depositar en cuenta destino
        cuentaDestino.saldo += monto;
        cuentaDestino.titular.setSaldo(cuentaDestino.saldo);
        
        // Registrar transacción de salida
        String idTransaccionSalida = generarIdTransaccion();
        Transaccion transaccionSalida = new Transaccion(
            idTransaccionSalida,
            TipoTransaccion.TRANSFERENCIA_ENVIADA,
            monto,
            numeroCuenta,
            cuentaDestino.numeroCuenta,
            saldo
        );
        historialTransacciones.add(transaccionSalida);
        
        // Registrar transacción de entrada en cuenta destino
        String idTransaccionEntrada = generarIdTransaccion();
        Transaccion transaccionEntrada = new Transaccion(
            idTransaccionEntrada,
            TipoTransaccion.TRANSFERENCIA_RECIBIDA,
            monto,
            numeroCuenta,
            cuentaDestino.numeroCuenta,
            cuentaDestino.saldo
        );
        cuentaDestino.historialTransacciones.add(transaccionEntrada);
        
        return true;
    }
    
    /**
     * Consulta el saldo actual de la cuenta
     */
    public double consultarSaldo() {
        return saldo;
    }
    
    /**
     * Valida si hay saldo suficiente para una operación
     */
    public boolean validarSaldo(double monto) {
        return saldo >= monto;
    }
    
    /**
     * Obtiene el historial completo de transacciones
     */
    public List<Transaccion> obtenerHistorial() {
        return new ArrayList<>(historialTransacciones);
    }
    
    /**
     * Agrega una transacción al historial (usado al cargar desde archivo)
     */
    public void agregarTransaccion(Transaccion transaccion) {
        historialTransacciones.add(transaccion);
    }
    
    /**
     * Genera un ID único para cada transacción
     */
    private String generarIdTransaccion() {
        return "TRX" + System.currentTimeMillis() + 
               (int)(Math.random() * 1000);
    }
    
    // Getters
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    
    public Usuario getTitular() {
        return titular;
    }
    
    public double getSaldo() {
        return saldo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
