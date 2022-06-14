import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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

        //Instantiating the Imgcodecs class
        Imgcodecs imageCodecs = new Imgcodecs();

        //Reading the Image from the file
        Mat matrix1 = Imgcodecs.imread(img1);
        Mat matrix2 = Imgcodecs.imread(img2);

        imshow(matrix1);
        imshow(matrix2);

        System.out.println("Images Loaded");

    }

    public static void imshow(Mat src){
        BufferedImage bufImage = null;
        try {
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", src, matOfByte);
            byte[] byteArray = matOfByte.toArray();
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);

            JFrame frame = new JFrame("Image");
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            //frame.setPreferredSize(new Dimension(400, 300));
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

