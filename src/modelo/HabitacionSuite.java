package modelo;

// suite con jacuzzi opcional y cargo de frigobar
public final class HabitacionSuite extends Habitacion{

    private boolean tieneJacuzzi;
    private double servicioFrigobar;

    public HabitacionSuite(int numero, double precio, boolean jacuzzi, double frigobar) {
        super(numero, precio);
        this.tieneJacuzzi = jacuzzi;
        this.servicioFrigobar = frigobar;
    }

    public boolean tieneJacuzzi() {
        return tieneJacuzzi;
    }

    public double getServicioFrigobar() {
        return servicioFrigobar;
    }

    @Override
    public double calcularTarifa(int dias)
    {
        return (dias * precioPorNoche) + servicioFrigobar;
    }

    @Override
    public String toString()
    {
        return "Habitacion suite [ " +super.toString()
                +", tiene jacuzzi: " +tieneJacuzzi
                +", precio del servicio frigobar: $" +servicioFrigobar +"]";
    }

}
