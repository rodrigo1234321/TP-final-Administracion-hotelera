public final class HabitacionSimple extends Habitacion{

   //Atributos:
    private boolean tieneCamaMatrimonial; //indica si la habitacion tiene o no cama matrimonial



    //Constructores:
    public HabitacionSimple(int numero, double precio, boolean matrimonial)
    {
        super(numero, precio);
        this.tieneCamaMatrimonial = matrimonial;
    }



    //Todos los metodos de la clase:
    @Override
    public String toString() //Muestra informacion detallada de una habitacion simple
    {
        return "Habitacion simple[ " +super.toString()
                +", tiene cama matrimonial: " +tieneCamaMatrimonial +"]";
    }

}
