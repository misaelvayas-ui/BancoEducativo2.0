package bancoeducativo.modelo;

public class Usuario {
    private  String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private double saldo;

    public Usuario(String id, String nombre, String correo, String contrasena, double saldo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.saldo = saldo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return id + ";"+ nombre + ";"+ correo + ";"+ contrasena + ";"+ saldo;
    }
}