import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class Scanner2 {
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

        //[READ, CROP, RESIZE & COLOR SPACE CHANGES]
        //Reading the Image from the file
        //Mat matrix1 = Imgcodecs.imread(img1);
        Mat matrix2 = Imgcodecs.imread(img2);

        Rect rectCrop = new Rect(190, 50, 1080, 920);
        //matrix1 = new Mat(matrix1, rectCrop);
        matrix2 = new Mat(matrix2, rectCrop);

        //resize
        //Imgproc.resize(matrix1, matrix1, new Size(0, 0), 0.8, 0.8,
                //Imgproc.INTER_AREA);
        Imgproc.resize(matrix2, matrix2, new Size(0, 0), 0.8, 0.8,
                Imgproc.INTER_AREA);

        //color space change
        //Imgproc.cvtColor(initialInput2, initialInput2, Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(matrix2, matrix2, Imgproc.COLOR_BGR2RGB);

        ArrayList<Mat> channels1 = new ArrayList<>(3);
        Core.split(matrix2, channels1);
        //ArrayList<Mat> channels2 = new ArrayList<>(3);
        //Core.split(image2, channels2);

        //CLAHE for green channel
        CLAHE clahe = Imgproc.createCLAHE(3, new Size(8, 8));
        clahe.apply(channels1.get(1), matrix2);
        //clahe.apply(channels2.get(1), image2);

        //[CONTRAST + SHARPENING]
        //grayscale [required for adaptive threshold] [not required when using CLAHE]
        //Imgproc.cvtColor(matrix2,matrix2,Imgproc.COLOR_RGB2GRAY,);

        //contrast
        //matrix1.convertTo(matrix1, -1, 1.3, 0);
        matrix2.convertTo(matrix2, -1, 1.3, 0);
        //matrix1.convertTo(matrix1, -1, 1, -40);
        matrix2.convertTo(matrix2, -1, 1, -40);

        Mat dst = new Mat(matrix2.rows(), matrix2.cols(), matrix2.type());
        //thresholding
        Imgproc.adaptiveThreshold(matrix2, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 10);

        //apply median blur
        Imgproc.medianBlur(dst, dst, 7); //higher values = less of image, lower = more of image

        //up to applying CONTOURS HERE



        //imshow(matrix1);
        imshow(dst);
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

