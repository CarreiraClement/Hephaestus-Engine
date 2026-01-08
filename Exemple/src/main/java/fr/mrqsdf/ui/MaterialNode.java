package fr.mrqsdf.ui;

public final class MaterialNode implements CraftNode {

    public enum Role { RAW, INTERMEDIATE, FINAL }

    private final String id;          // unique interne
    private final String materialId;  // ex: ex:coal
    private final Role role;

    private int x, y;
    private final int w, h;

    public MaterialNode(String id, String materialId, Role role) {
        this.id = id;
        this.materialId = materialId;
        this.role = role;

        // boîte 3 lignes
        this.h = 3;
        int minW = 14;
        int need = (materialId == null ? 3 : materialId.length()) + 4;
        this.w = Math.max(minW, need);
    }

    public String materialId() { return materialId; }
    public Role role() { return role; }

    @Override public String id() { return id; }
    @Override public int x() { return x; }
    @Override public int y() { return y; }
    @Override public int w() { return w; }
    @Override public int h() { return h; }

    @Override public void setPos(int x, int y) { this.x = x; this.y = y; }
}
