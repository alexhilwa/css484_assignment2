/*
 * Project 1
 */

import java.awt.image.BufferedImage;
import java.lang.Object.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.lang.Math;


public class readImage
{
  int imageCount = 1;
  double intensityBins [] = new double [26];
  double intensityMatrix [][] = new double[100][26];
  double colorCodeBins [] = new double [64];
  double colorCodeMatrix [][] = new double[100][64];

  /*Each image is retrieved from the file.  The height and width are found for the image and the getIntensity and
   * getColorCode methods are called.
  */
  public readImage()
  {
    while(imageCount < 101){
      try
      {
        // the line that reads the image file
        BufferedImage image = ImageIO.read(new File("images/" + imageCount + ".jpg"));
        int width = image.getWidth();
        int height = image.getHeight();

        //getIntensity fills the intensityBins array, which needs to be emptied into the intensityMatrix
        getIntensity(image, height, width);
        intensityMatrix[imageCount - 1] = intensityBins;
        intensityBins = new double[26];
        
        //similarly, getColorCode fills the colorCodeBins array, which is emptied into the colorCodeMatrix
        getColorCode(image, height, width);
        colorCodeMatrix[imageCount - 1] = colorCodeBins;
        colorCodeBins = new double [64];

        imageCount++;
      } 
      catch (IOException e)
      {
        System.out.println("Error occurred when reading the file.");
      } //*/
    }
    
    System.out.println("Matrices constructed.");

    writeIntensity();
    writeColorCode();
    
  }
  
  //intensity method 
  public void getIntensity(BufferedImage image, int height, int width){
    for(int i = 0; i < width; i++)
    {
      for(int j = 0; j < height; j++)
      {
        intensityBins[0]++;
        Color colorAtPixel = new Color(image.getRGB(i, j));
        int intensity = calculateIntensity(colorAtPixel);
        //System.out.println(intensity);
        insertIntensityToBin(intensity);
      }
    }

  }
  
  //color code method
  public void getColorCode(BufferedImage image, int height, int width){
    for(int i = 0; i < width; i++)
    {
      for(int j = 0; j < height; j++)
      {
        Color colorAtPixel = new Color(image.getRGB(i, j));
        int colorCode = calculateColorCode(colorAtPixel);       
        colorCodeBins[colorCode]++;
        
      }
    }
  }
  
  
  ///////////////////////////////////////////////
  //add other functions you think are necessary//
  ///////////////////////////////////////////////
  
  //This method calculates the intensity value of a color
  //From what I understand, in order to do that, I need to normalize the RGB values and add them together?
  //Actually, since the possible values of the intensity bins range from 0 to 25, I just need to add them together
  //and divide by ((255 * 3) / x )= 25, x = 30.6
  public int calculateIntensity(Color color)
  {
    double intensity = 0.299 * ((double) color.getRed()) + 0.587 * ((double) color.getGreen()) + 0.114 * ((double) color.getBlue());
    return (int)intensity;
  }

  //This method increments the correpsonding bin for an intensity score.
  public boolean insertIntensityToBin(int intensity)
  {
    if(intensity > 255)
    {
      return false;
    }
    if(intensity >= 250)
    {
      intensityBins[25]++;
      return true;
    }
    intensityBins[(intensity / 10) + 1]++;
    return true;
  }

  //This method calculates the color code of a Color object.
  public int calculateColorCode(Color color)//Color color)
  {
    /* */
    int redCode, greenCode, blueCode;
    redCode = color.getRed();
    greenCode = color.getGreen();
    blueCode = color.getBlue();
    String stringRedCode = intToStringBinary(redCode);
    String stringGreenCode = intToStringBinary(greenCode);
    String stringBlueCode = intToStringBinary(blueCode);
    String result = stringRedCode.substring(0, 2) + stringGreenCode.substring(0, 2) + stringBlueCode.substring(0, 2);
    return stringBinaryToInt(result);
  }

  //Convert a string binary number into an integer
  //I think it works with binaries of any size
  private int stringBinaryToInt(String value)
  {
    int result = 0;
    for(int i = value.length(); i > 0; i--)
    {
      double a = (double) Integer.parseInt(value.substring(i - 1, i));
      double b = (double) (value.length() - i);
      result += a * (int) Math.pow(2, b);
    }
    return result;
  }

  //Convert an integer into a string binary
  //The result will always have 8 digits
  private String intToStringBinary(int value)
  {
    //if the value is outside the bounds of a 6 digit binary, reduce it to be such
    if(value >= 256)
    {
      value = value % 256;
    }
    String result =  Integer.toBinaryString(value);
    while(result.length() < 8)
    {
      result = "0" + result;
    }
    return result;
  }

  //construct a string representation of a double array except the first element
  private String getArrayAsStringWithoutFirstElement(double[] array)
  {
    String result = "";
    if(array.length == 0)
    {
      return result; //empty array
    }
    result = result + (int)array[1];
    for(int i = 2; i < array.length; i++)
    {
      result = result + " ";
      result = result + (int)array[i];
    }
    return result;
  }

  //construct a string representation of a double array
  private String getArrayAsString(double[] array)
  {
    String result = "";
    if(array.length == 0)
    {
      return result; //empty array
    }
    result = result + (int)array[0];
    for(int i = 1; i < array.length; i++)
    {
      result = result + " ";
      result = result + (int)array[i];
    }
    return result;
  }

  //This method writes the contents of the colorCode matrix to a file named colorCodes.txt.
  public void writeColorCode(){
    
    try{
      File colorCodeFile = new File("colorCodes.txt");
      FileWriter fw = new FileWriter(colorCodeFile);
      if(colorCodeFile.createNewFile())
      {
        System.out.println("colorCodes.txt created.");
      }
      for(int i = 0; i < 100; i++)
      {
        fw.write(getArrayAsString(colorCodeMatrix[i]));
        fw.write("\n");
      }
      fw.close();
      System.out.println("Successfully wrote to file.");
    } catch(IOException e)
    {
      System.out.println("Error occurred when writing to the file.");
    }
  }
  
  //This method writes the contents of the intensity matrix to a file called intensity.txt
  public void writeIntensity(){
    try{
      File intensityFile = new File("intensity.txt");
      FileWriter fw = new FileWriter(intensityFile);
      if(intensityFile.createNewFile())
      {
        System.out.println("intensity.txt created.");
      }
      for(int i = 0; i < 100; i++)
      {
        fw.write(getArrayAsStringWithoutFirstElement(intensityMatrix[i]));
        fw.write("\n");
      }
      fw.close();
      System.out.println("Successfully wrote to file.");
    } catch(IOException e)
    {
      System.out.println("Error occurred when writing to the file.");
    }
  }
  
  public static void main(String[] args)
  {
    new readImage();
  }

}
