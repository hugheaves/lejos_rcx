import java.io.*;
import josx.rcxcomm.*;

/**
 * This program uses the josx.rcxcomm package to
 * communicate over the Lego IR link with a
 * corresponding program, SensorReader. 
 * It sends the Sensor ID, and reads back the sensor value 
 * as a short.
 */
public class ReadSensor {

  public static void main(String[] args) {

    try {

      RCXPort port = new RCXPort();

      InputStream is = port.getInputStream();
      OutputStream os = port.getOutputStream();
      DataInputStream dis = new DataInputStream(is);
      DataOutputStream dos = new DataOutputStream(os);

      System.out.println("Reading Light Sensor");

      dos.writeByte(1);
      dos.flush();

      int n = dis.readShort();

      System.out.println("Received " + n);

    }
    catch (Exception e) {
      System.out.println("Exception " + e.getMessage());
    }
  }
}