import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class CarRacingGame extends JFrame {
    public CarRacingGame() {
        setTitle("Car Race..developed by sajedur");
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

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int PANEL_WIDTH = 900;
    private static final int PANEL_HEIGHT = 700;
    private static final int FPS = 50;
    
    private Timer gameTimer;
    private PlayerCar playerCar;
    private Road road;
    private Environment environment;
    private ArrayList<EnemyCar> enemyCars;
    private ArrayList<GasItem> gasItems;
    private Random random;
    
    private double distanceTraveled;
    private boolean boosting;
    private boolean isGameOver;
    
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
        environment = new Environment();
        playerCar = new PlayerCar(PANEL_WIDTH / 2 - 20, PANEL_HEIGHT - 150);
        distanceTraveled = 0;
        boosting = false;
        isGameOver = false;
        
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
        double x = road.getLaneX(lane) - 22.5;
        double y = random.nextBoolean() ? -100 - random.nextInt(200) : PANEL_HEIGHT + random.nextInt(200);
        boolean comingTowards = y < 0;
        enemyCars.add(new EnemyCar(x, y, comingTowards));
    }

    private void spawnGasItem() {
        int lane = random.nextInt(3);
        double x = road.getLaneX(lane) - 15;
        double y = -100 - random.nextInt(150);
        gasItems.add(new GasItem(x, y));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            update();
        }
        repaint();
    }
    
    private void update() {
        distanceTraveled += playerCar.getSpeed() / 10.0;
        
        road.update(playerCar.getSpeed(), distanceTraveled);
        environment.update(playerCar.getSpeed(), distanceTraveled);
        playerCar.update(road);
        
        // AABB Collision Detection for Enemy Cars
        Rectangle2D.Double playerBounds = playerCar.getBounds();
        for (EnemyCar enemy : enemyCars) {
            if (playerBounds.intersects(enemy.getBounds())) {
                isGameOver = true;
                break;
            }
        }

        // AABB Collision Detection for Gas Items
        ArrayList<GasItem> gasToRemove = new ArrayList<>();
        for (GasItem gas : gasItems) {
            gas.update(playerCar.getSpeed(), road);
            if (playerBounds.intersects(gas.getBounds())) {
                playerCar.applyGasBoost();
                gasToRemove.add(gas);
            } else if (gas.getY() > PANEL_HEIGHT + 50) {
                gasToRemove.add(gas);
            }
        }
        gasItems.removeAll(gasToRemove);
        
        ArrayList<EnemyCar> carsToRemove = new ArrayList<>();
        for (EnemyCar enemy : enemyCars) {
            enemy.update(playerCar.getSpeed(), road);
            
            if (enemy.getY() > PANEL_HEIGHT + 100 || enemy.getY() < -200) {
                carsToRemove.add(enemy);
            }
        }
        enemyCars.removeAll(carsToRemove);
        
        if (random.nextInt(100) < 3) {
            spawnEnemyCar();
        }
        if (random.nextInt(250) < 2) {
            spawnGasItem();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        environment.drawSky(g2d, PANEL_WIDTH, PANEL_HEIGHT);
        environment.drawBackground(g2d, PANEL_WIDTH, PANEL_HEIGHT);
        
        road.draw(g2d, PANEL_HEIGHT);
        
        environment.drawScenery(g2d, PANEL_WIDTH, PANEL_HEIGHT, road);

        for (GasItem gas : gasItems) {
            gas.draw(g2d);
        }
        
        for (EnemyCar enemy : enemyCars) {
            enemy.draw(g2d);
        }
        
        playerCar.draw(g2d);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (isGameOver) {
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

class Environment {
    private ArrayList<Tree> trees;
    private ArrayList<Cloud> clouds;
    private ArrayList<Mountain> mountains;
    private ArrayList<Bush> bushes;
    private ArrayList<Bird> birds;
    private ArrayList<Lake> lakes;
    private ArrayList<Park> parks;
    private Random random;
    private double timeOfDay;
    
    public Environment() {
        random = new Random();
        trees = new ArrayList<>();
        clouds = new ArrayList<>();
        mountains = new ArrayList<>();
        bushes = new ArrayList<>();
        birds = new ArrayList<>();
        lakes = new ArrayList<>();
        parks = new ArrayList<>();
        timeOfDay = 0.3;
        
        for (int i = 0; i < 6; i++) {
            mountains.add(new Mountain(i * 220 - 80, 70 + random.nextInt(40)));
        }
        
        for (int i = 0; i < 10; i++) {
            clouds.add(new Cloud(random.nextInt(900), random.nextInt(160)));
        }
        
        for (int i = 0; i < 8; i++) {
            birds.add(new Bird(random.nextInt(900), random.nextInt(220) + 40));
        }
        
        for (int i = 0; i < 3; i++) {
            lakes.add(new Lake(70, i * 450 - 200, random.nextDouble() * 0.4 + 0.9));
            lakes.add(new Lake(760, i * 450 - 100, random.nextDouble() * 0.4 + 0.9));
        }
        
        for (int i = 0; i < 3; i++) {
            parks.add(new Park(50, i * 500 - 250));
            parks.add(new Park(780, i * 500 - 100));
        }
        
        for (int i = 0; i < 40; i++) {
            trees.add(new Tree(75 + random.nextInt(90), i * 40 - 100, random.nextDouble() * 0.7 + 0.6));
            trees.add(new Tree(735 + random.nextInt(90), i * 40 - 100, random.nextDouble() * 0.7 + 0.6));
        }
        
        for (int i = 0; i < 50; i++) {
            bushes.add(new Bush(45 + random.nextInt(110), i * 35 - 100, random.nextDouble() * 0.5 + 0.5));
            bushes.add(new Bush(745 + random.nextInt(110), i * 35 - 100, random.nextDouble() * 0.5 + 0.5));
        }
    }
    
    public void update(double speed, double distance) {
        timeOfDay = 0.2 + (distance / 5000.0) * 0.6;
        if (timeOfDay > 1.0) timeOfDay = 1.0;
        
        for (Cloud cloud : clouds) {
            cloud.update(speed * 0.04);
            if (cloud.x < -160) cloud.x = 950;
        }
        
        for (Bird bird : birds) {
            bird.update(speed * 0.12);
            if (bird.x < -100) {
                bird.x = 950;
                bird.y = random.nextInt(200) + 40;
            }
        }
        
        for (Lake lake : lakes) {
            lake.update(speed * 0.35);
        }
        
        for (Park park : parks) {
            park.update(speed * 0.45);
        }
        
        for (Tree tree : trees) {
            tree.update(speed * 0.45);
        }
        
        for (Bush bush : bushes) {
            bush.update(speed * 0.55);
        }
    }
    
    public void drawSky(Graphics2D g2d, int width, int height) {
        Color skyTop, skyBottom;
        
        if (timeOfDay < 0.3) {
            skyTop = new Color(135, 206, 250);
            skyBottom = new Color(255, 200, 150);
        } else if (timeOfDay < 0.7) {
            skyTop = new Color(90, 140, 240);
            skyBottom = new Color(155, 215, 255);
        } else {
            skyTop = new Color(255, 130, 90);
            skyBottom = new Color(255, 190, 130);
        }
        
        GradientPaint gradient = new GradientPaint(0, 0, skyTop, 0, height / 2, skyBottom);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height / 2);
        
        if (timeOfDay < 0.6) {
            g2d.setColor(new Color(255, 255, 120));
            int sunX = (int)(width * 0.82);
            int sunY = (int)(40 + timeOfDay * 90);
            g2d.fillOval(sunX - 35, sunY - 35, 70, 70);
            g2d.setColor(new Color(255, 255, 180, 90));
            g2d.fillOval(sunX - 48, sunY - 48, 96, 96);
        }
    }
    
    public void drawBackground(Graphics2D g2d, int width, int height) {
        for (Mountain mountain : mountains) {
            mountain.draw(g2d);
        }
        
        for (Cloud cloud : clouds) {
            cloud.draw(g2d);
        }
    }
    
    public void drawScenery(Graphics2D g2d, int width, int height, Road road) {
        g2d.setColor(new Color(85, 170, 85));
        g2d.fillRect(0, 0, road.getRoadLeft(), height);
        g2d.fillRect(road.getRoadLeft() + road.getRoadWidth(), 0, width, height);
        
        for (Lake lake : lakes) {
            lake.draw(g2d);
        }
        
        for (Park park : parks) {
            park.draw(g2d);
        }
        
        for (Bush bush : bushes) {
            bush.draw(g2d);
        }
        
        for (Tree tree : trees) {
            tree.draw(g2d);
        }

        // Fixed: Birds are now rendered properly in the scenery/foreground layer
        for (Bird bird : birds) {
            bird.draw(g2d);
        }
    }
}

class Bird {
    double x, y;
    double wingAngle = 0;
    double speed = 1.2;
    
    public Bird(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(double gameSpeed) {
        x -= speed;
        y += Math.sin(x * 0.04) * 0.35;
        wingAngle += 0.25;
    }
    
    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(40, 40, 40));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        g2d.fillOval((int)x, (int)y, 9, 6);
        
        int wingOffset = (int)(Math.sin(wingAngle) * 10);
        g2d.drawLine((int)x, (int)y + 3, (int)x - 14, (int)y + wingOffset);
        g2d.drawLine((int)x + 9, (int)y + 3, (int)x + 23, (int)y + wingOffset);
    }
}

class GasItem {
    double x, y;
    double width = 30;
    double height = 40;
    double spinAngle = 0;
    
    public GasItem(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(double playerSpeed, Road road) {
        y += playerSpeed * 0.7;
        spinAngle += 0.1;
    }
    
    public void draw(Graphics2D g2d) {
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        g2dCopy.translate(x + width / 2, y + height / 2);
        double scaleX = Math.cos(spinAngle);
        g2dCopy.scale(scaleX, 1.0);
        
        // Glow effect
        g2dCopy.setColor(new Color(255, 255, 0, 100));
        g2dCopy.fillOval((int)(-width/2 - 5), (int)(-height/2 - 5), (int)width + 10, (int)height + 10);
        
        // Canister Body
        g2dCopy.setColor(new Color(220, 50, 50));
        g2dCopy.fillRoundRect((int)(-width/2), (int)(-height/2), (int)width, (int)height, 8, 8);
        
        g2dCopy.setColor(Color.WHITE);
        g2dCopy.setFont(new Font("Arial", Font.BOLD, 10));
        g2dCopy.drawString("GAS", -12, 3);
        
        g2dCopy.dispose();
    }
    
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public double getY() {
        return y;
    }
}

class Lake {
    double x, y;
    double scale;
    double waveOffset = 0;
    
    public Lake(double x, double y, double scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }
    
    public void update(double speed) {
        y += speed;
        waveOffset += 0.12;
        if (y > 760) {
            y = -180;
        }
    }
    
    public void draw(Graphics2D g2d) {
        int width = (int)(135 * scale);
        int height = (int)(90 * scale);
        
        GradientPaint waterGradient = new GradientPaint(
            (int)x, (int)y, new Color(110, 160, 255),
            (int)x, (int)(y + height), new Color(60, 110, 195)
        );
        g2d.setPaint(waterGradient);
        g2d.fillOval((int)x - width / 2, (int)y, width, height);
        
        g2d.setColor(new Color(175, 215, 255, 160));
        for (int i = 0; i < 3; i++) {
            int waveY = (int)(y + 22 + i * 22 + Math.sin(waveOffset + i) * 6);
            g2d.drawArc((int)x - width / 2 + 12, waveY, width - 24, 16, 0, 180);
        }
        
        g2d.setColor(new Color(220, 235, 255, 120));
        g2d.fillOval((int)x - 22, (int)y + 12, 44, 22);
    }
}

class Park {
    double x, y;
    Random random = new Random(456);
    
    public Park(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(double speed) {
        y += speed;
        if (y > 760) {
            y = -220;
        }
    }
    
    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(115, 210, 115));
        g2d.fillRoundRect((int)x - 55, (int)y, 110, 130, 22, 22);
        
        g2d.setColor(new Color(175, 155, 115));
        g2d.fillRoundRect((int)x - 16, (int)y, 32, 130, 12, 12);
        
        for (int i = 0; i < 10; i++) {
            int flowerX = (int)x - 42 + (i % 2) * 76 + random.nextInt(8);
            int flowerY = (int)y + 18 + (i / 2) * 24;
            
            g2d.setColor(new Color(255, 110 + random.nextInt(90), 160));
            g2d.fillOval(flowerX - 3, flowerY - 3, 6, 6);
            g2d.fillOval(flowerX + 3, flowerY - 3, 6, 6);
            g2d.fillOval(flowerX - 3, flowerY + 3, 6, 6);
            g2d.fillOval(flowerX + 3, flowerY + 3, 6, 6);
            
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(flowerX, flowerY, 6, 6);
        }
        
        g2d.setColor(new Color(130, 80, 35));
        g2d.fillRect((int)x - 22, (int)y + 65, 44, 6);
        g2d.fillRect((int)x - 22, (int)y + 76, 44, 3);
        g2d.fillRect((int)x - 20, (int)y + 70, 4, 10);
        g2d.fillRect((int)x + 16, (int)y + 70, 4, 10);
    }
}

class Tree {
    double x, y;
    double scale;
    
    public Tree(double x, double y, double scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }
    
    public void update(double speed) {
        y += speed;
        if (y > 760) {
            y = -60;
        }
    }
    
    public void draw(Graphics2D g2d) {
        int baseX = (int)x;
        int baseY = (int)y;
        int size = (int)(45 * scale);
        
        g2d.setColor(new Color(95, 60, 30));
        g2d.fillRect(baseX - (int)(6 * scale), baseY, (int)(12 * scale), (int)(38 * scale));
        
        g2d.setColor(new Color(75, 45, 18));
        g2d.fillRect(baseX - (int)(6 * scale), baseY, (int)(4 * scale), (int)(38 * scale));
        
        g2d.setColor(new Color(30, 130, 30));
        g2d.fillOval(baseX - size / 2, baseY - (int)(28 * scale), size, size);
        
        g2d.setColor(new Color(45, 155, 45));
        g2d.fillOval(baseX - (int)(size * 0.62), baseY - (int)(16 * scale), (int)(size * 1.24), (int)(size * 0.82));
        
        g2d.setColor(new Color(38, 145, 38));
        g2d.fillOval(baseX - (int)(size * 0.42), baseY - (int)(11 * scale), (int)(size * 0.84), (int)(size * 0.62));
        
        g2d.setColor(new Color(70, 195, 70, 160));
        g2d.fillOval(baseX - (int)(size * 0.3), baseY - (int)(22 * scale), (int)(size * 0.4), (int)(size * 0.4));
    }
}

class Bush {
    double x, y;
    double scale;
    
    public Bush(double x, double y, double scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }
    
    public void update(double speed) {
        y += speed;
        if (y > 760) {
            y = -40;
        }
    }
    
    public void draw(Graphics2D g2d) {
        int size = (int)(28 * scale);
        g2d.setColor(new Color(55, 135, 55));
        g2d.fillOval((int)x - size / 2, (int)y, size, (int)(size * 0.62));
        g2d.fillOval((int)x - (int)(size * 0.32), (int)y + 5, (int)(size * 0.64), (int)(size * 0.52));
    }
}

class Cloud {
    double x, y;
    
    public Cloud(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(double speed) {
        x -= speed;
    }
    
    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 210));
        g2d.fillOval((int)x, (int)y, 65, 32);
        g2d.fillOval((int)x + 22, (int)y - 12, 54, 38);
        g2d.fillOval((int)x + 44, (int)y, 65, 32);
    }
}

class Mountain {
    double x, height;
    
    public Mountain(double x, double height) {
        this.x = x;
        this.height = height;
    }
    
    public void draw(Graphics2D g2d) {
        int[] xPoints = {(int)x, (int)(x + 130), (int)(x + 260)};
        int[] yPoints = {200, (int)(200 - height), 200};
        
        g2d.setColor(new Color(105, 105, 125));
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        int[] snowX = {(int)(x + 130), (int)(x + 98), (int)(x + 162)};
        int[] snowY = {(int)(200 - height), (int)(200 - height + 34), (int)(200 - height + 34)};
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(snowX, snowY, 3);
    }
}

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
            int crackY = (int)((roadOffset + i * 50) % height);
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
        int offset = (int)(roadOffset % (dashLength + dashGap));
        
        for (int lane = 1; lane < 3; lane++) {
            int laneX = leftEdge + (roadWidth * lane / 3);
            for (int y = -offset; y < height; y += dashLength + dashGap) {
                g2d.drawLine(laneX, y, laneX, y + dashLength);
            }
        }
        
        g2d.setColor(Color.RED);
        for (int y = 0; y < height; y += 40) {
            int stripY = (y + (int)roadOffset) % height;
            g2d.fillRect(leftEdge - 12, stripY, 8, 20);
            g2d.fillRect(leftEdge + roadWidth + 4, stripY, 8, 20);
        }
    }
    
    public int getLaneX(int lane) {
        int leftEdge = getRoadLeft();
        return leftEdge + (roadWidth / 6) + lane * (roadWidth / 3);
    }
    
    public int getRoadLeft() {
        return baseRoadLeft + (int)curveOffset;
    }
    
    public int getRoadWidth() {
        return roadWidth;
    }
}

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
    
    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect((int)x + 3, (int)y + 3, (int)width, (int)height, 12, 12);
        
        GradientPaint gradient = new GradientPaint(
            (int)x, (int)y, color,
            (int)x, (int)(y + height), color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect((int)x, (int)y, (int)width, (int)height, 12, 12);
        
        g2d.setColor(color.darker().darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int)x, (int)y, (int)width, (int)height, 12, 12);
        
        g2d.setColor(new Color(100, 150, 200, 200));
        g2d.fillRoundRect((int)x + 8, (int)y + 12, (int)width - 16, 22, 6, 6);
        g2d.fillRoundRect((int)x + 8, (int)y + 46, (int)width - 16, 22, 6, 6);
        
        g2d.setColor(new Color(40, 40, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int)x + 8, (int)y + 12, (int)width - 16, 22, 6, 6);
        g2d.drawRoundRect((int)x + 8, (int)y + 46, (int)width - 16, 22, 6, 6);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect((int)x - 4, (int)y + 12, 8, 18, 4, 4);
        g2d.fillRoundRect((int)x + (int)width - 4, (int)y + 12, 8, 18, 4, 4);
        g2d.fillRoundRect((int)x - 4, (int)y + (int)height - 30, 8, 18, 4, 4);
        g2d.fillRoundRect((int)x + (int)width - 4, (int)y + (int)height - 30, 8, 18, 4, 4);
        
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillOval((int)x - 2, (int)y + 16, 4, 10);
        g2d.fillOval((int)x + (int)width - 2, (int)y + 16, 4, 10);
        g2d.fillOval((int)x - 2, (int)y + (int)height - 26, 4, 10);
        g2d.fillOval((int)x + (int)width - 2, (int)y + (int)height - 26, 4, 10);
        
        if (y < 350) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval((int)x + 8, (int)y + 5, 6, 6);
            g2d.fillOval((int)x + (int)width - 14, (int)y + 5, 6, 6);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillOval((int)x + 8, (int)y + (int)height - 10, 6, 6);
            g2d.fillOval((int)x + (int)width - 14, (int)y + (int)height - 10, 6, 6);
        }
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }
}

class PlayerCar extends Car {
    private double speed = 6;
    private double baseSpeed = 6;
    private int targetLane = 1;
    private double laneX;
    private boolean isBoosting = false;
    private int boostTimer = 0;
    
    public PlayerCar(double x, double y) {
        super(x, y, new Color(220, 20, 20));
        laneX = x;
    }
    
    public void update(Road road) {
        double baseLaneX = road.getLaneX(targetLane) - (width / 2);
        laneX = baseLaneX;
        
        if (Math.abs(x - laneX) > 2) {
            x += (laneX - x) * 0.2;
        } else {
            x = laneX;
        }
        
        if (boostTimer > 0) {
            boostTimer--;
            if (boostTimer == 0) {
                speed = baseSpeed;
            }
        } else if (speed > baseSpeed && !isBoosting) {
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
    
    public void applyGasBoost() {
        speed = baseSpeed + 8;
        boostTimer = 75; // duration of boost frame counts
    }
    
    public double getSpeed() {
        return speed;
    }
}

class EnemyCar extends Car {
    private boolean comingTowards;
    private double speed;
    
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
        return colors[(int)(Math.random() * colors.length)];
    }
    
    public void update(double playerSpeed, Road road) {
        if (comingTowards) {
            y += speed + playerSpeed / 2;
        } else {
            y += playerSpeed / 2 - speed;
        }
        
        double targetLaneX = road.getLaneX((int)((x - road.getRoadLeft()) / (road.getRoadWidth() / 3))) - (width / 2);
        x += (targetLaneX - x) * 0.1;
    }
}