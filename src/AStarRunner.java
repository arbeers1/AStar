
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * @author Alexander Beers
 * AStarRunner controls the GUI and interacts with MAP to visualize the A* Algorithm.
 */
public class AStarRunner extends Application {
  private Stage stage; // main stage
  private static Scene scene; //grid scene
  private Scene menuS; //menu scene
  private final String TITLE = "A* Visualizer"; //stage title
  private double height; //screen height
  private double width; //screen width
  private Map map; //map
  private static GridPane grid; //grid
  private boolean menuOpen; //t if menuOpen, f otherwise
  private int tool; //The tool which is currently selected. 1 = start. 2 = finish. 3 = wall. 4 = erase
  private Label toolL;
  
  //COLORS
  private static final String PATHABLE = "-fx-background-color: #3792cb; -fx-border-color: #000000";
  private static final String PATHED = "-fx-background-color: #8E1600; -fx-border-color: #000000";
  private static final String FINAL_PATH = "-fx-background-color: #669966; -fx-border-color: #000000";
  private static final String WALL = "-fx-background-color: #333333; -fx-border-color: #000000";
  private static final String START = "-fx-background-color: #669966; -fx-border-color: #000000";
  private static final String FINISH = "-fx-background-color: #EFC501; -fx-border-color: #000000";
  
  private static final String HOVER = "-fx-border-color: #ffffff";
  private static final String BLANK = "-fx-border-color: #000000";
  
  /**
   * Starts the javaFx GUI
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    tool = 1;
    menuOpen = false;
    stage = new Stage();
    stage.setTitle(TITLE);
    
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    height = primaryScreenBounds.getHeight();
    width = primaryScreenBounds.getWidth();
    stage.setHeight(height);
    stage.setWidth(width);
    initialize();
  }
  
  /**
   * Creates Map object as well as visual map
   */
  private void initialize() {
    map = new Map();
    
    grid = new GridPane();
    for(int i = 0; i < 80; i++) {
      for(int j = 0; j < 80; j++) {
        Button b = defineButton();
        grid.add(b, j, i);
      }
    }
    
    menuS = buildMenu();
    scene = new Scene(grid, width, height);
    scene.setOnKeyPressed(e -> {
      switch(e.getCode()) {
        case M: menu(); break;
        case DIGIT1: tool = 1; break;
        case DIGIT2: tool = 2; break;
        case DIGIT3: tool = 3; break;
        case E: tool = 4; break;
        case S: map.exec(); break;
        case C: map.clear(); break;
      }
    });
    
    
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Creates a button for the node and on click logic.
   * @return button with default style
   */
  public Button defineButton() {
    Button b = new Button();
    b.setPrefSize(width/80, height/80);
    b.setStyle(PATHABLE);
    b.setOnMouseEntered(e -> b.setStyle(b.getStyle().substring(0, 30) + HOVER));
    b.setOnMouseExited(e -> b.setStyle(b.getStyle().substring(0, 30) + BLANK));
    b.setOnMouseClicked(e -> modify(b));
    return b;
  }
  
  /**
   * Responsible for making the display adjustments when a tool is used on the grid
   * @param b - button to be adjusted
   */
  private void modify(Button b) {
    int x = grid.getColumnIndex(b);
    int y = grid.getRowIndex(b);
    if(tool == 1) {
      Node temp = map.setStart(x, y);
      b.setStyle(START);
      if(temp != null) {
        map.erase(temp.getX(), temp.getY());
        grid.getChildren().get(temp.getY() * 80 + temp.getX()).setStyle(PATHABLE);
      }
    }else if(tool == 2) {
      Node temp = map.setEnd(x, y);
      b.setStyle(FINISH);
      if(temp != null) {
        map.erase(temp.getX(), temp.getY());
        grid.getChildren().get(temp.getY() * 80 + temp.getX()).setStyle(PATHABLE);
      }
    }else if(tool == 3) {
      map.setWall(x, y);
      b.setStyle(WALL);
    }else {
      map.erase(x, y);
      b.setStyle(PATHABLE);
    }
  }
  
  /**
   * Opens/Closes the menu
   */
  private void menu() {
    if(menuOpen) {
      menuOpen = !menuOpen;
      stage.setScene(scene);
    }else {
      menuOpen = !menuOpen;
      if(tool == 1) {
        toolL.setText("Current tool: start");
      }else if(tool == 2) {
        toolL.setText("Current tool: finish");
      }else if(tool == 3) {
        toolL.setText("Current tool: wall");
      }else {
        toolL.setText("Current tool: Erase");
      }
      stage.setScene(menuS);
    }
  }
  
  /**
   * Builds the menu
   * @return - Vbox
   */
  private Scene buildMenu() {
    VBox vbox = new VBox(5);
    Label l = new Label("Tools");
    Button start = new Button("Start Node (1)");
    Button end = new Button("End Node (2)");
    Button wall = new Button("Wall Node (3)");
    Button exec = new Button("Execute (S)");
    Button erase = new Button("Erase (E)");
    Button clear = new Button("Clear Board (C)");
    Button exit = new Button ("Exit Menu (M)");
    Label l2 = new Label("Shortcuts");
    toolL = new Label("Current tool: ");
    vbox.getChildren().addAll(l, start, end, wall, erase,l2, exec, clear, exit, toolL);
    Scene s = new Scene(vbox, width, height);
    s.setOnKeyPressed(e -> {
      switch(e.getCode()) {
        case M: menu(); break;
        case DIGIT1: tool = 1; toolL.setText("Current tool: start"); break;
        case DIGIT2: tool = 2; toolL.setText("Current tool: finish"); break;
        case DIGIT3: tool = 3; toolL.setText("Current tool: wall"); break;
        case E: tool = 4; toolL.setText("Current tool: Erase"); break;
      }
    });
    start.setOnMouseClicked(e -> tool = 1);
    end.setOnMouseClicked(e -> tool = 2);
    wall.setOnMouseClicked(e -> tool = 3);
    erase.setOnMouseClicked(e -> tool = 4);
    exec.setOnMouseClicked(e -> map.exec());
    clear.setOnMouseClicked(e -> map.clear());
    exit.setOnMouseClicked(e -> menu());
    return s;
  }
  
  /**
   * Redraws nodes to indicate that they were pathed. Pauses for viewer to watch.
   * @param x coord to draw
   * @param y coord to draw
   * @param finalPath - True if the final path (green), false if just a visited node (red)
   */
  public static void update(int x, int y, boolean finalPath) {
    Button b = (Button) grid.getChildren().get(y * 80 + x);
      if(finalPath) {
        b.setStyle(FINAL_PATH);
      }else {
        b.setStyle(PATHED);
      }
  }
  
  /**
   * Redraws the square to blue, indicating it is pathable.
   * @param x - x coord to draw
   * @param y coord to draw
   */
  public static void clear(int x, int y) {
    grid.getChildren().get(y * 80 + x).setStyle(PATHABLE);
  }
  
  public static void main(String[] args) {
    launch();
  }

}
