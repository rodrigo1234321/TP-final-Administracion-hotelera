package excepciones;

// habitacion invalida, bloqueada u ocupada
public class HabitacionNoDisponibleException extends Exception {
    public HabitacionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
