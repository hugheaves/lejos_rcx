package josx.rcxcomm;

import josx.platform.rcx.*;

public class LLC {
  public static native void init();
  public static native int read();
  public static native void write(byte b);
  public static native boolean dataAvailable();

  public static void send(byte b) {
    LLC.write(b);
    try {Thread.sleep(10);} catch (InterruptedException ie) {}
  }

  public static void send(byte [] buf) {
    for(int i=0;i<buf.length;i++) LLC.send(buf[i]);
  }

  public static int receive() {
    while (!LLC.dataAvailable()) Thread.yield();
    return LLC.read();
  }
}

