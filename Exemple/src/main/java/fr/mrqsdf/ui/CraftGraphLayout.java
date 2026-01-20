package fr.mrqsdf.ui;

import com.googlecode.lanterna.TerminalSize;

import java.util.*;

/**
 * Layout engine for CraftGraph.
 */
public final class CraftGraphLayout {

    private static final int COL_GAP = 8;
    private static final int ROW_GAP = 2;
    private static final int COMPONENT_GAP_Y = 3;

    private int offsetX = 0;
    private int offsetY = 0;

    /**
     * Perform layout of the graph.
     * @param graph Graph
     * @param screenSize Screen size
     */
    public void layout(CraftGraph graph, TerminalSize screenSize) {
        offsetX = 0;
        offsetY = 0;

        if (graph == null || graph.nodes().isEmpty()) return;

        Map<CraftNode, Integer> depth = computeDepths(graph);
        int maxDepth = depth.values().stream().max(Integer::compareTo).orElse(0);

        Map<Integer, Integer> layerX = new HashMap<>();
        int x = 2;
        for (int d = 0; d <= maxDepth; d++) {
            layerX.put(d, x);

            int colW = 0;
            for (CraftNode n : graph.nodes()) {
                if (depth.getOrDefault(n, 0) != d) continue;
                colW = Math.max(colW, n.w());
            }
            if (colW == 0) colW = 20;

            x += colW + COL_GAP;
        }

        Map<CraftNode, List<CraftNode>> preds = new HashMap<>();
        Map<CraftNode, List<CraftNode>> succs = new HashMap<>();
        for (CraftNode n : graph.nodes()) {
            preds.put(n, new ArrayList<>());
            succs.put(n, new ArrayList<>());
        }
        for (CraftEdge e : graph.edges()) {
            preds.get(e.to()).add(e.from());
            succs.get(e.from()).add(e.to());
        }

        for (List<CraftNode> list : preds.values()) list.sort(Comparator.comparing(CraftNode::id));
        for (List<CraftNode> list : succs.values()) list.sort(Comparator.comparing(CraftNode::id));

        List<CraftNode> roots = new ArrayList<>();
        for (CraftNode n : graph.nodes()) {
            if (succs.getOrDefault(n, List.of()).isEmpty()) roots.add(n);
        }
        roots.sort(Comparator.<CraftNode>comparingInt(n -> depth.getOrDefault(n, 0)).reversed()
                .thenComparing(CraftNode::id));

        int cursorY = 1;
        Set<CraftNode> placed = new HashSet<>();
        for (CraftNode root : roots) {
            if (placed.contains(root)) continue;
            cursorY = placeRec(root, preds, depth, layerX, cursorY, placed);
            cursorY += COMPONENT_GAP_Y;
        }

        clampOffsets(graph, screenSize);
    }

    /** Rect viewport (world - offset). */
    public CraftRect rectOf(CraftNode node) {
        if (node == null) return null;
        return new CraftRect(node.x() - offsetX, node.y() - offsetY, node.w(), node.h());
    }

    /** Rect world (sans offset). */
    public CraftRect rectWorldOf(CraftNode node) {
        if (node == null) return null;
        return new CraftRect(node.x(), node.y(), node.w(), node.h());
    }

    public void pan(CraftGraph graph, int dx, int dy, TerminalSize screenSize) {
        offsetX += dx;
        offsetY += dy;
        clampOffsets(graph, screenSize);
    }

    public void centerOnSelected(CraftGraph graph, TerminalSize size, CraftNode selected) {
        if (selected == null) return;

        int viewW = size.getColumns();
        int viewH = Math.max(0, size.getRows() - 4);

        offsetX = (selected.x() + selected.w() / 2) - viewW / 2;
        offsetY = (selected.y() + selected.h() / 2) - viewH / 2;

        clampOffsets(graph, size);
    }

    // ------------------------------------------------------------

    private int placeRec(CraftNode node,
                         Map<CraftNode, List<CraftNode>> preds,
                         Map<CraftNode, Integer> depth,
                         Map<Integer, Integer> layerX,
                         int cursorY,
                         Set<CraftNode> placed) {

        if (placed.contains(node)) return cursorY;

        List<CraftNode> children = preds.getOrDefault(node, List.of());

        if (children.isEmpty()) {
            int d = depth.getOrDefault(node, 0);
            int x = layerX.getOrDefault(d, 2);

            node.setPos(x, cursorY);
            placed.add(node);

            return cursorY + node.h() + ROW_GAP;
        }

        int startY = cursorY;
        for (CraftNode c : children) {
            cursorY = placeRec(c, preds, depth, layerX, cursorY, placed);
        }

        int minCY = Integer.MAX_VALUE;
        int maxCY = Integer.MIN_VALUE;
        for (CraftNode c : children) {
            int ccY = c.y() + c.h() / 2;
            minCY = Math.min(minCY, ccY);
            maxCY = Math.max(maxCY, ccY);
        }

        int d = depth.getOrDefault(node, 0);
        int x = layerX.getOrDefault(d, 2);

        int centerY = (minCY == Integer.MAX_VALUE) ? (startY + node.h() / 2) : ((minCY + maxCY) / 2);
        int yTop = centerY - node.h() / 2;
        if (yTop < startY) yTop = startY;

        node.setPos(x, yTop);
        placed.add(node);

        return cursorY;
    }

    private void clampOffsets(CraftGraph graph, TerminalSize size) {
        if (graph == null) return;

        Bounds b = computeBounds(graph);
        int viewW = size.getColumns();
        int viewH = Math.max(0, size.getRows() - 4);

        int minOX = 0;
        int minOY = 0;

        int maxOX = Math.max(0, b.maxX - viewW + 2);
        int maxOY = Math.max(0, b.maxY - viewH + 2);

        if (offsetX < minOX) offsetX = minOX;
        if (offsetY < minOY) offsetY = minOY;
        if (offsetX > maxOX) offsetX = maxOX;
        if (offsetY > maxOY) offsetY = maxOY;
    }

    private Bounds computeBounds(CraftGraph graph) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (CraftNode n : graph.nodes()) {
            minX = Math.min(minX, n.x());
            minY = Math.min(minY, n.y());
            maxX = Math.max(maxX, n.x() + n.w());
            maxY = Math.max(maxY, n.y() + n.h());
        }

        if (minX == Integer.MAX_VALUE) minX = minY = maxX = maxY = 0;
        return new Bounds(minX, minY, maxX, maxY);
    }

    private record Bounds(int minX, int minY, int maxX, int maxY) {}

    private Map<CraftNode, Integer> computeDepths(CraftGraph graph) {
        Map<CraftNode, List<CraftNode>> outEdges = new HashMap<>();
        Map<CraftNode, Integer> indeg = new HashMap<>();

        for (CraftNode n : graph.nodes()) {
            outEdges.put(n, new ArrayList<>());
            indeg.put(n, 0);
        }
        for (CraftEdge e : graph.edges()) {
            outEdges.get(e.from()).add(e.to());
            indeg.put(e.to(), indeg.get(e.to()) + 1);
        }

        Map<CraftNode, Integer> depth = new HashMap<>();
        ArrayDeque<CraftNode> q = new ArrayDeque<>();
        for (CraftNode n : graph.nodes()) {
            depth.put(n, 0);
            if (indeg.get(n) == 0) q.add(n);
        }

        while (!q.isEmpty()) {
            CraftNode cur = q.poll();
            int d = depth.getOrDefault(cur, 0);

            for (CraftNode nxt : outEdges.getOrDefault(cur, List.of())) {
                depth.put(nxt, Math.max(depth.getOrDefault(nxt, 0), d + 1));
                indeg.put(nxt, indeg.get(nxt) - 1);
                if (indeg.get(nxt) == 0) q.add(nxt);
            }
        }
        return depth;
    }
}
