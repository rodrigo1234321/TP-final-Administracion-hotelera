package jdbc;

import excepciones.HabitacionNoDisponibleException;
import excepciones.PasajeroInvalidoException;
import excepciones.PersistenciaExcepcion;
import excepciones.ReservaInvalidaException;
import excepciones.UsuarioInvalidoException;
import gestion.GestorHotel;
import modelo.EstadoHabitacion;
import modelo.Habitacion;
import modelo.HabitacionSimple;
import modelo.HabitacionSuite;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.RolUsuario;
import modelo.UsuarioHotel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

// carga y guarda los datos del hotel y permite hacer una copia
public class ConexionHotel implements AutoCloseable {
    private static final String[] TABLAS = {"habitaciones", "pasajeros", "usuarios", "reservas"};
    private final Connection conexion;

    public ConexionHotel() throws PersistenciaExcepcion {
        conexion = ConexionDB.obtenerConexion();
        try {
            crearBases();
            conexion.setCatalog("hotel_tp");
            try (PreparedStatement sentencia = conexion.prepareStatement("SELECT GET_LOCK('hotel_tp_app', 0)");
                 ResultSet filas = sentencia.executeQuery()) {
                if (!filas.next() || filas.getInt(1) != 1) {
                    throw new SQLException("ya hay otra instancia usando hotel_tp");
                }
            }
            crearTablas();
            conexion.setAutoCommit(false);
        } catch (SQLException e) {
            try {
                conexion.close();
            } catch (SQLException cierre) {
                e.addSuppressed(cierre);
            }
            throw new PersistenciaExcepcion("no se pudo preparar la base del hotel: " + e.getMessage(), e);
        }
    }

    private void crearBases() throws SQLException {
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("CREATE DATABASE IF NOT EXISTS hotel_tp CHARACTER SET utf8mb4");
            sentencia.executeUpdate("CREATE DATABASE IF NOT EXISTS hotel_tp_copia CHARACTER SET utf8mb4");
        }
    }

    private void crearTablas() throws SQLException {
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS habitaciones (
                        numero INT PRIMARY KEY, precio DOUBLE NOT NULL,
                        tipo VARCHAR(8) NOT NULL, matrimonial BOOLEAN NOT NULL,
                        jacuzzi BOOLEAN NOT NULL, frigobar DOUBLE NOT NULL,
                        estado VARCHAR(16) NOT NULL, motivo TEXT
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
                    """);
            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS pasajeros (
                        dni VARCHAR(128) PRIMARY KEY, nombre TEXT NOT NULL,
                        origen TEXT NOT NULL, domicilio TEXT NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
                    """);
            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        nombre VARCHAR(32) PRIMARY KEY, rol VARCHAR(16) NOT NULL,
                        sal VARCHAR(36) NOT NULL, clave_protegida VARCHAR(44) NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
                    """);
            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS reservas (
                        id INT PRIMARY KEY, dni VARCHAR(128) NOT NULL, numero INT NOT NULL,
                        inicio DATE NOT NULL, fin DATE NOT NULL, check_in BOOLEAN NOT NULL,
                        check_out BOOLEAN NOT NULL, cancelada BOOLEAN NOT NULL,
                        FOREIGN KEY (dni) REFERENCES pasajeros(dni),
                        FOREIGN KEY (numero) REFERENCES habitaciones(numero)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
                    """);
        }
    }

    public void cargarDatos(GestorHotel hotel, Map<String, UsuarioHotel> usuarios)
            throws PersistenciaExcepcion {
        try {
            cargarHabitaciones(hotel);
            cargarPasajeros(hotel);
            cargarUsuarios(usuarios);
            cargarReservas(hotel);
            conexion.commit();
        } catch (SQLException | HabitacionNoDisponibleException | PasajeroInvalidoException
                | UsuarioInvalidoException | ReservaInvalidaException | IllegalArgumentException e) {
            throw fallo("no se pudieron cargar los datos del hotel", e);
        }
    }

    private void cargarHabitaciones(GestorHotel hotel) throws SQLException, HabitacionNoDisponibleException {
        String sql = "SELECT numero, precio, tipo, matrimonial, jacuzzi, frigobar, estado, motivo FROM habitaciones";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                int numero = filas.getInt("numero");
                double precio = filas.getDouble("precio");
                Habitacion habitacion;
                if ("SUITE".equals(filas.getString("tipo"))) {
                    habitacion = new HabitacionSuite(numero, precio, filas.getBoolean("jacuzzi"),
                            filas.getDouble("frigobar"));
                } else if ("SIMPLE".equals(filas.getString("tipo"))) {
                    habitacion = new HabitacionSimple(numero, precio, filas.getBoolean("matrimonial"));
                } else {
                    throw new SQLException("tipo de habitacion guardado invalido");
                }
                EstadoHabitacion estado = EstadoHabitacion.valueOf(filas.getString("estado"));
                if (estado == EstadoHabitacion.OCUPADA) {
                    hotel.agregarHabitacion(habitacion);
                    habitacion.setEstado(estado, null);
                } else {
                    habitacion.setEstado(estado, filas.getString("motivo"));
                    hotel.agregarHabitacion(habitacion);
                }
            }
        }
    }

    private void cargarPasajeros(GestorHotel hotel) throws SQLException, PasajeroInvalidoException {
        String sql = "SELECT dni, nombre, origen, domicilio FROM pasajeros";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                Pasajero pasajero = new Pasajero(filas.getString("dni"), filas.getString("nombre"),
                        filas.getString("origen"), filas.getString("domicilio"));
                hotel.registrarPasajero(pasajero);
            }
        }
    }

    private void cargarUsuarios(Map<String, UsuarioHotel> usuarios) throws SQLException, UsuarioInvalidoException {
        String sql = "SELECT nombre, rol, sal, clave_protegida FROM usuarios";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                String nombre = filas.getString("nombre");
                RolUsuario rol = RolUsuario.valueOf(filas.getString("rol"));
                UsuarioHotel usuario = new UsuarioHotel(nombre, rol, filas.getString("sal"),
                        filas.getString("clave_protegida"));
                usuarios.put(nombre, usuario);
            }
        }
    }

    private void cargarReservas(GestorHotel hotel)
            throws SQLException, HabitacionNoDisponibleException, ReservaInvalidaException {
        String sql = "SELECT id, dni, numero, inicio, fin, check_in, check_out, cancelada FROM reservas ORDER BY id";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                int id = filas.getInt("id");
                Pasajero pasajero = hotel.buscarPasajero(filas.getString("dni"));
                Habitacion habitacion = hotel.buscarHabitacion(filas.getInt("numero"));
                Reserva reserva = new Reserva(id, pasajero, habitacion,
                        filas.getDate("inicio").toLocalDate(), filas.getDate("fin").toLocalDate());
                if (filas.getBoolean("cancelada")) {
                    reserva.cancelarReserva(id);
                }
                if (filas.getBoolean("check_in")) {
                    reserva.realizarCheckIn();
                }
                if (filas.getBoolean("check_out")) {
                    reserva.realizarCheckOut();
                }
                hotel.cargarReserva(reserva);
            }
        }
    }

    public void guardarHabitacion(Habitacion habitacion) throws PersistenciaExcepcion {
        String tipo = "SIMPLE";
        boolean matrimonial = false;
        boolean jacuzzi = false;
        double frigobar = 0;
        if (habitacion instanceof HabitacionSimple) {
            HabitacionSimple simple = (HabitacionSimple) habitacion;
            matrimonial = simple.tieneCamaMatrimonial();
        } else if (habitacion instanceof HabitacionSuite) {
            HabitacionSuite suite = (HabitacionSuite) habitacion;
            tipo = "SUITE";
            jacuzzi = suite.tieneJacuzzi();
            frigobar = suite.getServicioFrigobar();
        } else {
            throw new PersistenciaExcepcion("tipo de habitacion invalido");
        }

        String sql = "INSERT INTO habitaciones (numero, precio, tipo, matrimonial, jacuzzi, frigobar, estado, motivo)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, habitacion.getNumero());
            sentencia.setDouble(2, habitacion.getPrecioPorNoche());
            sentencia.setString(3, tipo);
            sentencia.setBoolean(4, matrimonial);
            sentencia.setBoolean(5, jacuzzi);
            sentencia.setDouble(6, frigobar);
            sentencia.setString(7, habitacion.getEstado().name());
            sentencia.setString(8, habitacion.getMotivoNoDisponible());
            sentencia.executeUpdate();
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar la habitacion", e);
        }
    }

    public void guardarPasajero(Pasajero pasajero) throws PersistenciaExcepcion {
        String sql = "INSERT INTO pasajeros (dni, nombre, origen, domicilio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, pasajero.getDni().trim());
            sentencia.setString(2, pasajero.getNombre());
            sentencia.setString(3, pasajero.getOrigen());
            sentencia.setString(4, pasajero.getDomicilio());
            sentencia.executeUpdate();
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar el pasajero", e);
        }
    }

    public void guardarUsuario(UsuarioHotel usuario) throws PersistenciaExcepcion {
        String sql = "INSERT INTO usuarios (nombre, rol, sal, clave_protegida) VALUES (?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getRol().name());
            sentencia.setString(3, usuario.getSal());
            sentencia.setString(4, usuario.getClaveProtegida());
            sentencia.executeUpdate();
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar el usuario", e);
        }
    }

    public void actualizarRol(UsuarioHotel usuario) throws PersistenciaExcepcion {
        String sql = "UPDATE usuarios SET rol = ? WHERE nombre = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, usuario.getRol().name());
            sentencia.setString(2, usuario.getNombre());
            sentencia.executeUpdate();
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar el rol", e);
        }
    }

    public void guardarReserva(Reserva reserva) throws PersistenciaExcepcion {
        String sql = "INSERT INTO reservas (id, dni, numero, inicio, fin, check_in, check_out, cancelada)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, reserva.getIdReserva());
            sentencia.setString(2, reserva.getPasajero().getDni().trim());
            sentencia.setInt(3, reserva.getHabitacion().getNumero());
            sentencia.setDate(4, Date.valueOf(reserva.getFechaInicio()));
            sentencia.setDate(5, Date.valueOf(reserva.getFechaFin()));
            sentencia.setBoolean(6, reserva.isCheckInRealizado());
            sentencia.setBoolean(7, reserva.isCheckOutRealizado());
            sentencia.setBoolean(8, reserva.isCancelada());
            sentencia.executeUpdate();
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar la reserva", e);
        }
    }

    private void actualizarReserva(Reserva reserva) throws SQLException {
        String sql = "UPDATE reservas SET check_in = ?, check_out = ?, cancelada = ? WHERE id = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setBoolean(1, reserva.isCheckInRealizado());
            sentencia.setBoolean(2, reserva.isCheckOutRealizado());
            sentencia.setBoolean(3, reserva.isCancelada());
            sentencia.setInt(4, reserva.getIdReserva());
            sentencia.executeUpdate();
        }
    }

    private void actualizarHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "UPDATE habitaciones SET estado = ?, motivo = ? WHERE numero = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, habitacion.getEstado().name());
            sentencia.setString(2, habitacion.getMotivoNoDisponible());
            sentencia.setInt(3, habitacion.getNumero());
            sentencia.executeUpdate();
        }
    }

    public void guardarEstadoHabitacion(Habitacion habitacion) throws PersistenciaExcepcion {
        try {
            actualizarHabitacion(habitacion);
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar el estado de la habitacion", e);
        }
    }

    public void guardarCancelacion(Reserva reserva) throws PersistenciaExcepcion {
        try {
            actualizarReserva(reserva);
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo guardar la cancelacion", e);
        }
    }

    public void guardarEstadia(Reserva reserva) throws PersistenciaExcepcion {
        try {
            actualizarReserva(reserva);
            actualizarHabitacion(reserva.getHabitacion());
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudieron guardar la reserva y la habitacion", e);
        }
    }

    public void hacerBackup() throws PersistenciaExcepcion {
        try (Statement sentencia = conexion.createStatement()) {
            for (String tabla : TABLAS) {
                sentencia.executeUpdate("CREATE TABLE IF NOT EXISTS hotel_tp_copia." + tabla
                        + " LIKE hotel_tp." + tabla);
            }
            for (int i = TABLAS.length - 1; i >= 0; i--) {
                sentencia.executeUpdate("DELETE FROM hotel_tp_copia." + TABLAS[i]);
            }
            for (String tabla : TABLAS) {
                sentencia.executeUpdate("INSERT INTO hotel_tp_copia." + tabla + " SELECT * FROM hotel_tp." + tabla);
            }
            conexion.commit();
        } catch (SQLException e) {
            throw fallo("no se pudo hacer el backup en hotel_tp_copia", e);
        }
    }

    private PersistenciaExcepcion fallo(String mensaje, Exception causa) {
        try {
            conexion.rollback();
        } catch (SQLException e) {
            causa.addSuppressed(e);
        }
        return new PersistenciaExcepcion(mensaje, causa);
    }

    @Override
    public void close() throws PersistenciaExcepcion {
        try {
            conexion.close();
        } catch (SQLException e) {
            throw new PersistenciaExcepcion("no se pudo cerrar la conexion", e);
        }
    }
}
