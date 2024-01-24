//Each node contains the information required to allocate each segment of snake's body including in which direction its moving
public class Node {
  Node prev;
  Node next;
  int dir;
  int x;
  int y;

  Node(int x, int y, int dir) {
    this.dir = dir;
    this.x = x;
    this.y = y;
  }

  Node() {
    dir = 555;
    this.x = 999;
    this.y = 999;
  }
}
