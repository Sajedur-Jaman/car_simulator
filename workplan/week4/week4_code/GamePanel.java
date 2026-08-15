import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Game Panel Class
// Deliverable 1: GamePanel with a defined update/repaint cycle.
// Deliverable 2: javax.swing.Timer running at ~60 FPS, driving
//                update() and repaint() every tick.
// Deliverable 3: 900x700 window with this panel attached (see
//                CarRacingGame.java).
// Deliverable 4: paintComponent() rendering pipeline, ready for
//                Road/PlayerCar/EnemyCar/Environment/GasItem to be
//                layered in during later weeks.
class GamePanel extends JPanel implements ActionListener {
    private static final int PANEL_WIDTH = 900;
    private static final int PANEL_HEIGHT = 700;
    private static final int FPS = 60;

    private Timer gameTimer;

    // Simple scrolling road-line marker. This is NOT the real Road class
    // (that is a later week) — it only exists to make the per-frame
    // update visibly change something on screen, proving the loop works.
    private int lineOffset = 0;
    private static final int LINE_SPEED = 5;
    private static final int DASH_LENGTH = 40;
    private static final int DASH_GAP = 30;

    // On-screen FPS counter to confirm the Timer is actually firing at
    // the target rate.
    private int frameCount = 0;
    private int currentFps = 0;
    private long lastFpsTime = System.currentTimeMillis();

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(100, 180, 100));
        setFocusable(true);
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
        // Advances the road-line offset each tick — this is the visible
        // proof that update() runs and changes state every frame.
        lineOffset += LINE_SPEED;
        if (lineOffset >= DASH_LENGTH + DASH_GAP) {
            lineOffset = 0;
        }

        // Full game-state updates (car movement, collisions, scoring,
        // spawning, etc.) will be added here in later weeks.

        // Track actual frames rendered per second.
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFpsTime >= 1000) {
            currentFps = frameCount;
            frameCount = 0;
            lastFpsTime = now;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Base rendering pipeline. The real Road, scenery, cars, and HUD
        // overlay from the Week 2 wireframe will be drawn here (back to
        // front) once implemented in later weeks.

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(6));
        int centerX = PANEL_WIDTH / 2;
        for (int y = -DASH_LENGTH + lineOffset; y < PANEL_HEIGHT; y += DASH_LENGTH + DASH_GAP) {
            g2d.drawLine(centerX, y, centerX, y + DASH_LENGTH);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("FPS: " + currentFps, 10, 20);
    }
}