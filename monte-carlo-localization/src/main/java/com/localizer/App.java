package com.localizer;

import java.io.*;
import java.util.*;
import java.lang.*;
import io.scanse.sweep.*;

public class App {
  public static String getUsbPortName() {
    // TODO: figure out how to use properties
    // get properties
    Properties prop = new Properties();
    // ClassLoader loader = Thread.currentThread().getContextClassLoader();
    // File configFile = new File(myProp.properties);
    // InputStream stream = loader.getResourceAsStream("/src/resources/config.properties");
    try {
      FileInputStream stream = new FileInputStream("src/resources/config.properties");
      prop.load(stream);
      return prop.getProperty("usbPortName");
    } catch (Exception ex) {
      System.out.println(ex.toString());
    }
    return("error in getUsbPortName");
  }
  public static void main(String[] args) {
    try (SweepDevice lidar = new SweepDevice(getUsbPortName())) { 
      int speed = lidar.getMotorSpeed();
      int rate = lidar.getSampleRate();

      System.out.println(String.format("Motor Speed: %s", speed));
      System.out.println(String.format("Sample Rate: %s", rate));

      lidar.startScanning();
    }
    System.out.print("USB port name: ");
    System.out.println(getUsbPortName());
    while(true);
  }
}