package modelo;

// estados que puede tener una habitacion
public enum EstadoHabitacion {

    DISPONIBLE("disponible"),
    OCUPADA("ocupada"),
    LIMPIEZA("limpieza"),
    REPARACION("reparacion"),
    DESINFECCION("desinfeccion");

    private String estado;

    private EstadoHabitacion(String estado)
    {
        this.estado = estado;
    }

    public String getEstado()
    {
        return estado;
    }

}
