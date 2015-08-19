import java.awt.Color;

public class CounterGem extends Gem{
 public static final int DEFAULT_VALUE=5;
 private int value;
 public CounterGem(int pxX, int pxY, Color color, int startValue){
   super(pxX, pxY, color);
   value=startValue;
   blockImage=Board.OI.imageMap.get(toString());
   //this line is here (even though it appears in the Block constructor) because in Block(), 
   //value=0 and there is no image for that
 }
 public CounterGem(int pxX, int pxY, Color color, int startValue, Board board){
   super(pxX, pxY, color, board);
   value=startValue;
   blockImage=Board.OI.imageMap.get(toString());
 }
 //accessors
 public int getValue(){
   return value;
 }
 public CounterGem copy(){
   return copy(container);
 }
 public CounterGem copy(Board destination){
   return new CounterGem(getRow(), getCol(), blockColor, value, destination);
 }
 public CounterGem copyNoBoard(Board destination){//this will never happen
   return null;
 }
 public String toString(){
   return "CounterGem-"+CounterGemSet.parseColor(blockColor)+"-"+value;
 }
 //mutators
 public void decrement(){
   //decrements the value, if it expires this is replaced with a regular block of the same color
   if(--value==0)
     container.setSpace(getRow(), getCol(), new Gem(getPixelsX(), getPixelsY(), blockColor, container));
   blockImage=Board.OI.imageMap.get(toString());
 }
 public void destroyNeighborsInternal(){
   //these can only get destroyed if they are directly adjacent to a regular block
   //and do not continue the chain reaction
   container.destroyBlock(this, Board.COUNTERGEM_FACTOR);
 }
}