import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

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
        //Apply contrast
        matrix1.convertTo(matrix1, -1, 2, 5);
        matrix2.convertTo(matrix2, -1, 2, 5);



        //display the images
        imshow(matrix1);
        imshow(matrix2);


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

