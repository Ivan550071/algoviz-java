# Algorithm Visualizer (JavaFX)

A JavaFX-based algorithm visualizer for exploring core **data structures** and **algorithms** through interactive, step-by-step animations.

This project currently includes visualizations for:

- **Sorting algorithms**
- **Maze solving (DFS / BFS)**
- **Binary Search Trees (BST)** with search + traversals

---
## Run project

mvn clean javafx:run

---

## Screenshots

### Sorting Visualizer
![Sorting Visualizer](docs/screenshots/sorting.png)

### Maze Visualizer (DFS/BFS)
![Maze Visualizer](docs/screenshots/maze.png)

### Tree Visualizer (BST)
![Tree Visualizer](docs/screenshots/trees.png)

---

## Features

### 1) Sorting Visualizer
- Multiple sorting algorithms (Bubble Sort + additional implemented sorts)
- Random array generation
- Step generation + playback
- **Play / Pause / Step / Reset** controls
- Speed slider
- Live counters:
  - comparisons
  - swaps
  - steps played
- Pseudocode panel
- Current operation display

### 2) Maze Visualizer
- Random grid generation with configurable:
  - rows
  - columns
  - wall density
- Solver selection:
  - **DFS**
  - **BFS**
- Step-by-step playback
- Visual cell states:
  - visited
  - backtracked
  - final path
- Start/end cells highlighted
- Playback controls + speed slider

### 3) Tree Visualizer (BST)
- Insert integer values into a **Binary Search Tree**
- Load sample tree
- Search visualization (highlights visited nodes)
- Search result label:
  - **Found / Not found**
- Traversal visualizations:
  - **In-order**
  - **Pre-order**
  - **Post-order**
- Traversal selector dropdown
- Pseudocode panel for search/traversals
- Playback controls + speed slider

---

## Tech Stack

- **Java 21** (Temurin JDK recommended)
- **JavaFX**
- **Maven**
- **IntelliJ IDEA**
- **Maven 3.9+** (or compatible)

---

## License

MIT
