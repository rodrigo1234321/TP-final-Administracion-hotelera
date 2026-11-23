import java.time.LocalDate;

public final class Reserva implements IReservable{

    //Atributos:
    private int idReserva;
    private Pasajero pasajero; //Pasajero que realiza la reserva
    private Habitacion habitacion; //habitacion en la que se realizo la reserva
    private LocalDate fechaInicio; //Fecha de ingreso prevista
    private LocalDate fechaFin; //Fecha de salida prevista
    private boolean checkInRealizado; //Indica si se inicio la estadia del pasajero en la habitacion
    private boolean checkOutRealizado; //Indica si se finalizo la estadia del pasajero en la habitacion
    private boolean cancelada;



    //Constructor de la clase:
    public Reserva(int id, Pasajero p, Habitacion h, LocalDate ini, LocalDate fin)
            throws ReservaInvalidaException
    {
        if (id <= 0 || h == null)
        {
            throw new ReservaInvalidaException("La reserva requiere un ID válido y una habitación.");
        }
        this.idReserva = id;
        this.habitacion = h;
        if (!reservar(p, ini, fin))
        {
            throw new ReservaInvalidaException("Revisa el pasajero y las fechas de la reserva.");
        }
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

    //La fecha de salida queda fuera del periodo reservado.
    public boolean isActivaEnPeriodo(LocalDate ini, LocalDate fin)
    {
        if (cancelada || checkOutRealizado || ini == null || fin == null || !ini.isBefore(fin))
        {
            return false;
        }
        return fechaInicio.isBefore(fin) && ini.isBefore(fechaFin);
    }

    public void realizarCheckIn() throws ReservaInvalidaException //Inicia la estadia
    {
        if (cancelada || checkInRealizado)
        {
            throw new ReservaInvalidaException("La reserva no admite el check-in.");
        }
        checkInRealizado = true;
    }

    public void realizarCheckOut() throws ReservaInvalidaException //Finaliza la estadia
    {
        if (!checkInRealizado || checkOutRealizado)
        {
            throw new ReservaInvalidaException("La reserva no admite el check-out.");
        }
        checkOutRealizado = true;
    }

    //Resta el dia de entrada al dia de salida para obtener las noches.
    public double calcularTotal() throws ReservaInvalidaException
    {
        int dias = (int) (fechaFin.toEpochDay() - fechaInicio.toEpochDay());
        return calcularCostoEstadia(dias);
    }

    //Asigna el pasajero y las fechas una sola vez al construir la reserva.
    @Override
    public boolean reservar(Pasajero p, LocalDate inicio, LocalDate fin)
    {
        if (pasajero != null || p == null || inicio == null || fin == null
                || !inicio.isBefore(fin)
                || (fin.toEpochDay() - inicio.toEpochDay()) > Integer.MAX_VALUE)
        {
            return false;
        }
        this.pasajero = p;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        return true;
    }

    @Override
    public boolean cancelarReserva(int idReserva) //Cancela una reserva antes del check-in
    {
        if (this.idReserva != idReserva || cancelada || checkInRealizado)
        {
            return false;
        }
        cancelada = true;
        return true;
    }

    @Override
    public double calcularCostoEstadia(int dias) throws ReservaInvalidaException
    {
        if (dias <= 0)
        {
            throw new ReservaInvalidaException("La estadia tiene que durar al menos una noche.");
        }
        //Cada tipo de habitacion aplica su propia tarifa.
        return habitacion.calcularTarifa(dias);
    }

}
