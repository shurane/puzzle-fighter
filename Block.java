import java.awt.Color;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

abstract class Block extends JLabel{
  protected int pixelsX, pixelsY;
  protected Board container;
  protected Color blockColor;
  protected ImageIcon blockImage;
  //accessors
  public Block(int pxX, int pxY, Color col){
    pixelsX=pxX;
    pixelsY=pxY;
    blockColor=col;
    blockImage=Board.OI.imageMap.get(toString());
    setIcon(blockImage);
    container=null;
  }
  public Block(int pxX, int pxY, Color color, Board board){
    pixelsX=pxX+(int)board.getOffset().getX();
    pixelsY=pxY+(int)board.getOffset().getY();
    blockColor=color;
    blockImage=Board.OI.imageMap.get(toString());
    setIcon(blockImage);
    container=board;
    board.setSpace(getRow(), getCol(), this);
    Board.OI.addBlock(this);
  }
  public int getPixelsX(){
    if(container==null)
      return pixelsX;
    return pixelsX-(int)container.getOffset().getX();
  }
  public int getPixelsY(){
    if(container==null)
      return pixelsY;
    return pixelsY-(int)container.getOffset().getY();
  }
  public int getRow(){
    return Board.pixelsToCoord(pixelsY, (int)container.getOffset().getY());
  }
  public int getCol(){
    return Board.pixelsToCoord(pixelsX, (int)container.getOffset().getX());
  }
  public Color getColor(){
    return blockColor;
  }
  public ImageIcon getImage(){
    return blockImage;
  }
  public Block copy(){
    return copy(container);
  }
  abstract Block copy(Board destination);
  abstract Block copyNoBoard();
  public abstract String toString();
  //mutators
  public void moveRC(int newRow, int newCol){
    container.setSpace(getRow(), getCol(), null);
    pixelsX=newRow*Board.BLOCK_SIZE+(int)container.getOffset().getX();
    pixelsY=newCol*Board.BLOCK_SIZE+(int)container.getOffset().getY();
    setLocation(pixelsX, pixelsY);
    container.setSpace(newRow, newCol, this);
  }
  public void movePixels(int newX, int newY){
    container.setSpace(getRow(), getCol(), null);
    pixelsX=newX+(int)container.getOffset().getX();
    pixelsY=newY+(int)container.getOffset().getY();
    setLocation(pixelsX, pixelsY);
    container.setSpace(getRow(), getCol(), this);
  }
  public void movePixelsNoBoard(int newX, int newY){
    pixelsX=newX;
    pixelsY=newY;
  }
  public void shift(int rows, int cols){
    moveRC(getRow()+rows, getCol()+cols);
  }
  public void shiftPixels(int x, int y){
    movePixels(getPixelsX()+x, getPixelsY()+y);
  }
  public void setColor(Color newColor){
    blockColor=newColor;
  }
  public void setBoard(Board b){
    container=b;
    b.setSpace(getRow(), getCol(), this);
    Board.OI.addBlock(this);
  }
  abstract void destroyNeighborsInternal();
}