public abstract class Habitacion {

  //Atributos:
    protected int numero; //El numero de la habitacion
    protected double precioPorNoche; //El precio de la habitacion por noche
    protected EstadoHabitacion estado; //Como esta la habitacion
    protected String motivoNoDisponible; //El motivo de porque la habitacion no esta disponible



    //Constructor:
    public Habitacion(int numero, double precioPorNoche)
    {
        this.numero = numero;
        this.precioPorNoche = precioPorNoche;
    }



    //Todos los demas metodos de la clase:
    public int getNumero() //Obtiene el numero de la habitacion
    {
        return numero;
    }

    public double getPrecioPorNoche() //Obtiene el precio por noche de la habitacion
    {
        return precioPorNoche;
    }

    public EstadoHabitacion getEstado() //Obtiene el estado de la habitacion
    {
        return estado;
    }

    public void setEstado(EstadoHabitacion e, String motivo) //Asigna un estado a la habitacion y su motivo de porque no esta disponible
    {
        this.estado = e;
        this.motivoNoDisponible = motivo;
    }

    public String getMotivoNoDisponible() //Obtiene el motivo del porque la habitacion no esta disponible
    {
        return motivoNoDisponible;
    }

    public double calcularTarifa(int dias) //Calcula el total a pagar por alquilar la habitacion
    {
        double tarifa = dias * precioPorNoche;

        return tarifa;
    }

    @Override
    public boolean equals(Object o) //Compara si dos habitaciones son iguales o no, dos habitaciones son iguales si tuviesen el mismo numero de habitacion
    {
        return this.numero == ((Habitacion) o).numero;
    }

    @Override
    public int hashCode() //Se obtiene un codigo unico de habitacion en base a su numero
    {
        return Integer.hashCode(numero);
    }

    @Override
    public String toString() //Muestra informacion detallada acerca de una habitacion
    {
        return "numero: " +numero
                +", precioPorNoche: $" +precioPorNoche
                +", estado: " +estado
                +", motivo de no disponible: " +motivoNoDisponible;
    }

}
