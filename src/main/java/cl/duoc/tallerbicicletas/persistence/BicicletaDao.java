package cl.duoc.tallerbicicletas.persistence;

import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import java.io.IOException;
import java.util.List;

public interface BicicletaDao {
    List<BicicletaElectrica> cargarElectricas() throws IOException;
    void guardarElectricas(List<BicicletaElectrica> lista) throws IOException;

    List<BicicletaMontanya> cargarMontanya() throws IOException;
    void guardarMontanya(List<BicicletaMontanya> lista) throws IOException;
}