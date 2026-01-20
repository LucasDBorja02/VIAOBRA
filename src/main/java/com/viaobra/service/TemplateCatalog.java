package com.viaobra.service;

import com.viaobra.model.LineItem;
import com.viaobra.model.LineType;
import com.viaobra.model.Stage;
import com.viaobra.repo.LineItemRepo;
import com.viaobra.repo.StageRepo;

import java.util.List;

public class TemplateCatalog {

    public static List<String> names() {
        return List.of(
                "Casa Residencial",
                "Prédio Comercial",
                "Ferrovia",
                "Aeroporto"
        );
    }

    public static void apply(String name, long projectId, int months, LineItemRepo itemRepo, StageRepo stageRepo) {
        stageRepo.deleteAllByProject(projectId);

        if ("Casa Residencial".equals(name)) {
            applyStages(projectId, months, stageRepo, new Object[][]{
                    {"Projeto e Licenças", 1, pct(months, 2), 0.06},
                    {"Terraplanagem e Fundação", pct(months, 2), pct(months, 5), 0.22},
                    {"Estrutura e Alvenaria", pct(months, 5), pct(months, 8), 0.28},
                    {"Instalações", pct(months, 8), pct(months, 11), 0.18},
                    {"Acabamento e Entrega", pct(months, 11), months, 0.26}
            });

            insert(projectId, itemRepo, costOnce("Terreno", "Compra de terreno", 200000, 1, 1));
            insert(projectId, itemRepo, costOnce("Materiais", "Materiais estruturais", 180000, 1, 1));
            insert(projectId, itemRepo, costMonthly("Mão de obra", "Equipe obra", 28000, 1, 1, months));
            insert(projectId, itemRepo, costMonthly("Máquinas", "Locação equipamentos", 6000, 1, 1, months));
            insert(projectId, itemRepo, costOnce("Licenças", "Projetos e taxas", 15000, 1, 1));
            insert(projectId, itemRepo, revenueOnce("Receita", "Venda do imóvel", 550000, 1, months));
        }

        if ("Prédio Comercial".equals(name)) {
            applyStages(projectId, months, stageRepo, new Object[][]{
                    {"Projeto e Aprovações", 1, pct(months, 3), 0.08},
                    {"Fundação", pct(months, 2), pct(months, 6), 0.18},
                    {"Estrutura", pct(months, 5), pct(months, 11), 0.30},
                    {"Fechamentos", pct(months, 10), pct(months, 15), 0.18},
                    {"Instalações", pct(months, 12), pct(months, 18), 0.14},
                    {"Acabamento e Comissionamento", pct(months, 16), months, 0.12}
            });

            insert(projectId, itemRepo, costOnce("Terreno", "Aquisição do terreno", 1200000, 1, 1));
            insert(projectId, itemRepo, costOnce("Materiais", "Materiais e estrutura", 4200000, 1, 1));
            insert(projectId, itemRepo, costMonthly("Mão de obra", "Mão de obra direta", 220000, 1, 1, months));
            insert(projectId, itemRepo, costMonthly("Indiretos", "Administração da obra", 65000, 1, 1, months));
            insert(projectId, itemRepo, costOnce("Licenças", "Aprovações e projetos", 180000, 1, 1));
            insert(projectId, itemRepo, revenueOnce("Receita", "Venda ou contrato", 8200000, 1, months));
        }

        if ("Ferrovia".equals(name)) {
            applyStages(projectId, months, stageRepo, new Object[][]{
                    {"Estudos e Licenciamento", 1, pct(months, 6), 0.10},
                    {"Terraplanagem", pct(months, 4), pct(months, 13), 0.22},
                    {"Obras de Arte", pct(months, 10), pct(months, 20), 0.18},
                    {"Via Permanente", pct(months, 14), pct(months, 26), 0.28},
                    {"Sistemas e Sinalização", pct(months, 20), pct(months, 30), 0.12},
                    {"Comissionamento", pct(months, 28), months, 0.10}
            });

            insert(projectId, itemRepo, costOnce("Terreno", "Desapropriações", 25000000, 1, 1));
            insert(projectId, itemRepo, costOnce("Materiais", "Trilhos e dormentes", 40000000, 1, 1));
            insert(projectId, itemRepo, costOnce("Sistemas", "Sinalização e telecom", 18000000, 1, 1));
            insert(projectId, itemRepo, costMonthly("Mão de obra", "Equipes e empreiteiras", 5200000, 1, 1, months));
            insert(projectId, itemRepo, costMonthly("Logística", "Transporte e apoio", 1200000, 1, 1, months));
            insert(projectId, itemRepo, revenueMonthly("Receita", "Receita operacional", 4500000, 1, months, months));
        }

        if ("Aeroporto".equals(name)) {
            applyStages(projectId, months, stageRepo, new Object[][]{
                    {"Estudos e Licenças", 1, pct(months, 6), 0.08},
                    {"Terraplanagem", pct(months, 4), pct(months, 12), 0.18},
                    {"Pista e Pátio", pct(months, 8), pct(months, 20), 0.26},
                    {"Terminal", pct(months, 10), pct(months, 24), 0.28},
                    {"Sistemas", pct(months, 18), pct(months, 30), 0.12},
                    {"Comissionamento", pct(months, 28), months, 0.08}
            });

            insert(projectId, itemRepo, costOnce("Terreno", "Área e desapropriações", 80000000, 1, 1));
            insert(projectId, itemRepo, costOnce("Materiais", "Obras civis", 220000000, 1, 1));
            insert(projectId, itemRepo, costOnce("Sistemas", "Operação e segurança", 60000000, 1, 1));
            insert(projectId, itemRepo, costMonthly("Mão de obra", "Execução e gerenciamento", 12500000, 1, 1, months));
            insert(projectId, itemRepo, costMonthly("Indiretos", "Custos indiretos", 2600000, 1, 1, months));
            insert(projectId, itemRepo, revenueMonthly("Receita", "Receita aeroportuária", 9000000, 1, months, months));
        }
    }

    private static void applyStages(long projectId, int months, StageRepo stageRepo, Object[][] rows) {
        for (Object[] r : rows) {
            Stage s = new Stage();
            s.projectId = projectId;
            s.name = (String) r[0];
            s.monthStart = clamp((int) r[1], 1, months);
            s.monthEnd = clamp((int) r[2], 1, months);
            s.capexPercent = clamp01((double) r[3]);
            stageRepo.insert(s);
        }
    }

    private static int pct(int months, int month) {
        if (months <= 30) return clamp(month, 1, months);
        double factor = months / 30.0;
        int v = (int) Math.round(month * factor);
        return clamp(v, 1, months);
    }

    private static void insert(long projectId, LineItemRepo repo, LineItem it) {
        it.projectId = projectId;
        repo.insert(it);
    }

    private static LineItem costOnce(String cat, String name, double unit, double qty, int month) {
        LineItem it = new LineItem();
        it.kind = LineType.COST;
        it.category = cat;
        it.name = name;
        it.unitCost = unit;
        it.quantity = qty;
        it.recurrence = "ONCE";
        it.monthStart = month;
        it.monthEnd = month;
        return it;
    }

    private static LineItem costMonthly(String cat, String name, double unit, double qty, int start, int end) {
        LineItem it = new LineItem();
        it.kind = LineType.COST;
        it.category = cat;
        it.name = name;
        it.unitCost = unit;
        it.quantity = qty;
        it.recurrence = "MONTHLY";
        it.monthStart = start;
        it.monthEnd = end;
        return it;
    }

    private static LineItem revenueOnce(String cat, String name, double unit, double qty, int month) {
        LineItem it = new LineItem();
        it.kind = LineType.REVENUE;
        it.category = cat;
        it.name = name;
        it.unitCost = unit;
        it.quantity = qty;
        it.recurrence = "ONCE";
        it.monthStart = month;
        it.monthEnd = month;
        return it;
    }

    private static LineItem revenueMonthly(String cat, String name, double unit, double qty, int start, int end) {
        LineItem it = new LineItem();
        it.kind = LineType.REVENUE;
        it.category = cat;
        it.name = name;
        it.unitCost = unit;
        it.quantity = qty;
        it.recurrence = "MONTHLY";
        it.monthStart = start;
        it.monthEnd = end;
        return it;
    }

    private static int clamp(int v, int a, int b) { return Math.max(a, Math.min(b, v)); }
    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }
}
