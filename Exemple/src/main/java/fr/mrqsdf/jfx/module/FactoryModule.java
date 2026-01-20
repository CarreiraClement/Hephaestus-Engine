
package fr.mrqsdf.jfx.module;

import fr.mrqsdf.jfx.game.GameContext;
import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.register.ProcessRecipeRegistryEntry;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factory module class that represents a factory in the UI.
 */
public class FactoryModule extends Pane {

    /**
     * Port and layout constants
     */
    private static final double PORT_RADIUS = 10.0;
    private static final double SIDE_PADDING = 16.0;
    private static final double V_PADDING = 16.0;
    private static final double PORT_GAP = 10.0;
    private static final double LABEL_PAD_X = 18.0;
    private static final double MIN_W = 220.0;
    private static final double MIN_H = 120.0;

    private final GameContext game;

    private final Rectangle background;

    private final Label factoryLabel;
    private final Label recipeLabel;
    private final Label timerLabel;
    private final VBox labelBox;

    private final Button removeButton;

    private final List<Circle> inputs = new ArrayList<>();
    private final List<Circle> outputs = new ArrayList<>();

    private final Factory factory;
    private final String factoryId;

    private String recipeId;
    private ProcessRecipe recipe;

    // --- production state (jeu)
    private boolean producing;
    private float remainingSeconds;

    // drag
    private boolean dragging;
    private double dragAnchorX, dragAnchorY;
    private double dragStartX, dragStartY;

    /**
     * Constructor.
     *
     * @param game    Game context
     * @param factory Factory
     */
    public FactoryModule(GameContext game, Factory factory) {
        this.game = Objects.requireNonNull(game, "game");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.factoryId = factory.getRegistryId();

        setPickOnBounds(false);

        background = new Rectangle(200, 150);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setStrokeWidth(2);
        background.setStroke(Color.WHITE);
        background.setFill(Color.rgb(30, 30, 30, 0.90));
        background.setManaged(false);

        factoryLabel = new Label("Factory: " + factoryId);
        recipeLabel = new Label("Recipe: <none>");
        timerLabel = new Label("Time: 0 s");

        factoryLabel.setTextFill(Color.WHITE);
        recipeLabel.setTextFill(Color.WHITE);
        timerLabel.setTextFill(Color.WHITE);

        labelBox = new VBox(4, factoryLabel, recipeLabel, timerLabel);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setManaged(false);

        removeButton = new Button("X");
        removeButton.setManaged(false);
        removeButton.setFocusTraversable(false);
        removeButton.setStyle("-fx-background-color: #b00020; -fx-text-fill: white; -fx-font-weight: bold;");
        removeButton.setOnAction(e -> {
            if (getParent() instanceof Pane p) {
                p.getChildren().remove(this);
            }
        });

        getChildren().addAll(background, labelBox, removeButton);

        rebuildPorts(0, 0);
        updatePreferredSize();
        resize(getPrefWidth(), getPrefHeight());

        setupMouseInteractions();
    }

    /**
     * Get the factory.
     *
     * @return Factory
     */
    public Factory getFactory() {
        return factory;
    }

    /**
     * Get the factory ID.
     *
     * @return Factory ID
     */
    public String getFactoryId() {
        return factoryId;
    }

    /**
     * Get the current recipe ID.
     *
     * @return Recipe ID
     */
    public String getRecipeId() {
        return recipeId;
    }

    /**
     * Set the current recipe ID.
     *
     * @param recipeId Recipe ID
     */
    public void setRecipeId(String recipeId) {
        if (recipeId == null || recipeId.isBlank()) {
            this.recipeId = null;
            this.recipe = null;
            this.producing = false;
            this.remainingSeconds = 0f;

            recipeLabel.setText("Recipe: <none>");
            timerLabel.setText("Time: 0 s");

            rebuildPorts(0, 0);
            updatePreferredSize();
            resize(getPrefWidth(), getPrefHeight());
            requestLayout();
            return;
        }

        ProcessRecipeRegistryEntry entry = game.data().getProcessRecipeById(recipeId);
        this.recipeId = recipeId;
        this.recipe = entry.recipe();

        recipeLabel.setText("Recipe: " + recipeId);

        float min = game.minSecondsOrZero(recipe);
        timerLabel.setText("Time: " + (int) Math.ceil(min) + " s");

        rebuildPorts(recipe.inputCount(), recipe.outputCount());
        updatePreferredSize();
        resize(getPrefWidth(), getPrefHeight());
        requestLayout();
    }

    /**
     * Tick method to update production state.
     *
     * @param dt Delta time
     */
    public void tick(float dt) {
        game.tickFactory(factory, dt);

        if (!producing || recipe == null) return;

        remainingSeconds -= dt;
        System.out.println("Factory " + factoryId + " producing " + recipeId + ", remaining time: " + remainingSeconds + " s");
        if (remainingSeconds < 0) remainingSeconds = 0;

        if (!factory.getSession()) {
            producing = false;
            float min = game.minSecondsOrZero(recipe);
            timerLabel.setText("Time: " + (int) Math.ceil(min) + " s");
            completeProduction();
            return;
        }
        timerLabel.setText("Time: " + (int) Math.ceil(remainingSeconds) + " s");
    }

    /**
     * Complete the current production.
     */
    private void completeProduction() {
        game.grantOutputsFromRecipe(recipe);

        factory.stopFactory();

        producing = false;

        float min = game.minSecondsOrZero(recipe);
        timerLabel.setText("Time: " + (int) Math.ceil(min) + " s");
    }

    /**
     * Layout children.
     */
    @Override
    protected void layoutChildren() {
        double w = background.getWidth();
        double h = background.getHeight();

        background.setX(0);
        background.setY(0);

        labelBox.autosize();
        double lw = labelBox.getWidth();
        double lh = labelBox.getHeight();
        labelBox.relocate((w - lw) * 0.5, (h - lh) * 0.5);

        removeButton.autosize();
        double rbw = removeButton.getWidth();
        removeButton.relocate(w - rbw - 6, 6);

        layoutPortsLeft(inputs, h);
        layoutPortsRight(outputs, w, h);
    }

    /**
     * Layout ports on the left side.
     *
     * @param ports List of ports
     * @param h     Height
     */
    private void layoutPortsLeft(List<Circle> ports, double h) {
        double x = SIDE_PADDING + PORT_RADIUS;
        layoutPortsVertical(ports, x, h);
    }

    /**
     * Layout ports on the right side.
     *
     * @param ports List of ports
     * @param w     Width
     * @param h     Height
     */
    private void layoutPortsRight(List<Circle> ports, double w, double h) {
        double x = w - (SIDE_PADDING + PORT_RADIUS);
        layoutPortsVertical(ports, x, h);
    }

    /**
     * Layout ports vertically at the given x position.
     *
     * @param ports List of ports
     * @param x     X position
     * @param h     Height
     */
    private void layoutPortsVertical(List<Circle> ports, double x, double h) {
        int n = ports.size();
        if (n == 0) return;

        double top = V_PADDING;
        double bottom = V_PADDING;

        if (n == 1) {
            Circle c = ports.get(0);
            c.setLayoutX(x);
            c.setLayoutY(h * 0.5);
            return;
        }

        double span = Math.max(0, h - top - bottom);
        double step = span / (n + 1);

        for (int i = 0; i < n; i++) {
            double y = top + step * (i + 1);
            Circle c = ports.get(i);
            c.setLayoutX(x);
            c.setLayoutY(y);
        }
    }

    /**
     * Rebuild input and output ports.
     *
     * @param inputCount  Number of input ports
     * @param outputCount Number of output ports
     */
    private void rebuildPorts(int inputCount, int outputCount) {
        for (Circle c : inputs) getChildren().remove(c);
        for (Circle c : outputs) getChildren().remove(c);
        inputs.clear();
        outputs.clear();

        for (int i = 0; i < inputCount; i++) {
            Circle c = new Circle(PORT_RADIUS);
            c.setFill(Color.DODGERBLUE);
            c.setStroke(Color.WHITE);
            c.setStrokeWidth(1.0);
            c.setManaged(false);
            inputs.add(c);
            getChildren().add(c);
        }

        for (int i = 0; i < outputCount; i++) {
            Circle c = new Circle(PORT_RADIUS);
            c.setFill(Color.LIMEGREEN);
            c.setStroke(Color.WHITE);
            c.setStrokeWidth(1.0);
            c.setManaged(false);
            outputs.add(c);
            getChildren().add(c);
        }
    }

    /**
     * Update preferred size based on content.
     */
    private void updatePreferredSize() {
        labelBox.applyCss();
        labelBox.autosize();
        double labelW = labelBox.prefWidth(-1);
        double labelH = labelBox.prefHeight(-1);

        int maxPorts = Math.max(inputs.size(), outputs.size());
        double portsH;
        if (maxPorts <= 1) {
            portsH = PORT_RADIUS * 2.0;
        } else {
            portsH = (maxPorts * (PORT_RADIUS * 2.0)) + ((maxPorts - 1) * PORT_GAP);
        }

        double contentH = Math.max(labelH, portsH) + (V_PADDING * 2.0);
        double h = Math.max(MIN_H, contentH);

        double sideZone = (SIDE_PADDING + PORT_RADIUS) + PORT_RADIUS;
        double centerZone = labelW + (LABEL_PAD_X * 2.0);
        double contentW = sideZone + centerZone + sideZone;

        double w = Math.max(MIN_W, contentW);

        background.setWidth(w);
        background.setHeight(h);

        setPrefSize(w, h);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    /**
     * Setup mouse interactions for the factory module.
     */
    private void setupMouseInteractions() {
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showRecipeMenu(e.getScreenX(), e.getScreenY());
                e.consume();
                return;
            }

            // clic gauche = start prod
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown()) {
                if (recipe == null) return;

                // si déjà en prod, on ignore
                if (producing || factory.getSession()) return;

                boolean ok = game.tryStartProduction(factory, recipe);
                if (ok) {
                    producing = true;
                    remainingSeconds = game.minSecondsOrZero(recipe);

                    // prod instant si 0
                    if (remainingSeconds <= 0f) {
                        completeProduction();
                    } else {
                        timerLabel.setText("Time: " + (int) Math.ceil(remainingSeconds) + " s");
                    }
                }
                e.consume();
            }
        });

        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.isShiftDown()) {
                dragging = true;
                dragAnchorX = e.getSceneX();
                dragAnchorY = e.getSceneY();
                dragStartX = getLayoutX();
                dragStartY = getLayoutY();
                e.consume();
            }
        });

        setOnMouseDragged(e -> {
            if (!dragging) return;

            double dx = e.getSceneX() - dragAnchorX;
            double dy = e.getSceneY() - dragAnchorY;

            double newX = dragStartX + dx;
            double newY = dragStartY + dy;

            if (getParent() instanceof Pane p) {
                double maxX = Math.max(0, p.getWidth() - getWidth());
                double maxY = Math.max(0, p.getHeight() - getHeight());

                if (newX < 0) newX = 0;
                if (newY < 0) newY = 0;
                if (newX > maxX) newX = maxX;
                if (newY > maxY) newY = maxY;
            }

            relocate(newX, newY);
            e.consume();
        });

        setOnMouseReleased(e -> dragging = false);
    }

    /**
     * Show the recipe selection menu.
     *
     * @param screenX Screen X position
     * @param screenY Screen Y position
     */
    private void showRecipeMenu(double screenX, double screenY) {
        ContextMenu menu = new ContextMenu();

        MenuItem none = new MenuItem("Aucune recette");
        none.setOnAction(ev -> setRecipeId(null));
        menu.getItems().add(none);

        List<ProcessRecipeRegistryEntry> entries = game.recipesForFactory(factory);
        entries.sort((a, b) -> a.recipe().id().compareToIgnoreCase(b.recipe().id()));

        if (!entries.isEmpty()) menu.getItems().add(new SeparatorMenuItem());

        for (ProcessRecipeRegistryEntry e : entries) {
            String id = e.recipe().id();
            MenuItem it = new MenuItem(id);
            it.setOnAction(ev -> setRecipeId(id));
            menu.getItems().add(it);
        }

        menu.show(this, screenX, screenY);
    }
}
