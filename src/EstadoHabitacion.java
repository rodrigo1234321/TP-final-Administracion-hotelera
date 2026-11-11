public enum EstadoHabitacion {

    //Los distintos valores que pueden tener las instancias de la enumeracion:
    DISPONIBLE("disponible"),
    OCUPADA("ocupada"),
    LIMPIEZA("limpieza"),
    REPARACION("reparacion"),
    DESINFECCION("desinfeccion");


    //Atributo de la enumeracion:
    private String estado;


    //Constructor de la enumeracion:
    private EstadoHabitacion(String estado)
    {
        this.estado = estado;
    }

    public String getEstado()
    {
        return estado;
    }

}
