import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Time Complexity: O(ROWS × COLS) or O(n).
//Space Complexity: O(ROWS × COLS) or O(n).

public class MazeSolver extends JFrame {
    private static final int ROWS = 10, COLS = 10; // Number of rows and columns in the maze
    private static final int CELL_SIZE = 40; // Size of each cell in pixels
    private static final CustomPoint START = new CustomPoint(0, 0); // Starting point coordinates
    private static final CustomPoint END = new CustomPoint(9, 9); // Ending point coordinates
    private int[][] maze = new int[ROWS][COLS]; // The maze grid (0: path, 1: wall, 2: solution path)
    private JPanel mazePanel;

    public MazeSolver() {
        setTitle("Maze Solver");
        setSize(COLS * CELL_SIZE + 50, ROWS * CELL_SIZE + 150); // Window size based on maze size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Layout manager for maze and buttons

        GridBagConstraints gbc = new GridBagConstraints(); // to specify the pos and size of the components

        mazePanel = new MazePanel(); // Panel to draw the maze
        mazePanel.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around the maze
        add(mazePanel, gbc);

        JPanel buttonPanel = createButtonPanel(); // for Solve maze and Reset maze buttons

        gbc.gridx = 0; // the buttons are placed below the maze
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        initializeMaze();
    }

    private JPanel createButtonPanel() { // Helper method to create button panels
        JPanel buttonPanel = new JPanel();
        JButton solveButton = new JButton("Solve Maze");
        JButton resetButton = new JButton("Reset Maze");

        solveButton.setBackground(new Color(34, 139, 34));
        solveButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(178, 34, 34)); // Red color for reset action
        resetButton.setForeground(Color.WHITE);

        solveButton.addActionListener(e -> solveMaze());
        resetButton.addActionListener(e -> initializeMaze());

        buttonPanel.add(solveButton);
        buttonPanel.add(resetButton);
        return buttonPanel;
    }

    // Method to reset the maze, making all cells empty (0)
    private void initializeMaze() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j] = 0;
            }
        }
        repaint(); // refresh the maze display
    }

    // Breadth-First Search (BFS) algorithm to solve the maze
    private void solveMaze() {
        boolean[][] visited = new boolean[ROWS][COLS];
        Queue queue = new Queue(ROWS * COLS);
        queue.enqueue(new Node(START, null)); // Start the queue with the starting point
        visited[START.x][START.y] = true;

        while (!queue.isEmpty()) {
            Node current = queue.dequeue();

            // If we reach the end point, trace and draw the solution path
            if (current.point.equals(END)) {
                markSolutionPath(current);
                return;
            }

            // Check neighbors (up, down, left, right)
            for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int newX = current.point.x + direction[0];
                int newY = current.point.y + direction[1];

                if (isValidMove(newX, newY, visited)) {
                    visited[newX][newY] = true;
                    queue.enqueue(new Node(new CustomPoint(newX, newY), current)); // Add valid move to the queue
                }
            }
        }

        JOptionPane.showMessageDialog(this, "No Path Found!");
    }

    // Helper method to check if a move is valid (within bounds, not visited, and not a wall)
    private boolean isValidMove(int x, int y, boolean[][] visited) {
        return x >= 0 && x < ROWS && y >= 0 && y < COLS && maze[x][y] == 0 && !visited[x][y];
    }

    // Marks the path from the end to the start
    private void markSolutionPath(Node current) {
        while (current != null) {
            CustomPoint p = current.point;
            maze[p.x][p.y] = 2; // Mark the cell as part of the solution path
            current = current.prev;
        }
        repaint();
    }

    // Custom class to store point coordinates (x, y)
    private static class CustomPoint {
        int x, y;

        CustomPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CustomPoint point = (CustomPoint) obj;
            return x == point.x && y == point.y;
        }
    }

    // Simple Queue implementation for BFS
    private static class Queue {
        private Node[] elements;
        private int front, rear, size, capacity;

        Queue(int capacity) {
            this.capacity = capacity;
            elements = new Node[capacity];
            front = rear = size = 0;
        }

        boolean isEmpty() {
            return size == 0;
        }

        void enqueue(Node node) {
            if (size == capacity) return; // Avoid overflow
            elements[rear] = node;
            rear = (rear + 1) % capacity;
            size++;
        }

        Node dequeue() {
            if (isEmpty()) return null; // Avoid underflow
            Node node = elements[front];
            front = (front + 1) % capacity;
            size--;
            return node;
        }
    }

    // Node class to store the point and the previous node (for path tracing)
    private static class Node {
        CustomPoint point;
        Node prev;

        Node(CustomPoint point, Node prev) {
            this.point = point;
            this.prev = prev;
        }
    }

    // Custom JPanel to handle drawing the maze grid and mouse interaction
    private class MazePanel extends JPanel {
        public MazePanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = e.getY() / CELL_SIZE; //converts this y-coordinate into a row index in the grid
                    int col = e.getX() / CELL_SIZE; //converts this x-coordinate into a row index in the grid
                    if (row < ROWS && col < COLS && !START.equals(new CustomPoint(row, col)) && !END.equals(new CustomPoint(row, col))) {
                        maze[row][col] = 1 - maze[row][col]; // Toggle between path and wall
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (maze[i][j] == 1) {
                        g.setColor(Color.BLACK); // Walls
                    } else if (maze[i][j] == 2) {
                        g.setColor(Color.GREEN); // Solution path
                    } else {
                        g.setColor(Color.WHITE); // Empty cells
                    }
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw cell borders
                }
            }

            // Draw the start and end points with distinct colors
            g.setColor(Color.RED);
            g.fillRect(START.y * CELL_SIZE, START.x * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Start point
            g.setColor(Color.BLUE);
            g.fillRect(END.y * CELL_SIZE, END.x * CELL_SIZE, CELL_SIZE, CELL_SIZE); // End point
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeSolver solver = new MazeSolver();
            solver.setVisible(true);
        });
    }
}