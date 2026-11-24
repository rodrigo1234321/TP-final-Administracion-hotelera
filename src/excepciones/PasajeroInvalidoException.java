package excepciones;

// datos de pasajero invalidos o repetidos
public class PasajeroInvalidoException extends Exception {
    public PasajeroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
