package fr.mrqsdf.jfx.module;

import fr.mrqsdf.jfx.game.MaterialInventory;
import fr.olympus.hephaestus.resources.HephaestusData;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Material module class that displays a list of material info modules.
 */
public class MaterialModule extends VBox {

    private final List<InfoModule> infoModules = new ArrayList<>();

    /**
     * Constructor.
     */
    public MaterialModule(HephaestusData data, MaterialInventory inventory) {
        setSpacing(8);
        setStyle("-fx-padding: 8; -fx-background-color: #1b1b1b;");

        List<String> ids = new ArrayList<>(data.getAllMaterialIds());
        ids.sort(Comparator.naturalOrder());

        for (String id : ids) {
            Label amount = new Label("0");
            ImageView icon = new ImageView();

            InfoModule info = new InfoModule(icon, amount, id);

            inventory.amountProperty(id).addListener((obs, oldV, newV) -> info.setAmount(newV.intValue()));
            info.setAmount(inventory.getAmount(id));

            Label idLabel = new Label(id);
            idLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");
            info.getChildren().add(0, idLabel);

            infoModules.add(info);
            getChildren().add(info);
        }
    }

    /**
     * Getters Info Modules.
     */
    public List<InfoModule> getInfoModules() {
        return infoModules;
    }

    /**
     * Get InfoModule by material ID.
     */
    public InfoModule getInfoModule(String materialId) {
        for (InfoModule m : infoModules) {
            if (m.getMaterialId().equals(materialId)) return m;
        }
        return null;
    }
}
