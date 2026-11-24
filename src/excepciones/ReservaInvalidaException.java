package excepciones;

// operacion invalida sobre una reserva
public class ReservaInvalidaException extends Exception {
    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
