package fr.mrqsdf.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public final class CraftGraphRenderer {

    public static final class Palette {
        public static final TextColor RAW = TextColor.ANSI.BLUE;
        public static final TextColor FACTORY = TextColor.ANSI.GREEN;
        public static final TextColor INTERMEDIATE = TextColor.ANSI.YELLOW;
        public static final TextColor FINAL = TextColor.ANSI.RED;

        public static final TextColor EDGE = TextColor.ANSI.WHITE;

        public static final TextColor SELECT_BG = TextColor.ANSI.WHITE;
        public static final TextColor SELECT_FG = TextColor.ANSI.BLACK;

        public static final TextColor PANEL_BG = TextColor.ANSI.BLACK;
        public static final TextColor PANEL_FG = TextColor.ANSI.WHITE;
    }

    public void render(Screen screen, CraftGraph graph, CraftGraphLayout layout, CraftNode selected) {
        TextGraphics g = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        int viewW = size.getColumns();
        int viewH = Math.max(0, size.getRows() - 4);

        g.setForegroundColor(Palette.EDGE);

        // edges
        for (CraftEdge e : graph.edges()) {
            drawEdge(g, layout, e, viewW, viewH);
        }

        // nodes
        for (CraftNode n : graph.nodes()) {
            drawNode(g, layout, n, n == selected, viewW, viewH);
        }

        drawInfoPanel(g, size, selected);
    }

    private void drawInfoPanel(TextGraphics g, TerminalSize size, CraftNode selected) {
        int h = 4;
        int y0 = Math.max(0, size.getRows() - h);
        int w = size.getColumns();

        g.setBackgroundColor(Palette.PANEL_BG);
        g.setForegroundColor(Palette.PANEL_FG);
        g.fillRectangle(new TerminalPosition(0, y0), new TerminalSize(w, h), ' ');

        if (selected == null) return;

        String line1;
        String line2;

        if (selected instanceof MaterialNode mn) {
            line1 = "Selected Material: " + mn.materialId() + "  (" + mn.role() + ")";
            line2 = "InternalId: " + mn.id();
        } else if (selected instanceof FactoryNode fn) {
            line1 = "Selected Factory: " + fn.label();
            line2 = "InternalId: " + fn.id();
        } else {
            line1 = "Selected: " + selected.id();
            line2 = "";
        }

        String line3 = "Arrows: navigate | PgUp/PgDn: pan | Home/End: first/last | Q/Esc: quit";

        safePut(g, 1, y0 + 0, line1, w - 2);
        safePut(g, 1, y0 + 1, line2, w - 2);
        safePut(g, 1, y0 + 2, line3, w - 2);
    }

    private void safePut(TextGraphics g, int x, int y, String s, int maxLen) {
        if (y < 0) return;
        String out = (s == null) ? "" : s;
        if (out.length() > maxLen) out = out.substring(0, Math.max(0, maxLen));
        g.putString(x, y, out);
    }

    private void drawNode(TextGraphics g, CraftGraphLayout layout, CraftNode n, boolean selected, int viewW, int viewH) {
        CraftRect r = layout.rectOf(n);
        if (r == null) return;

        // clip grossier
        if (r.x() >= viewW || r.y() >= viewH) return;
        if (r.x() + r.w() < 0 || r.y() + r.h() < 0) return;

        TextColor fg = pickNodeColor(n);
        TextColor bg = TextColor.ANSI.BLACK;

        if (selected) {
            fg = Palette.SELECT_FG;
            bg = Palette.SELECT_BG;
        }

        g.setForegroundColor(fg);
        g.setBackgroundColor(bg);

        drawBoxClipped(g, r.x(), r.y(), r.w(), r.h(), viewW, viewH);

        String title = titleOf(n);
        putCenteredClipped(g, r.x(), r.y() + 1, r.w(), title, viewW, viewH);
    }

    private String titleOf(CraftNode n) {
        if (n instanceof MaterialNode mn) return mn.materialId();
        if (n instanceof FactoryNode fn) return fn.label();
        return n.id();
    }

    private TextColor pickNodeColor(CraftNode n) {
        if (n instanceof FactoryNode) return Palette.FACTORY;
        if (n instanceof MaterialNode mn) {
            return switch (mn.role()) {
                case RAW -> Palette.RAW;
                case INTERMEDIATE -> Palette.INTERMEDIATE;
                case FINAL -> Palette.FINAL;
            };
        }
        return TextColor.ANSI.WHITE;
    }

    private void drawEdge(TextGraphics g, CraftGraphLayout layout, CraftEdge e, int viewW, int viewH) {
        CraftRect a = layout.rectOf(e.from());
        CraftRect b = layout.rectOf(e.to());
        if (a == null || b == null) return;

        int x1 = a.x() + a.w();
        int y1 = a.y() + a.h() / 2;

        int x2 = b.x() - 1;
        int y2 = b.y() + b.h() / 2;

        int midX = (x1 + x2) / 2;

        hLine(g, x1, midX, y1, viewW, viewH);
        vLine(g, midX, y1, y2, viewW, viewH);
        hLine(g, midX, x2, y2, viewW, viewH);

        safeSet(g, x2, y2, '▶', viewW, viewH);
    }

    private void hLine(TextGraphics g, int x1, int x2, int y, int viewW, int viewH) {
        int a = Math.min(x1, x2);
        int b = Math.max(x1, x2);
        for (int x = a; x <= b; x++) safeSet(g, x, y, '─', viewW, viewH);
    }

    private void vLine(TextGraphics g, int x, int y1, int y2, int viewW, int viewH) {
        int a = Math.min(y1, y2);
        int b = Math.max(y1, y2);
        for (int y = a; y <= b; y++) safeSet(g, x, y, '│', viewW, viewH);
    }

    private void drawBoxClipped(TextGraphics g, int x, int y, int w, int h, int viewW, int viewH) {
        if (w < 2 || h < 2) return;

        char tl = '┌', tr = '┐', bl = '└', br = '┘', hz = '─', vt = '│';

        safeSet(g, x, y, tl, viewW, viewH);
        safeSet(g, x + w - 1, y, tr, viewW, viewH);
        safeSet(g, x, y + h - 1, bl, viewW, viewH);
        safeSet(g, x + w - 1, y + h - 1, br, viewW, viewH);

        for (int i = 1; i < w - 1; i++) {
            safeSet(g, x + i, y, hz, viewW, viewH);
            safeSet(g, x + i, y + h - 1, hz, viewW, viewH);
        }
        for (int j = 1; j < h - 1; j++) {
            safeSet(g, x, y + j, vt, viewW, viewH);
            safeSet(g, x + w - 1, y + j, vt, viewW, viewH);
        }

        for (int j = 1; j < h - 1; j++) {
            for (int i = 1; i < w - 1; i++) safeSet(g, x + i, y + j, ' ', viewW, viewH);
        }
    }

    private void putCenteredClipped(TextGraphics g, int x, int y, int w, String text, int viewW, int viewH) {
        if (y < 0 || y >= viewH) return;
        if (w <= 2) return;
        if (text == null) text = "";

        int inner = w - 2;
        String t = text.length() > inner ? text.substring(0, inner) : text;

        int padLeft = (inner - t.length()) / 2;
        String out = " ".repeat(Math.max(0, padLeft)) + t;
        if (out.length() < inner) out += " ".repeat(inner - out.length());

        int startX = x + 1;
        for (int i = 0; i < out.length(); i++) safeSet(g, startX + i, y, out.charAt(i), viewW, viewH);
    }

    private void safeSet(TextGraphics g, int x, int y, char ch, int viewW, int viewH) {
        if (x < 0 || y < 0 || x >= viewW || y >= viewH) return;
        g.setCharacter(x, y, ch);
    }
}
