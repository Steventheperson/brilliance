/*
 *  Copyright 2023 RAMPAGE. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rip.artemis.brilliance;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


public class Brilliance {

    private BufferedImage bufferedImage;
    private int imageQuality = 1;
    private boolean imageAllowGray = false;
    private int grayTolerance = 15;
    private boolean qualityScale = false;
    private int speed = 2;
    private boolean log = false;
    private long benchmarkTime = 0;

    /**
     * Image used for the entire color finding process.
     *
     * @param image buffered image
     */
    public final Brilliance image(final BufferedImage image) {
        bufferedImage = image;
        return this;
    }

    /**
     * Image used for the entire color finding process.
     *
     * @param url String URL of the image
     */
    public final Brilliance image(final String url) {
        BufferedImage img;
        try {
            img = ImageIO.read(new URL(url));
        } catch (final IOException malformedURLException) {
            throw new IllegalStateException("Specified image URL string is null.");
        }
        bufferedImage = img;
        return this;
    }

    /*
     * Image used for the entire color finding process.
     *
     * @param filePath Path to the image.
     */

    /*public final Brilliance image(final String filePath) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(filePath));
        } catch (final IOException malformedURLException) {
            throw new IllegalStateException("Specified file path string is null.");
        }
        bufferedImage = img;
        return this;
    }*/


    /**
     * Image used for the entire color finding process.
     *
     * @param url URL of the image
     */
    public final Brilliance image(final URL url) {
        BufferedImage img;
        try {
            img = ImageIO.read(url);
        } catch (final IOException malformedURLException) {
            throw new IllegalStateException("Specified image URL is null.");
        }
        bufferedImage = img;
        return this;
    }

    /**
     * Image used for the entire color finding process. (Not recommended)
     *
     * @param image Image that gets translated to a buffered image
     */
    public final Brilliance image(final Image image) {
        final BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        final Graphics bg = bi.getGraphics();
        bg.drawImage(image, 0, 0, null);
        bg.dispose();
        return this;
    }

    /**
     * Automatically scale the {@link rip.artemis.brilliance.Brilliance quality} setting with the size of the image.
     * With scaling a larger image will perform better with almost no hit to color accuracy.
     *
     * @param scaleQuality turn scaling on/off
     */
    public final Brilliance scale(final boolean scaleQuality) {
        qualityScale = scaleQuality;
        return this;
    }

    /**
     * Splits the image into columns/rows to improve searching performance.
     * Lower = higher quality. Can affect color accuracy.
     *
     * @param quality splitting quality
     */
    public final Brilliance quality(final int quality) {
        imageQuality = quality;
        return this;
    }

    /**
     * Allow gray/white/black pixels to be counted towards the frequency.
     * Turning this on does not cancel out a specified "grayTolerance" setting.
     *
     * @param includeGray include gray pixels
     */
    public final Brilliance includeGray(final boolean includeGray) {
        imageAllowGray = includeGray;
        return this;
    }

    /**
     * Tolerance for including gray/white/black toned colors.
     * Higher = less gray, used to force the most prominent/vibrant color.
     * If a picture has no colored pixels, it will loop through the function until the built color is not null.
     * (Don't set this too high in a production setting)
     *
     * @param tolerance the tolerance at which it searches with
     */
    public final Brilliance grayTolerance(final int tolerance) {
        grayTolerance = tolerance;
        return this;
    }

    /**
     * When the builder fails to find a color, it will reattempt with a lesser gray tolerance.
     * (current_tolerance - progressiveDecrease) = new_tolerance
     *
     * @param progressiveDecrease the speed at which it decreases
     */
    public final Brilliance speed(final int progressiveDecrease) {
        speed = progressiveDecrease;
        return this;
    }

    /**
     * Print the result of the color + its statistics into the console.
     * ex. "[144,193,197] [10ms] Image: 1920x1080, Quality: 1 -> 4, Tolerance: 100 -> 64, Speed: 2"
     *
     * @param logging switch logging on/off
     */
    public final Brilliance log(final boolean logging) {
        log = logging;
        return this;
    }

    public final void copyFrom(final Brilliance colorBuilder) {
        if (colorBuilder != null) {
            this.bufferedImage = colorBuilder.bufferedImage;
            this.grayTolerance = colorBuilder.grayTolerance;
            this.imageAllowGray = colorBuilder.imageAllowGray;
            this.imageQuality = colorBuilder.imageQuality;
            this.speed = colorBuilder.speed;
        }
    }

    public final int getGrayTolerance() {
        return grayTolerance;
    }

    public final int getImageQuality() {
        return imageQuality;
    }

    public final boolean getScaling() {
        return qualityScale;
    }

    public final boolean getAllowGray() {
        return imageAllowGray;
    }

    public final BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    /**
     * Gets the benchmark results for the color finding procedure. Does encompass a few other operations also.
     *
     * @return time in milliseconds.
     */
    public final long getBenchmark() {
        return benchmarkTime;
    }

    /**
     * Builds the color.
     *
     * @return built {@link java.awt.color color}
     */
    public final Color build() {
        if (bufferedImage == null) throw new IllegalStateException("Specified image is null.");
        if (grayTolerance > 500) throw new IllegalStateException("Specified gray tolerance is over the max of 500!");
        if (imageQuality > 500) throw new IllegalStateException("Specified quality is over the max of 500!");

        long start = System.currentTimeMillis();

        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final int pixelCount = width * height;

        int oldQuality = imageQuality;
        int oldTolerance = grayTolerance;

        if (qualityScale) {
            if (pixelCount >= 400000) {
                imageQuality = (int) Math.floor(pixelCount / 250000D);
            }
            /*if (pixelCount >= 1000000) {
                speed = (int) (speed + Math.floor(pixelCount / 1000000D));
            }*/
        }

        final int[] colorFrequencies = new int[16777216]; // 256^3
        int maxFrequency = 0;
        int mostFrequentColor = 0;

        while (maxFrequency == 0) {
            for (int i = 0; i < pixelCount; i += imageQuality) {

                final int color = bufferedImage.getRGB(i % width, i / width);
                final int red = (color >> 16) & 0xFF;
                final int green = (color >> 8) & 0xFF;
                final int blue = color & 0xFF;

                if (!imageAllowGray) {
                    final int grayscale = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue); // Relative "luminance" formula. Has helped results not skew red.
                    if (!(Math.abs(red - grayscale) <= grayTolerance && Math.abs(green - grayscale) <= grayTolerance && Math.abs(blue - grayscale) <= grayTolerance)) {
                        final int rgb = (red << 16) | (green << 8) | blue;
                        final int frequency = colorFrequencies[rgb]++;
                        if (frequency > maxFrequency) {
                            maxFrequency = frequency;
                            mostFrequentColor = rgb;
                        }
                    }
                } else {
                    final int rgb = (red << 16) | (green << 8) | blue;
                    final int frequency = colorFrequencies[rgb]++;
                    if (frequency > maxFrequency) {
                        maxFrequency = frequency;
                        mostFrequentColor = rgb;
                    }
                }
            }
            if (maxFrequency == 0) {
                grayTolerance -= speed;
            }
        }
        long stop = System.currentTimeMillis();
        benchmarkTime = stop - start;
        final Color color = new Color(mostFrequentColor);
        if (log) {
            //[[38;2;%d;%d;%dm■[0m]
            System.out.printf("[%d,%d,%d] [%dms] Image: %dx%d, Quality: %d -> %d, Tolerance: %d -> %d, Speed: %d %n", color.getRed(), color.getGreen(), color.getBlue(), benchmarkTime, width, height, oldQuality, imageQuality, oldTolerance, grayTolerance, speed);
        }
        return color;
    }
}
