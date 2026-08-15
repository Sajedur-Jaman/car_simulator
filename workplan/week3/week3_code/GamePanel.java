import javax.swing.*;
import java.awt.*;

// Game Panel Class
// NOTE: This is a placeholder for Week 3. Only the panel's size and
// background are set here so the window can be created and displayed.
// Game loop, rendering, input handling, and all game objects
// (Road, PlayerCar, EnemyCar, Environment, GasItem, etc.) will be
// added in later weeks as per the Week 2 class architecture plan.
class GamePanel extends JPanel {
    private static final int PANEL_WIDTH = 900;
    private static final int PANEL_HEIGHT = 700;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(100, 180, 100));
    }
}