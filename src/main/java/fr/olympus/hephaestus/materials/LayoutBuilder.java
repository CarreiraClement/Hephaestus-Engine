package fr.olympus.hephaestus.materials;

public final class LayoutBuilder {

    public static final byte PRESENT   = 0b100;
    public static final byte CAN_CHANGE= 0b010;
    public static final byte CHANGED   = 0b001;

    private byte[][][] layout;

    private LayoutBuilder() {}

    public LayoutBuilder setSize(int x, int y, int z) {
        if (x <= 0 || y <= 0 || z <= 0) throw new IllegalArgumentException("Size must be > 0.");
        this.layout = new byte[x][y][z];
        return this;
    }

    public LayoutBuilder setFlag(int x, int y, int z, byte flag) {
        check();
        checkBounds(x, y, z);
        layout[x][y][z] |= flag;
        return this;
    }

    public LayoutBuilder isPresent(int x, int y, int z) {
        return setFlag(x, y, z, PRESENT);
    }

    public LayoutBuilder canChange(int x, int y, int z) {
        return setFlag(x, y, z, CAN_CHANGE);
    }

    public LayoutBuilder isChanged(int x, int y, int z) {
        return setFlag(x, y, z, CHANGED);
    }

    public byte[][][] build() {
        check();
        return layout;
    }

    public static void markChanged(byte[][][] layout, int x, int y, int z) {
        if (layout == null) throw new IllegalArgumentException("layout cannot be null.");
        layout[x][y][z] |= CHANGED;
    }

    public static LayoutBuilder create() {
        return new LayoutBuilder();
    }

    private void check() {
        if (layout == null) throw new IllegalStateException("Layout size not set. Call setSize(...) first.");
    }

    private void checkBounds(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0
                || x >= layout.length
                || y >= layout[0].length
                || z >= layout[0][0].length) {
            throw new IndexOutOfBoundsException("Out of bounds: " + x + "," + y + "," + z);
        }
    }
}
