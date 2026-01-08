package fr.mrqsdf.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import fr.mrqsdf.Planner.Plan;

import java.io.IOException;
import java.util.Set;

public final class CraftLanternaViewer {

    private CraftLanternaViewer() {}

    public static void viewBestOnly(Plan plan, Set<String> availableRawIds) throws IOException {
        CraftGraph graph = CraftGraphBuilder.fromBestOnlyPlan(plan, availableRawIds);

        DefaultTerminalFactory tf = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(140, 40));

        try (Screen screen = new TerminalScreen(tf.createTerminal())) {
            screen.startScreen();
            screen.setCursorPosition(null);

            CraftGraphLayout layout = new CraftGraphLayout();
            layout.layout(graph, screen.getTerminalSize());

            CraftGraphNavigator nav = new CraftGraphNavigator(graph);
            CraftGraphRenderer renderer = new CraftGraphRenderer();

            boolean running = true;

            // ✅ followSelection = auto-center (flèches)
            boolean followSelection = true;

            while (running) {

                TerminalSize newSize = screen.doResizeIfNecessary();
                if (newSize != null) {
                    layout.layout(graph, newSize);
                }

                if (followSelection) {
                    layout.centerOnSelected(graph, screen.getTerminalSize(), nav.getSelected());
                }

                screen.clear();
                renderer.render(screen, graph, layout, nav.getSelected());
                screen.refresh();

                KeyStroke ks = screen.readInput();
                if (ks == null) continue;

                KeyType kt = ks.getKeyType();

                if (kt == KeyType.Escape) {
                    running = false;
                    continue;
                }
                if (kt == KeyType.Character) {
                    Character ch = ks.getCharacter();
                    if (ch != null) {
                        if (ch == 'q' || ch == 'Q') {
                            running = false;
                            continue;
                        }
                        if (ch == 'c' || ch == 'C') {
                            // re-center manuel
                            followSelection = true;
                            continue;
                        }
                    }
                }

                if (kt == KeyType.ArrowUp) {
                    nav.move(Direction.UP, layout);
                    followSelection = true;
                } else if (kt == KeyType.ArrowDown) {
                    nav.move(Direction.DOWN, layout);
                    followSelection = true;
                } else if (kt == KeyType.ArrowLeft) {
                    nav.move(Direction.LEFT, layout);
                    followSelection = true;
                } else if (kt == KeyType.ArrowRight) {
                    nav.move(Direction.RIGHT, layout);
                    followSelection = true;
                } else if (kt == KeyType.PageDown) {
                    // ✅ pan = désactive auto-center
                    layout.pan(graph, 0, +3, screen.getTerminalSize());
                    followSelection = false;
                } else if (kt == KeyType.PageUp) {
                    layout.pan(graph, 0, -3, screen.getTerminalSize());
                    followSelection = false;
                } else if (kt == KeyType.Home) {
                    nav.selectFirst();
                    followSelection = true;
                } else if (kt == KeyType.End) {
                    nav.selectLast();
                    followSelection = true;
                }
            }

            screen.stopScreen();
        }
    }

    public enum Direction { UP, DOWN, LEFT, RIGHT }
}
