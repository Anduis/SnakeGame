import java.awt.Toolkit;

public class Snake {

  SnakeList sList;
  Board board;
  boolean usrHasChosen = false;
  int RIGHT = 6;
  int DOWN = 2;
  int LEFT = 4;
  int UP = 8;
  int movingDirection = UP;

  Snake(Board board) {
    this.board = board;
    sList = new SnakeList();
    for (int i = 0; i < board.level + 1; i++)
      sList.addFirst(new Node((board.size / 2), (board.size / 2) + i, 8));
  }

  void bodyForm() {
    Node n = sList.getFirst();
    board.body[n.x][n.y] = n.dir * 11;
    if (((n.dir % 100) - (n.dir % 10)) / 10 == 5)
      board.body[n.x][n.y] = (n.dir % 10) * 11;
    while (n.next.x != 999) {
      n = n.next;
      if (n.prev.dir == n.dir || ((n.prev.dir % 100) - (n.prev.dir % 10)) / 10 == 5
          || ((n.dir % 100) - (n.dir % 10)) / 10 == 5) {
        board.body[n.x][n.y] = n.dir;
        if (n.prev.dir != n.dir && ((n.prev.dir % 100) - (n.prev.dir % 10)) / 10 == 5)
          if (isRect(n) == false) {
            board.body[n.x][n.y] = ((n.prev.dir - (n.prev.dir % 100)) / 10) + n.prev.dir % 10;
            if (n.prev == sList.getFirst() && n.dir * 11 == board.body[n.x][n.y])
              board.body[n.x][n.y] = n.dir % 10;
          }
      } else
        board.body[n.x][n.y] = ((10 * (n.dir % 10)) + (n.prev.dir % 10));
    }
  }

  boolean isRect(Node n) {// detects if there is a turn nearby a node
    int d = n.dir % 10;
    if (d == UP)
      return (board.body[n.x][n.y - 1] == n.prev.dir);
    if (d == DOWN)
      return (board.body[n.x][n.y + 1] == n.prev.dir);
    if (d == RIGHT)
      return (board.body[n.x + 1][n.y] == n.prev.dir);
    else // LEFT
      return (board.body[n.x - 1][n.y] == n.prev.dir);
  }

  int[] next() {
    Node head = sList.getFirst();
    int[] next = { head.x, head.y };
    if (movingDirection == LEFT || movingDirection == RIGHT)
      next[0] = movingDirection == LEFT ? head.x - 1 : head.x + 1;
    else
      next[1] = movingDirection == UP ? head.y - 1 : head.y + 1;
    return next;
  }

  void turn(int usrDirection) {
    movingDirection = usrDirection;
    usrHasChosen = true;
  }

  void move() {
    int x = next()[0];
    int y = next()[1];
    Node tail = sList.removeLast();
    boolean drawBelly = false;
    if (board.getMarker(x, y) == board.SNAKE || board.getMarker(x, y) == board.WALL) {// dies :(
      board.gameOver = true;
      return;
    }
    if (board.getMarker(x, y) == board.FOOD) { // eats :)
      drawBelly = true;
      board.putMarker(tail.x, tail.y, board.SNAKE);
      sList.addLast(tail);
      board.score += 5;
      board.addFood();
      Toolkit.getDefaultToolkit().beep();
    }
    if (board.getMarker(x, y) == board.ALT || board.getMarker(x - 1, y) == board.ALT) {// eats bonus
      drawBelly = true;
      board.putMarker(tail.x, tail.y, board.SNAKE);
      sList.addLast(tail);
      board.score += 15;
      board.removeAltFood();
      board.newBonusTimer();
      Toolkit.getDefaultToolkit().beep();
    }
    // comun
    sList.addFirst(new Node(x, y, movingDirection));
    board.putMarker(tail.x, tail.y, board.NONE);
    board.body[tail.x][tail.y] = 0;
    board.putMarker(x, y, board.SNAKE);
    if (drawBelly)
      sList.getFirst().dir = (sList.getFirst().next.dir * 100) + 50 + movingDirection;
    bodyForm();
    if (board.getMarker(next()[0], next()[1]) != board.NONE)// opens its mouth
      board.body[x][y] = movingDirection * 111;
  }
}
