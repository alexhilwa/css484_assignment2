/* Project 1
*/

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.*;

import javax.imageio.ImageIO;

public class CBIR extends JFrame{

    private final int IMAGE_COUNT = 100;
    private final int INTENSITY_BIN_COUNT = 25;
    private final int COLORCODE_BIN_COUNT = 64;
    private final int FEATURE_COUNT = INTENSITY_BIN_COUNT + COLORCODE_BIN_COUNT;
    
    private JLabel photographLabel = new JLabel();  //container to hold a large 
    private JButton [] button; //creates an array of JButtons
    private int [] buttonOrder = new int [101]; //creates an array to keep up with the image order
    private double [] imageSize = new double[101]; //keeps up with the image sizes
    private GridLayout gridLayout1;
    private GridLayout gridLayout2;
    private GridLayout gridLayout3;
    private GridLayout gridLayout4;
    private JPanel panelBottom1;
    private JPanel panelBottom2;
    private JPanel panelTop;
    private JPanel buttonPanel;
    private Double [][] intensityMatrix = new Double [IMAGE_COUNT][25]; //why is this 101?
    private Double [][] colorCodeMatrix = new Double [IMAGE_COUNT][64];
    private Double [][] combinedMatrix = new Double [IMAGE_COUNT][89];
    private Double [][] normalizedFeatureMatrix = new Double [IMAGE_COUNT][FEATURE_COUNT];

    private ArrayList<ComparableImage> comparableImages = new ArrayList<ComparableImage>();
    private HashMap<Integer, ComparableImage> imageMap = new HashMap<Integer, ComparableImage>();

    int picNo = 0;
    int imageCount = 1; //keeps up with the number of images displayed since the first page.
    int pageNo = 1;
    
    
    public static void main(String args[]) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CBIR app = new CBIR();
                app.setVisible(true);
            }
        });
    }
    
    
    
    public CBIR() {
      //The following lines set up the interface including the layout of the buttons and JPanels.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Icon Demo: Please Select an Image");        
        panelBottom1 = new JPanel();
        panelBottom2 = new JPanel();
        panelTop = new JPanel();
        buttonPanel = new JPanel();
        gridLayout1 = new GridLayout(8, 5, 5, 5);
        gridLayout2 = new GridLayout(2, 1, 5, 5);
        gridLayout3 = new GridLayout(1, 2, 5, 5);
        gridLayout4 = new GridLayout(2, 3, 5, 5);
        setLayout(gridLayout2);
        panelBottom1.setLayout(gridLayout1);
        panelBottom2.setLayout(gridLayout1);
        panelTop.setLayout(gridLayout3);
        add(panelTop);
        add(panelBottom1);
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setLayout(gridLayout4);
        panelTop.add(photographLabel);

        panelTop.add(buttonPanel);
        JButton previousPage = new JButton("Previous Page");
        JButton nextPage = new JButton("Next Page");
        JButton intensity = new JButton("Intensity");
        JButton colorCode = new JButton("Color Code");
        buttonPanel.add(previousPage);
        buttonPanel.add(nextPage);
        buttonPanel.add(intensity);
        buttonPanel.add(colorCode);
        
        nextPage.addActionListener(new nextPageHandler());
        previousPage.addActionListener(new previousPageHandler());
        intensity.addActionListener(new intensityHandler());
        colorCode.addActionListener(new colorCodeHandler());
        setSize(1100, 750);
        // this centers the frame on the screen
        setLocationRelativeTo(null);
        
        
        button = new JButton[IMAGE_COUNT + 1];
        /*This for loop goes through the images in the database and stores them as icons and adds
         * the images to JButtons and then to the JButton array
        */
        for (int i = 1; i < IMAGE_COUNT + 1; i++) {
                ImageIcon icon;
                icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
                
                 if(icon != null){
                    button[i] = new JButton(icon);
                    //panelBottom1.add(button[i]);
                    button[i].addActionListener(new IconButtonHandler(i, icon));
                    buttonOrder[i] = i;
                }
        }

        
        readIntensityFile();
        readColorCodeFile();
        createCombinedMatrix();
        constructComparables();
        displayFirstPage();
        
    }
    
    /*This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix.
    */
    public void readIntensityFile(){
      System.out.println("Reading intensity.txt...");
      StringTokenizer token;
      Scanner read;
      Double intensityBin;
      String line = "";
      int lineNumber = 0;
         
      try{
        read = new Scanner(new File ("intensity.txt"));
        int matrixIndexI = 0;
        while(read.hasNextLine())
        {
          token = new StringTokenizer(read.nextLine());
          int matrixIndexJ = 0;
          while(token.hasMoreTokens())
          {
            int nextInt = Integer.parseInt(token.nextToken());
            intensityMatrix[matrixIndexI][matrixIndexJ] = (double)nextInt;
            matrixIndexJ++;
          }
          matrixIndexI++;
        }
      }
      catch(FileNotFoundException EE){
        System.out.println("The file intensity.txt does not exist");
      }
      //*/
    }
    
    /*This method opens the color code text file containing the color code matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called colorCodeMatrix.
    */
    private void readColorCodeFile(){
      StringTokenizer token;
      Scanner read;
      Double colorCodeBin;
      int lineNumber = 0;
      try{
        read =new Scanner(new File ("colorCodes.txt"));
        int matrixIndexI = 0;
        while(read.hasNextLine())
        {
          token = new StringTokenizer(read.nextLine());
          int matrixIndexJ = 0;
          while(token.hasMoreTokens())
          {
            int nextInt = Integer.parseInt(token.nextToken());
            colorCodeMatrix[matrixIndexI][matrixIndexJ] = (double)nextInt;
            matrixIndexJ++;
          }
          matrixIndexI++;
        }
      
      }
      catch(FileNotFoundException EE){
        System.out.println("The file intensity.txt does not exist");
      }
      
      
    }

    private void createCombinedMatrix()
    {
      //step 1: add intensities to matrix
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        for(int j = 0; j < INTENSITY_BIN_COUNT; j++)
        {
          combinedMatrix[i][j] = intensityMatrix[i][j];
        }
      }
      //step 2: add color codes to matrix
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        for(int j = 0; j < COLORCODE_BIN_COUNT; j++)
        {
          combinedMatrix[i][INTENSITY_BIN_COUNT + j] = colorCodeMatrix[i][j];
        }
      }
    }

    
    
    /*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
     * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
     * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
    */
    private void displayFirstPage(){
      int imageButNo = 0;
      panelBottom1.removeAll(); 
      for(int i = 1; i < 21; i++){
        //System.out.println(button[i]);
        imageButNo = buttonOrder[i];
        panelBottom1.add(button[imageButNo]); 
        imageCount ++;
      }
      panelBottom1.revalidate();  
      panelBottom1.repaint();

    }

    //This method creates all the ComparableImage objects used in comparing images by intensity and color code.
    private void constructComparables()
    {
      try {
        for(int i = 1; i < IMAGE_COUNT + 1; i++)
        {
          BufferedImage image = ImageIO.read(new File("images/" + i + ".jpg"));
          ComparableImage ci = new ComparableImage(image, i - 1, IMAGE_COUNT);
          ci.setIntensity(intensityMatrix[i - 1]);
          ci.setColorCodes(colorCodeMatrix[i - 1]);
          //pass in relevancy matrix slice
          comparableImages.add(ci);
          imageMap.put(i - 1, ci);
        }
        System.out.println("Comparables constructed successfully.");
      } catch(IOException e)
      {
        System.out.println("Error reading images. Could not construct ComparableImages.");
      }
    }


    //This method assigns all the images a new image to calculate the distance between, and then runs that calculation.
    private void setComparableImagesToCompareTo(ComparableImage other)
    {
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        comparableImages.get(i).setCompareTarget(other);
      }
    }

    private Double[] generateWeightsWithRespectTo(int imageIndex)
    {
      Double[][] newMatrix = new Double[IMAGE_COUNT][FEATURE_COUNT];
      Double[] sums = new Double[IMAGE_COUNT];
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        for(int j = 0; j < FEATURE_COUNT; j++)
        {
          newMatrix[i][j] = (1.0/(double)IMAGE_COUNT) * Math.abs(combinedMatrix[imageIndex][j] - combinedMatrix[i][j]);
        }
      }
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        double sum = 0.0;
        for(int j = 0; j < FEATURE_COUNT; j++)
        {
          sum += newMatrix[i][j];
        }
        sums[i] = sum;
      }
      return sums;
    }

    public static double calcAvg(Double []nums)
    {
        double total = 0;

        for (int i = 0; i < nums.length; i++) {
            total += nums[i];
        }

        return (total / nums.length );
    }

    public static double calcStdDev(Double []nums)
    {
        double numsAvg = calcAvg(nums);
        double total = 0;

        for (int i = 0; i < nums.length; i++)
            total += Math.pow(nums[i] - numsAvg, 2.0);
        total /= nums.length;

        System.out.println(total);

        return Math.sqrt(total);
    }

    public static Double[] gaussianNormalize(Double []nums)
    {
        double numMean = calcAvg(nums);
        double numStdDev = calcStdDev(nums);
        Double output[] = new Double[nums.length];

        for (int i = 0; i < nums.length; i++) {
            output[i] = (nums[i] - numMean)/numStdDev;
        }

        return output;
    }

    private void generateNormalizedFeatureMatrix()
    {
      //do things
      Double[][] transposedMatrix = transposeMatrix(combinedMatrix);
      for(int i = 0; i < FEATURE_COUNT; i++)
      {
        transposedMatrix[i] = gaussianNormalize(transposedMatrix[i]);
      }
      normalizedFeatureMatrix = transposeMatrix(transposedMatrix);
    }

    private Double[][] transposeMatrix(Double[][] input)
    {
      int rowLen = input.length;
      int colLen = input[0].length;
      Double[][] output = new Double[colLen][rowLen];
      for(int i = 0; i < rowLen; i++) {
        for (int j = 0; j < colLen; j++) {
          output[j][i] = input[i][j];
        }
      }
      return output;
    }

    //This method sorts comparableImages by intensity.
    private void sortImagesByIntensity()
    {
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        comparableImages.get(i).setMethod(1);
      }
      Collections.sort(comparableImages, Collections.reverseOrder());
    }

    //This method sorts comparableImages by color code.
    private void sortImagesByColorCode()
    {
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        comparableImages.get(i).setMethod(2);
      }
      Collections.sort(comparableImages, Collections.reverseOrder());
    }

    private void sortImagesByRelevanceFeedback()
    {
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        comparableImages.get(i).setMethod(3);
      }
      Collections.sort(comparableImages, Collections.reverseOrder());

    }

    //This method returns an image with a given index.
    private ComparableImage getImageWithIndex(int index)
    {
      /*
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        //System.out.println("Checking index: " + i);
        if(comparableImages.get(i).index == index)
        {
          return comparableImages.get(i);
        }
      }
      //*/
      
      return imageMap.get(index); //this shouldn't happen
    }

    //This method writes the contents of a 2d double array to the console.
    private void print2DArray(Double[][] array)
    {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < array.length; i++)
      {
        sb.append(array[i][0]);
        for(int j = 1; j < array[0].length; j++)
        {
          sb.append(", ");
          sb.append(array[i][j]);
        }
        sb.append("\n");
      }
      System.out.println(sb.toString());
    }

    private void print1DArray(Double[] array)
    {

    }

    //This method writes the current sorted order of comparableImages to the console.
    private void writeImageOrder()
    {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < IMAGE_COUNT; i++)
      {
        sb.append(comparableImages.get(i).index + 1);
        sb.append(".jpg, ");
      }
      sb.deleteCharAt(sb.length() - 1);
      System.out.println(sb.toString());
    }

    //This method updates the buttonOrder array to match the current sorted order of comparableImages.
    private void updateButtonOrder()
    {
      for(int i = 1; i < IMAGE_COUNT + 1; i++)
      {
        buttonOrder[i] = comparableImages.get(i - 1).index + 1;
      }
    }
    
    //This method updates the icons displayed.
    private void updateDisplay()
    {
      int imageButNo = 0;
      int startImage = imageCount - 20;
      int endImage = imageCount;
      if(startImage >= 1)
      {
        panelBottom1.removeAll();
        /*The for loop goes through the buttonOrder array starting with the startImage value
          * and retrieves the image at that place and then adds the button to the panelBottom1.
        */
        for (int i = startImage; i < endImage; i++)
        {
                imageButNo = buttonOrder[i];
                panelBottom1.add(button[imageButNo]);
                imageCount--;
      
        }

        panelBottom1.revalidate();  
        panelBottom1.repaint();
      }
    }


    /*This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the 
     * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
    */ 
    private class IconButtonHandler implements ActionListener{
      int pNo = 0;
      ImageIcon iconUsed;
      
      IconButtonHandler(int i, ImageIcon j){
        pNo = i;
        iconUsed = j;  //sets the icon to the one used in the button
      }
      
      public void actionPerformed( ActionEvent e){
        photographLabel.setIcon(iconUsed);
        picNo = pNo;
        //System.out.println("Image displayed: " + picNo + ".jpg displayed.");
      }
      
    }
    
    /*This class implements an ActionListener for the nextPageButton.  The last image number to be displayed is set to the 
     * current image count plus 20.  If the endImage number equals 101, then the next page button does not display any new 
     * images because there are only 100 images to be displayed.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount
    */
    private class nextPageHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          int imageButNo = 0;
          int endImage = imageCount + 20;
          if(endImage <= IMAGE_COUNT + 1){
            panelBottom1.removeAll(); 
            for (int i = imageCount; i < endImage; i++) {
                    imageButNo = buttonOrder[i];
                    panelBottom1.add(button[imageButNo]);
                    imageCount++;
          
            }
  
            panelBottom1.revalidate();  
            panelBottom1.repaint();
          }
      }
      
    }
    
    /*This class implements an ActionListener for the previousPageButton.  The last image number to be displayed is set to the 
     * current image count minus 40.  If the endImage number is less than 1, then the previous page button does not display any new 
     * images because the starting image is 1.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount
    */
    private class previousPageHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          int imageButNo = 0;
          int startImage = imageCount - 40;
          int endImage = imageCount - 20;
          if(startImage >= 1){
            panelBottom1.removeAll();
            /*The for loop goes through the buttonOrder array starting with the startImage value
             * and retrieves the image at that place and then adds the button to the panelBottom1.
            */
            for (int i = startImage; i < endImage; i++) {
                    imageButNo = buttonOrder[i];
                    panelBottom1.add(button[imageButNo]);
                    imageCount--;
          
            }
  
            panelBottom1.revalidate();  
            panelBottom1.repaint();
          }
      }
      
    }
    
    
    /*This class implements an ActionListener when the user selects the intensityHandler button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one.
     * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least.
     */
    private class intensityHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          int pic = (picNo - 1);
          
          ComparableImage image = getImageWithIndex(pic);
          setComparableImagesToCompareTo(image);
          sortImagesByIntensity();
          writeImageOrder();
          updateButtonOrder();
          updateDisplay();

          panelBottom1.revalidate();  
          panelBottom1.repaint();
      }
      
    }
    
    /*This class implements an ActionListener when the user selects the colorCode button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one. 
     * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least.
     */ 
    private class colorCodeHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          int pic = (picNo - 1);

          ComparableImage image = getImageWithIndex(pic);
          setComparableImagesToCompareTo(image);
          sortImagesByColorCode();
          writeImageOrder();
          updateButtonOrder();
          updateDisplay();

          panelBottom1.revalidate();  
          panelBottom1.repaint();
      }
    }
}
