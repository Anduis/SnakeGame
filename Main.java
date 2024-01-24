import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class Main extends JFrame {
  public static void main(String args[]) {
    new Main();
  }

  long time = 200;
  Board board;
  Snake snake;
  Thread thread;
  int gridSize;

  Main() {
    Dimension pS = Toolkit.getDefaultToolkit().getScreenSize();
    setTitle("Snake Game");
    gridSize = pS.height / 24;
    board = new Board(gridSize);
    getContentPane().add(board, BorderLayout.CENTER);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JFrame.setDefaultLookAndFeelDecorated(false);
    setUndecorated(true);
    thread = new Thread(new Movimiento());
    addKeyListener(new KeyListener());
    setSize(gridSize * 20, (gridSize + 6) * 20);
    System.out.println(gridSize);
    setVisible(true);
    this.setLocationRelativeTo(null);
  }

  class Movimiento extends Thread {
    public void run() {
      int q = 0;
      int o = 0;
      board.reset();
      snake = new Snake(board);
      int[][] salva = board.body;
      while (true) {
        if (!board.gameOver && !board.isPaused && board.isLevelSetted) {
          o = 0;
          snake.move();
          board.bonusTimer--;
          if (board.bonusTimer == 0)
            board.addAltFood();
          if (board.bonusTimer == -board.bonusTimeLeft) {// usr dindt eated bonus
            board.removeAltFood();
            board.newBonusTimer();
          }
        }
        if (board.gameOver) {
          time = 200;
          if (board.highScore < board.score) {
            board.highScore = board.score;
            board.highScoreAchieved = true;
          }
          if (q % 2 == 0) {
            board.body = new int[board.size][board.size];
            if (o < 6)
              Toolkit.getDefaultToolkit().beep();
          }
          o++;
        }
        repaint();
        try {
          Thread.sleep(time);
        } catch (InterruptedException e) {
        }
        q++;
        snake.usrHasChosen = false;
        board.body = salva;
      }
    }
  }

  class KeyListener extends java.awt.event.KeyAdapter {
    public void keyPressed(KeyEvent e) { // to perfom usr actions
      try {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) // exit
          System.exit(0);
        if (key == KeyEvent.VK_P) // pause
          board.isPaused = !board.isPaused;
        if (key == KeyEvent.VK_ENTER && !board.hasStarted) { // start
          thread.start();
          board.hasStarted = true;
        }
        if (key == KeyEvent.VK_R && board.gameOver) { // restart
          board.reset();
          snake = new Snake(board);
          time = (time / board.level) - (10 * (1 / board.level) + 10);
        }
        if (key == KeyEvent.VK_F && board.gameOver) { // change level
          board.isLevelSetted = false;
          board.reset();
        }
        if (board.hasStarted && (key > 96 && key < 102 || key > 48 && key < 54) && !board.isLevelSetted) { // choose_level
          if (key > 48 && key < 54)
            key = key - 48;
          else
            key = key - 96;
          board.isLevelSetted = true;
          board.level = (key);
          time = (time / key) - (10 * (1 / key) + 10);
          snake = new Snake(board);
          board.reset();
        }
        if (!snake.usrHasChosen) { // usr changes direction
          if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && snake.movingDirection != snake.LEFT)
            snake.turn(snake.RIGHT);
          else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && snake.movingDirection != snake.RIGHT)
            snake.turn(snake.LEFT);
          else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && snake.movingDirection != snake.DOWN)
            snake.turn(snake.UP);
          else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && snake.movingDirection != snake.UP)
            snake.turn(snake.DOWN);
        }
      } catch (java.lang.NullPointerException s) {
      }
    }
  }
}
