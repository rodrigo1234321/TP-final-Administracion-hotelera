package modelo;

import java.util.ArrayList;
import java.util.List;

// datos del pasajero y su historial
public class Pasajero extends Persona{

    private List<String> historialHabitaciones;

    public Pasajero(String dni, String nombre, String origen, String domicilio){

        super(dni, nombre, origen, domicilio);
        historialHabitaciones = new ArrayList<String>();
    }

    public void agregarHistorial(String habitacion){

        this.historialHabitaciones.add(habitacion);
    }

    public List<String> getHistorialHabitaciones(){

        return historialHabitaciones;
    }

    @Override
    public String getTipoPersona(){

        return this.getClass().getName();

    }

    @Override
    public String toString(){

        return "pasajero [ " +super.toString() +", historial de habitaciones: " +historialHabitaciones +"]";
    }

}
