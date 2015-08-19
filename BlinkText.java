import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Date;
import java.io.File;
import javax.imageio.*;
import javax.management.timer.Timer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.NotificationFilterSupport;

public class BlinkText implements NotificationListener{
  private static final int BLINK_DELAY=500, WARNING_AMOUNT=12, DANGER_AMOUNT=30;
  private String type;
  private Point location;
  private boolean showing;
  private Timer t;
  private HashMap<String,Image> textImageMap;
  private int which;
  public BlinkText(Point where){
    which=1;
    location=where;
    setupTextImageMap();
    t=new Timer();
    Date d=new Date();
    t.addNotification(null, "str2", "str3", d, BLINK_DELAY);
    t.addNotificationListener(this, null, null);
    t.start();
  }
  public void setType(String t){
    type=t;
  }
  public void show(){
    showing=true;
    which=1;
  }
  public void hide(){
    showing=false;
    ((Graphics2D) Board.OI.getGraphics()).clearRect((int)location.getX(), (int)location.getY(), Board.BLOCK_SIZE*3, Board.BLOCK_SIZE);
    which=1;
  }
  public boolean getVisibility(){
    return showing;
  }
  public void setSpace(Point where){
    location=where;
  }
  public void findWarningLevel(Board b){
    double tempNum=b.getNumBlocks();
    if(tempNum>=DANGER_AMOUNT)
      type="danger";
    else if(tempNum>=WARNING_AMOUNT)
      type="warning";
    else if(tempNum>1)
      type="caution";
  }
  private void setupTextImageMap(){
    textImageMap=new HashMap<String,Image>();
    File f;
    String tempName;
    BufferedImage img;
    String[] imageNames={"caution", "warning", "danger"};
    for(int i=0; i<3; ++i){
      for(int j=1; j<3; ++j){
        tempName=imageNames[i]+j;
        f=new File(OverallInterface.imageDir+tempName+".png");
        try{
          img=ImageIO.read(f);
          textImageMap.put(tempName, img);
        }
        catch (Exception e){}
      }
    }
  }
  public void handleNotification(Notification n, Object o){
    if(!showing)
      return;
    ((Graphics2D) Board.OI.getGraphics()).drawImage(textImageMap.get(type+which),
                                                    (int)location.getX(), (int)location.getY(), Board.OI);
    which=3-which;//toggle which from 1 to 2 or vice versa
  }
}
  