package cl.duoc.tallerbicicletas.Controller;

import cl.duoc.tallerbicicletas.model.Bicicleta;
import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import cl.duoc.tallerbicicletas.persistence.JsonBicicletaDao;
import cl.duoc.tallerbicicletas.repository.ClaseRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class MainController {
    private static final String TIPO_ELECTRICA = "Bicicleta Eléctrica";
    private static final String TIPO_MONTANYA = "Bicicleta de Montaña";

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtAnio;
    @FXML private TextField txtPeso;

    @FXML private TitledPane paneElectrica;
    @FXML private TextField txtAutonomia;
    @FXML private CheckBox chkBateriaCertificada;
    @FXML private CheckBox chkGarantiaExtendida;

    @FXML private TitledPane paneMontanya;
    @FXML private TextField txtSuspensiones;

    @FXML private TableView<Bicicleta> tblBicicletas;
    @FXML private TableColumn<Bicicleta, String> colTipo;
    @FXML private TableColumn<Bicicleta, String> colCodigo;
    @FXML private TableColumn<Bicicleta, String> colAnio;
    @FXML private TableColumn<Bicicleta, String> colPeso;
    @FXML private TableColumn<Bicicleta, String> colCosto;

    @FXML private Label lblMensaje;

    private ClaseRepository repository;
    private final ObservableList<Bicicleta> listaObservable = FXCollections.observableArrayList();
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(TIPO_ELECTRICA, TIPO_MONTANYA));


        cmbTipo.valueProperty().addListener((obs, anterior, actual) -> {
            boolean esElectrica = TIPO_ELECTRICA.equals(actual);
            boolean esMontanya = TIPO_MONTANYA.equals(actual);

            paneElectrica.setDisable(!esElectrica);
            paneMontanya.setDisable(!esMontanya);
        });

        configurarTabla();

        try {
            repository = new ClaseRepository(new JsonBicicletaDao());
            listaObservable.setAll(repository.obtenerTodas());
            tblBicicletas.setItems(listaObservable);
        } catch (IOException e) {
            mostrarError("Error al cargar los archivos de bicicletas: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colTipo.setCellValueFactory(cell -> {
            Bicicleta b = cell.getValue();
            String tipo = (b instanceof BicicletaElectrica) ? TIPO_ELECTRICA : TIPO_MONTANYA;
            return new SimpleStringProperty(tipo);
        });

        colCodigo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCodigo()));

        colAnio.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getAnioFabricacion())));

        colPeso.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format(Locale.US, "%.1f", cell.getValue().getPeso())));

        colCosto.setCellValueFactory(cell -> {
            double costo = cell.getValue().calcularCostoMantencion();
            return new SimpleStringProperty(formatoMoneda.format(Math.round(costo)));
        });


        colAnio.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPeso.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCosto.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    @FXML
    private void onRegistrarClick() {
        String tipo = cmbTipo.getValue();
        if (tipo == null || tipo.isBlank()) {
            mostrarError("Tipo: debe seleccionar un tipo de bicicleta.");
            return;
        }

        String codigo = txtCodigo.getText();
        if (codigo == null || codigo.trim().isEmpty()) {
            mostrarError("Código: no puede estar vacío ni contener solo espacios.");
            return;
        }
        codigo = codigo.trim();

        if (repository != null && repository.existeCodigo(codigo)) {
            mostrarError("Código: ya existe una bicicleta registrada con el código " + codigo + ".");
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(txtAnio.getText().trim());
            if (anio < 2000 || anio > 2026) {
                mostrarError("Año de fabricación: debe ser un número entero entre 2000 y 2026.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Año de fabricación: debe ser un número entero entre 2000 y 2026.");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(txtPeso.getText().trim().replace(",", "."));
            if (peso <= 0) {
                mostrarError("Peso (kg): debe ser un número mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Peso (kg): debe ser un número mayor que cero.");
            return;
        }

        try {
            Bicicleta nuevaBicicleta;

            if (TIPO_ELECTRICA.equals(tipo)) {
                int autonomia;
                try {
                    autonomia = Integer.parseInt(txtAutonomia.getText().trim());
                    if (autonomia <= 0) {
                        mostrarError("Autonomía (km): debe ser un número entero mayor que cero.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("Autonomía (km): debe ser un número entero mayor que cero.");
                    return;
                }

                boolean bateriaCertificada = chkBateriaCertificada.isSelected();
                boolean garantiaExtendida = chkGarantiaExtendida.isSelected();


                BicicletaElectrica electrica = new BicicletaElectrica(
                        codigo, anio, peso, autonomia, bateriaCertificada, garantiaExtendida
                );
                nuevaBicicleta = electrica;

            } else {
                int suspensiones;
                try {
                    suspensiones = Integer.parseInt(txtSuspensiones.getText().trim());
                    if (suspensiones < 0) {
                        mostrarError("Suspensiones: debe ser un número entero mayor o igual que cero.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("Suspensiones: debe ser un número entero mayor o igual que cero.");
                    return;
                }


                nuevaBicicleta = new BicicletaMontanya(codigo, anio, peso, suspensiones);
            }

            repository.agregar(nuevaBicicleta);
            listaObservable.setAll(repository.obtenerTodas());
            limpiarFormulario();
            mostrarExito(codigo + " (" + tipo + ") registrada correctamente.");

        } catch (IllegalArgumentException e) {

            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("Error al guardar el archivo JSON: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminarClick() {
        Bicicleta seleccionada = tblBicicletas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Debe seleccionar una bicicleta de la tabla para eliminarla.");
            return;
        }

        try {
            String codigo = seleccionada.getCodigo();
            String tipo = (seleccionada instanceof BicicletaElectrica) ? TIPO_ELECTRICA : TIPO_MONTANYA;

            repository.eliminar(seleccionada);
            listaObservable.setAll(repository.obtenerTodas());
            mostrarExito(codigo + " (" + tipo + ") eliminada correctamente.");
        } catch (IOException e) {
            mostrarError("Error al actualizar los archivos tras eliminar: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        cmbTipo.getSelectionModel().clearSelection();
        cmbTipo.setValue(null);
        txtCodigo.clear();
        txtAnio.clear();
        txtPeso.clear();
        txtAutonomia.clear();
        chkBateriaCertificada.setSelected(false);
        chkGarantiaExtendida.setSelected(false);
        txtSuspensiones.clear();
        paneElectrica.setDisable(true);
        paneMontanya.setDisable(true);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #1b5e20; -fx-font-weight: bold;");
        lblMensaje.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        lblMensaje.setText(mensaje);
    }
}
