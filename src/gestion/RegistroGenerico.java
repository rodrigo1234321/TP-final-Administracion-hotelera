package gestion;

import java.util.ArrayList;
import java.util.List;

// lista generica para registrar elementos
public class RegistroGenerico<T> {
    private List<T> elementos = new ArrayList<>();

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public boolean eliminar(T elemento) {
        return elementos.remove(elemento);
    }

    public List<T> obtenerTodos() {
        return new ArrayList<>(elementos);
    }

    public int contar() {
        return elementos.size();
    }
}
