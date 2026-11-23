// Indica que los datos o el estado de la reserva impiden la operacion.
public class ReservaInvalidaException extends Exception {
    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
