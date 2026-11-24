package excepciones;

// error en una cuenta de usuario
public class UsuarioInvalidoException extends Exception {
    public UsuarioInvalidoException(String mensaje) {
        super(mensaje);
    }

    public UsuarioInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
