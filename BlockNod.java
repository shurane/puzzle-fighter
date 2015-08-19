public class BlockNod{
  private BlockPair value;
  private BlockNod previous, next;
  public BlockNod(BlockPair b){
    value=b;
  }
  //accessors
  public BlockNod getNext(){
    return next;
  }
  public BlockNod getPrev(){
    return previous;
  }
  public BlockPair getValue(){
    return value;
  }
  //mutators
  public void setNext(BlockNod b){
    next=b;
  }
  public void setPrev(BlockNod b){
    previous=b;
  }
  public void setValue(BlockPair b){
    value=b;
  }
}