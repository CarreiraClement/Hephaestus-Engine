package fr.mrqsdf.ui;

public record CraftRect(int x, int y, int w, int h) {
    public int centerX() {
        return x + w / 2;
    }

    public int centerY() {
        return y + h / 2;
    }
}
