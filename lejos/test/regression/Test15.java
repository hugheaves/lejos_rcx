
// Sensor test

import josx.platform.rcx.*;

public class Test15
{ 
  public static void main (String[] argv)
  {
    //for (int i = 0; i < 200; i++)
    //{
      //Sensor.S2.activate();
      int pValue = Sensor.readSensorValue (1, 1);
      LCD.showNumber (pValue);
      //LCD.showProgramNumber (i % 10);
      //for (int k = 0; k < 500; k++) { }
    //}
  }
}

