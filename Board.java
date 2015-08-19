import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.NotificationFilterSupport;

public class Board extends JPanel implements NotificationListener, KeyListener{
 //216x468, each space is 36x36
 /*to do:
  * create contiguous blocks
  * implement countergemset drops
  * make block attacks exponential and not linear, make combos worth more
  * draw message images (win, lose), implement menus
  * implement and display scoring (combos = more points, as do larger blocks)
  * sometimes rotation makes block disappear
  */
 public static final int BLOCK_VALUE=10, PIXELS_DOWN=4, BLOCK_SIZE=32, NUM_ROWS=14, NUM_COLS=6;
 public static final double BLOCK_DROP_NUM=1.0, BLOCK_COUNTER_NUM=.8, DEFAULT_FACTOR=1.0, COUNTERGEM_FACTOR=.67, DIAMOND_FACTOR=.75;
 public static OverallInterface OI;
 private Block[][] board;
 private int score, listIndex;
 private double numBlocks, tempNumBlocks;
 private Point offset;
 private BlockList list;
 private NotificationFilterSupport filter;
 private BlockPair currentPair;
 private Board otherBoard;
 private Diamond curDiamond;
 private CounterGemSet counterSet;
 private int[] legalKeys;
 private boolean waiting, beganDrop, duringDrop;
 private BlinkText text;
 private String currentFilter;
 public Board(Point cornerOffset, int[] keys){
   board=new Block[NUM_ROWS][NUM_COLS];
   offset=cornerOffset;
   legalKeys=keys;
 }
 public void setList(BlockList b){
   list=b;
 }
 public void setOtherBoard(Board b){
   otherBoard=b;
 }
 public void setFilter(NotificationFilterSupport f){
   filter=f;
 }
 public void setBlinkText(BlinkText b){
   text=b;
 }
 public void setCounter(CounterGemSet c){
   counterSet=c;
 }
 //accessors
 public static Point pixelsToPoint(int x, int y, Point off){
   Point p=new Point((int)(x-off.getX())/BLOCK_SIZE, (int)(y-off.getY())/BLOCK_SIZE);
   if((x-off.getX())%BLOCK_SIZE!=0)
     p.setLocation((int)p.getX()+1, (int)p.getY());
   if((y-off.getY())%BLOCK_SIZE!=0)
     p.setLocation((int)p.getX(), (int)p.getY()+1);
   return p;
 }
 public static int pixelsToCoord(int px, int offset){
   int coord=(px-offset)/BLOCK_SIZE;
   if((px-offset)%BLOCK_SIZE!=0)
     return coord+1;
   return coord;
 }
 private static int[] randSeq(int length){
   //returns a random sequence, where every integer 0<=i<length appears exactly once in random order
   ArrayList<Integer> temp=new ArrayList<Integer>(length);
   int[] result=new int[length];
   Random r=new Random();
   for(int i=0; i<length; ++i)
     temp.add(i);
   int size=temp.size(), i=0;
   while(size>0){
     result[i]=temp.remove(r.nextInt(size));
     ++i;
     size=temp.size();
   }
   return result;
 }
 private static Block[] toArray(ArrayList<Block> blocks){
   Block[] newArray=new Block[blocks.size()];
   for(int i=0; i<newArray.length; ++i)
     newArray[i]=blocks.get(i);
   return newArray;
 }
 public void handleNotification(Notification n, Object o){
   if(currentPair!=null && !moveDownPair()){
     //if the pair has landed...
     waiting=true;
     currentPair=null;
     decrementAll();
     toggleFilter("fastest");
     beganDrop=false;
   }
   if(currentPair!=null)
     return;
   if(beganDrop)
     dropBlocks();
   if(!moveDown()){
     createBigGems();
     if(!destroyBlocks()){
       if(!beganDrop){
         /*beforehand, a set of blocks would drop, then the other player could destroy more and the player being attacked 
          had to wait as each set fell separately. with this if, extra sets fall as soon as they are sent by the other player*/
         beganDrop=duringDrop=true;
         if(dropBlocks())
           return;
       }
       duringDrop=false;
       //if all possible actions have been performed before the next pair...
       if(board[1][3]!=null){
         //and the game isnt over...
         String s="Game over.";
         if(OI.getBoard(0)==this)
           s+=" Player two wins!";
         else
           s+=" Player one wins!";
         OI.writeString(s, new Point(150, 300));
         OI.t.stop();
         stop();
         otherBoard.stop();
         text.hide();
         otherBoard.getBlinkText().hide();
         return;
       }
       //...then add the next pair
       numBlocks=tempNumBlocks;
       tempNumBlocks=0;
       if(numBlocks>1){
         text.findWarningLevel(this);
         text.show();
       }
       waiting=false;
       addNextPair();
       toggleFilter("normal");
     }
   }
 }
 public void keyPressed(KeyEvent e){
   keyTyped(e);
 }
 public void keyReleased(KeyEvent e){
   if(!waiting && e.getKeyCode()==legalKeys[0])
     toggleFilter("normal");
 }
 public void keyTyped(KeyEvent e){
   if(waiting)
     return;
   //order of keys: down, left, right, upleft, upright
   int key=e.getKeyCode();
   if(key==legalKeys[0])
     toggleFilter("fast");
   else if(key==legalKeys[1])
     currentPair.shift(-1);
   else if(key==legalKeys[2])
     currentPair.shift(1);
   else if(key==legalKeys[3])
     currentPair.rotate(true);
   else if(key==legalKeys[4])
     currentPair.rotate(false);
 }
 public BlockPair getCurrentPair(){
   return currentPair;
 }
 public int getWidth(){
   return board[0].length*BLOCK_SIZE;
 }
 public int getHeight(){
   return board.length*BLOCK_SIZE;
 }
 public double getNumBlocks(){
   return numBlocks;
 }
 public NotificationFilterSupport getFilter(){
   return filter;
 }
 public Block getSpace(int row, int col){
   return board[row][col];
 }
 public Block getSpace(Point p){
   return getSpace((int)p.getX(), (int)p.getY());
 }
 public boolean spaceEmpty(int row, int col){
   if(row>NUM_ROWS-1 || col>NUM_COLS-1 || row<0 || col<0)
     return false;
   return board[row][col]==null;
 }
 public boolean spaceEmpty(Point p){
   return spaceEmpty((int)p.getX(), (int)p.getY());
 }
 public Point getOffset(){
   return offset;
 }
 public int getIndex(){
   return listIndex;
 }
 public void stop(){
   waiting=true;
 }
 private boolean topRowClear(){
   for(int i=0; i<NUM_COLS; ++i){
     if(board[0][i]!=null || (board[1][i]!=null && board[1][i].getPixelsY()%BLOCK_SIZE!=0))
       return false;
   }
   return true;
 }
 private ArrayList<Block> getNeighborsInternal(int row, int col){
   ArrayList<Block> neighbors=new ArrayList<Block>(4);
   if(row<NUM_ROWS-1 && !spaceEmpty(row+1, col))
     neighbors.add(board[row+1][col]);
   if(col<NUM_COLS-1 && !spaceEmpty(row, col+1))
     neighbors.add(board[row][col+1]);
   if(row>0 && !spaceEmpty(row-1, col))
     neighbors.add(board[row-1][col]);
   if(col>0 && !spaceEmpty(row, col-1))
     neighbors.add(board[row][col-1]);
   return neighbors;
 }
 public Block[] getNeighbors(int row, int col){
   return toArray(getNeighborsInternal(row, col));
 }
 public Block[] getNeighborsSameColor(int row, int col){
   if(spaceEmpty(row, col))
     //instead of throwing a null pointer, which previously caused errors due to the interruption, it simply fails quietly
     return new Block[] {};
   ArrayList<Block> neighbors=getNeighborsInternal(row, col);
   Color c=board[row][col].getColor();
   for(Iterator i=neighbors.iterator(); i.hasNext();){
     if(!(((Block) i.next()).getColor().equals(c)))
       i.remove();
   }
   return toArray(neighbors);
 }
 public Block[] getLegalDestroyTargets(int row, int col){
   if(spaceEmpty(row, col))
     return new Block[] {};
   ArrayList<Block> neighbors=getNeighborsInternal(row, col);
   Block b=board[row][col], temp;
   for(Iterator i=neighbors.iterator(); i.hasNext();){
     temp=(Block) i.next();
     if(!(temp.getColor().equals(b.getColor()) || temp instanceof CounterGem))
       i.remove();
   }
   return toArray(neighbors);
 }
 public Block[] removeCounterGems(Block[] blocks){
   ArrayList<Block> blockList=new ArrayList<Block>(blocks.length);
   for(Block b : blocks){
     if(!(b instanceof CounterGem))
       blockList.add(b);
   }
   return toArray(blockList);
 }
 //mutators
 public void addIndex(int toAdd){
   listIndex+=toAdd;
 }
 public void addNumBlocks(double toAdd){
   if(duringDrop)
     tempNumBlocks+=toAdd;
   else{
     text.show();
     numBlocks+=toAdd;
   }
 }
 public void setSpace(int row, int col, Block b){
   if(row>=0 && col>=0 && row<NUM_ROWS && col<NUM_COLS)
     board[row][col]=b;
 }
 public void setSpace(Point p, Block b){
   setSpace((int)p.getX(),(int)p.getY(),b);
 }
 private void addNextPair(){
   currentPair=list.getNext(this);
   if(currentPair.getPivot() instanceof Diamond)
     curDiamond=(Diamond)currentPair.getPivot();
   else if(currentPair.getOther() instanceof Diamond)
     curDiamond=(Diamond)currentPair.getOther();
   OI.nextPreviewPair(this);
 }
 private void decrementAll(){
   Block temp;
   for(int row=0; row<NUM_ROWS; ++row){
     for(int col=0; col<NUM_COLS; ++col){
       temp=board[row][col];
       if(temp instanceof CounterGem)
         ((CounterGem)temp).decrement();
     }
   }
 }
 public void createBigGems(){
   for(int row=0; row<NUM_ROWS; ++row){
     for(int col=0; col<NUM_COLS; ++col){
       if(board[row][col] instanceof Gem){
         if(removeCounterGems(getNeighborsSameColor(row, col)).length>0)
           ((Gem)board[row][col]).setSize("large");
         else
           ((Gem)board[row][col]).setSize("small");
       }
     }
   }
 }
 public void toggleFilter(String prefix){
   filter.disableAllTypes();
   filter.enableType(prefix);
   currentFilter=prefix;
 }
 public BlinkText getBlinkText(){
   return text;
 }
 private boolean moveDownPair(){
   Block p=currentPair.getPivot(), o=currentPair.getOther();
   board[p.getRow()][p.getCol()]=board[o.getRow()][o.getCol()]=null;
   boolean free=false;
   if((p.getPixelsY()%BLOCK_SIZE!=0 || spaceEmpty(p.getRow()+1, p.getCol())) && (o.getPixelsY()%BLOCK_SIZE!=0 || spaceEmpty(o.getRow()+1, o.getCol()))){
     p.shiftPixels(0, PIXELS_DOWN);
     o.shiftPixels(0, PIXELS_DOWN);
   }
   board[p.getRow()][p.getCol()]=board[o.getRow()][o.getCol()]=null;
   if((p.getPixelsY()%BLOCK_SIZE!=0 || spaceEmpty(p.getRow()+1, p.getCol())) && (o.getPixelsY()%BLOCK_SIZE!=0 || spaceEmpty(o.getRow()+1, o.getCol())))
     free=true;
   board[o.getRow()][o.getCol()]=o;
   board[p.getRow()][p.getCol()]=p;
   return free;
 }
 private boolean moveDown(){
   boolean free=false;
   Block temp;
   for(int row=NUM_ROWS-1; row>=0; --row){
     for(int col=0; col<NUM_COLS; ++col){
       temp=board[row][col];
       if(temp==null)
         continue;
       if(temp.getPixelsY()%BLOCK_SIZE!=0 || spaceEmpty(temp.getRow()+1, temp.getCol())){
         temp.shiftPixels(0, PIXELS_DOWN);
       }
       if(free)
         continue;
       if(temp.getPixelsY()%BLOCK_SIZE!=0 || spaceEmpty(temp.getRow()+1, temp.getCol()))
         //same test: this checks if this block would change with another run of moveDown(), meaning it is freefloating
         free=true;
     }
   }
   return free;
 }
 private void removeBlock(Block b){
   setSpace(b.getRow(), b.getCol(), null);
   OI.removeBlock(b);
 }
 public void destroyBlock(Block b, double factor, int points){
   //called by the block in question so the board knows to remove it
   if(numBlocks>=1){
     numBlocks-=BLOCK_COUNTER_NUM*factor;
     if(numBlocks<1)
       text.hide();
   }
   else
     otherBoard.addNumBlocks(BLOCK_DROP_NUM*factor);
   score+=points;
   removeBlock(b);
 }
 public void destroyBlock(Block b, double factor){
   destroyBlock(b, factor, BLOCK_VALUE);
 }
 private boolean destroyBlocks(){
   //to be run ONLY after all blocks have settled (moveDown() returns false)
   boolean destroyed=false;
   Block temp;
   if(curDiamond!=null){
     curDiamond.destroyNeighbors();
     destroyed=true;
     curDiamond=null;
   }
   for(int row=0; row<NUM_ROWS; ++row){
     for(int col=0; col<NUM_COLS; ++col){
       temp=board[row][col];
       if(temp instanceof CrashGem && ((CrashGem) temp).destroyNeighbors())
           destroyed=true;
     }
   }
   if(destroyed && otherBoard.getNumBlocks()>1){
     otherBoard.getBlinkText().findWarningLevel(otherBoard);
     otherBoard.getBlinkText().show();
   }
   return destroyed;
 }
 private boolean dropBlocks(){
   CounterGem temp;
   if(numBlocks>=1){
     if(topRowClear()){
       Random r=new Random();
       int[] order=Board.randSeq(NUM_COLS);
       for(int i=0; i<NUM_COLS && numBlocks-->=1;++i){
         temp=new CounterGem(order[i]*BLOCK_SIZE, 0, BlockPair.allColors[r.nextInt(4)], CounterGem.DEFAULT_VALUE, this);
         board[0][order[i]]=temp;
         OI.addBlock(temp);
       }
       if(++numBlocks<1){//because the final test decrements it once more than the number of blocks dropped
         text.hide();
         numBlocks=0;
       }
     }
     //blocks either dropped or were waiting to drop, meaning the creation of the blocks isnt done yet and moveDown should be called more
     return true;
   }
   else
     return false;
 }
}
                                  