public class Reserva implements Ireservable{

    //Atributos:
    private int idReserva;
    private Pasajero pasajero; //Pasajero que realiza la reserva
    private Habitacion habitacion; //habitacion en la que se realizo la reserva
    private LocalDate fechaInicio; //Fecha en la que se inicio la reserva
    private LocalDate fechaFin; //Fecha en la que se finalizo la reserva
    private boolean checkInRealizado; //Indica si se inicio la estadia del pasajero en la habitacion
    private boolean checkOutRealizado; //Indica si se finalizo la estadia del pasajero en la habitacion



    //Constructor de la clase:
    public void Reserva(int id, Pasajero p, Habitacion h, LocalDate ini, LocalDate fin)
    {
        this.idReserva = id;
        this.pasajero = p;
        this.habitacion = h;
        this.fechaInicio = ini;
        this.fechaFin = fin;
    }



    //Todos los metodos de la clase:
    public int getIdReserva() //Obtiene el id de la reserva
    {
        return idReserva;
    }

    public Pasajero getPasajero() //Obtiene al pasajero que realizo la reserva
    {
        return pasajero;
    }

    public Habitacion getHabitacion() //Obtiene la habitacion de la reserva
    {
        return habitacion;
    }

    public boolean isActivaEnPeriodo(LocalDate ini, LocalDate fin) //Indica si se finalizo la reserva o no
    {
         if(ini.compareTo(fin) >= 0)
         {
             return true;
         }
         else
         {
             return false;
         }
    }

    public void realizarCheckIn() //Se inicia la estadia del pasajero en la habitacion
    {
        checkInRealizado = true;
    }

    public void realizarCheckOut() //Se finaliza la estadia del pasajero en la habitacion
    {
        checkOutRealizado = true;
    }

    public double calcularTotal() //Calcula el precio total de la reserva
    {
        double total = (fechaInicio.getDayOfYear() - fechaFin.getDayOfYear()) * habitacion.getPrecioPorNoche();

        return total;
    }

    public boolean reservar(Pasajero p, LocalDate inicio, LocalDate fin) //Se realiza la reserva
    {
        if(this.checkInRealizado == true)
        {
            return false; //No se pudo realizar la reserva
        }
        else
        {
            this.pasajero = p;
            this.fechaInicio = inicio;
            this.fechaFin = fin;
            this.checkInRealizado = true;

            return true; //Se pudo realizar la reserva
        }

    }

    public boolean cancelarReserva(int idReserva) //Se realiza una cancelacion de la reserva
    {
        if(this.checkInRealizado == true)
        {
            checkInRealizado = false;

            return true; //Se pudo cancelar la reserva
        }
        else
        {
            return false; //No se pudo cancelar la reserva
        }
    }

    public double calcularCostoEstadia(int dias) //Se realiza el calculo del costo de la estadia
    {
        double costoEstadia = dias * habitacion.getPrecioPorNoche();

        return costoEstadia;
    }

}
