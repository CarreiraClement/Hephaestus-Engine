package fr.mrqsdf.ui;

import java.util.*;

public final class CraftGraph {

    private final List<CraftNode> nodes = new ArrayList<>();
    private final List<CraftEdge> edges = new ArrayList<>();

    private final Map<CraftNode, List<CraftNode>> out = new HashMap<>();
    private final Map<CraftNode, List<CraftNode>> in  = new HashMap<>();

    void addNode(CraftNode n) {
        nodes.add(n);
        out.computeIfAbsent(n, k -> new ArrayList<>());
        in.computeIfAbsent(n, k -> new ArrayList<>());
    }

    void addEdge(CraftNode a, CraftNode b) {
        edges.add(new CraftEdge(a, b));
        out.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        in.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
    }

    public List<CraftNode> nodes() { return Collections.unmodifiableList(nodes); }
    public List<CraftEdge> edges() { return Collections.unmodifiableList(edges); }

    public List<CraftNode> outgoing(CraftNode n) { return out.getOrDefault(n, List.of()); }
    public List<CraftNode> incoming(CraftNode n) { return in.getOrDefault(n, List.of()); }
}
