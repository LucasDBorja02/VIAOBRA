package com.viaobra.ui;

import com.viaobra.model.LineItem;
import com.viaobra.model.LineType;
import com.viaobra.model.Project;
import com.viaobra.model.Scenario;
import com.viaobra.model.Stage;
import com.viaobra.repo.LineItemRepo;
import com.viaobra.repo.ProjectRepo;
import com.viaobra.repo.StageRepo;
import com.viaobra.service.FeasibilityService;
import com.viaobra.service.TemplateCatalog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML private TableView<Project> tblProjects;
    @FXML private TableColumn<Project, String> colProjId;
    @FXML private TableColumn<Project, String> colProjName;
    @FXML private TableColumn<Project, String> colProjType;

    @FXML private TextField txtName, txtType, txtLocation, txtDiscount, txtTax, txtCont;
    @FXML private Spinner<Integer> spMonths;

    @FXML private ComboBox<String> cbTemplate;

    @FXML private TableView<LineItem> tblItems;
    @FXML private TableColumn<LineItem, String> colKind, colCategory, colItemName, colValue, colRec, colWindow;

    @FXML private ComboBox<String> cbKind, cbRec;
    @FXML private TextField txtCategory, txtItemName, txtUnit, txtQty;
    @FXML private Spinner<Integer> spStart, spEnd;

    @FXML private TableView<Stage> tblStages;
    @FXML private TableColumn<Stage, String> colStageName, colStageWindow, colStagePct;
    @FXML private TextField txtStageName, txtStagePct;
    @FXML private Spinner<Integer> spStageStart, spStageEnd;

    @FXML private TextField txtCostMult, txtRevMult;
    @FXML private Spinner<Integer> spDelay;

    @FXML private TextArea txtResult;

    private final ProjectRepo projectRepo = new ProjectRepo();
    private final LineItemRepo itemRepo = new LineItemRepo();
    private final StageRepo stageRepo = new StageRepo();
    private final FeasibilityService feas = new FeasibilityService();

    private final NumberFormat br = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    public void initialize() {
        spMonths.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600, 24));

        cbTemplate.setItems(FXCollections.observableArrayList(TemplateCatalog.names()));
        cbTemplate.getSelectionModel().select(0);

        cbKind.setItems(FXCollections.observableArrayList("COST", "REVENUE"));
        cbKind.getSelectionModel().select(0);

        cbRec.setItems(FXCollections.observableArrayList("ONCE", "MONTHLY"));
        cbRec.getSelectionModel().select(0);

        spStart.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600, 1));
        spEnd.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600, 1));

        spStageStart.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600, 1));
        spStageEnd.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600, 1));

        spDelay.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 0));

        txtCostMult.setText("100");
        txtRevMult.setText("100");

        colProjId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().id)));
        colProjName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colProjType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type));

        colKind.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().kind.name()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().category));
        colItemName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colValue.setCellValueFactory(c -> new SimpleStringProperty(br.format(c.getValue().totalValue())));
        colRec.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().recurrence));
        colWindow.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().monthStart + "-" + c.getValue().monthEnd));

        colStageName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colStageWindow.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().monthStart + "-" + c.getValue().monthEnd));
        colStagePct.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "%.2f%%", c.getValue().capexPercent * 100.0)));

        tblProjects.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            if (b != null) {
                reloadAll(b.id, b.months);
                txtResult.clear();
            }
        });

        onReload();
    }

    @FXML
    public void onReload() {
        List<Project> projects = projectRepo.list();
        tblProjects.setItems(FXCollections.observableArrayList(projects));
        if (!projects.isEmpty()) {
            tblProjects.getSelectionModel().select(0);
        } else {
            tblItems.setItems(FXCollections.observableArrayList());
            tblStages.setItems(FXCollections.observableArrayList());
            txtResult.clear();
        }
    }

    @FXML
    public void onSaveProject() {
        Project p = new Project();
        p.name = safe(txtName.getText(), "Projeto");
        p.type = safe(txtType.getText(), "Obra");
        p.location = safe(txtLocation.getText(), "");
        p.months = spMonths.getValue();

        p.discountRateMonth = percentToDecimal(txtDiscount.getText(), 1.5);
        p.taxRate = percentToDecimal(txtTax.getText(), 12);
        p.contingencyRate = percentToDecimal(txtCont.getText(), 8);

        long id = projectRepo.insert(p);
        txtResult.setText("Projeto salvo com ID: " + id);
        onReload();
    }

    @FXML
    public void onApplyTemplate() {
        Project p = tblProjects.getSelectionModel().getSelectedItem();
        if (p == null) {
            alert("Selecione um projeto.");
            return;
        }
        String t = cbTemplate.getValue();
        TemplateCatalog.apply(t, p.id, p.months, itemRepo, stageRepo);
        reloadAll(p.id, p.months);
        txtResult.setText("Template aplicado: " + t);
    }

    @FXML
    public void onAddItem() {
        Project p = tblProjects.getSelectionModel().getSelectedItem();
        if (p == null) {
            alert("Selecione um projeto.");
            return;
        }

        LineItem it = new LineItem();
        it.projectId = p.id;
        it.kind = LineType.valueOf(cbKind.getValue());
        it.category = safe(txtCategory.getText(), "Geral");
        it.name = safe(txtItemName.getText(), "Item");
        it.unitCost = parseDouble(txtUnit.getText(), 0);
        it.quantity = parseDouble(txtQty.getText(), 1);
        it.recurrence = cbRec.getValue();
        it.monthStart = clamp(spStart.getValue(), 1, p.months);
        it.monthEnd = clamp(spEnd.getValue(), 1, p.months);

        if ("ONCE".equalsIgnoreCase(it.recurrence)) it.monthEnd = it.monthStart;

        itemRepo.insert(it);
        reloadItems(p.id);
    }

    @FXML
    public void onRemoveItem() {
        LineItem it = tblItems.getSelectionModel().getSelectedItem();
        Project p = tblProjects.getSelectionModel().getSelectedItem();
        if (p == null || it == null) return;
        itemRepo.deleteById(it.id);
        reloadItems(p.id);
    }

    @FXML
    public void onAddStage() {
        Project p = tblProjects.getSelectionModel().getSelectedItem();
        if (p == null) {
            alert("Selecione um projeto.");
            return;
        }

        Stage s = new Stage();
        s.projectId = p.id;
        s.name = safe(txtStageName.getText(), "Etapa");
        s.monthStart = clamp(spStageStart.getValue(), 1, p.months);
        s.monthEnd = clamp(spStageEnd.getValue(), 1, p.months);
        s.capexPercent = clamp01(percentToDecimal(txtStagePct.getText(), 10));

        stageRepo.insert(s);
        reloadStages(p.id);
    }

    @FXML
    public void onRemoveStage() {
        Stage s = tblStages.getSelectionModel().getSelectedItem();
        Project p = tblProjects.getSelectionModel().getSelectedItem();
        if (p == null || s == null) return;
        stageRepo.deleteById(s.id);
        reloadStages(p.id);
    }

    @FXML
    public void onCalc() {
        Project p = tblProjects.getSelectionModel().getSelectedItem();
        if (p == null) return;

        double costMult = percentToFactor(txtCostMult.getText(), 100);
        double revMult = percentToFactor(txtRevMult.getText(), 100);
        int delay = spDelay.getValue();

        Scenario sc = new Scenario(costMult, revMult, delay);

        List<LineItem> items = itemRepo.listByProject(p.id);
        List<Stage> stages = stageRepo.listByProject(p.id);

        var r = feas.evaluate(p, items, stages, sc);

        String payback = (r.paybackMonth() > 0) ? (r.paybackMonth() + " mês(es)") : "Não recupera";
        String be = (r.breakEvenMonth() > 0) ? (r.breakEvenMonth() + " mês(es)") : "Não atinge";

        txtResult.setText("""
                VIAOBRA – RESULTADO

                Projeto: %s (%s) – %s
                Prazo base: %d meses
                Atraso de receita: %d meses
                Horizonte: %d meses

                Cenário:
                Custos: %.2f%%
                Receitas: %.2f%%

                Financeiro:
                CAPEX: %s
                OPEX Total: %s
                Receita Total: %s
                Contingência: %s
                Impostos (estim.): %s

                Lucro (estim.): %s
                VPL/NPV: %s
                TIR/IRR (aprox a.m.): %.4f (%.2f%%)
                Payback: %s
                Break-even: %s

                Score: %d/100
                Veredito: %s
                """.formatted(
                p.name, p.type, p.location,
                p.months,
                delay,
                r.horizonMonths(),
                costMult * 100.0,
                revMult * 100.0,
                br.format(r.capex()),
                br.format(r.opexTotal()),
                br.format(r.revenueTotal()),
                br.format(r.contingencyValue()),
                br.format(r.taxValue()),
                br.format(r.profit()),
                br.format(r.npv()),
                r.irrApprox(), r.irrApprox() * 100.0,
                payback,
                be,
                r.score(),
                r.verdict()
        ));
    }

    private void reloadAll(long projectId, int months) {
        reloadItems(projectId);
        reloadStages(projectId);

        ((SpinnerValueFactory.IntegerSpinnerValueFactory) spStart.getValueFactory()).setMax(months);
        ((SpinnerValueFactory.IntegerSpinnerValueFactory) spEnd.getValueFactory()).setMax(months);

        ((SpinnerValueFactory.IntegerSpinnerValueFactory) spStageStart.getValueFactory()).setMax(months);
        ((SpinnerValueFactory.IntegerSpinnerValueFactory) spStageEnd.getValueFactory()).setMax(months);
    }

    private void reloadItems(long projectId) {
        tblItems.setItems(FXCollections.observableArrayList(itemRepo.listByProject(projectId)));
    }

    private void reloadStages(long projectId) {
        tblStages.setItems(FXCollections.observableArrayList(stageRepo.listByProject(projectId)));
    }

    private static String safe(String s, String def) {
        if (s == null) return def;
        s = s.trim();
        return s.isEmpty() ? def : s;
    }

    private static double parseDouble(String s, double def) {
        try {
            if (s == null) return def;
            s = s.trim().replace(".", "").replace(",", ".");
            if (s.isEmpty()) return def;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static double percentToDecimal(String s, double defPercent) {
        double v = parseDouble(s, defPercent);
        return v / 100.0;
    }

    private static double percentToFactor(String s, double defPercent) {
        return percentToDecimal(s, defPercent);
    }

    private static int clamp(int v, int a, int b) { return Math.max(a, Math.min(b, v)); }
    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
