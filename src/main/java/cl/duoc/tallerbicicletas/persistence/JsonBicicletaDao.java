package cl.duoc.tallerbicicletas.persistence;

import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonBicicletaDao implements BicicletaDao {

    private static final String ARCHIVO_ELECTRICAS = "bicicletas_electricas.json";
    private static final String ARCHIVO_MONTANYA = "bicicletas_montanya.json";
    private final Gson gson;

    public JsonBicicletaDao() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public List<BicicletaElectrica> cargarElectricas() throws IOException {
        Path path = Paths.get(ARCHIVO_ELECTRICAS);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(path.toFile())) {
            Type listType = new TypeToken<ArrayList<BicicletaElectrica>>() {}.getType();
            List<BicicletaElectrica> lista = gson.fromJson(reader, listType);
            return lista != null ? lista : new ArrayList<>();
        }
    }

    @Override
    public void guardarElectricas(List<BicicletaElectrica> lista) throws IOException {
        try (Writer writer = new FileWriter(ARCHIVO_ELECTRICAS)) {
            gson.toJson(lista, writer);
        }
    }

    @Override
    public List<BicicletaMontanya> cargarMontanya() throws IOException {
        Path path = Paths.get(ARCHIVO_MONTANYA);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(path.toFile())) {
            Type listType = new TypeToken<ArrayList<BicicletaMontanya>>() {}.getType();
            List<BicicletaMontanya> lista = gson.fromJson(reader, listType);
            return lista != null ? lista : new ArrayList<>();
        }
    }

    @Override
    public void guardarMontanya(List<BicicletaMontanya> lista) throws IOException {
        try (Writer writer = new FileWriter(ARCHIVO_MONTANYA)) {
            gson.toJson(lista, writer);
        }
    }
}