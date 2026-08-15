import javax.swing.*;

// Main Game Class
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarRacingGame());
    }
}