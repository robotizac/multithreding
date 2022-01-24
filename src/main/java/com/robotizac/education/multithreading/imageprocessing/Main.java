package com.robotizac.education.multithreading.imageprocessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String SRC_FILE = "./target/classes/many-flowers.jpg";
    private static final String DEST_FILE = "./many-flowers.jpg";
    public static final int cores = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage image = ImageIO.read(Files.newInputStream(Path.of(SRC_FILE)));
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int tNum = Math.max(cores, cores - 2);

        Instant start = Instant.now();
        recolorImageToPurpleMuiltiThreaded(image, result, tNum);
//        recolorImageToPurpleSingleThreaded(image, result);
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Duration: " + duration);
        if (!Files.exists(Path.of(DEST_FILE))) {
            Files.createFile(Path.of(DEST_FILE));
        }
        OutputStream resultFile = Files.newOutputStream(Path.of(DEST_FILE));
        ImageIO.write(result, "jpg", resultFile);
    }

    private static void recolorImageToPurpleMuiltiThreaded(BufferedImage image, BufferedImage result, int tNum) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight() / tNum;

        for (int i = 0; i < tNum; i++) {
            int finalI = i;
            int finalY = height + height * i;
            threads.add(new Thread(() -> recolorPart(image, result, finalI, height * finalI, width, finalY)));
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void recolorImageToPurpleSingleThreaded(BufferedImage original, BufferedImage recolored) {
        recolorPart(original, recolored, 0, 0, original.getWidth(), original.getHeight());
    }

    private static void recolorPart(BufferedImage original, BufferedImage recolored, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < width; x++) {
            for (int y = topCorner; y < height; y++) {
                int originalRGB = original.getRGB(x, y);
                int recoloredRGB = getPixelRecoloredIfNeeded(originalRGB);
                recolored.getRaster().setDataElements(x, y, recolored.getColorModel().getDataElements(recoloredRGB, null));
            }
        }
    }

    private static int getPixelRecoloredIfNeeded(int rgb) {
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        if (isShadeOfGrey(red, green, blue)) {
            int r = Math.min(255, red + 10);
            int g = Math.max(0, green - 80);
            int b = Math.max(0, blue - 20);

            return rgbFromDistinctColors(r,g,b);
        }
        return rgb;
    }

    private static int rgbFromDistinctColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= red << 16;
        rgb |= green << 8;
        rgb |= blue;

        rgb |= 0xFF000000;
        return rgb;
    }

    private static int getBlue(int RGB) {
        return (RGB & 0x000000FF);
    }

    private static int getGreen(int RGB) {
        return (RGB & 0x0000FF00) >> 8;
    }

    private static int getRed(int RGB) {
        return (RGB & 0x00FF0000) >> 16;
    }

    private static boolean isShadeOfGrey(int red, int green, int blue) {
        return Math.abs(red - green) < 30 &&
                Math.abs(red - blue) < 30 &&
                Math.abs(green - blue) < 30;
    }
}
