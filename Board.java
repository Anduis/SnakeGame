import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JPanel;

//graphic core needed to display the game
public class Board extends JPanel {
  byte[][] board; // each element in the matrix represents a graphic
  int[][] body; // to draw the snake including its direction, and state
  int size; // the board is a square
  Random rand = new Random();
  boolean highScoreAchieved;
  boolean hasStarted;
  boolean isPaused;
  boolean gameOver;
  boolean isLevelSetted;
  int bonusTimeLeft;// time to eat it
  int bonusTimer;// time to spawn
  int highScore;
  int score;
  int level;
  int[] alt = { -1, -1, -1 }; // which animal the bonus will be
  final byte NONE = 0;
  final byte SNAKE = 1;
  final byte FOOD = 2;
  final byte WALL = 3;
  final byte ALT = 4;

  Board(int size) {
    this.size = size;
    reset();
    body = new int[size][size];
    highScore = 0;
    isLevelSetted = false;
  }

  void reset() { // initialize the board
    board = new byte[size][size];
    for (int i = 0; i < size; i++) {
      board[i][0] = WALL;
      board[0][i] = WALL;
      board[i][size - 1] = WALL;
      board[size - 1][i] = WALL;
    }
    if (hasStarted && isLevelSetted)
      addFood();
    newBonusTimer();
    gameOver = false;
    score = 0;
    highScoreAchieved = false;
  }

  void addFood() {
    int x = rand.nextInt(size);
    int y = rand.nextInt(size);
    while (getMarker(x, y) != NONE) {
      x = rand.nextInt(size);
      y = rand.nextInt(size);
    }
    putMarker(x, y, FOOD);
  }

  void newBonusTimer() {
    bonusTimer = rand.nextInt(200) + 100;// [100-300]
    alt[0] = -1;
    bonusTimeLeft = rand.nextInt(size / 2) + 3 * size / 4;// [40,60]
    System.out.println("steps" + bonusTimeLeft);
  }

  void addAltFood() {
    alt[0] = rand.nextInt(4);
    int x = rand.nextInt(size - 1);// alt graphs are wider than normal food
    int y = rand.nextInt(size);
    while (getMarker(x, y) != NONE || getMarker(x + 1, y) != NONE) {
      x = rand.nextInt(size - 1);
      y = rand.nextInt(size);
    }
    putMarker(x, y, ALT);
    alt[1] = x;
    alt[2] = y;
  }

  void removeAltFood() {
    putMarker(alt[1], alt[2], NONE);
    alt[0] = -1;
    alt[1] = -1;
    alt[2] = -1;
  }

  void putMarker(int x, int y, byte marker) {
    board[x][y] = marker;
  }

  void sbody(int x, int y, int marker) {
    body[x][y] = marker;
  }

  byte getMarker(int x, int y) {
    return board[x][y];
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension d = getSize();
    int siz = d.width;
    // background
    g.setColor(new Color(145, 191, 59));
    g.fillRect(0, 0, siz, d.height);
    // pixels texture
    g.setColor(new Color(135, 181, 50));
    for (int i = 0; i < d.height; i += 5) {
      g.drawLine(0, i, siz, i);
      g.drawLine(i, 0, i, d.height);
    }
    g.setColor(new Color(45, 60, 21));
    // walls
    drawCage(g, siz);
    // scorebar
    g.fillRect(0, siz + 10, siz, 10);
    g.fillRect(0, d.height - 10, siz, 10);
    g.fillRect(0, siz + 10, 10, d.height - siz);
    g.fillRect(siz - 10, siz + 15, 10, d.height - siz);
    // title
    g.setFont(new Font("Times New Roman", Font.BOLD, 40));
    g.drawString("SNAKE", (siz / 2) - 75, siz + 75);
    // score
    g.setFont(new Font("Courier", Font.BOLD, 20));
    g.drawString("SCORE: ", 30, siz + 50);
    g.drawString(String.valueOf(score), 100, siz + 50);
    g.drawString("HIGHSCORE: ", 30, siz + 70);
    g.drawString(String.valueOf(highScore), 150, siz + 70);
    // bonus
    g.drawString("BONUS: ", siz - 150, siz + 50);
    if (alt[0] != -1) {
      g.drawString(String.valueOf(bonusTimeLeft + bonusTimer), siz - 60, siz + 50);
      drawAltFood(g, siz - 60, siz + 65);
    }
    // other objects
    for (int x = 0; x < size; x++)
      for (int y = 0; y < size; y++) {
        switch (board[x][y]) {
          case FOOD:
            drawFood(g, x * 20, y * 20);
            break;
          case ALT:
            drawAltFood(g, x * 20, y * 20);
            break;
          case SNAKE:
            drawSnake(g, x * 20, y * 20, body[x][y]);
            break;
        }
      }
    // options
    g.setFont(new Font("Courier", Font.BOLD, 30));
    int v = ((siz + 30) / 2) - (20 * 12);
    if (!hasStarted)
      g.drawString("PRESS ENTER TO START", v, 200);
    if (isPaused)
      g.drawString("PAUSED", ((siz + 30) / 2) - (20 * 3), 170);
    if (gameOver)
      drawGameOverScreen(g, v);
    if (hasStarted && !isLevelSetted)
      drawLevelSelector(g, v);
  }

  void drawCage(Graphics g, int l) {
    g.fillRect(0, 0, l, 15);
    g.fillRect(0, 0, 15, l);
    g.fillRect(l - 15, 0, 15, l);
    g.fillRect(0, l - 15, l, 15);
  }

  void drawGameOverScreen(Graphics g, int v) {
    g.setColor(new Color((float) 0.5686, (float) 0.749, (float) 0.2313, (float) 0.5));
    g.fillRect(v - 10, 140, 520, 160);
    g.setColor(new Color(45, 60, 21));
    g.drawString("GAME OVER!", v, 170);
    if (highScoreAchieved)
      g.drawString("HIGH SCORE!!", v, 200);
    g.drawString("NEW GAME.- PRESS R", v, 230);
    g.drawString("EXIT.- PRESS ESC", v, 260);
    g.drawString("CHANGE DIFFICULTY.- PRESS F", v, 290);
  }

  void drawLevelSelector(Graphics g, int v) {
    g.drawString("CHOOSE SKILL LEVEL:", v, 100);
    g.setFont(new Font("Times New Roman", Font.BOLD, 25));
    g.drawString("1.- I'M TOO YOUNG TO DIE.", v, 150 + 70);
    g.drawString("2.- HEY, NOT TOO ROUGH.", v, 200 + 70);
    g.drawString("3.- HURT ME PLENTY.", v, 250 + 70);
    g.drawString("4.- ULTRA-VIOLENCE.", v, 300 + 70);
    g.drawString("5.- NIGHTMARE!.", v, 350 + 70);
  }

  void drawFood(Graphics g, int x, int y) {
    g.fillRect(x + 5, y + 5, 5, 5);
    g.fillRect(x + 10, y, 5, 5);
    g.fillRect(x + 10, y + 10, 5, 5);
    g.fillRect(x + 15, y + 5, 5, 5);
  }

  void drawAltFood(Graphics g, int x, int y) {
    if (alt[0] == 4)// rat
    {
      g.fillRect(x + 10 / 2, y, 10 / 2, 5 / 2);
      g.fillRect(x + 5 / 2, y + 5 / 2, 5 / 2, 5 / 2);
      g.fillRect(x + 15 / 2, y + 5 / 2, 10 / 2, 5 / 2);
      g.fillRect(x + 30 / 2, y + 5 / 2, 5 / 2, 5 / 2);
      g.fillRect(x, y + 10 / 2, 35 / 2, 5 / 2);
      g.fillRect(x + 10 / 2, y + 15 / 2, 20 / 2, 5 / 2);
    }
    if (alt[0] == 3)// scorpion
    {
      g.fillRect(x + 10, y, 5, 5);
      g.fillRect(x + 30, y, 10, 10);
      g.fillRect(x + 5, y + 5, 15, 5);
      g.fillRect(x, y + 10, 30, 5);
      g.fillRect(x + 5, y + 15, 5, 5);
      g.fillRect(x + 15, y + 15, 5, 5);
    }
    if (alt[0] == 2)// spider
    {
      g.fillRect(x, y + 5, 5, 15);
      g.fillRect(x + 5, y + 5, 5, 5);
      g.fillRect(x + 35, y + 5, 5, 15);
      g.fillRect(x + 30, y + 5, 5, 5);
      g.fillRect(x + 10, y, 20, 15);
      g.fillRect(x + 10, y + 15, 5, 5);
      g.fillRect(x + 25, y + 15, 5, 5);
    }
    if (alt[0] == 1)// centipide
    {
      g.fillRect(x, y + 5, 5, 5);
      g.fillRect(x, y + 10, 40, 5);
      g.fillRect(x + 5, y + 15, 5, 5);
      g.fillRect(x + 15, y + 15, 5, 5);
      g.fillRect(x + 25, y + 15, 5, 5);
      g.fillRect(x + 35, y + 15, 5, 5);
    }
    if (alt[0] == 0)// chamaleon
    {
      g.fillRect(x + 5, y, 5, 5);
      g.fillRect(x + 15, y, 5, 5);
      g.fillRect(x + 25, y, 5, 5);
      g.fillRect(x, y + 5, 5, 5);
      g.fillRect(x + 10, y + 5, 25, 5);
      g.fillRect(x, y + 10, 40, 5);
      g.fillRect(x + 15, y + 15, 5, 5);
      g.fillRect(x + 25, y + 15, 5, 5);
    }
  }

  void drawSnake(Graphics g, int x, int y, int direction) {
    if (direction == 8)// body pointing upward
    {
      g.fillRect(x + 5, y + 5, 5, 15);
      g.fillRect(x + 10, y, 5, 15);
    }
    if (direction == 2)// body pointing downward
    {
      g.fillRect(x + 5, y, 5, 15);
      g.fillRect(x + 10, y + 5, 5, 15);
    }
    if (direction == 6)// body pointing rightward
    {
      g.fillRect(x + 5, y + 5, 15, 5);
      g.fillRect(x, y + 10, 15, 5);
    }
    if (direction == 4)// body pointing leftward
    {
      g.fillRect(x, y + 5, 15, 5);
      g.fillRect(x + 5, y + 10, 15, 5);
    }

    if (direction == 88)// head pointing upward
    {
      g.fillRect(x + 5, y - 5, 5, 25);
      g.fillRect(x + 10, y - 5, 5, 10);
      g.fillRect(x + 10, y + 10, 5, 5);
      g.fillRect(x + 15, y + 5, 5, 5);
    }
    if (direction == 22)// head pointing downward
    {
      g.fillRect(x + 5, y, 5, 25);
      g.fillRect(x + 10, y + 5, 5, 5);
      g.fillRect(x + 10, y + 15, 5, 10);
      g.fillRect(x + 15, y + 10, 5, 5);
    }
    if (direction == 66)// head pointing rightward
    {
      g.fillRect(x + 10, y, 5, 5);
      g.fillRect(x + 15, y + 5, 10, 5);
      g.fillRect(x + 5, y + 5, 5, 5);
      g.fillRect(x, y + 10, 25, 5);
    }
    if (direction == 44)// head pointing leftward
    {
      g.fillRect(x + 5, y, 5, 5);
      g.fillRect(x - 5, y + 5, 10, 5);
      g.fillRect(x + 10, y + 5, 5, 5);
      g.fillRect(x - 5, y + 10, 25, 5);
    }

    if (direction == 888)// mouth pointing upward
    {
      g.fillRect(x, y - 5, 5, 5);
      g.fillRect(x + 15, y - 5, 5, 5);
      g.fillRect(x + 5, y, 5, 20);
      g.fillRect(x + 10, y, 5, 5);
      g.fillRect(x + 10, y + 10, 5, 5);
      g.fillRect(x + 15, y + 5, 5, 5);
    }
    if (direction == 222)// mouth pointing downward
    {
      g.fillRect(x + 5, y, 5, 20);
      g.fillRect(x + 10, y + 5, 5, 5);
      g.fillRect(x + 10, y + 15, 5, 5);
      g.fillRect(x + 15, y + 10, 5, 5);
      g.fillRect(x, y + 20, 5, 5);
      g.fillRect(x + 15, y + 20, 5, 5);
    }
    if (direction == 666)// mouth pointing rightward
    {
      g.fillRect(x + 10, y, 5, 5);
      g.fillRect(x + 20, y, 5, 5);
      g.fillRect(x + 5, y + 5, 5, 5);
      g.fillRect(x + 15, y + 5, 5, 5);
      g.fillRect(x, y + 10, 20, 5);
      g.fillRect(x + 20, y + 15, 5, 5);
    }
    if (direction == 444)// mouth pointing leftward
    {
      g.fillRect(x - 5, y, 5, 5);
      g.fillRect(x + 5, y, 5, 5);
      g.fillRect(x + 10, y + 5, 5, 5);
      g.fillRect(x, y + 5, 5, 5);
      g.fillRect(x, y + 10, 20, 5);
      g.fillRect(x - 5, y + 15, 5, 5);
    }

    if (((direction % 100) - (direction % 10)) / 10 == 5)// belly
    {
      g.fillRect(x + 5, y, 10, 5);
      g.fillRect(x, y + 5, 5, 10);
      g.fillRect(x + 5, y + 5, 5, 5);
      g.fillRect(x + 10, y + 10, 5, 5);
      g.fillRect(x + 15, y + 5, 5, 10);
      g.fillRect(x + 5, y + 15, 10, 5);
    }

    if (direction == 24)// curves sections
    {
      g.fillRect(x + 5, y, 5, 5);
      g.fillRect(x, y + 5, 5, 5);
      g.fillRect(x + 10, y + 5, 5, 5);
      g.fillRect(x + 5, y + 10, 10, 5);
    }
    if (direction == 26) {
      g.fillRect(x + 5, y, 5, 5);
      g.fillRect(x + 15, y, 5, 5);
      g.fillRect(x + 5, y + 5, 10, 5);
      g.fillRect(x + 10, y + 10, 10, 5);
    }
    if (direction == 42) {
      g.fillRect(x + 5, y + 5, 10, 5);
      g.fillRect(x + 5, y + 10, 5, 5);
      g.fillRect(x + 15, y + 10, 5, 5);
      g.fillRect(x + 10, y + 15, 5, 5);
    }
    if (direction == 48) {
      g.fillRect(x + 5, y, 5, 5);
      g.fillRect(x + 15, y, 5, 5);
      g.fillRect(x + 10, y + 5, 5, 5);
      g.fillRect(x + 15, y + 10, 5, 5);
    }
    if (direction == 62) {
      g.fillRect(x, y + 5, 5, 5);
      g.fillRect(x + 5, y + 10, 5, 5);
      g.fillRect(x + 10, y + 15, 5, 5);
      g.fillRect(x, y + 15, 5, 5);
    }
    if (direction == 68) {
      g.fillRect(x, y, 5, 5);
      g.fillRect(x + 10, y, 5, 5);
      g.fillRect(x + 5, y + 5, 10, 5);
      g.fillRect(x, y + 10, 10, 5);
    }
    if (direction == 84) {
      g.fillRect(x, y + 5, 5, 5);
      g.fillRect(x + 5, y + 10, 5, 5);
      g.fillRect(x, y + 15, 5, 5);
      g.fillRect(x + 10, y + 15, 5, 5);
    }
    if (direction == 86) {
      g.fillRect(x + 10, y + 5, 10, 5);
      g.fillRect(x + 5, y + 10, 10, 5);
      g.fillRect(x + 5, y + 15, 5, 5);
      g.fillRect(x + 15, y + 15, 5, 5);
    }
  }
}
