import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public final class Chart {
    private Chart() {
    }

    private static void drawSeries(java.awt.Graphics2D g, List<Integer> ns, List<Long> ys, Color c,
                                   int L, int T, int pw, int ph, double maxX, double maxY) {
        g.setColor(c);
        g.setStroke(new BasicStroke(2.2f));
        int px = 0, py = 0;
        for (int i = 0; i < ns.size(); i++) {
            int x = L + (int) (pw * ns.get(i) / maxX);
            int y = T + ph - (int) (ph * ys.get(i) / maxY);
            if (i > 0) g.drawLine(px, py, x, y);
            g.fillOval(x - 3, y - 3, 6, 6);
            px = x;
            py = y;
        }
    }

    // Rysuje punkty (ns, ys) oraz krzywą odniesienia log2 n i zapisuje wykres do pliku PNG.
    public static void draw(List<Integer> ns, List<Long> ys, String title, String file) throws IOException {
        draw(ns, ys, "Insert", null, null, title, file, true);
    }

    // Rysuje 1 lub 2 serie (ys2 może być null); log2 n opcjonalnie.
    public static void draw(List<Integer> ns, List<Long> ys, String label1, List<Long> ys2, String label2,
                            String title, String file, boolean showLog) throws IOException {
        int W = 900, H = 560, L = 80, R = 30, T = 50, B = 70;
        var img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        var g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        double maxX = ns.get(ns.size() - 1);
        double maxY = 0;
        for (long v : ys) maxY = Math.max(maxY, v);
        if (ys2 != null) for (long v : ys2) maxY = Math.max(maxY, v);
        if (showLog) maxY = Math.max(maxY, Math.log(maxX) / Math.log(2));
        maxY *= 1.1;
        int pw = W - L - R, ph = H - T - B;

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i <= 5; i++) {
            int y = T + ph - ph * i / 5;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(L, y, L + pw, y);
            g.setColor(Color.BLACK);
            g.drawString(String.format("%.1f", maxY * i / 5), L - 50, y + 4);
        }
        int step = (int) Math.max(1, Math.round(maxX / 15 / 10) * 10);
        for (int xv = 0; xv <= maxX; xv += step) {
            int x = L + (int) (pw * xv / maxX);
            g.setColor(new Color(225, 225, 225));
            g.drawLine(x, T, x, T + ph);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(xv), x - 15, T + ph + 18);
        }
        g.drawRect(L, T, pw, ph);
        g.drawString("n", L + pw / 2, H - 25);
        g.drawString("liczba przebiegów pętli while", 10, T - 15);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 15f));
        g.drawString(title, L, 28);

        int px = 0, py = 0;
        Color green = new Color(40, 150, 60), blue = new Color(30, 100, 220), red = new Color(210, 50, 50);

        if (showLog) {
            g.setColor(green);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f, new float[]{7f, 5f}, 0f));
            for (int i = 0; i <= pw; i++) {
                double nv = Math.max(1, maxX * i / pw);
                int x = L + i;
                int y = T + ph - (int) (ph * (Math.log(nv) / Math.log(2)) / maxY);
                if (i > 0) g.drawLine(px, py, x, y);
                px = x;
                py = y;
            }
        }

        drawSeries(g, ns, ys, blue, L, T, pw, ph, maxX, maxY);
        if (ys2 != null) drawSeries(g, ns, ys2, red, L, T, pw, ph, maxX, maxY);

        int lx = L + 15;
        g.setStroke(new BasicStroke(2.2f));
        g.setColor(blue);
        g.fillRect(lx, T + 12, 14, 4);
        g.setColor(Color.BLACK);
        g.drawString(label1, lx + 20, T + 18);
        lx += 30 + g.getFontMetrics().stringWidth(label1) + 15;
        if (ys2 != null) {
            g.setColor(red);
            g.fillRect(lx, T + 12, 14, 4);
            g.setColor(Color.BLACK);
            g.drawString(label2, lx + 20, T + 18);
            lx += 30 + g.getFontMetrics().stringWidth(label2) + 15;
        }
        if (showLog) {
            g.setColor(green);
            g.drawLine(lx, T + 14, lx + 14, T + 14);
            g.setColor(Color.BLACK);
            g.drawString("log2 n", lx + 20, T + 18);
        }
        g.dispose();
        ImageIO.write(img, "png", new File(file));
    }
}
