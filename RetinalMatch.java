import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Size;
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

import static org.opencv.imgproc.Imgproc.*;

public class RetinalMatch {
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

        //Matrixes for First and Second input
        Mat FirstInput = Imgcodecs.imread(img1);
        Mat SecondInput = Imgcodecs.imread(img2);


        //resize images
        Imgproc.resize(FirstInput, FirstInput, new Size(0, 0), 0.8, 0.8, Imgproc.INTER_AREA);
        Imgproc.resize(SecondInput, SecondInput, new Size(0, 0), 0.8, 0.8, Imgproc.INTER_AREA);

        //contrast and brightness
        FirstInput.convertTo(FirstInput, -1, 1.6, -15);
        SecondInput.convertTo(SecondInput, -1, 1.6, -15);

        //split channels
        ArrayList<Mat> channels1 = new ArrayList<>(3);
        ArrayList<Mat> channels2 = new ArrayList<>(3);
        Core.split(FirstInput, channels1);
        Core.split(SecondInput,channels2);

        //apply CLAHE
        CLAHE clahe = Imgproc.createCLAHE(3, new Size(8, 8));
        clahe.apply(channels1.get(1), FirstInput);
        clahe.apply(channels2.get(1),SecondInput);

        //apply median blur
        Imgproc.medianBlur(FirstInput, FirstInput, 11);
        //Imgproc.medianBlur(SecondInput, SecondInput, 11);

        //thresholding
        Imgproc.adaptiveThreshold(FirstInput, FirstInput, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 59, 13);
        Imgproc.adaptiveThreshold(SecondInput, SecondInput, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 59, 13);

        //
        int kernelSize = 3;
        Mat element = getStructuringElement(CV_SHAPE_RECT, new Size(2 * kernelSize + 1, 2 * kernelSize + 1), new Point(kernelSize, kernelSize));

        erode(FirstInput,FirstInput,element);
        dilate(FirstInput,FirstInput,element);

        imshow(FirstInput);
        imshow(SecondInput);
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
