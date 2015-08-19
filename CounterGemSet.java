import java.awt.Color;
import java.util.Random;

public class CounterGemSet{
  private CounterGem[][] counter;
  
  public CounterGemSet(CounterGem[][] blocks) throws ArrayIndexOutOfBoundsException{
    if(blocks.length!=4 || blocks[0].length!=6)
      throw new ArrayIndexOutOfBoundsException("Array must be 4 by 6.");
    counter = blocks;
  }
  public CounterGemSet(String[] colors) throws ArrayIndexOutOfBoundsException{
    if(colors.length!=24)
      throw new ArrayIndexOutOfBoundsException("Array must be exactly 24 elements.");
    CounterGem[][] blocks = new CounterGem[4][6];
    for (int i = 0 ; i<4 ; i++){
      for (int j = 0 ; j<6 ; j++)
        blocks[i][j] = new CounterGem(0, 0, CounterGemSet.parseColor(colors[i*6+j]), 5);
    }
    counter=blocks;
  }
  public static Color parseColor(String color){
    if(color.equals("red"))
      return Color.red;
    if(color.equals("blue"))
      return Color.blue;
    if(color.equals("green"))
      return Color.green;
    if(color.equals("yellow"))
      return Color.yellow;
    Random r=new Random();
    return BlockPair.allColors[r.nextInt(4)];
  }
  public static String parseColor(Color c){
    if(c.equals(Color.red))
      return "red";
    if(c.equals(Color.green))
      return "green";
    if(c.equals(Color.blue))
      return "blue";
    if(c.equals(Color.yellow))
      return "yellow";
    if(c.equals(Color.white))
      return "white";
    return null;
  }
}