
/**
 * @author Alexander Beers
 * A Node is a visitable point on the map. Consist of a value determining if it is pathable or not.
 */
public class Node {
  private int value; // represents node type. 0 = pathable, 1 = start, 2 = target, 3 = wall
                     // 4 = pathed
  private int x; // x coordinate
  private int y; // y coordinate
  private Node parent; //The parent node that was visited previous to this node.
  private double distance;
  
  public Node(int x, int y) {
    value = 0; // All nodes default to 0.
    distance = 0.0;
    this.x = x;
    this.y = y;
    parent = null;
  }
  
  /**
   * Sets the value of the node
   * @param i - value to set node to
   */
  public void setValue(int i) {
    value = i;
  }
  
  /**
   * @return value of node
   */
  public int getValue() {
    return value;
  }
  
  /**
   * @return x coordinate
   */
  public int getX() {
    return x;
  }
  
  /**
   * @return y coordinate
   */
  public int getY() {
    return y;
  }
  
  /**
   * @param d - distance to set
   */
  public void setDistance(double d) {
    distance = d;
  }
  
  /**
   * @return - returns distance from target
   */
  public double getDistance() {
    return distance;
  }
  
  /**
   * @param parent node which visited this node
   */
  public void setParent(Node parent) {
    this.parent = parent;
  }
  
  /**
   * @return the parent node
   */
  public Node getParent() {
    return parent;
  }
  
  /**
   * Determines if a given node is a neighbor of this node
   * @param x coord of node
   * @param y coord of node
   * @return true if it is a neighbor, false otherwise
   */
  public boolean isNeighbor(int x, int y) {
    int dx = this.x - x;
    int dy = this.y - y;
    if((dx == 1 || dx == -1) && dy == 0) {
      return true;
    }else if((dy == 1 || dy == -1) && dx == 0) {
      return true;
    }else {
      return false;
    }
  }
}
