import josx.platform.rcx.*;
import josx.rcxcomm.*;

public class TestLLC {
  static byte [] msg = {(byte) 0xff, (byte) 0x00, (byte) 0x55};
 
  public static void main(String [] args) throws InterruptedException {
    TextLCD.print("start");
    LLC.init();
    Button.RUN.waitForPressAndRelease();
    //LLC.send(msg);
    // Button.RUN.waitForPressAndRelease();
    while (true) {
      LCD.showNumber(LLC.receive());
      try {Thread.sleep(1000);} catch (InterruptedException ie) {}
    }
  }
}

