import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class BrickBreaker extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0, totalBricks, lives = 3;
    private Timer timer;
    private final int delay = 8;
    private int playerX = 310, ballPosX = 120, ballPosY = 350, ballDirX = -1, ballDirY = -2;
    private BrickMap brickMap;
    private boolean powerUpActive = false;

    public BrickBreaker() {
        initGame();
    }

    private void initGame() {
        brickMap = new BrickMap(3, 7);
        totalBricks = brickMap.totalBricks;
        addKeyListener(this);
        setFocusable(true);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);
        brickMap.draw(g);
        drawBorders(g);
        drawPaddle(g);
        drawBall(g);
        drawScore(g);
        drawLives(g);
        handleGameOver(g);
        g.dispose();
    }

    private void drawBorders(Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);
    }

    private void drawPaddle(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);
    }

    private void drawBall(Graphics g) {
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, 20, 20);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 590, 30);
    }

    private void drawLives(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString("Lives: " + lives, 10, 30);
    }

    private void handleGameOver(Graphics g) {
        if (ballPosY > 570) {
            displayMessage(g, "Game Over, Score: " + score, Color.red);
            lives--;
            if (lives > 0) resetGame();
            else displayMessage(g, "You Lost! Press Enter to Restart", Color.red);
        } else if (totalBricks == 0) {
            displayMessage(g, "You Won! Score: " + score, Color.green);
        }
    }

    private void displayMessage(Graphics g, String message, Color color) {
        play = false;
        ballDirX = ballDirY = 0;
        g.setColor(color);
        g.setFont(new Font("Serif", Font.BOLD, 30));
        g.drawString(message, 190, 300);
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString("Press Enter to Restart", 230, 350);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            handleBallMovement();
            handleCollision();
            repaint();
        }
    }

    private void handleBallMovement() {
        ballPosX += ballDirX;
        ballPosY += ballDirY;
        if (ballPosX < 0 || ballPosX > 670) ballDirX = -ballDirX;
        if (ballPosY < 0) ballDirY = -ballDirY;
    }

    private void handleCollision() {
        if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
            ballDirY = -ballDirY;
        }
        for (int i = 0; i < brickMap.map.length; i++) {
            for (int j = 0; j < brickMap.map[0].length; j++) {
                if (brickMap.map[i][j] > 0) {
                    int brickX = j * brickMap.brickWidth + 80;
                    int brickY = i * brickMap.brickHeight + 50;
                    Rectangle brickRect = new Rectangle(brickX, brickY, brickMap.brickWidth, brickMap.brickHeight);
                    if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(brickRect)) {
                        brickMap.setBrickValue(0, i, j);
                        totalBricks--;
                        score += 5;
                        ballDirY = (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) ? -ballDirX : -ballDirY;
                        return; // Exit after collision
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 600) playerX += 20;
        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 10) playerX -= 20;
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) resetGame();
        }
    }

    private void resetGame() {
        play = true;
        ballPosX = 120;
        ballPosY = 350;
        ballDirX = -1;
        ballDirY = -2;
        playerX = 310;
        score = 0;
        totalBricks = 21;
        brickMap = new BrickMap(3, 7);
        repaint();
    }

    public static void main(String[] args) {
        JFrame obj = new JFrame();
        BrickBreaker gamePlay = new BrickBreaker();
        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Brick Breaker");
        obj.setResizable(false);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gamePlay);
        obj.setVisible(true);
    }
}

class BrickMap {
    public int[][] map;
    public int brickWidth, brickHeight, totalBricks;

    public BrickMap(int row, int col) {
        map = new int[row][col];
        for (int[] bricks : map) Arrays.fill(bricks, 1);
        brickWidth = 540 / col;
        brickHeight = 150 / row;
        totalBricks = row * col; // Calculate total bricks
    }

    public void draw(Graphics g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
