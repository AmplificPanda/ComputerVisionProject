import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class RetinalScanner {
    //Compulsory for OpenCV | OpenCV Core Library
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {
        String img1 = null;
        String img2 = null;

        //param check
        if (args.length != 2) {
            System.err.println("Parameters are incorrect. Correct format: RetinalMatch img1 img2");
        } else {
            img1 = args[0];
            img2 = args[1];
        }

        //Reading the Image from the file
        Mat matrix1 = Imgcodecs.imread(img1);
        Mat matrix2 = Imgcodecs.imread(img2);

        //crop images [To Remove black area]
        Rect rectCrop = new Rect(190, 50, 1080, 920);
        //matrix1 = new Mat(matrix1, rectCrop);
        matrix2 = new Mat(matrix2, rectCrop);

        //resize image
        // Scaling the Image using Resize function
        Imgproc.resize(matrix1, matrix1, new Size(0, 0), 0.8, 0.8,
                Imgproc.INTER_AREA);
        Imgproc.resize(matrix2, matrix2, new Size(0, 0), 0.8, 0.8,
                Imgproc.INTER_AREA);

        /*Image Processing */
        //Apply contrast | brightness
        matrix1.convertTo(matrix1, -1, 1.3, 0);
        matrix2.convertTo(matrix2, -1, 1.3, 0);
        matrix1.convertTo(matrix1, -1, 1, -40);
        matrix2.convertTo(matrix2, -1, 1, -40);

        //Apply sharpness
        //Creating an empty matrix
        Mat sharpened1 = new Mat(matrix1.rows(), matrix1.cols(), matrix1.type());
        Imgproc.GaussianBlur(matrix1, sharpened1, new Size(0,0), 6);
        Core.addWeighted(matrix1, 1.5, sharpened1, -0.5, 0, sharpened1);
        //do same for second image
        Mat sharpened2 = new Mat(matrix2.rows(), matrix2.cols(), matrix2.type());
        Imgproc.GaussianBlur(matrix2, sharpened2, new Size(0,0), 6);
        Core.addWeighted(matrix2, 1.5, sharpened2, -0.5, 0, sharpened2);

        //CLAHE | Adpative histogram equalization
        LinkedList<Mat> channels = new LinkedList();
        Core.split(matrix2, channels);
        CLAHE clahe = Imgproc.createCLAHE();
        Mat destImage = new Mat(matrix2.cols(),matrix2.rows(), CvType.CV_8UC1);
        clahe.apply(channels.get(0), destImage);
        Core.merge(channels, matrix2);

        imshow(destImage);

        //Reduce noise
        Imgproc.GaussianBlur(sharpened1, sharpened1,
                new Size(0, 0), 1.25);
        Core.addWeighted(sharpened1, 1.5, sharpened1, -0.5,
                0, sharpened1);
        Imgproc.GaussianBlur(sharpened2, sharpened2,
                new Size(0, 0), 1.25);
        Core.addWeighted(sharpened2, 1.5, sharpened2, -0.5,
                0, sharpened2);

        //display images prior grayscale manipulation
        imshow(sharpened1);
        imshow(sharpened2);

        //segmentation || Thresholding
        //First: Source must be gray scale image
        //Held as grayscale image here
        Imgproc.cvtColor(sharpened1, sharpened1, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(sharpened2, sharpened2, Imgproc.COLOR_RGB2GRAY);


        //apply morphology:
        //Creating destination matrix
        Mat morphedImage1 = new Mat(sharpened1.rows(), sharpened1.cols(), sharpened1.type());
        Mat morphedImage2 = new Mat(sharpened2.rows(), sharpened2.cols(), sharpened2.type());
        //Preparing the kernel matrix object
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size((2*2) + 1, (2*2)+1));

        //Applying dilate on the Images
        Imgproc.dilate(sharpened1, morphedImage1, kernel);
        Imgproc.dilate(sharpened2, morphedImage2, kernel);

        //divide grayscale image and the morphed dilated image
        Mat divisionResult1 = new Mat();
        Mat divisionResult2 = new Mat();
        Core.divide(sharpened1,morphedImage1 ,divisionResult1,200);
        Core.divide(sharpened2,morphedImage2,divisionResult2,200);

        //display the images
        imshow(divisionResult1);
        imshow(divisionResult2);


        //once imshow is terminated, program terminates (for now).
        System.out.println("Program Exited");



    }

    public static void imshow(Mat src){ //displays images
        BufferedImage bufImage;
        try {
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", src, matOfByte);
            byte[] byteArray = matOfByte.toArray();
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);

            JFrame frame = new JFrame("Retinal Scan");
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.setSize(new Dimension(400, 300));
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

