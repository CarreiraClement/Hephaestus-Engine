package fr.mrqsdf.jfx.module;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Info module class that displays an icon and an amount.
 */
public class InfoModule extends VBox {
    private final ImageView iconImageView;
    private final Label amountLabel;
    private final String materialId;

    /**
     * Constructor.
     */
    public InfoModule(ImageView iconImageView, Label amountLabel, String materialId) {
        this.iconImageView = iconImageView;
        this.amountLabel = amountLabel;
        this.materialId = materialId;

        this.getChildren().addAll(iconImageView, amountLabel);
        this.setSpacing(5);

        this.setStyle("""
                -fx-padding: 6;
                -fx-background-color: #252525;
                -fx-background-radius: 8;
                """);

        this.amountLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
    }

    /**
     * Set the amount displayed.
     */
    public void setAmount(int amount) {
        this.amountLabel.setText(String.valueOf(amount));
    }

    /**
     * Getters Icon.
     */
    public ImageView getIconImageView() {
        return iconImageView;
    }

    /**
     * Getters Amount Label.
     */
    public Label getAmountLabel() {
        return amountLabel;
    }

    /**
     * Getters Material ID.
     */
    public String getMaterialId() {
        return materialId;
    }
}
