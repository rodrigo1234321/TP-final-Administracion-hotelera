package modelo;

import excepciones.ReservaInvalidaException;

import java.time.LocalDate;

// reserva de una habitacion; la fecha de salida no cuenta como noche
public final class Reserva implements IReservable{

    private int idReserva;
    private Pasajero pasajero;
    private Habitacion habitacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean checkInRealizado;
    private boolean checkOutRealizado;
    private boolean cancelada;

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

    public int getIdReserva()
    {
        return idReserva;
    }

    public Pasajero getPasajero()
    {
        return pasajero;
    }

    public Habitacion getHabitacion()
    {
        return habitacion;
    }

    public LocalDate getFechaInicio()
    {
        return fechaInicio;
    }

    public LocalDate getFechaFin()
    {
        return fechaFin;
    }

    public boolean isOcupando()
    {
        return checkInRealizado && !checkOutRealizado;
    }

    public boolean isCancelada()
    {
        return cancelada;
    }

    public boolean isCheckInRealizado()
    {
        return checkInRealizado;
    }

    public boolean isCheckOutRealizado()
    {
        return checkOutRealizado;
    }

    public boolean isActivaEnPeriodo(LocalDate ini, LocalDate fin)
    {
        if (cancelada || checkOutRealizado || ini == null || fin == null || !ini.isBefore(fin))
        {
            return false;
        }
        return fechaInicio.isBefore(fin) && ini.isBefore(fechaFin);
    }

    public void realizarCheckIn() throws ReservaInvalidaException
    {
        if (cancelada || checkInRealizado)
        {
            throw new ReservaInvalidaException("La reserva no admite el check-in.");
        }
        checkInRealizado = true;
    }

    public void realizarCheckOut() throws ReservaInvalidaException
    {
        if (!checkInRealizado || checkOutRealizado)
        {
            throw new ReservaInvalidaException("La reserva no admite el check-out.");
        }
        checkOutRealizado = true;
    }

    public double calcularTotal() throws ReservaInvalidaException
    {
        int dias = (int) (fechaFin.toEpochDay() - fechaInicio.toEpochDay());
        return calcularCostoEstadia(dias);
    }

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
    public boolean cancelarReserva(int idReserva)
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
        return habitacion.calcularTarifa(dias);
    }

}
