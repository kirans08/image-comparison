/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagediff;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author KIRAN
 */
public class ImageDiff extends Application {

    private java.awt.Color[][] signature1;
    private java.awt.Color[][] signature2;

    // The base size of the images.
    private static final int baseSize = 300;
    String url1, url2;

    @Override
    public void start(Stage primaryStage) {

        url1 = "";
        url2 = "";

        final Group root = new Group();
        primaryStage.setResizable(false);

        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.LIGHTGRAY);
        primaryStage.setTitle("Compare Images");

        Label image1Label = new Label(" URL 1   ");
        image1Label.setFont(Font.font("Comic Sans MS"));
        image1Label.setTextFill(Color.WHITE);
        image1Label.setStyle("-fx-background-color:#1278a3");
        image1Label.setLayoutX(120);
        image1Label.setLayoutY(50);
        image1Label.setScaleX(2.5);
        image1Label.setScaleY(2.7);

        Label image2Label = new Label(" URL 2   ");
        image2Label.setFont(Font.font("Comic Sans MS"));
        image2Label.setTextFill(Color.WHITE);
        image2Label.setLayoutX(120);
        image2Label.setLayoutY(120);
        image2Label.setScaleX(2.5);
        image2Label.setScaleY(2.7);
        image2Label.setStyle("-fx-background-color:#1278a3");

        TextField imageSource1 = new TextField();
        imageSource1.setPromptText("Enter URL 1");
        imageSource1.setFont(Font.font("Comic Sans MS"));
        imageSource1.setLayoutX(320);
        imageSource1.setLayoutY(46);
        imageSource1.setScaleX(2.0);
        imageSource1.setScaleY(2.0);
        imageSource1.setPrefColumnCount(20);

        TextField imageSource2 = new TextField();
        imageSource2.setPromptText("Enter URL 2");
        imageSource2.setFont(Font.font("Comic Sans MS"));
        imageSource2.setLayoutX(320);
        imageSource2.setLayoutY(116);
        imageSource2.setScaleX(2.0);
        imageSource2.setScaleY(2.0);
        imageSource2.setPrefColumnCount(20);

        Button compareButton = new Button("COMPARE");
        compareButton.setScaleX(2.5);
        compareButton.setScaleY(2.5);
        compareButton.setLayoutX(370);
        compareButton.setLayoutY(195);
        compareButton.setStyle("-fx-background-color:#1278a3;");
        compareButton.setTextFill(Color.WHITE);

        ImageView image1View = new ImageView();
        image1View.setLayoutX(100);
        image1View.setLayoutY(260);
        image1View.setPreserveRatio(true);
        image1View.setFitWidth(250);
        image1View.setFitHeight(250);
        image1View.setStyle("-fx-background-color:grey;");

        ImageView image2View = new ImageView();
        image2View.setLayoutX(450);
        image2View.setLayoutY(260);
        image2View.setPreserveRatio(true);
        image2View.setFitWidth(250);
        image2View.setFitHeight(250);
        image2View.setStyle("-fx-background-color:grey;");

        Label result = new Label("");
        result.setTextFill(Color.WHITE);
        result.setFont(Font.font("Impact"));
        result.setScaleX(1.6);
        result.setScaleY(1.6);
        result.setLayoutX(370);
        result.setLayoutY(550);
        result.setScaleX(2);
        result.setScaleY(2);
        result.setTextAlignment(TextAlignment.CENTER);

        Rectangle progressRect = new Rectangle(820, 30);
        progressRect.setLayoutX(0);
        progressRect.setLayoutY(580);
        progressRect.setFill(Color.TRANSPARENT);

        imageSource1.setOnAction((ActionEvent event) -> {
            try {
                url1 = imageSource1.getText().replaceAll(" ", "%20");
                Image image1 = new Image(url1);
                image1View.setImage(image1);
            } catch (Exception e) {
                System.out.println("Invalid URL");

            }
            imageSource2.requestFocus();
        });

        imageSource2.setOnAction((ActionEvent event) -> {
            try {
                url2 = imageSource2.getText().replaceAll(" ", "%20");
                Image image2 = new Image(url2);
                image2View.setImage(image2);
            } catch (Exception e) {
                System.out.println("Invalid URL");
            }
        });

        compareButton.setOnAction((ActionEvent event) -> {
            try {
                String turl1, turl2;
                turl1 = imageSource1.getText().replaceAll(" ", "%20");
                turl2 = imageSource2.getText().replaceAll(" ", "%20");

                if (!url1.equals(turl1)) {
                    url1 = turl1;
                    Image image1 = new Image(turl1);
                    image1View.setImage(image1);
                }
                if (!url2.equals(turl2)) {
                    url2 = turl2;
                    Image image2 = new Image(turl2);
                    image2View.setImage(image2);
                }
                if (turl1.equals("") || turl2.equals("")) {
                    result.setText("ENTER BOTH URLS");
                    result.setTextFill(Color.CADETBLUE);
                }

            } catch (Exception e) {
                System.out.println("Invalid URL");
            }
            double dist = 0;
            try {
                dist = compareImages(imageSource1.getText(), imageSource2.getText());
            } catch (IOException ex) {
                System.out.println("Images not found");
            }
            result.setText(String.format("%3.0f", dist) + "%  Match");
            float r = (float) ((100 - dist) * 2.55);
            float g = (float) ((dist) * 2);
            result.setTextFill(Color.color(r / 255, g / 255, 0));
            progressRect.setFill(Color.color(r / 255, g / 255, 0));
        });

        root.getChildren().addAll(result, compareButton, image1Label, image2Label, imageSource1, imageSource2, image1View, image2View, progressRect);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public double compareImages(String img1, String img2) throws IOException {

        double distance = 0;
        URL url1 = null, url2 = null;
        java.awt.image.RenderedImage imageReference1 = null;
        java.awt.image.RenderedImage imageReference2 = null;

        img1 = img1.replaceAll(" ", "%20");
        img2 = img2.replaceAll(" ", "%20");

        try {
            url1 = new URL(img1);
            url2 = new URL(img2);
            
            imageReference1 = rescale(ImageIO.read(url1));
            imageReference2 = rescale(ImageIO.read(url2));
        } catch (Exception e) {
            System.out.println("Invalid URL");

        }

        // Calculate the signature vector for the reference.
        signature1 = calcSignature(imageReference1);
        signature2 = calcSignature(imageReference2);

        distance = calcDistance(imageReference1, imageReference2);
        double percent, ratio;
        ratio = distance;

        if (ratio < 1000) {
            ratio /= 100;
            percent = 100 - ratio;
        } else if (ratio < 2000) {
            ratio = ratio - 1000;
            ratio /= 50;
            percent = 90 - ratio;
        } else if (ratio < 3000) {
            ratio -= 2000;
            ratio /= 40;
            percent = 70 - ratio;
        } else if (ratio < 4000) {
            ratio -= 3000;
            ratio /= 33.333333333;
            percent = 45 - ratio;
        } else if (ratio < 6000) {
            ratio -= 4000;
            ratio /= 400;
            percent = 15 - ratio;
        } else {
            ratio -= 6000;
            ratio /= 500;
            percent = 10 - ratio;
        }
        return percent;

    }

    /*
     * This method rescales an image to 300,300 pixels using the JAI scale
     * operator.
     */
    private RenderedImage rescale(RenderedImage i) {
        float scaleW = ((float) baseSize) / i.getWidth();
        float scaleH = ((float) baseSize) / i.getHeight();
        // Scales the original image
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(i);
        pb.add(scaleW);
        pb.add(scaleH);
        pb.add(0.0F);
        pb.add(0.0F);
        pb.add(new InterpolationNearest());
        // Creates a new, scaled image and uses it on the DisplayJAI component
        return JAI.create("scale", pb);
    }

    /*
     * This method calculates and returns signature vectors for the input image.
     */
    private java.awt.Color[][] calcSignature(RenderedImage i) {
        // Get memory for the signature.
        java.awt.Color[][] sig = new java.awt.Color[5][5];
        // For each of the 25 signature values average the pixels around it.
        // Note that the coordinate of the central pixel is in proportions.
        float[] prop = new float[]{1f / 10f, 3f / 10f, 5f / 10f, 7f / 10f, 9f / 10f};
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                sig[x][y] = averageAround(i, prop[x], prop[y]);
            }
        }
        return sig;
    }

    /*
     * This method averages the pixel values around a central point and return the
     * average as an instance of Color. The point coordinates are proportional to
     * the image.
     */
    private java.awt.Color averageAround(RenderedImage i, double px, double py) {
        // Get an iterator for the image.
        RandomIter iterator = RandomIterFactory.create(i, null);
        // Get memory for a pixel and for the accumulator.
        double[] pixel = new double[3];
        double[] accum = new double[3];
        // The size of the sampling area.
        int sampleSize = 15;
        int numPixels = 0;
        // Sample the pixels.
        for (double x = px * baseSize - sampleSize; x < px * baseSize + sampleSize; x++) {
            for (double y = py * baseSize - sampleSize; y < py * baseSize + sampleSize; y++) {
                iterator.getPixel((int) x, (int) y, pixel);
                accum[0] += pixel[0];
                accum[1] += pixel[1];
                accum[2] += pixel[2];
                numPixels++;
            }
        }
        // Average the accumulated values.
        accum[0] /= numPixels;
        accum[1] /= numPixels;
        accum[2] /= numPixels;
        return new java.awt.Color((int) accum[0], (int) accum[1], (int) accum[2]);
    }

    /*
     * This method calculates the distance between the signatures of an image and
     * the reference one. The signatures for the image passed as the parameter are
     * calculated inside the method.
     */
    private double calcDistance(RenderedImage ref, RenderedImage other) {
        // Calculate the signature for that image.
        java.awt.Color[][] sigOther = calcSignature(other);
        // There are several ways to calculate distances between two vectors,
        // we will calculate the sum of the distances between the RGB values of
        // pixels in the same positions.
        double dist = 0;
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                int r1 = signature1[x][y].getRed();
                int g1 = signature1[x][y].getGreen();
                int b1 = signature1[x][y].getBlue();
                int r2 = signature2[x][y].getRed();
                int g2 = signature2[x][y].getGreen();
                int b2 = signature2[x][y].getBlue();
                double tempDist = Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2)
                        * (g1 - g2) + (b1 - b2) * (b1 - b2));
                dist += tempDist;
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
