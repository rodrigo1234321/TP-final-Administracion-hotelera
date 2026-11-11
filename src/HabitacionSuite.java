public final class HabitacionSuite extends Habitacion{
     
     //Atributos:
    private boolean tieneJacuzzi; //Indica si la habitacion tiene o no tiene jacuzzi
    private double servicioFrigobar; //El precio de ese servicio


    //Constructores:
    public HabitacionSuite(int numero, double precio, boolean jacuzzi, double frigobar) {
        super(numero, precio);
        this.tieneJacuzzi = jacuzzi;
        this.servicioFrigobar = frigobar;
    }


    //Todos los metodos de la clase:
    @Override
    public double calcularTarifa(int dias) //Calcula el precio de la tarifa de la habitacion suite
    {
        return (dias * precioPorNoche) + servicioFrigobar;
    }

    @Override
    public String toString() //Retorna una cadena de texto que muestra los detalles de la habitacion suite
    {
        return "Habitacion suite [ " +super.toString()
                +", tiene jacuzzi: " +tieneJacuzzi
                +", precio del servicio frigobar: $" +servicioFrigobar +"]";
    }
  
}
