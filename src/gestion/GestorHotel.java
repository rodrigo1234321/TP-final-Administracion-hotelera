package gestion;

import excepciones.HabitacionNoDisponibleException;
import excepciones.PasajeroInvalidoException;
import excepciones.ReservaInvalidaException;
import modelo.EstadoHabitacion;
import modelo.Habitacion;
import modelo.Pasajero;
import modelo.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// guarda los datos del hotel y controla las reservas
public class GestorHotel {
    private Set<Habitacion> habitaciones = new HashSet<>();
    private Map<String, Pasajero> pasajerosPorDni = new HashMap<>();
    private RegistroGenerico<Reserva> registroReservas = new RegistroGenerico<>();
    private int siguienteIdReserva = 1;

    public void agregarHabitacion(Habitacion habitacion) throws HabitacionNoDisponibleException {
        if (habitacion == null || habitacion.getNumero() <= 0) {
            throw new HabitacionNoDisponibleException("numero de habitacion invalido");
        }
        if (habitacion.getEstado() == null || habitacion.getEstado() == EstadoHabitacion.OCUPADA) {
            throw new HabitacionNoDisponibleException("estado inicial de habitacion invalido");
        }
        if (estaBloqueada(habitacion) && faltaDato(habitacion.getMotivoNoDisponible())) {
            throw new HabitacionNoDisponibleException("falta el motivo del bloqueo");
        }
        if (!habitaciones.add(habitacion)) {
            throw new HabitacionNoDisponibleException("ya existe una habitacion con ese numero");
        }
    }

    public void registrarPasajero(Pasajero pasajero) throws PasajeroInvalidoException {
        if (pasajero == null || faltaDato(pasajero.getDni()) || faltaDato(pasajero.getNombre())
                || faltaDato(pasajero.getOrigen()) || faltaDato(pasajero.getDomicilio())) {
            throw new PasajeroInvalidoException("faltan datos del pasajero");
        }
        String dni = pasajero.getDni().trim();
        if (dni.length() > 128) {
            throw new PasajeroInvalidoException("el dni es demasiado largo");
        }
        if (pasajerosPorDni.containsKey(dni)) {
            throw new PasajeroInvalidoException("ya existe un pasajero con ese dni");
        }
        pasajerosPorDni.put(dni, pasajero);
    }

    public Reserva crearReserva(String dni, int nroHab, LocalDate ini, LocalDate fin)
            throws ReservaInvalidaException, HabitacionNoDisponibleException {
        Pasajero pasajero = buscarPasajero(dni);
        Habitacion habitacion = buscarHabitacion(nroHab);
        LocalDate hoy = LocalDate.now();
        validarPeriodo(ini, fin, hoy);

        if (!estaDisponible(habitacion, ini, fin, hoy)) {
            throw new HabitacionNoDisponibleException("la habitacion no esta libre en esas fechas");
        }

        Reserva reserva = new Reserva(siguienteIdReserva, pasajero, habitacion, ini, fin);
        registroReservas.agregar(reserva);
        siguienteIdReserva++;
        return reserva;
    }

    public boolean consultarDisponibilidad(int nroHab, LocalDate ini, LocalDate fin)
            throws ReservaInvalidaException, HabitacionNoDisponibleException {
        LocalDate hoy = LocalDate.now();
        validarPeriodo(ini, fin, hoy);
        return estaDisponible(buscarHabitacion(nroHab), ini, fin, hoy);
    }

    public void cancelarReserva(int idReserva) throws ReservaInvalidaException {
        Reserva reserva = buscarReserva(idReserva);
        if (!reserva.cancelarReserva(idReserva)) {
            throw new ReservaInvalidaException("solo se pueden cancelar reservas sin check-in");
        }
    }

    public void realizarCheckIn(int idReserva)
            throws ReservaInvalidaException, HabitacionNoDisponibleException {
        realizarCheckIn(buscarReserva(idReserva));
    }

    public void realizarCheckIn(int idReserva, String dni)
            throws ReservaInvalidaException, HabitacionNoDisponibleException {
        Reserva reserva = buscarReserva(idReserva);
        if (faltaDato(dni) || !reserva.getPasajero().getDni().trim().equals(dni.trim())) {
            throw new ReservaInvalidaException("el dni no corresponde a la reserva");
        }
        realizarCheckIn(reserva);
    }

    private void realizarCheckIn(Reserva reserva)
            throws ReservaInvalidaException, HabitacionNoDisponibleException {
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(reserva.getFechaInicio()) || !hoy.isBefore(reserva.getFechaFin())) {
            throw new ReservaInvalidaException("el check-in esta fuera de las fechas de la reserva");
        }
        Habitacion habitacion = reserva.getHabitacion();
        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE) {
            throw new HabitacionNoDisponibleException("la habitacion no esta lista para el ingreso");
        }
        reserva.realizarCheckIn();
        habitacion.setEstado(EstadoHabitacion.OCUPADA, null);
    }

    public double realizarCheckOut(int idReserva) throws ReservaInvalidaException {
        Reserva reserva = buscarReserva(idReserva);
        double total = reserva.calcularTotal();
        reserva.realizarCheckOut();
        reserva.getHabitacion().setEstado(EstadoHabitacion.LIMPIEZA, "limpieza despues del check-out");
        return total;
    }

    public void cambiarEstadoHabitacion(int nroHab, EstadoHabitacion estado, String motivo)
            throws HabitacionNoDisponibleException {
        Habitacion habitacion = buscarHabitacion(nroHab);
        if (estado == null || estado == EstadoHabitacion.OCUPADA
                || habitacion.getEstado() == EstadoHabitacion.OCUPADA) {
            throw new HabitacionNoDisponibleException("la ocupacion se cambia con check-in y check-out");
        }
        if (estado == EstadoHabitacion.DISPONIBLE) {
            habitacion.setEstado(estado, null);
        } else {
            if (faltaDato(motivo)) {
                throw new HabitacionNoDisponibleException("falta el motivo del bloqueo");
            }
            habitacion.setEstado(estado, motivo);
        }
    }

    public List<Habitacion> listarHabitacionesDisponibles() {
        List<Habitacion> disponibles = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getEstado() == EstadoHabitacion.DISPONIBLE
                    && estaDisponible(habitacion, hoy, hoy.plusDays(1), hoy)) {
                disponibles.add(habitacion);
            }
        }
        return disponibles;
    }

    public List<Habitacion> listarHabitacionesNoDisponibles() {
        List<Habitacion> bloqueadas = new ArrayList<>();
        for (Habitacion habitacion : habitaciones) {
            if (estaBloqueada(habitacion)) {
                bloqueadas.add(habitacion);
            }
        }
        return bloqueadas;
    }

    public List<Reserva> listarHabitacionesOcupadas() {
        List<Reserva> ocupadas = new ArrayList<>();
        for (Reserva reserva : listarReservas()) {
            if (reserva.isOcupando()) {
                ocupadas.add(reserva);
            }
        }
        return ocupadas;
    }

    public List<Pasajero> listarPasajeros() {
        return new ArrayList<>(pasajerosPorDni.values());
    }

    public List<Reserva> listarReservas() {
        return registroReservas.obtenerTodos();
    }

    public void cargarReserva(Reserva reserva) {
        registroReservas.agregar(reserva);
        if (reserva.getIdReserva() >= siguienteIdReserva) {
            siguienteIdReserva = reserva.getIdReserva() + 1;
        }
    }

    private boolean estaDisponible(Habitacion habitacion, LocalDate ini, LocalDate fin, LocalDate hoy) {
        if (estaBloqueada(habitacion)) {
            return false;
        }
        for (Reserva reserva : listarReservas()) {
            if (reserva.getHabitacion().equals(habitacion)) {
                if (reserva.isOcupando() && !hoy.isBefore(reserva.getFechaFin())) {
                    return false;
                }
                if (reserva.isActivaEnPeriodo(ini, fin)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean estaBloqueada(Habitacion habitacion) {
        EstadoHabitacion estado = habitacion.getEstado();
        return estado != EstadoHabitacion.DISPONIBLE && estado != EstadoHabitacion.OCUPADA;
    }

    private void validarPeriodo(LocalDate ini, LocalDate fin, LocalDate hoy)
            throws ReservaInvalidaException {
        if (ini == null || fin == null || ini.isBefore(hoy) || !ini.isBefore(fin)) {
            throw new ReservaInvalidaException("la entrada tiene que ser desde hoy y antes de la salida");
        }
        if (fin.getYear() > 9999 || fin.toEpochDay() - ini.toEpochDay() > Integer.MAX_VALUE) {
            throw new ReservaInvalidaException("el periodo de la reserva es demasiado largo");
        }
    }

    public Habitacion buscarHabitacion(int numero) throws HabitacionNoDisponibleException {
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getNumero() == numero) {
                return habitacion;
            }
        }
        throw new HabitacionNoDisponibleException("no existe esa habitacion");
    }

    public Pasajero buscarPasajero(String dni) throws ReservaInvalidaException {
        if (faltaDato(dni)) {
            throw new ReservaInvalidaException("falta el dni del pasajero");
        }
        Pasajero pasajero = pasajerosPorDni.get(dni.trim());
        if (pasajero == null) {
            throw new ReservaInvalidaException("no existe un pasajero con ese dni");
        }
        return pasajero;
    }

    public Reserva buscarReserva(int idReserva) throws ReservaInvalidaException {
        for (Reserva reserva : listarReservas()) {
            if (reserva.getIdReserva() == idReserva) {
                return reserva;
            }
        }
        throw new ReservaInvalidaException("no existe esa reserva");
    }

    private boolean faltaDato(String dato) {
        return dato == null || dato.trim().isEmpty();
    }
}
