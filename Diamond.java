import java.awt.Point;
import java.awt.Color;

public class Diamond extends Block{
  public Diamond(int pxX, int pxY){
    super(pxX, pxY, Color.white);
  }
  public Diamond(int pxX, int pxY, Board board){
    super(pxX, pxY, Color.white, board);
  }
  //accessors
  public Diamond copy(Board destination){
    return new Diamond(getPixelsX(), getPixelsY(), destination);
  }
  public Diamond copyNoBoard(){
    return new Diamond(0, 0);
  }
  public String toString(){
    return "Diamond";
  }
  //mutators
  public boolean destroyNeighbors(){
    //this block destroys ALL blocks of the color matching the one below this block
    if(getRow()==Board.NUM_ROWS-1)
      //if there are none below, give a bonus for being able to waste a valuable block
      container.destroyBlock(this, 0, 5000);
    else{
      Color colorToDestroy=container.getSpace(getRow()+1, getCol()).getColor();
      for(int row=0; row<Board.NUM_ROWS; ++row){
        for(int col=0; col<Board.NUM_COLS; ++col){
          if(!container.spaceEmpty(row, col) && container.getSpace(row, col).getColor().equals(colorToDestroy))
            container.destroyBlock(container.getSpace(row, col), Board.DIAMOND_FACTOR);
        }
      }
    }
    container.destroyBlock(this, Board.DIAMOND_FACTOR);
    return true; //this type of block is always destroyed
  }
  public void destroyNeighborsInternal(){}//no use for this
}