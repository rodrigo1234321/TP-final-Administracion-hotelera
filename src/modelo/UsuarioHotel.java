package modelo;

import excepciones.UsuarioInvalidoException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// datos de la cuenta y huella de la clave
public final class UsuarioHotel {
    private final String nombre;
    private final String sal;
    private final String claveProtegida;
    private RolUsuario rol;

    public UsuarioHotel(String nombre, String clave, RolUsuario rol) throws UsuarioInvalidoException {
        validarNombre(nombre);
        if (clave == null || clave.isBlank() || clave.length() < 12 || clave.length() > 128) {
            throw new UsuarioInvalidoException("usa una clave de 12 a 128 caracteres");
        }
        this.nombre = nombre;
        setRol(rol);
        this.sal = UUID.randomUUID().toString();
        this.claveProtegida = protegerClave(clave);
    }

    public UsuarioHotel(String nombre, RolUsuario rol, String sal, String claveProtegida)
            throws UsuarioInvalidoException {
        validarNombre(nombre);
        if (sal == null || sal.isBlank() || claveProtegida == null || claveProtegida.isBlank()) {
            throw new UsuarioInvalidoException("faltan datos de la clave guardada");
        }
        this.nombre = nombre;
        setRol(rol);
        this.sal = sal;
        this.claveProtegida = claveProtegida;
    }

    private void validarNombre(String nombre) throws UsuarioInvalidoException {
        if (nombre == null || !nombre.matches("[a-z0-9_]{3,32}")) {
            throw new UsuarioInvalidoException("usa de 3 a 32 letras, numeros o guiones bajos para el usuario");
        }
    }

    public String getNombre() {
        return nombre;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public String getSal() {
        return sal;
    }

    public String getClaveProtegida() {
        return claveProtegida;
    }

    public void setRol(RolUsuario rol) throws UsuarioInvalidoException {
        if (rol == null) {
            throw new UsuarioInvalidoException("falta el rol del usuario");
        }
        this.rol = rol;
    }

    public boolean coincideClave(String clave) throws UsuarioInvalidoException {
        if (clave == null || clave.length() < 12 || clave.length() > 128) {
            return false;
        }
        String claveIngresada = protegerClave(clave);
        return MessageDigest.isEqual(claveProtegida.getBytes(StandardCharsets.UTF_8),
                claveIngresada.getBytes(StandardCharsets.UTF_8));
    }

    private String protegerClave(String clave) throws UsuarioInvalidoException {
        PBEKeySpec datos = new PBEKeySpec(clave.toCharArray(), sal.getBytes(StandardCharsets.UTF_8),
                600000, 256);
        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(fabrica.generateSecret(datos).getEncoded());
        } catch (GeneralSecurityException e) {
            throw new UsuarioInvalidoException("no se pudo procesar la clave", e);
        } finally {
            datos.clearPassword();
        }
    }
}
