package fr.olympus.hephaestus.materials;

public class LayoutBuilder {

    private LayoutBuilder builder;

    private byte[][][] layout;

    private LayoutBuilder() {
        this.builder = this;
    }

    public LayoutBuilder setSize(int x, int y, int z) {
        this.layout = new byte[x][y][z];
        return this.builder;
    }

    public LayoutBuilder isPresent(int x, int y, int z) {
        this.layout[x][y][z] |= 0b100;
        return this.builder;
    }

    public LayoutBuilder canChange(int x, int y, int z) {
        this.layout[x][y][z] |= 0b010;
        return this.builder;
    }

    public LayoutBuilder isChanged(int x, int y, int z) {
        this.layout[x][y][z] |= 0b001;
        return this.builder;
    }

    public byte[][][] build() {
        return this.layout;
    }

    public static byte[][][] isChanged(byte[][][] layout, int x, int y, int z) {
       layout[x][y][z] |= 0b001;
        return layout;
    }

    public static LayoutBuilder create() {
        return new LayoutBuilder();
    }

}
