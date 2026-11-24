package modelo;

// datos y tarifa base de una habitacion
public abstract class Habitacion {

    protected int numero;
    protected double precioPorNoche;
    protected EstadoHabitacion estado;
    protected String motivoNoDisponible;

    public Habitacion(int numero, double precioPorNoche)
    {
        this.numero = numero;
        this.precioPorNoche = precioPorNoche;
        this.estado = EstadoHabitacion.DISPONIBLE;
    }

    public int getNumero()
    {
        return numero;
    }

    public double getPrecioPorNoche()
    {
        return precioPorNoche;
    }

    public EstadoHabitacion getEstado()
    {
        return estado;
    }

    public void setEstado(EstadoHabitacion e, String motivo)
    {
        this.estado = e;
        this.motivoNoDisponible = motivo;
    }

    public String getMotivoNoDisponible()
    {
        return motivoNoDisponible;
    }

    public double calcularTarifa(int dias)
    {
        double tarifa = dias * precioPorNoche;

        return tarifa;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Habitacion))
        {
            return false;
        }
        Habitacion habitacion = (Habitacion) o;
        return this.numero == habitacion.numero;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(numero);
    }

    @Override
    public String toString()
    {
        return "numero: " +numero
                +", precioPorNoche: $" +precioPorNoche
                +", estado: " +estado
                +", motivo de no disponible: " +motivoNoDisponible;
    }

}
