import josx.rcxcomm.*;

public class Send {

  static byte [] test = new byte[1];
 
  public static void main(String [] args) {
    Tower t = new Tower();
    t.open();
    for(int i = 0;i<32;i++) {
      test[0] = (byte) i;
      System.out.println("Sending: " + i);
      t.write(test,1);
      try {Thread.sleep(1000);} catch (InterruptedException ie) {}
    }
  }
}

