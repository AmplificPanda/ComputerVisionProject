import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

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
        Rect rectCrop = new Rect(190, 50, 1053, 920);
        matrix1 = new Mat(matrix1, rectCrop);
        matrix2 = new Mat(matrix2, rectCrop);

        /*Image Processing */
        //Apply contrast | brightness
        matrix1.convertTo(matrix1, -1, 2, 0);
        matrix2.convertTo(matrix2, -1, 2, 0);
        matrix1.convertTo(matrix1, -1, 1, 20);
        matrix2.convertTo(matrix2, -1, 1, 20);

        //Apply sharpness
        //Creating an empty matrix
        Mat sharpened1 = new Mat(matrix1.rows(), matrix1.cols(), matrix1.type());
        Imgproc.GaussianBlur(matrix1, sharpened1, new Size(0,0), 10);
        Core.addWeighted(matrix1, 1.5, sharpened1, -0.5, 0, sharpened1);
        //do same for second image
        Mat sharpened2 = new Mat(matrix2.rows(), matrix2.cols(), matrix2.type());
        Imgproc.GaussianBlur(matrix2, sharpened2, new Size(0,0), 10);
        Core.addWeighted(matrix2, 1.5, sharpened2, -0.5, 0, sharpened2);



        //display the images
        imshow(sharpened1);
        imshow(sharpened2);


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

