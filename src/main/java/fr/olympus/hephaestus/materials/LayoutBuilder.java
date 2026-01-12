package fr.olympus.hephaestus.materials;


/**
 * Builder for 3D layout arrays with flags.
 */
public final class LayoutBuilder {

    // Flag definitions
    /**
     * Bit 2 (0b100): PRESENT
     */
    public static final byte PRESENT = 0b100;
    /**
     * Bit 1 (0b010): CAN_CHANGE
     */
    public static final byte CAN_CHANGE = 0b010;
    /**
     * Bit 0 (0b001): CHANGED
     */
    public static final byte CHANGED = 0b001;

    /**
     * 3D layout array storing flags for each position.
     */
    private byte[][][] layout;

    private LayoutBuilder() {
    }

    /**
     * Sets the size of the 3D layout array.
     *
     * @param x Size in the X dimension (must be > 0).
     * @param y Size in the Y dimension (must be > 0).
     * @param z Size in the Z dimension (must be > 0).
     * @return The current LayoutBuilder instance.
     * @throws IllegalArgumentException if any dimension is inferior or equals 0.
     */
    public LayoutBuilder setSize(int x, int y, int z) {
        if (x <= 0 || y <= 0 || z <= 0) throw new IllegalArgumentException("Size must be > 0.");
        this.layout = new byte[x][y][z];
        return this;
    }

    /**
     * Sets a flag at the specified coordinates.
     *
     * @param x    X coordinate.
     * @param y    Y coordinate.
     * @param z    Z coordinate.
     * @param flag The flag to set.
     * @return The current LayoutBuilder instance.
     * @throws IllegalStateException     if the layout size has not been set.
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds.
     */
    public LayoutBuilder setFlag(int x, int y, int z, byte flag) {
        check();
        checkBounds(x, y, z);
        layout[x][y][z] |= flag;
        return this;
    }

    /**
     * Marks the position at (x, y, z) as present.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return The current LayoutBuilder instance.
     */
    public LayoutBuilder isPresent(int x, int y, int z) {
        return setFlag(x, y, z, PRESENT);
    }

    /**
     * Marks the position at (x, y, z) as changeable.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return The current LayoutBuilder instance.
     */
    public LayoutBuilder canChange(int x, int y, int z) {
        return setFlag(x, y, z, CAN_CHANGE);
    }

    /**
     * Marks the position at (x, y, z) as changed.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return The current LayoutBuilder instance.
     */
    public LayoutBuilder isChanged(int x, int y, int z) {
        return setFlag(x, y, z, CHANGED);
    }

    /**
     * Builds and returns the 3D layout array.
     *
     * @return The constructed 3D layout array.
     * @throws IllegalStateException if the layout size has not been set.
     */
    public byte[][][] build() {
        check();
        return layout;
    }

    /**
     * Marks the position at (x, y, z) in the given layout as changed.
     *
     * @param layout The 3D layout array.
     * @param x      X coordinate.
     * @param y      Y coordinate.
     * @param z      Z coordinate.
     * @throws IllegalArgumentException if the layout is null.
     */
    public static void markChanged(byte[][][] layout, int x, int y, int z) {
        if (layout == null) throw new IllegalArgumentException("layout cannot be null.");
        layout[x][y][z] |= CHANGED;
    }

    /**
     * Creates a new LayoutBuilder instance.
     *
     * @return A new LayoutBuilder.
     */
    public static LayoutBuilder create() {
        return new LayoutBuilder();
    }

    // Private helper methods

    /**
     * Checks if the layout size has been set.
     */
    private void check() {
        if (layout == null) throw new IllegalStateException("Layout size not set. Call setSize(...) first.");
    }

    /**
     * Checks if the given coordinates are within bounds of the layout.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds.
     */
    private void checkBounds(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0
                || x >= layout.length
                || y >= layout[0].length
                || z >= layout[0][0].length) {
            throw new IndexOutOfBoundsException("Out of bounds: " + x + "," + y + "," + z);
        }
    }
}
