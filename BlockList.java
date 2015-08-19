public class BlockList{
  private Board[] boards;
  private int gemCounter;
  private BlockNode first, last;
  public BlockList(Board one, Board two){
    boards=new Board[2];
    boards[0]=one;
    boards[1]=two;
    one.setList(this);
    two.setList(this);
    one.setOtherBoard(two);
    two.setOtherBoard(one);
    first=new BlockNode(new BlockPair(this));
    last=new BlockNode(new BlockPair(this));
    first.setNext(last);
    last.setPrev(first);
    gemCounter=1;
  }
  //accessors
  public int getGemCounter(){
    return gemCounter;
  }
  public BlockPair getNext(Board requester){
    if(requester.getIndex()==0)
      return getFirst(requester);
    return getLast(requester);
  }
  public BlockPair peekNext(Board requester){
    if(requester.getIndex()==0)
      return peekFirst(requester);
    return peekLast(requester);
  }
  private BlockPair getFirst(Board requester){
    requester.addIndex(1);
    if(boards[0].getIndex()>0 && boards[1].getIndex()>0){
      boards[0].addIndex(-1);
      boards[1].addIndex(-1);
      if(boards[0].getIndex()==boards[1].getIndex())
        //this should only happen only when both are zero, meaning that there needs to be a new last node
        addLast(new BlockPair(this));
      return removeFirst().copy(requester);
    }
    return first.getValue().copy(requester);
  }
  private BlockPair getLast(Board requester){
    requester.addIndex(1);
    addLast(new BlockPair(this));
    return last.getPrev().getValue().copy(requester);
  }
  private BlockPair peekLast(Board requester){
    return last.getValue().copyNoBoard();
  }
  private BlockPair peekFirst(Board requester){
    return first.getValue().copyNoBoard();
  }
  //mutators
  public void addGemCounter(){
    if(++gemCounter==25)
      gemCounter=0;
  }
  private BlockPair removeFirst(){
    BlockPair b=first.getValue();
    first.getNext().setPrev(null);
    first=first.getNext();
    return b;
  }
  private void addLast(BlockPair b){
    last.setNext(new BlockNode(b));
    last.getNext().setPrev(last);
    last=last.getNext();
  }
}

class BlockNode{
  private BlockPair value;
  private BlockNode previous, next;
  public BlockNode(BlockPair b){
    value=b;
  }
  //accessors
  public BlockNode getNext(){
    return next;
  }
  public BlockNode getPrev(){
    return previous;
  }
  public BlockPair getValue(){
    return value;
  }
  //mutators
  public void setNext(BlockNode b){
    next=b;
  }
  public void setPrev(BlockNode b){
    previous=b;
  }
  public void setValue(BlockPair b){
    value=b;
  }
}