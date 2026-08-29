import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Game Panel Class
class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int PANEL_WIDTH = 900;
    private static final int PANEL_HEIGHT = 700;
    private static final int FPS = 50;

    private Timer gameTimer;
    private PlayerCar playerCar;
    private Road road;
    private boolean boosting;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(100, 180, 100));
        setFocusable(true);
        addKeyListener(this);

        initGame();
    }

    private void initGame() {
        road = new Road();
        playerCar = new PlayerCar(PANEL_WIDTH / 2 - 20, PANEL_HEIGHT - 150);
        boosting = false;
    }

    public void startGame() {
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        road.update(playerCar.getSpeed(), 0);
        playerCar.update(road);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        road.draw(g2d, PANEL_HEIGHT);
        playerCar.draw(g2d);

        drawUI(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, 10, 220, 60, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Speed: " + (int) playerCar.getSpeed() + " km/h", 25, 45);

        if (boosting) {
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.drawString("\u26A1 BOOST! \u26A1", PANEL_WIDTH / 2 - 80, 60);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
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