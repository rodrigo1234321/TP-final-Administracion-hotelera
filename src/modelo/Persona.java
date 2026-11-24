package modelo;

import java.util.Objects;

// datos que comparten pasajeros y personal
public abstract class Persona {
    protected String dni;
    protected String nombre;
    protected String origen;
    protected String domicilio;

    public Persona(String dni, String nombre, String origen, String domicilio) {
        this.dni = dni;
        this.nombre = nombre;
        this.origen = origen;
        this.domicilio = domicilio;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public abstract String getTipoPersona();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Persona persona = (Persona) o;
        return Objects.equals(dni, persona.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dni);
    }

    @Override
    public String toString() {
        return "DNI: " + dni + ", nombre: " + nombre
                + ", origen: " + origen + ", domicilio de origen: " + domicilio;
    }
}
