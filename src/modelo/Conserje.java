package modelo;

// reutiliza los datos del personal de recepcion
public class Conserje extends Recepcionista {
    public Conserje(String dni, String nombre, String origen, String domicilio,
                    int nroLegajo, String turno) {
        super(dni, nombre, origen, domicilio, nroLegajo, turno);
    }
}
