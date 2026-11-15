package cl.duoc.tallerbicicletas.repository;

import cl.duoc.tallerbicicletas.model.Bicicleta;
import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import cl.duoc.tallerbicicletas.persistence.BicicletaDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClaseRepository {

    private final BicicletaDao dao;
    private final List<Bicicleta> bicicletas;

    public ClaseRepository(BicicletaDao dao) throws IOException {
        this.dao = dao;
        this.bicicletas = new ArrayList<>();
        cargarTodo();
    }

    private void cargarTodo() throws IOException {
        bicicletas.clear();
        bicicletas.addAll(dao.cargarElectricas());
        bicicletas.addAll(dao.cargarMontanya());
    }

    public List<Bicicleta> obtenerTodas() {
        return new ArrayList<>(bicicletas);
    }

    public boolean existeCodigo(String codigo) {
        return bicicletas.stream()
                .anyMatch(b -> b.getCodigo().equalsIgnoreCase(codigo.trim()));
    }

    public void agregar(Bicicleta bicicleta) throws IOException {
        bicicletas.add(bicicleta);
        guardarCambios();
    }

    public void eliminar(Bicicleta bicicleta) throws IOException {
        bicicletas.remove(bicicleta);
        guardarCambios();
    }

    private void guardarCambios() throws IOException {
        List<BicicletaElectrica> electricas = new ArrayList<>();
        List<BicicletaMontanya> montanya = new ArrayList<>();

        for (Bicicleta b : bicicletas) {
            if (b instanceof BicicletaElectrica) {
                electricas.add((BicicletaElectrica) b);
            } else if (b instanceof BicicletaMontanya) {
                montanya.add((BicicletaMontanya) b);
            }
        }

        dao.guardarElectricas(electricas);
        dao.guardarMontanya(montanya);
    }
}