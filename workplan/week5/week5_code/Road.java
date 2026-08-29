import java.awt.*;
import java.util.Random;

// Road Class - 3-lane scrolling road with curve physics
class Road {
    private int baseRoadLeft = 220;
    private int roadWidth = 460;
    private double roadOffset = 0;
    private double curveOffset = 0;
    private double curveTarget = 0;
    private double curveSpeed = 0;
    private int curveTimer = 0;

    public void update(double speed, double distance) {
        roadOffset += speed / 2;

        curveTimer++;
        if (curveTimer > 120 + Math.random() * 120) {
            curveTarget = (Math.random() - 0.5) * 150;
            curveTimer = 0;
        }

        curveSpeed += (curveTarget - curveOffset) * 0.001;
        curveSpeed *= 0.95;
        curveOffset += curveSpeed;

        if (curveOffset > 100) curveOffset = 100;
        if (curveOffset < -100) curveOffset = -100;
    }

    public void draw(Graphics2D g2d, int height) {
        int leftEdge = getRoadLeft();

        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(leftEdge - 15, 0, roadWidth + 30, height);

        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(leftEdge, 0, roadWidth, height);

        g2d.setColor(new Color(55, 55, 55));
        Random rand = new Random(12345);
        for (int i = 0; i < 30; i++) {
            int crackY = (int) ((roadOffset + i * 50) % height);
            int crackX = leftEdge + rand.nextInt(roadWidth);
            g2d.drawLine(crackX, crackY, crackX + rand.nextInt(20), crackY + rand.nextInt(10));
        }

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawLine(leftEdge, 0, leftEdge, height);
        g2d.drawLine(leftEdge + roadWidth, 0, leftEdge + roadWidth, height);

        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(3));

        int dashLength = 50;
        int dashGap = 40;
        int offset = (int) (roadOffset % (dashLength + dashGap));

        for (int lane = 1; lane < 3; lane++) {
            int laneX = leftEdge + (roadWidth * lane / 3);
            for (int y = -offset; y < height; y += dashLength + dashGap) {
                g2d.drawLine(laneX, y, laneX, y + dashLength);
            }
        }

        g2d.setColor(Color.RED);
        for (int y = 0; y < height; y += 40) {
            int stripY = (y + (int) roadOffset) % height;
            g2d.fillRect(leftEdge - 12, stripY, 8, 20);
            g2d.fillRect(leftEdge + roadWidth + 4, stripY, 8, 20);
        }
    }

    public int getLaneX(int lane) {
        int leftEdge = getRoadLeft();
        return leftEdge + (roadWidth / 6) + lane * (roadWidth / 3);
    }

    public int getRoadLeft() {
        return baseRoadLeft + (int) curveOffset;
    }

    public int getRoadWidth() {
        return roadWidth;
    }

    public double getCurveOffset() {
        return curveOffset;
    }
}