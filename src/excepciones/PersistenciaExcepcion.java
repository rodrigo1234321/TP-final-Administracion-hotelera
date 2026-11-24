package excepciones;

// error al conectar, leer o guardar en MySQL
public class PersistenciaExcepcion extends Exception {
    public PersistenciaExcepcion(String mensaje) {
        super(mensaje);
    }

    public PersistenciaExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
