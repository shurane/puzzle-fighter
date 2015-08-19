import java.awt.Color;
import java.awt.Point;

public class CrashGem extends Block{
  public CrashGem(int pxX, int pxY, Color color){
    super(pxX, pxY, color);
  }
  public CrashGem(int pxX, int pxY, Color color, Board board){
    super(pxX, pxY, color, board);
  }
  //accessors
  public CrashGem copy(Board destination){
    return new CrashGem(getPixelsX(), getPixelsY(), blockColor, destination);
  }
  public CrashGem copyNoBoard(){
    return new CrashGem(0, 0, blockColor);
  }
  public String toString(){
    return "CrashGem-"+CounterGemSet.parseColor(blockColor);
  }
  //mutators
  public boolean destroyNeighbors(){
    Block[] nextToNoNumbers=container.removeCounterGems(container.getNeighborsSameColor(getRow(),getCol()));
    if(nextToNoNumbers.length>0){
      Block[] nextTo=container.getLegalDestroyTargets(getRow(), getCol());
      container.destroyBlock(this, Board.DEFAULT_FACTOR);
      for(Block b : nextTo)
        b.destroyNeighborsInternal();
      return true;
    }
    return false;
  }
  public void destroyNeighborsInternal(){
    Block[] nextTo=container.getLegalDestroyTargets(getRow(), getCol());
    container.destroyBlock(this, Board.DEFAULT_FACTOR);
    for(Block b : nextTo)
      b.destroyNeighborsInternal();
  }
}