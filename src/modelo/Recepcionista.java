package modelo;

// personal del hotel con legajo y turno
public class Recepcionista extends Persona {
    private int nroLegajo;
    private String turno;

    public Recepcionista(String dni, String nombre, String origen, String domicilio,
                         int nroLegajo, String turno) {
        super(dni, nombre, origen, domicilio);
        this.nroLegajo = nroLegajo;
        this.turno = turno;
    }

    public int getNroLegajo() {
        return nroLegajo;
    }

    public String getTurno() {
        return turno;
    }

    @Override
    public String getTipoPersona() {
        return "Recepcionista";
    }
}
