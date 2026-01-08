package fr.mrqsdf.ui;

public final class FactoryNode implements CraftNode {

    private final String id;     // unique interne
    private final String label;  // ex: "GROUP ex:group/furnace [AUTO 8-15s]"

    private int x, y;
    private final int w, h;

    public FactoryNode(String id, String label) {
        this.id = id;
        this.label = label;

        this.h = 3;
        int minW = 22;
        int need = (label == null ? 7 : label.length()) + 4;
        this.w = Math.max(minW, need);
    }

    public String label() { return label; }

    @Override public String id() { return id; }
    @Override public int x() { return x; }
    @Override public int y() { return y; }
    @Override public int w() { return w; }
    @Override public int h() { return h; }

    @Override public void setPos(int x, int y) { this.x = x; this.y = y; }
}
