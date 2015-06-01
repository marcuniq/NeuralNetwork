
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Jama.*;

public class TrainingDataLoader {
    
    // image width and length and number of pixels of a image dataset from Kaggle
    public final static int IMAGE_WIDTH_PIXELS_KAGGLE = 28;
    public final static int IMAGE_HEIGHT_PIXELS_KAGGLE = 28;    
    public final static int NUMBER_OF_PIXELS_KAGGLE = IMAGE_WIDTH_PIXELS_KAGGLE * IMAGE_HEIGHT_PIXELS_KAGGLE;
    
    public static int[] readSingleKaggleImage(String filename, int rowNumber) {
    // PRE: filename must reference a valid Kaggle CSV file
    // POST: returns the data in row number 'rowNumber' as integer array        
        BufferedReader bufferedReaderForCsv = null;
        String line = "";
        String splitCharacter = ",";
        int rowCounter = 0;
        int[] pixelRow = null;

        try {
            bufferedReaderForCsv = new BufferedReader(new FileReader(filename));
            
            // read first line (contains the label data) and check if number of columns is correct for Kaggle input
            line = bufferedReaderForCsv.readLine();
            int numberOfColsOfInput = line.split(splitCharacter).length;
            if (numberOfColsOfInput != NUMBER_OF_PIXELS_KAGGLE + 1) {
                System.out.println("Error/Warning: The input file contains " + numberOfColsOfInput + 
                        " columns but a valid Kaggle input file should contain " + 
                        (NUMBER_OF_PIXELS_KAGGLE + 1) + " columns (one label column and " + 
                        NUMBER_OF_PIXELS_KAGGLE +" pixel columns).\nThis could be a problem with the line break format.");
            }
            // read the remaining rows of the input file
            while ((line = bufferedReaderForCsv.readLine()) != null && rowCounter <= rowNumber) {
                rowCounter = rowCounter + 1;
                if (rowCounter == rowNumber) {
                    String[] currentPixelRowString = line.split(splitCharacter);
                    int[] currentPixelRow = new int[currentPixelRowString.length];
                    for (int i = 0; i < currentPixelRowString.length; i++) {
                        currentPixelRow[i] = Integer.parseInt(currentPixelRowString[i]);
                    }
                    pixelRow = currentPixelRow;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReaderForCsv != null) {
                try {
                    bufferedReaderForCsv.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pixelRow;
    }

    public static Matrix readKaggleData(String filename) {
        BufferedReader bufferedReaderForCsv = null;
        String line = "";
        String splitCharacter = ",";
        int rowCounter = 0;
        Matrix data = null;
        
        try {
            bufferedReaderForCsv = new BufferedReader(new FileReader(filename));
            
            // read first line (contains the label data) and check if number of columns is correct for Kaggle input
            line = bufferedReaderForCsv.readLine();
            int numberOfColsOfInput = line.split(splitCharacter).length;
            if (numberOfColsOfInput != NUMBER_OF_PIXELS_KAGGLE + 1) {
                System.out.println("Error/Warning: The input file contains " + numberOfColsOfInput + 
                        " columns but a valid Kaggle input file should contain " + 
                        (NUMBER_OF_PIXELS_KAGGLE + 1) + " columns (one label column and " + 
                        NUMBER_OF_PIXELS_KAGGLE +" pixel columns).\nThis could be a problem with the line break format.");
            }
            
            // read the remaining rows of the input file
            ArrayList<int[]> imageDataRows = new ArrayList<int[]>();
            while ((line = bufferedReaderForCsv.readLine()) != null) {
                rowCounter = rowCounter + 1;
                String[] currentPixelRowString = line.split(splitCharacter);
                int[] currentPixelRow = new int[currentPixelRowString.length];
                for (int i = 0; i < currentPixelRowString.length; i++) {
                    currentPixelRow[i] = Integer.parseInt(currentPixelRowString[i].trim());
                }
                imageDataRows.add(currentPixelRow);
            }

            // construct matrix
            data = new Matrix(rowCounter, NUMBER_OF_PIXELS_KAGGLE + 1);
            for (int i = 0; i < imageDataRows.size(); i++) {
                for (int j = 0; j < imageDataRows.get(i).length; j++) {
                    data.set(i, j, imageDataRows.get(i)[j]);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReaderForCsv != null) {
                try {
                    bufferedReaderForCsv.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("Training Data from file " + filename + " successfully loaded. There were " + 
                            rowCounter + " image rows contained in the file.");
        return data;
        
    }

    public static BufferedImage getSingleKaggleImage(int[] imageDataRow) {
        byte[] buffer = new byte[imageDataRow.length];
        for (int i = 0; i < imageDataRow.length; i++) {
            buffer[i] = (byte)imageDataRow[i];
        }
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int[] nBits = { 8 };
        ColorModel myColorModel = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel mySampleModel = myColorModel.createCompatibleSampleModel(IMAGE_WIDTH_PIXELS_KAGGLE, IMAGE_HEIGHT_PIXELS_KAGGLE);
        DataBufferByte myDataBufferByte = new DataBufferByte(buffer, NUMBER_OF_PIXELS_KAGGLE);
        WritableRaster myRaster = Raster.createWritableRaster(mySampleModel, myDataBufferByte, null);
        BufferedImage result = new BufferedImage(myColorModel, myRaster, false, null);
        return result;
    }

}
