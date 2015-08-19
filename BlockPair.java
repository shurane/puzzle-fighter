import java.util.Random;
import java.awt.Color;
import java.awt.Point;

public class BlockPair{
 public static final int CRASH_CHANCE=30, bs=Board.BLOCK_SIZE;
 public static final Color[] allColors={Color.blue, Color.green, Color.yellow, Color.red};
 private Block pivot, other;
 private Board container;
 public BlockPair(BlockList list){
   Random rand=new Random();
   int chance=rand.nextInt(100);
   if(list.getGemCounter()==24)
     pivot=new Diamond(0, 0);
   else if(chance<=CRASH_CHANCE)
     pivot=new CrashGem(0, 0, allColors[rand.nextInt(4)]);
   else
     pivot=new Gem(0, 0, allColors[rand.nextInt(4)]);
   do{
     chance=rand.nextInt(100);
     if(chance<=CRASH_CHANCE)
       other=new CrashGem(0, 0, allColors[rand.nextInt(4)]);
     else
       other=new Gem(0, 0, allColors[rand.nextInt(4)]);
   }
   while(other instanceof CrashGem && pivot instanceof CrashGem && other.getColor().equals(pivot.getColor()));
   list.addGemCounter();
   container=null;
 }
 public BlockPair(Block p, Block o, Board board){
   //for use with copy()
   pivot=p;
   other=o;
   container=board;
 }
 //accessors
 public BlockPair copy(Board dest){
   Block p=pivot.copyNoBoard(), o=other.copyNoBoard();
   int x=3*bs+(int)dest.getOffset().getX(), y=(int)dest.getOffset().getY();
   p.movePixelsNoBoard(x, bs+y);
   o.movePixelsNoBoard(x, y);
   p.setBoard(dest);
   o.setBoard(dest);
   return new BlockPair(p, o, dest);
 }
 public BlockPair copyNoBoard(){
   return new BlockPair(pivot.copyNoBoard(), other.copyNoBoard(), null);
 }
 //mutators
 public void setBoard(Board board){
   container=board;
   Board.OI.getContentPane().add(pivot);
   Board.OI.getContentPane().add(other);
 }
 public Block getPivot(){
   return pivot;
 }
 public Block getOther(){
   return other;
 }
 public void shift(int dir){
   container.setSpace(pivot.getRow(), pivot.getCol(), null);
   container.setSpace(other.getRow(), other.getCol(), null);
   if(container.spaceEmpty(pivot.getRow(), pivot.getCol()+dir) && container.spaceEmpty(other.getRow(), other.getCol()+dir)){
     pivot.shiftPixels(bs*dir, 0);
     other.shiftPixels(bs*dir, 0);
   }
   else{
     container.setSpace(pivot.getRow(), pivot.getCol(), pivot);
     container.setSpace(other.getRow(), other.getCol(), other);
   }
 }
 public void rotate(boolean ccw){
   //rotates counter clockwise if ccw, otherwise clockwise
   //handles special cases (along walls, between blocks, etc)
   int row=pivot.getRow(), col=pivot.getCol();
   Point pivotPoint=new Point(row, col);
   RotatePoints r=new RotatePoints(row, col);
   row=other.getRow();
   col=other.getCol();
   Point otherPoint=new Point(row, col);
   Point temp, nextCW=r.nextCW(otherPoint), nextCCW=r.nextCCW(otherPoint);
   if(!ccw){
     if((int)nextCW.getX()==Board.NUM_ROWS)
       //stops rotating into the ground, this would otherwise force the pivots up and allow for indefinte suspension
       return;
     if(container.spaceEmpty(nextCW)){
       //if the rotation is legal, then continue as expected
       temp=RotatePoints.differenceBetween(otherPoint, nextCW);
       other.shiftPixels((int)temp.getY()*bs, (int)temp.getX()*bs);
       return;
     }
     
     if(container.spaceEmpty(nextCCW)){
       //if not, then try to force pivot out of the way and move other down
       other.movePixels(pivot.getPixelsX(), pivot.getPixelsY());
       temp=RotatePoints.differenceBetween(pivotPoint, nextCCW);
       pivot.shiftPixels((int)temp.getY()*bs, 0);
       return;
     }
   }
   else{
     //see corresponding notes for clockwise rotation
     if((int)nextCCW.getX()==Board.NUM_ROWS)
       return;
     container.setSpace(pivotPoint, null);
     container.setSpace(otherPoint, null);
     if(container.spaceEmpty(nextCCW)){
       temp=RotatePoints.differenceBetween(otherPoint, nextCCW);
       other.shiftPixels((int)temp.getY()*bs, (int)temp.getX()*bs);
       return;
     }
     if(container.spaceEmpty(nextCW)){
       other.movePixels(pivot.getPixelsX(), pivot.getPixelsY());
       temp=RotatePoints.differenceBetween(pivotPoint, nextCW);
       pivot.shiftPixels((int)temp.getY()*bs, 0);
       return;
     }
   }
   //if it cant rotate CW or CCW, then just switch the blocks
   temp=RotatePoints.differenceBetween(pivotPoint, otherPoint);
   other.shiftPixels(0, -bs*(int)temp.getX());
   pivot.shiftPixels(0, bs*(int)temp.getX());
 }
}

class RotatePoints{
  private Point[] points;
  public RotatePoints(int rowCenter, int colCenter){
    points=new Point[4];
    points[0]=new Point(rowCenter+1, colCenter);//north
    points[1]=new Point(rowCenter, colCenter+1);//east
    points[2]=new Point(rowCenter-1, colCenter);//south
    points[3]=new Point(rowCenter, colCenter-1);//west
  }
  public static Point differenceBetween(Point first, Point second){
    return new Point((int)(second.getX()-first.getX()), (int)(second.getY()-first.getY()));
  }
  public Point nextCCW(Point p){
    for(int i=0; i<4; ++i){
      if(points[i].equals(p)){
        if(i==3)
          return points[0];
        return points[i+1];
      }
    }
    return null;
  }
  public Point nextCW(Point p){
    for(int i=0; i<4; ++i){
      if(points[i].equals(p)){
        if(i==0)
          return points[3];
        return points[i-1];
      }
    }
    return null;
  }
}