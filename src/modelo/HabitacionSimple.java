package modelo;

// habitacion simple con opcion de cama matrimonial
public final class HabitacionSimple extends Habitacion{

    private boolean tieneCamaMatrimonial;

    public HabitacionSimple(int numero, double precio, boolean matrimonial)
    {
        super(numero, precio);
        this.tieneCamaMatrimonial = matrimonial;
    }

    public boolean tieneCamaMatrimonial() {
        return tieneCamaMatrimonial;
    }

    @Override
    public String toString()
    {
        return "Habitacion simple[ " +super.toString()
                +", tiene cama matrimonial: " +tieneCamaMatrimonial +"]";
    }

}
