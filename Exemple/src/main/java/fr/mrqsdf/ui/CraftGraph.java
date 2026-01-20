package fr.mrqsdf.ui;

import java.util.*;

/**
 * Crafting graph representation.
 */
public final class CraftGraph {

    private final List<CraftNode> nodes = new ArrayList<>();
    private final List<CraftEdge> edges = new ArrayList<>();

    private final Map<CraftNode, List<CraftNode>> out = new HashMap<>();
    private final Map<CraftNode, List<CraftNode>> in  = new HashMap<>();

    /**
     * Add a node to the graph.
     * @param n the node to add
     */
    void addNode(CraftNode n) {
        nodes.add(n);
        out.computeIfAbsent(n, k -> new ArrayList<>());
        in.computeIfAbsent(n, k -> new ArrayList<>());
    }

    /**
     * Add an edge to the graph.
     * @param a the source node
     * @param b the target node
     */
    void addEdge(CraftNode a, CraftNode b) {
        edges.add(new CraftEdge(a, b));
        out.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        in.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
    }

    /**
     * Get the list of nodes in the graph.
     * @return the list of nodes
     */
    public List<CraftNode> nodes() { return Collections.unmodifiableList(nodes); }
    /**
     * Get the list of edges in the graph.
     * @return the list of edges
     */
    public List<CraftEdge> edges() { return Collections.unmodifiableList(edges); }

    /**
     * Get the outgoing neighbors of a node.
     * @param n the node
     * @return the list of outgoing neighbors
     */
    public List<CraftNode> outgoing(CraftNode n) { return out.getOrDefault(n, List.of()); }

    /**
     * Get the incoming neighbors of a node.
     * @param n the node
     * @return the list of incoming neighbors
     */
    public List<CraftNode> incoming(CraftNode n) { return in.getOrDefault(n, List.of()); }
}
