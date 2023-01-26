import java.awt.image.BufferedImage;
import java.lang.Math;

//This class represents a BufferedImage object that can be compared against others
//with its color code and intensity.
public class ComparableImage implements Comparable<ComparableImage>
{
    static final int INTENSITY = 1;
    static final int COLORCODE = 2;
    static final int RELEVANCE_FEEDBACK = 3;

    private int imageCount;
    private static Double[] featureWeights;
    
    public BufferedImage image;
    public int height;
    public int width;
    public int size;
    public String filename;
    public int method = INTENSITY;
    public Double[] intensity = new Double[25];
    public Double[] colorCodes = new Double[64]; 
    public Double[] relevancies = new Double[89];

    public boolean isRelevant = false;

    public int index;
    public ComparableImage compareTarget = null;
    public double colorCodeScoreWithCompareTarget = 0.0;
    public double intensityScoreWithCompareTarget = 0.0;
    public double relevanceFeedbackScoreWithCompareTarget = 0.0;

    //constructor accepts a BufferedImage and a unique number identifier for the image
    public ComparableImage(BufferedImage newImage, int index, int imageCount)
    {
        this.index = index;
        image = newImage;
        height = image.getHeight();
        width = image.getWidth();
        size = height * width;

        this.imageCount = imageCount;
        this.featureWeights = new Double[imageCount];
    }

    //This method accepts another ComparableImage and recalculates the distances for
    //intensity and colorCode between itself and the other image.
    public void setCompareTarget(ComparableImage other)
    {
        compareTarget = other;
        intensityScoreWithCompareTarget = getIntensityScore(other);
        colorCodeScoreWithCompareTarget = getColorCodeScore(other);
        relevanceFeedbackScoreWithCompareTarget = getRelevanceFeedbackScore(other);
    }

    //This method sets the method by which the images are compared.
    //Either intensity or color code.
    public boolean setMethod(int newMethod)
    {
        if(method == INTENSITY || method == COLORCODE || method == RELEVANCE_FEEDBACK)
        {
            method = newMethod;
            return true;
        }
        else
        {
            System.out.println("Method set incorrectly. " + newMethod + " is not a valid comparison method.");
            return false;
        }
    }

    //This method sets the image's intensity bins.
    public boolean setIntensity(Double[] newIntensity)
    {
        if(newIntensity.length != 25)
        {
            return false;
        }
        intensity = newIntensity;
        return true;
    }

    //This method sets the image's color code bins.
    public boolean setColorCodes(Double[] newColorCodes)
    {
        if(newColorCodes.length != 64)
        {
            return false;
        }
        colorCodes = newColorCodes;
        return true;
    }

    public boolean setRelevance(Double[] newRelevancies)
    {
        if(relevancies.length != 89)
        {
            return false;
        }
        this.relevancies = newRelevancies;
        return true;
    }

    public boolean setFeatureWeights(Double[] weights)
    {
        if(weights.length != imageCount)
        {
            return false;
        }
        this.featureWeights = weights;
        return true;
    }

    //This method calculates the intensity distance between this image and another.
    private double getIntensityScore(ComparableImage other)
    {
        double score = 0;
        for(int i = 0; i < intensity.length - 1; i++)
        {
            score += Math.abs((intensity[i] / size) - (other.intensity[i] / other.size));
        }
        return score;
    }

    //This method calculates the color code distance between this image and another.
    private double getColorCodeScore(ComparableImage other)
    {
        double score = 0;
        for(int i = 0; i < colorCodes.length - 1; i++)
        {
            score += Math.abs((colorCodes[i] / size) - (other.colorCodes[i] / other.size));
        }
        return score;
    }

    private double getRelevanceFeedbackScore(ComparableImage other)
    {
        double score = 0;
        //write me
        return score;
    }
    
    //This method overrides Comparable's compareTo
    public int compareTo(ComparableImage other)
    {
        if(method == INTENSITY)
        {
            if(this.intensityScoreWithCompareTarget > other.intensityScoreWithCompareTarget)
            {
                return -1; //other image has a lower distance, is closer
            }
            else if(this.intensityScoreWithCompareTarget < other.intensityScoreWithCompareTarget)
            {
                return 1; //this image has as lower distance, is closer
            }
            else //images have equal distance (somehow)
            {
                return 0;
            }
        }
        else if(method == COLORCODE)
        {
            if(this.colorCodeScoreWithCompareTarget > other.colorCodeScoreWithCompareTarget)
            {
                return -1; //other image has a lower distance, is closer
            }
            else if(this.colorCodeScoreWithCompareTarget < other.colorCodeScoreWithCompareTarget)
            {
                return 1; //other image has a lower distance, is closer
            }
            else //images have equal distance
            {
                return 0;
            }
        }
        else if(method == RELEVANCE_FEEDBACK)
        {
            if(this.relevanceFeedbackScoreWithCompareTarget > other.relevanceFeedbackScoreWithCompareTarget)
            {
                return -1;
            }
            else if(this.relevanceFeedbackScoreWithCompareTarget < other.relevanceFeedbackScoreWithCompareTarget)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        System.out.println("This should never happen. Ever.");
        return 0;
    }

}
