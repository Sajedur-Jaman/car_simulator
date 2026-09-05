import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

// Main Game Class
public class CarRacingGame extends JFrame {
    public CarRacingGame() {
        setTitle("Car Race..developed by sajedur (Week 6 Build)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        gamePanel.startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarRacingGame());
    }
}

// Game Panel Class - Week 6: adds EnemyCar, AABB collision detection, GasItem pickups
class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int PANEL_WIDTH = 900;
    private static final int PANEL_HEIGHT = 700;
    private static final int FPS = 60;

    private Timer gameTimer;
    private PlayerCar playerCar;
    private Road road;
    private ArrayList<EnemyCar> enemyCars;
    private ArrayList<GasItem> gasItems;
    private Random random;

    private int score;
    private double distanceTraveled;
    private boolean gameOver;
    private boolean boosting;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(100, 180, 100));
        setFocusable(true);
        addKeyListener(this);

        random = new Random();
        enemyCars = new ArrayList<>();
        gasItems = new ArrayList<>();

        initGame();
    }

    private void initGame() {
        road = new Road();
        playerCar = new PlayerCar(PANEL_WIDTH / 2 - 20, PANEL_HEIGHT - 150);
        score = 0;
        distanceTraveled = 0;
        gameOver = false;
        boosting = false;

        enemyCars.clear();
        gasItems.clear();

        for (int i = 0; i < 3; i++) {
            spawnEnemyCar();
        }
    }

    public void startGame() {
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
    }

    private void spawnEnemyCar() {
        int lane = random.nextInt(3);
        double x = road.getLaneX(lane);
        double y = random.nextBoolean() ? -100 - random.nextInt(200) : PANEL_HEIGHT + random.nextInt(200);
        boolean comingTowards = y < 0;
        enemyCars.add(new EnemyCar(x, y, comingTowards));
    }

    private void spawnGasItem() {
        int lane = random.nextInt(3);
        double x = road.getLaneX(lane);
        double y = -50;
        gasItems.add(new GasItem(x, y));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            update();
        }
        repaint();
    }

    private void update() {
        distanceTraveled += playerCar.getSpeed() / 10.0;

        road.update(playerCar.getSpeed(), distanceTraveled);
        playerCar.update(road);

        // --- Enemy cars: movement + AABB collision detection ---
        ArrayList<EnemyCar> carsToRemove = new ArrayList<>();
        for (EnemyCar enemy : enemyCars) {
            enemy.update(playerCar.getSpeed(), road);

            if (playerCar.intersects(enemy)) {
                gameOver = true;
                return;
            }

            if (!enemy.isScored()) {
                if (enemy.isComingTowards()) {
                    if (enemy.getY() > playerCar.getY() + playerCar.getHeight()) {
                        score += 10;
                        enemy.setScored(true);
                    }
                } else {
                    if (playerCar.getY() > enemy.getY() + enemy.getHeight()) {
                        score += 10;
                        enemy.setScored(true);
                    }
                }
            }

            if (enemy.getY() > PANEL_HEIGHT + 100 || enemy.getY() < -200) {
                carsToRemove.add(enemy);
            }
        }
        enemyCars.removeAll(carsToRemove);

        if (random.nextInt(100) < 3) {
            spawnEnemyCar();
        }

        // --- Gas items: movement + collection ---
        ArrayList<GasItem> itemsToRemove = new ArrayList<>();
        for (GasItem gas : gasItems) {
            gas.update(playerCar.getSpeed(), road);

            if (playerCar.intersects(gas)) {
                playerCar.collectGas();
                score += 5;
                itemsToRemove.add(gas);
            }

            if (gas.getY() > PANEL_HEIGHT + 50) {
                itemsToRemove.add(gas);
            }
        }
        gasItems.removeAll(itemsToRemove);

        if (random.nextInt(200) < 2) {
            spawnGasItem();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Simple placeholder sky/grass - full scenery (trees, lakes, birds) is Week 7
        g2d.setColor(new Color(135, 206, 250));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        road.draw(g2d, PANEL_HEIGHT);

        for (GasItem gas : gasItems) {
            gas.draw(g2d);
        }

        for (EnemyCar enemy : enemyCars) {
            enemy.draw(g2d);
        }

        playerCar.draw(g2d);

        drawUI(g2d);

        if (gameOver) {
            drawGameOver(g2d);
        }
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, 10, 280, 100, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        g2d.drawString("Score: " + score, 25, 35);
        g2d.drawString("Speed: " + (int) playerCar.getSpeed() + " km/h", 25, 60);
        g2d.drawString("Distance: " + (int) distanceTraveled, 25, 85);

        if (boosting) {
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.drawString("\u26A1 BOOST! \u26A1", PANEL_WIDTH / 2 - 80, 60);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 70));
        g2d.drawString("GAME OVER", PANEL_WIDTH / 2 - 220, PANEL_HEIGHT / 2 - 50);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 35));
        g2d.drawString("Final Score: " + score, PANEL_WIDTH / 2 - 130, PANEL_HEIGHT / 2 + 30);
        g2d.drawString("Press R to Restart", PANEL_WIDTH / 2 - 150, PANEL_HEIGHT / 2 + 90);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                initGame();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                playerCar.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                playerCar.moveRight();
                break;
            case KeyEvent.VK_SPACE:
                playerCar.boost();
                boosting = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            playerCar.stopBoost();
            boosting = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

// Road Class - 3-lane scrolling road with curve physics (Week 5)
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

// Car Base Class (Week 5)
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

    // AABB collision check against another car
    public boolean intersects(Car other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }

    // AABB collision check against a gas item
    public boolean intersects(GasItem item) {
        return x < item.getX() + item.getSize() &&
               x + width > item.getX() &&
               y < item.getY() + item.getSize() &&
               y + height > item.getY();
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

        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int) x + 8, (int) y + 5, 6, 6);
        g2d.fillOval((int) x + (int) width - 14, (int) y + 5, 6, 6);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}

// Player Car Class (Week 5)
class PlayerCar extends Car {
    private double speed = 6;
    private double baseSpeed = 6;
    private int targetLane = 1;
    private double laneX;
    private boolean isBoosting = false;

    public PlayerCar(double x, double y) {
        super(x, y, new Color(220, 20, 20));
        laneX = x;
    }

    public void update(Road road) {
        double baseLaneX = road.getLaneX(targetLane);
        laneX = baseLaneX;

        if (Math.abs(x - laneX) > 2) {
            x += (laneX - x) * 0.2;
        } else {
            x = laneX;
        }

        if (speed > baseSpeed && !isBoosting) {
            speed -= 0.08;
            if (speed < baseSpeed) speed = baseSpeed;
        }
    }

    public void moveLeft() {
        if (targetLane > 0) {
            targetLane--;
        }
    }

    public void moveRight() {
        if (targetLane < 2) {
            targetLane++;
        }
    }

    public void boost() {
        isBoosting = true;
        speed = baseSpeed + 6;
    }

    public void stopBoost() {
        isBoosting = false;
    }

    public void collectGas() {
        speed += 2.5;
        if (speed > baseSpeed + 10) speed = baseSpeed + 10;
    }

    public double getSpeed() {
        return speed;
    }
}

// Enemy Car Class - Week 6: randomized colors, speeds, and lane positions
class EnemyCar extends Car {
    private boolean comingTowards;
    private double speed;
    private boolean scored = false;

    public EnemyCar(double x, double y, boolean comingTowards) {
        super(x, y, getRandomCarColor());
        this.comingTowards = comingTowards;
        this.speed = comingTowards ? 9 + Math.random() * 5 : 2.5 + Math.random() * 3;
    }

    private static Color getRandomCarColor() {
        Color[] colors = {
                new Color(50, 100, 200),
                new Color(200, 200, 50),
                new Color(50, 200, 100),
                new Color(150, 50, 200),
                new Color(200, 100, 50)
        };
        return colors[(int) (Math.random() * colors.length)];
    }

    public void update(double playerSpeed, Road road) {
        if (comingTowards) {
            y += speed + playerSpeed / 2;
        } else {
            y += playerSpeed / 2 - speed;
        }

        double targetX = road.getLaneX((int) ((x - road.getRoadLeft()) / (road.getRoadWidth() / 3)));
        x += (targetX - x) * 0.1;
    }

    public boolean isComingTowards() {
        return comingTowards;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }
}

// Gas Item Class - Week 6: collectible with spin/glow animation, boosts player speed
class GasItem {
    private double x, y;
    private double size = 35;
    private double rotation = 0;

    public GasItem(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double playerSpeed, Road road) {
        y += playerSpeed / 2;
        rotation += 0.1;

        double targetX = road.getLaneX((int) ((x - road.getRoadLeft()) / (road.getRoadWidth() / 3)));
        x += (targetX - x) * 0.1;
    }

    public void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.rotate(rotation, x + size / 2, y + size / 2);

        g2d.setColor(new Color(255, 215, 0));
        g2d.fillRoundRect((int) x, (int) y, (int) size, (int) size, 8, 8);

        g2d.setColor(new Color(200, 160, 0));
        g2d.fillRect((int) x, (int) y + (int) size / 2, (int) size, (int) size / 2);

        g2d.setColor(new Color(180, 140, 0));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect((int) x, (int) y, (int) size, (int) size, 8, 8);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("GAS", (int) x + 5, (int) y + 22);

        g2d.setTransform(old);

        g2d.setColor(new Color(255, 215, 0, 50));
        g2d.fillOval((int) x - 8, (int) y - 8, (int) size + 16, (int) size + 16);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
}