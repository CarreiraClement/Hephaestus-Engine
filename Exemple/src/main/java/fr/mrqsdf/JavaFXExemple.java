package fr.mrqsdf;

import fr.mrqsdf.jfx.game.GameContext;
import fr.mrqsdf.jfx.game.MaterialInventory;
import fr.mrqsdf.jfx.module.CenterModule;
import fr.mrqsdf.jfx.module.MaterialModule;
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.register.AutoRegistrar;
import fr.olympus.hephaestus.register.RegisterType;
import fr.olympus.hephaestus.resources.HephaestusData;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.util.Scanner;

import static fr.mrqsdf.resources.Data.*;

public class JavaFXExemple extends Application {

    @Override
    public void start(Stage stage) {
        // init
        Hephaestus.init();
        HephaestusData data = Hephaestus.getData();


        AutoRegistrar.register(RegisterType.MATERIAL, "fr.mrqsdf.material");
        AutoRegistrar.register(RegisterType.FACTORY, "fr.mrqsdf.factory");
        AutoRegistrar.register(RegisterType.RECIPE, "fr.mrqsdf.recipe");

        MaterialInventory inventory = new MaterialInventory();

        inventory.add(WATER, 20);
        inventory.add(LOG_OAK, 20);
        inventory.add(IRON_ORE, 20);
        inventory.add(COAL, 20);
        inventory.add(BARLEY, 20);

        GameContext game = new GameContext(data, inventory);

        // LEFT
        Label leftTitle = new Label("Matériaux");
        leftTitle.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        MaterialModule materialModule = new MaterialModule(data, inventory);
        VBox leftBox = new VBox(8, leftTitle, materialModule);
        leftBox.setPadding(new Insets(8));
        leftBox.setStyle("-fx-background-color: #1b1b1b;");

        ScrollPane leftScroll = new ScrollPane(leftBox);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


        // CENTER
        CenterModule center = new CenterModule(game);
        center.setStyle("-fx-background-color: #121212;");


        // BOTTOM
        Label bottomTitle = new Label("Usines (sélection = mode placement)");
        bottomTitle.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        ListView<String> factories = new ListView<>();
        factories.getItems().setAll(data.getFactoryIdsSnapshot());
        factories.setPrefHeight(150);

        factories.getSelectionModel().selectedItemProperty().addListener((obs, oldV, id) -> {
            if (id == null) return;
            center.beginPlacement(id);
        });

        Button cancelPlacement = new Button("Annuler placement");
        cancelPlacement.setOnAction(e -> {
            factories.getSelectionModel().clearSelection();
            center.endPlacement();
        });

        HBox bottomButtons = new HBox(8, cancelPlacement);
        VBox bottom = new VBox(8, bottomTitle, factories, bottomButtons);
        bottom.setPadding(new Insets(8));
        bottom.setStyle("-fx-background-color: #1b1b1b;");

        BorderPane root = new BorderPane();
        root.setLeft(leftScroll);
        root.setCenter(center);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("Automation Game (JavaFX)");
        stage.setScene(scene);
        stage.show();

        // TICK LOOP
        AnimationTimer timer = new AnimationTimer() {
            long last = -1;

            @Override
            public void handle(long now) {
                if (last < 0) {
                    last = now;
                    return;
                }
                float dt = (now - last) / 1_000_000_000f;
                last = now;

                // clamp dt (si alt-tab)
                if (dt > 0.1f) dt = 0.1f;

                center.tick(dt);
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
