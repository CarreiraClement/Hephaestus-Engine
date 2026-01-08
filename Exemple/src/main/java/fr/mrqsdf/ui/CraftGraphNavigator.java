package fr.mrqsdf.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CraftGraphNavigator {

    private final List<CraftNode> nodes;
    private int idx = 0;

    public CraftGraphNavigator(CraftGraph graph) {
        this.nodes = new ArrayList<>(graph.nodes());
        this.nodes.sort(Comparator.comparing(CraftNode::id));
    }

    public CraftNode getSelected() {
        if (nodes.isEmpty()) return null;
        idx = Math.max(0, Math.min(idx, nodes.size() - 1));
        return nodes.get(idx);
    }

    public void selectFirst() { idx = 0; }
    public void selectLast() { idx = Math.max(0, nodes.size() - 1); }

    public void move(CraftLanternaViewer.Direction dir, CraftGraphLayout layout) {
        if (nodes.size() <= 1) return;

        CraftNode cur = getSelected();
        CraftRect cr = layout.rectWorldOf(cur);
        if (cr == null) return;

        double bestScore = Double.POSITIVE_INFINITY;
        int bestIdx = idx;

        int cx = cr.centerX();
        int cy = cr.centerY();

        for (int i = 0; i < nodes.size(); i++) {
            if (i == idx) continue;

            CraftNode n = nodes.get(i);
            CraftRect r = layout.rectWorldOf(n);
            if (r == null) continue;

            int nx = r.centerX();
            int ny = r.centerY();

            boolean ok = switch (dir) {
                case LEFT -> nx < cx;
                case RIGHT -> nx > cx;
                case UP -> ny < cy;
                case DOWN -> ny > cy;
            };
            if (!ok) continue;

            int dx = nx - cx;
            int dy = ny - cy;

            double dist = Math.sqrt((double) dx * dx + (double) dy * dy);
            double axisPenalty = (dir == CraftLanternaViewer.Direction.LEFT || dir == CraftLanternaViewer.Direction.RIGHT)
                    ? Math.abs(dy) * 1.25
                    : Math.abs(dx) * 1.25;

            double score = dist + axisPenalty;

            if (score < bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }

        idx = bestIdx;
    }
}
