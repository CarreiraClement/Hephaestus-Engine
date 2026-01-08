package fr.mrqsdf;

import fr.mrqsdf.Planner.Plan;
import fr.mrqsdf.Planner.SimplePlanner;
import fr.mrqsdf.ui.CraftLanternaViewer;
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.register.*;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.*;
import java.util.logging.Logger;

import static fr.mrqsdf.resources.Data.*;

public final class MainExemple {

    public static void main(String[] args) throws Exception {
        Hephaestus.init();
        HephaestusData data = Hephaestus.getData();

        AutoRegistrar.register(RegisterType.MATERIAL, "fr.mrqsdf.material");
        AutoRegistrar.register(RegisterType.FACTORY, "fr.mrqsdf.factory");
        AutoRegistrar.register(RegisterType.RECIPE, "fr.mrqsdf.recipe");

        Factory f1 = data.createFactory(FURNACE_STONE);
        Factory f2 = data.createFactory(FURNACE_BLAST);
        Factory a1 = data.createFactory(ANVIL_IRON);
        Factory b1 = data.createFactory(BARREL);
        Logger.getLogger("MainExemple").info("Factories created: " + f1.getRegistryId() + ", " + f2.getRegistryId() + ", " + a1.getRegistryId() + ", " + b1.getRegistryId());

        Set<String> available = Set.of(WATER, LOG_OAK, IRON_ORE, COAL, BARLEY, YEAST);

        List<ProcessRecipe> allRecipes = new ArrayList<>();
        for (ProcessRecipeRegistryEntry e : data.getProcessRecipeEntriesSnapshot()) {
            allRecipes.add(e.recipe());
        }

        SimplePlanner planner = new SimplePlanner(allRecipes, data);

        // ======= BEST ONLY : affichage Lanterna navigable =======
        Plan bestSword = planner.bestOnly(STEEL_SWORD, available, 20, 5000);
        CraftLanternaViewer.viewBestOnly(bestSword, available);
    }
}
