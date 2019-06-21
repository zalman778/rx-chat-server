package com.hwx.rx_chat_server.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class ImageUtil {

    private static int SIZE = 200; //size of image in px
    private static int BORDER_PADDING = 10;

    public static void createImageOfText(
              String text
            , String fileFullPath
            , boolean isCircle
    ) {
        text = text.toUpperCase();
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        int fontSize = (int) Math.round(SIZE * 1.4 / text.length());
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int diff = 0;
        int actualSize = SIZE + BORDER_PADDING * 2;

        int textHeight = fm.getMaxAscent();
        int textTopPadding = (actualSize - textHeight) / 2 ;



        g2d.dispose();

        img = new BufferedImage(actualSize, actualSize, BufferedImage.TYPE_INT_ARGB);

        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();

        //circle
        if (isCircle) {
            Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            Color randomColor = new Color(r, g, b);

            g2d.setColor(randomColor);
            Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, actualSize, actualSize);
            g2d.fill(circle);
        }

        //text
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, BORDER_PADDING+1, actualSize/2+1);
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, BORDER_PADDING, actualSize/2);


        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File(fileFullPath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        String uploadRootPath = "/home/hiwoo/projects/git/rx-chat/context-path/temp";
//        File uploadRootDir = new File(uploadRootPath);
//        if (!uploadRootDir.exists()) {
//            uploadRootDir.mkdirs();
//        }
//
//        File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + "test.png");
//        createImageOfText("Pe. Al.", serverFile.getAbsolutePath(), true);
//
//    }
}
