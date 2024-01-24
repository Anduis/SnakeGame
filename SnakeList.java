//the snake's list of segments in its body
public class SnakeList {
  Node head;
  Node tail;

  SnakeList() {
    head = new Node();
    tail = new Node();
    tail.prev = head;
    head.next = tail;
  }

  void addFirst(Node p) {
    p.prev = head;
    p.next = head.next;
    head.next.prev = p;
    head.next = p;
  }

  Node getFirst() {
    if (head.next != tail)
      return head.next;
    else
      return null;
  }

  Node removeLast() {
    if (tail.prev != head) {
      Node temp = tail.prev;
      tail.prev.prev.next = tail;
      tail.prev = tail.prev.prev;
      return temp;
    } else
      return null;
  }

  void addLast(Node n) {
    n.prev = tail.prev;
    n.next = tail;
    tail.prev = n;
    tail.prev.next = n;
  }
}