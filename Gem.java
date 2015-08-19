import java.awt.Color;
import java.awt.Point;

public class Gem extends Block{
  String size;
  public Gem(int pxX, int pxY, Color color){
    super(pxX, pxY, color);
    size="small";
    blockImage=Board.OI.imageMap.get(toString());
    setIcon(blockImage);
  }
  public Gem(int pxX, int pxY, Color color, Board board){
    super(pxX, pxY, color, board);
    size="small";
    blockImage=Board.OI.imageMap.get(toString());
    setIcon(blockImage);
  }
  //accessors
  public Gem copy(Board destination){
    return new Gem(getPixelsX(), getPixelsY(), blockColor, destination);
  }
  public Gem copyNoBoard(){
    return new Gem(0, 0, blockColor);
  }
  public String toString(){
    return "Gem-"+CounterGemSet.parseColor(blockColor)+"-"+size;
  }
  //mutators
  public void setSize(String s){
    size=s;
    blockImage=Board.OI.imageMap.get(toString());
    setIcon(blockImage);
  }
  public void destroyNeighborsInternal(){
    Block[] nextTo=container.getLegalDestroyTargets(getRow(), getCol());
    container.destroyBlock(this, Board.DEFAULT_FACTOR);
    for(Block b : nextTo)
      b.destroyNeighborsInternal();
  }
}