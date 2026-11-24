package modelo;

import excepciones.ReservaInvalidaException;

import java.time.LocalDate;

// operaciones de una reserva
public interface IReservable {
    boolean reservar(Pasajero p, LocalDate inicio, LocalDate fin);
    boolean cancelarReserva(int idReserva);
    double calcularCostoEstadia(int dias) throws ReservaInvalidaException;
}
