# Robot Vacuum Cleaning Simulation

A comprehensive JavaFX-based simulation application for autonomous robot vacuum cleaners, developed as part of the BZ 214 Visual Programming course.

## Overview

This project implements a realistic robot vacuum simulation system with intelligent pathfinding, battery management, and multiple cleaning algorithms. The application provides a visual interface to observe the robot's behavior as it navigates rooms, avoids obstacles, and maintains charging station awareness.

## Features

- **Intelligent Pathfinding**: BFS-based algorithm for optimal route planning to charging stations
- **Multiple Cleaning Algorithms**: 
  - Random movement
  - Wall-following behavior
  - Coverage-based optimization
- **Battery Management System**: 
  - Battery consumption tracking
  - Automatic return to charging station when battery is low
  - Charging mode with realistic charge times
- **Dynamic Obstacle Avoidance**: Real-time detection and bypass of furniture obstacles
- **Dirt Type System**: 
  - Dust (2 battery units)
  - Liquid (3 battery units)
  - Stain (4 battery units)
- **Visual Statistics**: Real-time display of:
  - Robot position and direction
  - Battery level with indicator
  - Coverage percentage
  - Dirt remaining
  - Simulation time
- **Interactive Simulation Controls**:
  - Play/Pause functionality
  - Speed adjustment
  - Algorithm selection
  - Room configuration

## Project Structure

```
src/main/java/com/example/robotsimulation/
├── MainApplication.java          # Application entry point
├── controller/
│   └── MainController.java        # Main UI logic and simulation control
├── model/
│   ├── Robot.java                 # Robot entity
│   ├── Room.java                  # Room environment
│   ├── Cell.java                  # Individual cell properties
│   ├── Position.java              # Position coordinates
│   ├── Direction.java             # Direction enumeration
│   ├── Furniture.java             # Furniture obstacles
│   ├── FurnitureType.java         # Furniture type definitions
│   ├── CleaningAlgorithm.java     # Algorithm enumeration
│   └── CellType.java              # Cell type definitions
├── service/
│   ├── SimulationService.java     # Core simulation logic
│   ├── BatteryService.java        # Battery management
│   ├── CleaningService.java       # Cleaning operations
│   └── PathFindingService.java    # BFS pathfinding algorithm
└── view/
    └── RoomView.java              # Visual rendering system

src/main/resources/
└── com/example/robotsimulation/
    └── main-view.fxml             # JavaFX UI layout
```

## Technical Architecture

### Model-View-Controller (MVC) Pattern
The application follows the MVC architecture:
- **Model**: Robot, Room, Cell, Position entities
- **View**: JavaFX-based RoomView for visualization
- **Controller**: MainController managing simulation flow and user interaction

### Key Algorithms

#### Pathfinding Algorithm
Uses Breadth-First Search (BFS) to find the shortest path from the robot's current position to the charging station, considering obstacles and room boundaries.

#### Cleaning Algorithms
- **Random Walk**: Random movement in valid directions
- **Wall Following**: Follows walls to ensure coverage
- **Coverage Optimization**: Prioritizes least-visited cells

#### Obstacle Bypass
When an obstacle is encountered, the robot attempts to:
1. Calculate a bypass path around the obstacle
2. Return to the original path
3. Continue movement if bypass is possible

## System Requirements

- **Java Version**: Java 11 or higher
- **JavaFX SDK**: 11.0 or higher
- **Build Tool**: Maven or Gradle (if applicable)
- **Operating System**: Windows, macOS, or Linux

## Installation & Setup

### Prerequisites
1. Install Java Development Kit (JDK) 11+
2. Download and configure JavaFX SDK
3. Clone the repository

### Building the Project
```bash
# Using Maven
mvn clean compile

# Or using your IDE's build function
```

### Running the Application
```bash
# Launch the application
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
  -cp target/classes com.example.robotsimulation.MainApplication
```

## Usage Guide

### Starting the Simulation
1. Launch the application
2. The robot will be initialized at position (0, 0)
3. The charging station is located at position (0, 3)
4. Room is pre-populated with furniture and dirt

### Controls
- **Play/Pause Button**: Start or pause the simulation
- **Algorithm Selection**: Choose between different cleaning algorithms
- **Speed Slider**: Adjust simulation speed (1x to 10x)
- **Reset Button**: Restart the simulation with initial conditions

### Observing the Simulation
- **Grid Visualization**: Shows room layout, obstacles, and visited cells
- **Robot Visualization**: Red circle/image showing current robot position
- **Direction Indicator**: Arrow showing robot's facing direction
- **Statistics Panel**: Real-time updates on simulation metrics

### Metrics Explanation
- **Battery Level**: 0-100% indicator
- **Coverage**: Percentage of room cleaned/visited
- **Remaining Dirt**: Number of dirty cells left
- **Time Elapsed**: Simulation steps completed

## Performance Considerations

- **Memory Usage**: Scales with room size (approximately O(rows × columns))
- **Computation**: BFS pathfinding is O(rows × columns) per charging station return
- **Rendering**: Canvas-based drawing optimized for 50+ FPS

## Known Limitations

- Room size maximum: 50×50 cells (for optimal performance)
- Single robot simulation (multi-robot support can be added)
- Static furniture placement (dynamic furniture not supported)

## Future Enhancements

- [ ] Multi-robot coordination
- [ ] Dynamic obstacle addition/removal
- [ ] Advanced algorithms (A* pathfinding, Genetic algorithms)
- [ ] Simulation data export (CSV, JSON)
- [ ] 3D visualization mode
- [ ] User-defined room layouts

## Troubleshooting

### Application Won't Start
- Verify JavaFX modules are properly configured
- Check Java version compatibility
- Ensure all resources (FXML files, images) are included

### Robot Behavior Issues
- Low battery warnings display when battery < 20%
- Robot stops if battery reaches 0%
- Check algorithm selection in UI controls

### Performance Issues
- Reduce simulation speed
- Decrease room size
- Close unnecessary background applications

## Contributing

This project was developed as an educational assignment. Contributions for improvements and bug fixes are welcome.

## Course Information

**Course**: BZ 214 Visual Programming  
**Institution**: Erciyes Üniversitesi
**Academic Year**: 2026

## License

This project is provided as-is for educational purposes.

## Support

For issues, questions, or suggestions, please refer to the project documentation or contact the development team.

---

**Last Updated**: 6 June 2026  
**Version**: 1.0.0
