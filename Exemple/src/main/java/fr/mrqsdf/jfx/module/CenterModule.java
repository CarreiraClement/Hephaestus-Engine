
package fr.mrqsdf.jfx.module;

import fr.mrqsdf.jfx.game.GameContext;
import fr.olympus.hephaestus.factory.Factory;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Center module class that manages factory modules and Display Center.
 */
public class CenterModule extends Pane {

    /**
     * Game context.
     */
    private final GameContext game;

    /**
     * List of factory modules.
     */
    private final List<FactoryModule> modules = new ArrayList<>();

    /**
     * Currently placing factory id.
     */
    private String placingFactoryId;

    /**
     * Constructor.
     *
     * @param game Game context.
     */
    public CenterModule(GameContext game) {
        this.game = Objects.requireNonNull(game, "game");

        setOnMouseClicked(e -> {
            if (placingFactoryId == null) return;
            if (e.getButton() != MouseButton.PRIMARY) return;

            Factory f = game.data().createFactory(placingFactoryId);
            FactoryModule fm = new FactoryModule(game, f);

            fm.applyCss();
            fm.autosize();
            fm.resize(fm.getPrefWidth(), fm.getPrefHeight());

            double x = e.getX() - fm.getWidth() * 0.5;
            double y = e.getY() - fm.getHeight() * 0.5;

            if (x < 0) x = 0;
            if (y < 0) y = 0;

            fm.relocate(x, y);

            getChildren().add(fm);
            modules.add(fm);

            endPlacement();
            e.consume();
        });
    }

    /**
     * Begin placement of a factory with the given ID.
     *
     * @param factoryId Factory ID.
     */
    public void beginPlacement(String factoryId) {
        this.placingFactoryId = factoryId;
        setCursor(Cursor.CROSSHAIR);
    }

    /**
     * End placement of a factory.
     */
    public void endPlacement() {
        this.placingFactoryId = null;
        setCursor(Cursor.DEFAULT);
    }

    /**
     * Tick method to update modules.
     *
     * @param dt Delta time.
     */
    public void tick(float dt) {
        for (FactoryModule m : modules) {
            if (m.getParent() == null) continue;
            m.tick(dt);
        }
        modules.removeIf(m -> m.getParent() == null);
    }

    /**
     * Get the list of factory modules.
     *
     * @return List of factory modules.
     */
    public List<FactoryModule> getModules() {
        return modules;
    }
}
