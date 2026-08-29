import java.awt.*;

// Player Car Class
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

    public double getSpeed() {
        return speed;
    }
}