import josx.rcxcomm.*;

public class Read {
  public static void main(String [] args) {
    byte [] b = new byte[10];
    Tower t = new Tower();
    t.open();
    int l;

    while (true) {
      l =t.read(b);
      if (l > 0) {
        System.out.println("Received " + l + " bytes :");
        for(int i=0;i<l;i++) System.out.println("" + b[i]);
      }
    }
  }
}


