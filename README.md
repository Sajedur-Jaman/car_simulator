# car_simulator
A 2D Car Racing game developed using Java Swing with dynamic environment.

🏎️ Car Racing Game 

A feature-rich, 2D top-down car racing game built entirely in Java using Swing and AWT graphics. Navigate through traffic, collect fuel pickups, dodge oncoming cars, and race to the finish line across a dynamically rendered environment.


📸 Preview

A 900×700 game window with a scrolling 3-lane road, richly detailed scenery, and animated sky.


✨ Features

Smooth 60 FPS gameplay powered by a Swing Timer game loop

3-lane road system with dynamic curve physics and animated lane markings

Player car with lane-switching, speed boosting, and gradual deceleration

AI enemy cars — both oncoming and slower traffic — in randomized colors and speeds

Gas pickup items that spin, glow, and boost your speed when collected

Scoring system — earn points for passing enemy cars and collecting gas

Finish line at 5,000 distance units with a win screen

Dynamic sky rendering that transitions from dawn → afternoon → dusk as you progress

Rich environment including:

Parallax-scrolling trees, bushes, mountains, and clouds

Animated flying birds with wing-flap motion

Lakeside reflections and park areas with benches & flowers

Sun with glow effect





HUD overlay showing live score, speed (km/h), and distance progress
Game Over & You Won screens with final score display
Instant restart with R key




🎮 Controls


KeyAction

← Left Arrow Move to left lane

→ Right Arrow Move to right lane

Space Activate speed boost (hold)

R Restart after Game Over / Win


🏗️ Project Structure

CarRacingGame.java

│

├── CarRacingGame          # JFrame entry point — launches the window

│

├── GamePanel              # Core game loop, input handling, rendering pipeline

│

├── Environment            # Sky, scenery, parallax background manager

│   │

│   ├── Bird               # Animated flapping birds
  
│   │

│   ├── Cloud              # Drifting clouds

│   │

│   ├── Mountain           # Snow-capped mountain backdrop

│   │

│   ├── Lake               # Reflective lakeside scenery

│   │

│   ├── Park               # Park areas with benches and flowers

│   │

│   ├── Tree               # Multi-layer foliage trees

│   │

│   └── Bush               # Roadside bushes

│

├── Road                   # Scrolling 3-lane road with curve physics

│

├── Car (abstract)         # Base class for all vehicles — rendering & collision

│   │

│   ├── PlayerCar          # Player-controlled car with boost & lane logic

│   │

│   └── EnemyCar           # Traffic AI with randomized speed & direction

│

└── GasItem                # Rotating collectible that grants a speed boost




🚀 Getting Started

Prerequisites

Java 8 or higher (JDK)
No external libraries required — uses only standard Java SE


Compile & Run

bash# Clone the repository
git clone https://github.com/Sajedur-Jaman/car-racing-game.git
cd car-racing-game

# Compile
javac CarRacingGame.java

# Run
java CarRacingGame


On Windows you can also double-click a compiled .jar if you package it.



Build a Runnable JAR (optional)

bashjar cfe CarRacingGame.jar CarRacingGame *.class
java -jar CarRacingGame.jar


🧠 How It Works

SystemDetailsGame Loopjavax.swing.Timer fires every 1000/60 ms (~60 FPS), calling update() then repaint()Collision DetectionAxis-aligned bounding box (AABB) intersection between the player and all enemies/itemsRoad CurvesA sinusoidal curveOffset smoothly shifts the road left/right at random intervalsScoring+10 pts per enemy car passed, +5 pts per gas canister collectedWin ConditionReach distanceTraveled >= 5000 unitsEnvironment ParallaxBackground layers scroll at different fractions of player speed for depth


🤝 Contributing

Contributions are welcome! Here are some ideas for future improvements:


Sound effects & background music
Multiple difficulty levels
High-score persistence (file I/O or local database)
Additional power-ups (shield, slow-motion)
Animated player car exhaust / crash effects


Fork the repository
Create a feature branch (git checkout -b feature/my-feature)
Commit your changes (git commit -m 'Add my feature')
Push to the branch (git push origin feature/my-feature)
Open a Pull Request



📄 License

This project is licensed under the MIT License.


👨‍💻 Author

Built with Java Swing, no game engine, just pure AWT/2D graphics.


Feel free to ⭐ the repo if you found it useful!
