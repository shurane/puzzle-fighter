import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Random;
import java.util.Date;
import java.io.File;
import javax.imageio.*;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.management.timer.Timer;
import javax.management.NotificationFilterSupport;

public class OverallInterface extends JFrame implements WindowListener{
  private static final int BLOCK_SIZE = Board.BLOCK_SIZE, SPEED_NORMAL=100, SPEED_FAST=50, SPEED_FASTEST=10, PREVIEW_SPACER=30;
  private static final int[] CONTROLS_ONE_DEFAULT=new int[] {KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_Q, KeyEvent.VK_E},
                             CONTROLS_TWO_DEFAULT=new int[] {KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD9},
                             CONTROLS_TWO_LAPTOP=new int[] {KeyEvent.VK_SEMICOLON, KeyEvent.VK_L, KeyEvent.VK_QUOTE, KeyEvent.VK_O, KeyEvent.VK_OPEN_BRACKET},
                             //order: down, left, right, upleft, upright
                             BORDERS=new int[] {30, 30, 30, 30, 5*BLOCK_SIZE}; //left, right, up, down, divider
  private static final Dimension SIZE=new Dimension(BORDERS[0]+BORDERS[1]+2*Board.NUM_COLS*BLOCK_SIZE+BORDERS[4], 
                                                    BORDERS[2]+BORDERS[3]+Board.NUM_ROWS*BLOCK_SIZE); 
  public static final String imageDir="images/";
  private Board board1,board2;
  private BlockList blist;
  private Container contentPane;
  public HashMap<String,ImageIcon> imageMap;
  public Timer t;
  public OverallInterface(){
    this(false);
  }                            
  public OverallInterface(boolean laptopGame){ //put scale into it later
    super("Puzzle Fighter");
    SIZE.height = 800;
    System.out.println(SIZE);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(SIZE);
    setResizable(false);
    setFocusable(true);
    setBackground(Color.black);
    setIconImage(getToolkit().getImage(imageDir+"Gem-blue-small.png"));
    addWindowListener(this);
    Board.OI=this;
    board1 = new Board(new Point((int)(getLocation().getX()+BORDERS[0]),(int)getLocation().getY()+BORDERS[2]), CONTROLS_ONE_DEFAULT);
    if(laptopGame)
      board2 = new Board(new Point((int)(getLocation().getX()+BORDERS[0]+BORDERS[4]+Board.NUM_COLS*BLOCK_SIZE),
                                   (int)getLocation().getY()+BORDERS[2]), CONTROLS_TWO_LAPTOP);
    else
      board2 = new Board(new Point((int)(getLocation().getX()+BORDERS[0]+BORDERS[4]+Board.NUM_COLS*BLOCK_SIZE),
                                   (int)getLocation().getY()+BORDERS[2]), CONTROLS_TWO_DEFAULT);
    setupImageMap();
    blist = new BlockList(board1,board2);
    Point a=board1.getOffset(), b=board2.getOffset();
    board1.setBlinkText(new BlinkText(new Point((int)a.getX(), (int)a.getY()+BLOCK_SIZE)));
    board2.setBlinkText(new BlinkText(new Point((int)b.getX(), (int)b.getY()+BLOCK_SIZE)));
    addKeyListener(board1);
    addKeyListener(board2);
    beginTimer();
  }
  private void beginTimer(){
    t=new Timer();
    Date d=new Date();
    t.addNotification("normal", "str2", "str3", d, SPEED_NORMAL);
    t.addNotification("fast", "str2", "str3", d, SPEED_FAST);
    t.addNotification("fastest", "str2", "str3", d, SPEED_FASTEST);
    board1.setFilter(new NotificationFilterSupport());
    board2.setFilter(new NotificationFilterSupport());
    board1.toggleFilter("normal");
    board2.toggleFilter("normal");
    t.addNotificationListener(board1, board1.getFilter(), this);
    t.addNotificationListener(board2, board2.getFilter(), this);
    t.start();
  }
  private void setupImageMap(){
    imageMap=new HashMap<String,ImageIcon>();
    String[] colorString={"red", "green", "blue", "yellow"};
    for(int colorIndex=0; colorIndex<4; ++colorIndex){
      addToMap("Gem-"+colorString[colorIndex]+"-small", ".png");
      addToMap("Gem-"+colorString[colorIndex]+"-large", ".png");
      addToMap("CrashGem-"+colorString[colorIndex], ".gif");
      for(int counterIndex=1; counterIndex<6; counterIndex++)
        addToMap("CounterGem-"+colorString[colorIndex]+"-"+counterIndex, ".gif");
    }
    addToMap("Diamond", ".gif");
  }
  private void addToMap(String imageName, String extension){
    imageMap.put(imageName, new ImageIcon(getClass().getResource(imageDir+imageName+extension)));
  } 
  public void findContentPane(){
    contentPane=getContentPane();
  }
  public void windowOpened(WindowEvent e){
    windowActivated(e);
  } 
  public void windowActivated(WindowEvent e){ 
    t.start();
  }
  public void windowDeactivated(WindowEvent e){
    t.stop();
  }
  public void windowIconified(WindowEvent e){
  }
  public void windowDeiconified(WindowEvent e){
    windowActivated(e);
  } 
  public void windowClosing(WindowEvent e){ 
    setVisible(false); 
    t.stop();
  } 
  public void windowClosed(WindowEvent e){
    dispose(); 
  }
  public Board getBoard(int which){
    if(which==0)
      return board1;
    return board2;
  }
  public void writeString(String text, Point location){
    writeString(text, location, Color.white);
  }
  public void writeString(String text, Point location, Color color){
    Graphics2D graphics=(Graphics2D) getGraphics();
    graphics.setFont(new Font("Courier", Font.BOLD, (int) (BLOCK_SIZE/1.66)));
    graphics.setPaint(color);
    graphics.drawString(text, (int)location.getX(), (int)location.getY());
  } 
  public void nextPreviewPair(Board board){
    BlockPair temp = blist.peekNext(board);
    if(board==board1){
      temp.getPivot().movePixelsNoBoard((int)(Board.NUM_COLS*BLOCK_SIZE+PREVIEW_SPACER+BORDERS[0]), 4*BLOCK_SIZE);
      temp.getOther().movePixelsNoBoard((int)(Board.NUM_COLS*BLOCK_SIZE+PREVIEW_SPACER+BORDERS[0]), 3*BLOCK_SIZE);
    }
    else{
      temp.getPivot().movePixelsNoBoard((int)(Board.NUM_COLS*BLOCK_SIZE+BORDERS[0]+BORDERS[4]-PREVIEW_SPACER-BLOCK_SIZE), 4*BLOCK_SIZE);
      temp.getOther().movePixelsNoBoard((int)(Board.NUM_COLS*BLOCK_SIZE+BORDERS[0]+BORDERS[4]-PREVIEW_SPACER-BLOCK_SIZE), 3*BLOCK_SIZE);
    }
    Point zero=new Point(0,0);
  }
  public void addBlock(Block b){
    contentPane.add(b);
    setVisible(true);
  }
  public void removeBlock(Block b){
    contentPane.remove(b);
    setVisible(true);
  }
  public static void main(String[] args){
    OverallInterface q;
    try{
      String useless=args[0];//if there is input, assume it's asking for laptop mode
      System.out.println("Laptop controls set for player two.");
      q = new OverallInterface(true);
    }
    catch(ArrayIndexOutOfBoundsException n){
      q = new OverallInterface(false);
    }
    q.setVisible(true);
    q.findContentPane();
  }
}
