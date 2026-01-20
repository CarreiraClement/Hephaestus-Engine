package fr.mrqsdf.ui;

/**
 * Represents an edge between two craft nodes in the UI.
 *
 * @param from The starting craft node.
 * @param to   The ending craft node.
 */
public record CraftEdge(CraftNode from, CraftNode to) {}
