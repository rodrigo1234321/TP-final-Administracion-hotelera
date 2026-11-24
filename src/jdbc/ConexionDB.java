package jdbc;

import excepciones.PersistenciaExcepcion;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// abre la conexion al servidor MySQL con los datos del archivo local
public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/";

    public static Connection obtenerConexion() throws PersistenciaExcepcion {
        Properties datos = leerDatos();
        String usuario = datos.getProperty("usuario");
        String clave = datos.getProperty("clave");
        if (usuario == null || usuario.isBlank() || clave == null) {
            throw new PersistenciaExcepcion("completa el usuario y la clave en conexion.local.properties");
        }
        String url = datos.getProperty("url", URL);

        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1045) {
                throw new PersistenciaExcepcion("usuario o clave de MySQL incorrectos", e);
            }
            throw new PersistenciaExcepcion("no se pudo conectar a MySQL, revisa la conexion", e);
        }
    }

    public static String obtenerUsuarioHotelInicial() throws PersistenciaExcepcion {
        return leerDatos().getProperty("usuarioHotel", "").trim();
    }

    private static Properties leerDatos() throws PersistenciaExcepcion {
        Properties datos = new Properties();
        try (Reader archivo = Files.newBufferedReader(Path.of("conexion.local.properties"))) {
            datos.load(archivo);
            return datos;
        } catch (IOException e) {
            throw new PersistenciaExcepcion("falta conexion.local.properties en la raiz del proyecto", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaExcepcion("revisa el formato de conexion.local.properties", e);
        }
    }
}
