import java.awt.*;

// Car Base Class
abstract class Car {
    protected double x, y;
    protected double width = 45;
    protected double height = 80;
    protected Color color;

    public Car(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public boolean intersects(Car other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect((int) x + 3, (int) y + 3, (int) width, (int) height, 12, 12);

        GradientPaint gradient = new GradientPaint(
            (int) x, (int) y, color,
            (int) x, (int) (y + height), color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect((int) x, (int) y, (int) width, (int) height, 12, 12);

        g2d.setColor(color.darker().darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int) x, (int) y, (int) width, (int) height, 12, 12);

        g2d.setColor(new Color(100, 150, 200, 200));
        g2d.fillRoundRect((int) x + 8, (int) y + 12, (int) width - 16, 22, 6, 6);
        g2d.fillRoundRect((int) x + 8, (int) y + 46, (int) width - 16, 22, 6, 6);

        g2d.setColor(new Color(40, 40, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int) x + 8, (int) y + 12, (int) width - 16, 22, 6, 6);
        g2d.drawRoundRect((int) x + 8, (int) y + 46, (int) width - 16, 22, 6, 6);

        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect((int) x - 4, (int) y + 12, 8, 18, 4, 4);
        g2d.fillRoundRect((int) x + (int) width - 4, (int) y + 12, 8, 18, 4, 4);
        g2d.fillRoundRect((int) x - 4, (int) y + (int) height - 30, 8, 18, 4, 4);
        g2d.fillRoundRect((int) x + (int) width - 4, (int) y + (int) height - 30, 8, 18, 4, 4);

        g2d.setColor(new Color(180, 180, 180));
        g2d.fillOval((int) x - 2, (int) y + 16, 4, 10);
        g2d.fillOval((int) x + (int) width - 2, (int) y + 16, 4, 10);
        g2d.fillOval((int) x - 2, (int) y + (int) height - 26, 4, 10);
        g2d.fillOval((int) x + (int) width - 2, (int) y + (int) height - 26, 4, 10);

        if (y < 350) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval((int) x + 8, (int) y + 5, 6, 6);
            g2d.fillOval((int) x + (int) width - 14, (int) y + 5, 6, 6);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillOval((int) x + 8, (int) y + (int) height - 10, 6, 6);
            g2d.fillOval((int) x + (int) width - 14, (int) y + (int) height - 10, 6, 6);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}