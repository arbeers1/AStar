import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import javafx.application.Platform;

/**
 * @author Alexander Beers
 * Represents a 2D Grid of nodes
 */
public class Map {
  
  private Node[][] map; //Map is a 2d Array consisting of Nodes
  private Node start;
  private Node target;
  private PriorityQueue<Node> open; //The list of Nodes to search
  private ArrayList<Node> closed; //The list of visited Nodes
  
  
  public Map() {
    start = null;
    target = null;
    map = new Node[80][80];
    //Loads the map with nodes
    for(int i = 0; i < map.length; i++) {
      for(int j = 0; j < map[0].length; j++) {
        map[i][j] = new Node(i, j);
      }
    }
    
    open = new PriorityQueue<Node>(1, (o1, o2) -> { 
      if(o1.getDistance() < o2.getDistance()) {
        return -1;
      }else if(o1 == o2) {
        return 0;
      }else {
        return 1;
      }
    });
    closed = new ArrayList<Node>();
  }
  
  /**
   * Resets the all nodes to 1
   */
  public void clear() {
    start = null;
    target = null;
    open.clear();
    closed.clear();
    for(int i = 0; i < map.length; i++) {
      for(int j = 0; j < map[0].length; j++) {
        map[i][j].setValue(1);
        AStarRunner.clear(i, j);
      }
    }
  }
  
  /**
   * Starts pathfinding
   */
  public void exec() {
    //Ensures a start and target exists before pathfinding.
    if(start != null && target != null) {
      new Thread(new Runnable() { //Starts a new thread to prevent pausing GUI Thread
        @Override
        public void run() {
          AStar();
        }
      }).start();
    }
  }
  
  /**
   * Searches for a path from start node to target node with the A* algorithm.
   */
  private void AStar() {        
    open.add(start); //Starts with the head node.
    while(!open.isEmpty()) {
      Node current = open.poll();
      current.setValue(4); //Marks visited
      AStarRunner.update(current.getX(), current.getY(), false); //Updates in display
      if(current == target) { //break case
        break;
      }
      Node[] neigh = neighbors(current, target); //checks neighbors and adds them to the queue
      for(int i = 0; i < neigh.length; i++) {
        if(neigh[i] != null) {
          if(neigh[i].getValue() != 3 && neigh[i].getValue() != 4) {
            open.add(neigh[i]);
          }
        }
      }
      current.setDistance(heuristic(current, start)); //Sets currents distance to start for reconstruction
      closed.add(current); //adds current to closed
      try {
        Thread.sleep(10);
      }catch(InterruptedException e) {}
    }
    reconstruct(); //Examines all visited nodes to pick the most efficient path.
  }
  
  /**
   * Gets neighbors of current Node
   * @param n - current node
   * @return array of neighbor nodes
   */
  private Node[] neighbors(Node n, Node dest) {
    int x = n.getX();
    int y = n.getY();
    Node[] arr = new Node[4];
    try {
      Node temp = map[x-1][y];
      arr[0] = temp;
      temp.setDistance(heuristic(temp, dest));
    }catch(IndexOutOfBoundsException | NullPointerException e) { arr[0] = null;}
    try {
      Node temp = map[x+1][y];
      arr[1] = temp;
      temp.setDistance(heuristic(temp, dest));
    }catch(IndexOutOfBoundsException | NullPointerException e) { arr[1] = null;}
    try {
      Node temp = map[x][y-1];
      arr[2] = temp;
      temp.setDistance(heuristic(temp, dest));
    }catch(IndexOutOfBoundsException | NullPointerException e) { arr[2] = null;}
    try {
      Node temp = map[x][y+1]; 
      arr[3] = temp;
      temp.setDistance(heuristic(temp, dest));
    }catch(IndexOutOfBoundsException | NullPointerException e) { arr[3] = null;}
    return arr;
  }
  
  /**
   * Returns the distance between Node n, and Target.
   * @param n - Node to work with
   * @return - double distance between the nodes.
   */
  private double heuristic(Node n, Node dest) {
    return Math.hypot(dest.getX() - n.getX(), dest.getY() - n.getY());
  }
  
  
  /**
   * Constructs the path from target to start after search has finished.
   */
  private void reconstruct() {
    Node current = target;
    while(current != start) {
      ArrayList<Node> prio = neighborsWithPrio(current);
      AStarRunner.update(current.getX(), current.getY(), true);
        for(int i = 0; i < prio.size(); i++) {
          if(closed.contains(prio.get(i))) {
            closed.remove(current);
            current = prio.get(i);
            break;
          }
        }
      try {
        Thread.sleep(50);
      }catch(InterruptedException e) {}
    }
  }
  
  /**
   * Helper method for reconstruct. Returns the list of neighbors with priority.
   * @param n - Node to get neighbors for
   * @return - the list of neighbors ordered from highest to lowest priority
   */
  private ArrayList<Node> neighborsWithPrio(Node n) {
    Node[] arr = neighbors(n, start);
    ArrayList<Node> result = new ArrayList<Node>(Arrays.asList(arr));
    for(int i = 1; i < result.size(); i++) {
      if(result.get(i) == null) {
        result.remove(i);
      }else if(result.get(i).getValue() == 3) {
        result.remove(i);
      }
    }
    result.sort((o1, o2) -> { 
      if(o1.getDistance() < o2.getDistance()) {
        return -1;
      }else if(o1 == o2) {
        return 0;
      }else {
        return 1;
      }
    });
    return result;
  }
  
  /**
   * Returns node value at x,y coordinate pair
   * @param x - x coordinate
   * @param y - y coordinate
   * @return the value of the node
   */
  public int getValueAt(int x, int y) {
    return map[x][y].getValue();
  }
  
  /**
   * Sets the start node at the desired coordinate
   * @param x - x coordinate to start
   * @param y - y coordinate to start
   * @return Old start node, if it exists, so that the display can erase it. Otherwise returns null.
   */
  public Node setStart(int x, int y) {
    Node temp = start;
    if(target != null) {
      if(target.getX() == x && target.getY() == y) {
        target = null;
      }
    }
    map[x][y].setValue(1);
    start = map[x][y];
    return temp;
  }
  
  /**
   * Sets the end node at the desired coordinate
   * @param x - x coordinate to end
   * @param y - y coordinate to end
   * @return Old end node, if it exists, so that the display can erase it. Otherwise returns null.
   */
  public Node setEnd(int x, int y) {
    Node temp = target;
    if(start != null) {
      if(start.getX() == x && start.getY() == y) {
        start = null;
      }
    }
    map[x][y].setValue(2);
    target = map[x][y];
    return temp;
  }
  
  /**
   * Sets a wall node at the desired coordinate
   * @param x - x coordinate to set wall
   * @param y - y coordinate to set wall
   */
  public void setWall(int x, int y) {
    //Checks for if the node is already a start or target node
    if(start != null) {
      if(start.getX() == x && start.getY() == y) {
        start = null;
      }
    }else if(target != null) {
      if(target.getX() == x && target.getY() == y) {
        target = null;
      }
    }
    map[x][y].setValue(3);
  }
  
  /**
   * Erases a node at the desired coordinate
   * @param x - x coordinate to erase
   * @param y - y coordinate to erase
   */
  public void erase(int x, int y) {
    //Checks for if the node is already a start or target node
    if(start != null) {
      if(start.getX() == x && start.getY() == y) {
        start = null;
      }
    }else if(target != null) {
      if(target.getX() == x && target.getY() == y) {
        target = null;
      }
    }
    map[x][y].setValue(0);
  }
}
