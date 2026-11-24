import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private final Scanner scanner;
    private final GestorHotel hotel = new GestorHotel();
    private final Map<String, UsuarioHotel> usuarios = new HashMap<>();

    public Main(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Main programa = new Main(scanner);
            programa.iniciar();
        } catch (NoSuchElementException e) {
            System.out.println("\nfin de la entrada");
        }
    }

    private void iniciar() {
        while (usuarios.isEmpty()) {
            System.out.println("crea el primer administrador");
            System.out.println("usa una clave de prueba, puede verse al escribir en la consola");
            try {
                crearUsuario(RolUsuario.ADMINISTRADOR);
            } catch (UsuarioInvalidoException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            String nombre = leer("usuario (enter para salir): ").toLowerCase(Locale.ROOT);
            if (nombre.isEmpty()) {
                return;
            }

            String clave = leerClave();
            UsuarioHotel usuario = usuarios.get(nombre);
            try {
                if (usuario == null || !usuario.coincideClave(clave)) {
                    System.out.println("usuario o clave incorrectos");
                } else if (usuario.getRol() == RolUsuario.ADMINISTRADOR) {
                    menuAdministrador(usuario);
                } else {
                    menuRecepcion();
                }
            } catch (UsuarioInvalidoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void menuAdministrador(UsuarioHotel usuario) {
        int opcion = -1;
        do {
            System.out.println("\n--- menu administrador ---");
            System.out.println("1. crear usuario");
            System.out.println("2. cambiar rol");
            System.out.println("3. listar usuarios");
            System.out.println("4. agregar habitacion");
            System.out.println("0. cerrar sesion");

            try {
                opcion = leerEntero("opcion: ");
                switch (opcion) {
                    case 1:
                        crearUsuario(leerRol());
                        break;
                    case 2:
                        cambiarRol(usuario);
                        break;
                    case 3:
                        for (UsuarioHotel cuenta : usuarios.values()) {
                            System.out.println(cuenta.getNombre() + " - " + cuenta.getRol());
                        }
                        break;
                    case 4:
                        agregarHabitacion();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("opcion invalida");
                }
            } catch (EntradaInvalidaException | UsuarioInvalidoException | HabitacionNoDisponibleException e) {
                System.out.println(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void menuRecepcion() {
        int opcion = -1;
        do {
            System.out.println("\n--- menu recepcion ---");
            System.out.println("1. registrar pasajero");
            System.out.println("2. hacer reserva");
            System.out.println("3. consultar disponibilidad");
            System.out.println("4. check-in");
            System.out.println("5. check-out");
            System.out.println("6. cancelar reserva");
            System.out.println("7. listar ocupadas");
            System.out.println("8. listar disponibles hoy");
            System.out.println("9. listar bloqueadas");
            System.out.println("10. cambiar estado de habitacion");
            System.out.println("11. listar pasajeros");
            System.out.println("12. listar reservas");
            System.out.println("0. cerrar sesion");

            try {
                opcion = leerEntero("opcion: ");
                switch (opcion) {
                    case 1:
                        registrarPasajero();
                        break;
                    case 2:
                        hacerReserva();
                        break;
                    case 3:
                        consultarDisponibilidad();
                        break;
                    case 4:
                        int id = leerEntero("id de la reserva: ");
                        String dni = leer("dni del pasajero: ");
                        hotel.realizarCheckIn(id, dni);
                        System.out.println("check-in registrado");
                        break;
                    case 5:
                        double total = hotel.realizarCheckOut(leerEntero("id de la reserva: "));
                        System.out.printf("check-out registrado. total de la reserva: $%.2f%n", total);
                        break;
                    case 6:
                        hotel.cancelarReserva(leerEntero("id de la reserva: "));
                        System.out.println("reserva cancelada");
                        break;
                    case 7:
                        listarOcupadas();
                        break;
                    case 8:
                        listarHabitaciones(hotel.listarHabitacionesDisponibles());
                        break;
                    case 9:
                        listarHabitaciones(hotel.listarHabitacionesNoDisponibles());
                        break;
                    case 10:
                        cambiarEstadoHabitacion();
                        break;
                    case 11:
                        listarPasajeros();
                        break;
                    case 12:
                        listarReservas();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("opcion invalida");
                }
            } catch (EntradaInvalidaException | PasajeroInvalidoException
                    | ReservaInvalidaException | HabitacionNoDisponibleException e) {
                System.out.println(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void crearUsuario(RolUsuario rol) throws UsuarioInvalidoException {
        String nombre = leer("nombre de usuario: ").toLowerCase(Locale.ROOT);
        if (usuarios.containsKey(nombre)) {
            throw new UsuarioInvalidoException("ya existe un usuario con ese nombre");
        }
        UsuarioHotel usuario = new UsuarioHotel(nombre, leerClave(), rol);
        usuarios.put(nombre, usuario);
        System.out.println("usuario creado");
    }

    private void cambiarRol(UsuarioHotel administrador)
            throws EntradaInvalidaException, UsuarioInvalidoException {
        String nombre = leer("usuario a modificar: ").toLowerCase(Locale.ROOT);
        UsuarioHotel usuario = usuarios.get(nombre);
        if (usuario == null) {
            throw new UsuarioInvalidoException("no existe ese usuario");
        }
        // dejamos al menos la cuenta del administrador que esta en sesion
        if (usuario == administrador) {
            throw new UsuarioInvalidoException("elegi otro usuario para cambiarle el rol");
        }
        usuario.setRol(leerRol());
        System.out.println("rol actualizado");
    }

    private RolUsuario leerRol() throws EntradaInvalidaException {
        int opcion = leerEntero("rol (1 administrador, 2 recepcionista): ");
        if (opcion == 1) {
            return RolUsuario.ADMINISTRADOR;
        }
        if (opcion == 2) {
            return RolUsuario.RECEPCIONISTA;
        }
        throw new EntradaInvalidaException("rol invalido");
    }

    private void agregarHabitacion() throws EntradaInvalidaException, HabitacionNoDisponibleException {
        int tipo = leerEntero("tipo (1 simple, 2 suite): ");
        if (tipo != 1 && tipo != 2) {
            throw new EntradaInvalidaException("tipo de habitacion invalido");
        }
        int numero = leerEntero("numero de habitacion: ");
        double precio = leerPrecio("precio por noche: ");
        if (tipo == 1) {
            boolean matrimonial = leerSiNo("tiene cama matrimonial");
            hotel.agregarHabitacion(new HabitacionSimple(numero, precio, matrimonial));
        } else {
            boolean jacuzzi = leerSiNo("tiene jacuzzi");
            double frigobar = leerPrecio("precio del frigobar: ");
            hotel.agregarHabitacion(new HabitacionSuite(numero, precio, jacuzzi, frigobar));
        }
        System.out.println("habitacion agregada");
    }

    private void registrarPasajero() throws PasajeroInvalidoException {
        String dni = leer("dni: ");
        String nombre = leer("nombre: ");
        String origen = leer("origen: ");
        String domicilio = leer("domicilio de origen: ");
        hotel.registrarPasajero(new Pasajero(dni, nombre, origen, domicilio));
        System.out.println("pasajero registrado");
    }

    private void hacerReserva()
            throws EntradaInvalidaException, ReservaInvalidaException, HabitacionNoDisponibleException {
        String dni = leer("dni del pasajero: ");
        int numero = leerEntero("numero de habitacion: ");
        LocalDate inicio = leerFecha("entrada");
        LocalDate fin = leerFecha("salida");
        Reserva reserva = hotel.crearReserva(dni, numero, inicio, fin);
        System.out.println("reserva creada con id " + reserva.getIdReserva());
    }

    private void consultarDisponibilidad()
            throws EntradaInvalidaException, ReservaInvalidaException, HabitacionNoDisponibleException {
        int numero = leerEntero("numero de habitacion: ");
        LocalDate inicio = leerFecha("entrada");
        LocalDate fin = leerFecha("salida");
        if (hotel.consultarDisponibilidad(numero, inicio, fin)) {
            System.out.println("habitacion disponible en esas fechas");
        } else {
            System.out.println("habitacion no disponible en esas fechas");
        }
    }

    private void cambiarEstadoHabitacion() throws EntradaInvalidaException, HabitacionNoDisponibleException {
        int numero = leerEntero("numero de habitacion: ");
        int opcion = leerEntero("estado (1 disponible, 2 limpieza, 3 reparacion, 4 desinfeccion): ");
        EstadoHabitacion estado;
        switch (opcion) {
            case 1:
                estado = EstadoHabitacion.DISPONIBLE;
                break;
            case 2:
                estado = EstadoHabitacion.LIMPIEZA;
                break;
            case 3:
                estado = EstadoHabitacion.REPARACION;
                break;
            case 4:
                estado = EstadoHabitacion.DESINFECCION;
                break;
            default:
                throw new EntradaInvalidaException("estado invalido");
        }

        String motivo = null;
        if (estado != EstadoHabitacion.DISPONIBLE) {
            motivo = leer("motivo: ");
        }
        hotel.cambiarEstadoHabitacion(numero, estado, motivo);
        System.out.println("estado actualizado");
    }

    private void listarOcupadas() {
        List<Reserva> ocupadas = hotel.listarHabitacionesOcupadas();
        if (ocupadas.isEmpty()) {
            System.out.println("no hay habitaciones ocupadas");
        }
        for (Reserva reserva : ocupadas) {
            System.out.println("habitacion " + reserva.getHabitacion().getNumero()
                    + " | reserva " + reserva.getIdReserva() + " | " + reserva.getPasajero());
        }
    }

    private void listarHabitaciones(List<Habitacion> habitaciones) {
        if (habitaciones.isEmpty()) {
            System.out.println("no hay habitaciones para mostrar");
        }
        for (Habitacion habitacion : habitaciones) {
            String linea = "habitacion " + habitacion.getNumero() + " - " + habitacion.getEstado().getEstado();
            if (habitacion.getMotivoNoDisponible() != null) {
                linea += " - " + habitacion.getMotivoNoDisponible();
            }
            System.out.println(linea);
        }
    }

    private void listarPasajeros() {
        List<Pasajero> pasajeros = hotel.listarPasajeros();
        if (pasajeros.isEmpty()) {
            System.out.println("todavia no hay pasajeros");
        }
        for (Pasajero pasajero : pasajeros) {
            System.out.println(pasajero);
        }
    }

    private void listarReservas() {
        List<Reserva> reservas = hotel.listarReservas();
        if (reservas.isEmpty()) {
            System.out.println("todavia no hay reservas");
        }
        for (Reserva reserva : reservas) {
            System.out.println("id " + reserva.getIdReserva() + " | habitacion "
                    + reserva.getHabitacion().getNumero() + " | dni " + reserva.getPasajero().getDni()
                    + " | " + reserva.getFechaInicio() + " a " + reserva.getFechaFin()
                    + " | " + estadoReserva(reserva));
        }
    }

    private String estadoReserva(Reserva reserva) {
        if (reserva.isCancelada()) {
            return "cancelada";
        }
        if (reserva.isOcupando()) {
            return "ocupada";
        }
        if (reserva.isCheckOutRealizado()) {
            return "finalizada";
        }
        return "pendiente";
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private String leerClave() {
        System.out.print("clave: ");
        return scanner.nextLine();
    }

    private int leerEntero(String mensaje) throws EntradaInvalidaException {
        try {
            return Integer.parseInt(leer(mensaje));
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException("ingresa un numero entero");
        }
    }

    private LocalDate leerFecha(String nombre) throws EntradaInvalidaException {
        try {
            return LocalDate.parse(leer(nombre + " (aaaa-mm-dd): "));
        } catch (DateTimeParseException e) {
            throw new EntradaInvalidaException("fecha invalida, usa aaaa-mm-dd");
        }
    }

    private double leerPrecio(String mensaje) throws EntradaInvalidaException {
        double precio;
        try {
            precio = Double.parseDouble(leer(mensaje).replace(",", "."));
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException("precio invalido");
        }
        if (!Double.isFinite(precio) || precio < 0) {
            throw new EntradaInvalidaException("precio invalido");
        }
        return precio;
    }

    private boolean leerSiNo(String dato) throws EntradaInvalidaException {
        String respuesta = leer(dato + " (s/n): ");
        if (respuesta.equalsIgnoreCase("s")) {
            return true;
        }
        if (respuesta.equalsIgnoreCase("n")) {
            return false;
        }
        throw new EntradaInvalidaException("responde s o n");
    }
}
