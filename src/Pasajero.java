public class Pasajero extends Persona{

    //Atributos:
    private List<String> historialHabitaciones; //Detalla los periodos en los que el pasajero estuvo alojado y en que habitaciones



    //Constructores:
    public Pasajero(String dni, String nombre, String origen, String domicilio){
      
        super(dni, nombre, origen, domicilio);
        historialHabitaciones = new ArrayList<String>();
    }



    //Todos los metodos de la clase:
    public void agregarHistorial(String habitacion){ //Agrega detalles a un pasajero acerca de su estadia en el hotel
    
        this.historialHabitaciones.add(habitacion);
    }

    public List<String> getHistorialHabitaciones(){ //Obtiene el historial de habitaciones del pasajero
    
        return historialHabitaciones;
    }

    @Override
    public String getTipoPersona(){ //Retorna un string indicando si la persona es recepcionista o pasajera
    
        return this.getClass().getName();

    }

    @Override
    public String toString(){ //Obtiene informacion detallada acerca de un pasajero
    
        return "pasajero [ " +super.toString +"historial de habitaciones" +historialHabitaciones.toString() +"]";
    }   
  
}
