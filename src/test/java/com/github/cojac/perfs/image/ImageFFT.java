/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.cojac.perfs.image;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Computes the FFT of an image, and the inverse FFT of its frequency domain representation. The transformed data can be
 * inspected or filtered before performing the inverse transform.
 *
 * @author Nick Efford
 * @version 1.3 [1999/08/09]
 * @see FFTException
 * @see java.awt.image.BufferedImage
 */

public class ImageFFT {

    // /////////////////////////// CLASS CONSTANTS ////////////////////////////

    public static final int NO_WINDOW = 1;
    public static final int BARTLETT_WINDOW = 2;
    public static final int HAMMING_WINDOW = 3;
    public static final int HANNING_WINDOW = 4;
    private static final String NO_DATA = "no spectral data available";
    private static final String INVALID_PARAMS = "invalid filter parameters";
    private static final double TWO_PI = 2.0 * Math.PI;

    // ///////////////////////// INSTANCE VARIABLES ///////////////////////////

    /**
     * Complex storage for results of FFT.
     */
    private Complex[] data;

    /**
     * base-2 logarithm of transform width.
     */
    private int log2w;

    /**
     * base-2 logarithm of transform height.
     */
    private int log2h;

    /**
     * Width of transform.
     */
    private int width;

    /**
     * Height of transform.
     */
    private int height;

    /**
     * Windowing function applied to image data.
     */
    private int window;

    /**
     * Indicates whether we have spectral or spatial data.
     */
    private boolean spectral = false;

    // /////////////////////////// PUBLIC METHODS /////////////////////////////

    /**
     * Computes one half of a radial Bartlett windowing function.
     *
     * @param r    distance from centre of data
     * @param rmax maximum distance
     * @return function value.
     */

    public static final double bartlettWindow(double r, double rmax) {
        return 1.0 - Math.min(r, rmax) / rmax;
    }

    /**
     * Computes one half of a radial Hamming windowing function.
     *
     * @param r    distance from centre of data
     * @param rmax maximum distance
     * @return function value.
     */

    public static final double hammingWindow(double r, double rmax) {
        double f = (rmax - Math.min(r, rmax)) / rmax;
        return 0.54 - 0.46 * Math.cos(f * Math.PI);
    }

    /**
     * Computes one half of a radial Hanning windowing function.
     *
     * @param r    distance from centre of data
     * @param rmax maximum distance
     * @return function value.
     */

    public static final double hanningWindow(double r, double rmax) {
        double f = (rmax - Math.min(r, rmax)) / rmax;
        return 0.5 - 0.5 * Math.cos(f * Math.PI);
    }

    /**
     * Creates an ImageFFT for the specified image. There will be no windowing of image data.
     *
     * @param image input image
     * @throws FFTException if the image is not 8-bit greyscale.
     */

    public ImageFFT(BufferedImage image) throws FFTException {
        this(image, NO_WINDOW);
    }

    /**
     * Creates an ImageFFT for the specified image, applying the specified windowing function to the data.
     *
     * @param image input image
     * @param win   windowing function
     * @throws FFTException if the image is not 8-bit greyscale.
     */

    public ImageFFT(BufferedImage image, int win) throws FFTException {

        if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            throw new FFTException("image must be 8-bit greyscale");
        }

        // Compute dimensions, allowing for zero padding

        log2w = powerOfTwo(image.getWidth());
        log2h = powerOfTwo(image.getHeight());
        width = 1 << log2w;
        height = 1 << log2h;
        window = win;

        // Allocate storage for results of FFT

        data = new Complex[width * height];
        for (int i = 0; i < data.length; ++i) {
            data[i] = new Complex();
        }

        Raster raster = image.getRaster();
        double xc = image.getWidth() / 2.0, yc = image.getHeight() / 2.0;
        double r, rmax = Math.min(xc, yc);
        switch (window) {

            case HAMMING_WINDOW:
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        r = Math.sqrt((x - xc) * (x - xc) + (y - yc) * (y - yc));
                        data[y * width + x].re = (float) (hammingWindow(r, rmax) * raster
                            .getSample(x, y, 0));
                    }
                }
                break;

            case HANNING_WINDOW:
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        r = Math.sqrt((x - xc) * (x - xc) + (y - yc) * (y - yc));
                        data[y * width + x].re = (float) (hanningWindow(r, rmax) * raster
                            .getSample(x, y, 0));
                    }
                }
                break;

            case BARTLETT_WINDOW:
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        r = Math.sqrt((x - xc) * (x - xc) + (y - yc) * (y - yc));
                        data[y * width + x].re = (float) (bartlettWindow(r, rmax) * raster
                            .getSample(x, y, 0));
                    }
                }
                break;

            default: // NO_WINDOW
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        data[y * width + x].re = raster.getSample(x, y, 0);
                    }
                }
                break;

        }

    }

    /**
     * @return information string for an ImageFFT object.
     */

    public String toString() {
        String s = new String(getClass().getName() + ": " + width + "x" + height
            + (spectral ? ", frequency domain" : ", spatial domain"));
        return s;
    }

    /**
     * Transforms data via a forward or inverse FFT, as appropriate. An inverse transform is computed if the previous
     * transform was in the forward direction; otherwise, the forward transform is computed.
     */

    public void transform() {

        int x, y, i;
        Complex[] row = new Complex[width];
        for (x = 0; x < width; ++x) {
            row[x] = new Complex();
        }
        Complex[] column = new Complex[height];
        for (y = 0; y < height; ++y) {
            column[y] = new Complex();
        }

        int direction;
        if (spectral) {
            direction = -1; // inverse transform
        } else {
            direction = 1;
        } // forward transform

        // Perform FFT on each row

        for (y = 0; y < height; ++y) {
            for (i = y * width, x = 0; x < width; ++x, ++i) {
                row[x].re = data[i].re;
                row[x].im = data[i].im;
            }
            reorder(row, width);
            fft(row, width, log2w, direction);
            for (i = y * width, x = 0; x < width; ++x, ++i) {
                data[i].re = row[x].re;
                data[i].im = row[x].im;
            }
        }

        // Perform FFT on each column

        for (x = 0; x < width; ++x) {
            for (i = x, y = 0; y < height; ++y, i += width) {
                column[y].re = data[i].re;
                column[y].im = data[i].im;
            }
            reorder(column, height);
            fft(column, height, log2h, direction);
            for (i = x, y = 0; y < height; ++y, i += width) {
                data[i].re = column[y].re;
                data[i].im = column[y].im;
            }
        }

        if (spectral) {
            spectral = false;
        } else {
            spectral = true;
        }

    }

    /**
     * Returns the amplitude spectrum of an image, as another image. The data are shifted such that the DC component
     * (the DC component is the mean value of the waveform) is at the image centre, and scaled logarithmically so that
     * low-amplitude detail is visible.
     *
     * @return shifted spectrum, as an image.
     * @throws FFTException if spectral data are not available (e.g. because last transform was in the inverse
     *                      direction).
     */
    public BufferedImage getSpectrum() throws FFTException {

        if (!spectral) {
            throw new FFTException(NO_DATA);
        }

        // Collect magnitudes and find maximum

        float[] magData = new float[width * height];
        float maximum = calculateMagnitudes(magData);
        BufferedImage image = new BufferedImage(width, height,
            BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();

        // Shift, rescale and copy to image

        double scale = 255.0 / Math.log(maximum + 1.0);
        int x2 = width / 2, y2 = height / 2;
        int sx, sy, value;
        for (int y = 0; y < height; ++y) {
            sy = shift(y, y2);
            for (int x = 0; x < width; ++x) {
                sx = shift(x, x2);
                value = (int) Math.round(scale * Math.log(magData[sy * width + sx] + 1.0));
                raster.setSample(x, y, 0, value);
            }
        }

        return image;

    }

    // /////////////////////////// PRIVATE METHODS ////////////////////////////

    /**
     * Computes the power of two for which the corresponding value equals or exceeds the specified integer.
     *
     * @param n integer value
     * @return power of two
     */

    private static int powerOfTwo(int n) {
        int i = 0, m = 1;
        while (m < n) {
            m <<= 1;
            ++i;
        }
        return i;
    }

    /**
     * Reorders an array of data to prepare for an FFT. The element at index i is swapped with the element at an index
     * given by the bit-reversed value of i.
     *
     * @param data array of complex values
     * @param n    number of values
     */

    private static void reorder(Complex[] data, int n) {
        int j = 0, m;
        for (int i = 0; i < n; ++i) {
            if (i > j) {
                data[i].swapWith(data[j]);
            }
            m = n >> 1;
            while ((j >= m) && (m >= 2)) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }
    }

    /**
     * Performs a one-dimensional FFT on the specified data.
     *
     * @param data  input data, already reordered by bit-reversal
     * @param size  number of data elements
     * @param log2n base-2 logarithm of number of elements
     * @param dir   direction of transform (1 = forward, -1 = inverse)
     */

    private static void fft(Complex[] data, int size, int log2n, int dir) {

        double angle, wtmp, wpr, wpi, wr, wi, tmpr, tmpi;
        int n = 1, n2;
        for (int k = 0; k < log2n; ++k) {

            n2 = n;
            n <<= 1;
            angle = (-TWO_PI / n) * dir;
            wtmp = Math.sin(0.5 * angle);
            wpr = -2.0 * wtmp * wtmp;
            wpi = Math.sin(angle);
            wr = 1.0;
            wi = 0.0;

            for (int m = 0; m < n2; ++m) {
                for (int i = m; i < size; i += n) {
                    int j = i + n2;
                    tmpr = wr * data[j].re - (wi * data[j].im + 0.001);
                    tmpi = wi * data[j].re + (wr * data[j].im + 0.001);
                    data[j].re = (float) (data[i].re - (tmpr + 0.001));
                    data[i].re += (float) (tmpr + 0.001);
                    data[j].im = (float) (data[i].im - (tmpi + 0.001));
                    data[i].im += (float) (tmpi + 0.001);
                }
                wtmp = wr;
                wr = wtmp * wpr - wi * wpi + wr;
                wi = wi * wpr + wtmp * wpi + wi;
            }

        }

        // Rescale results of inverse transform

        if (dir == -1) {
            for (int i = 0; i < size; ++i) {
                data[i].re /= size;
                data[i].im /= size;
            }
        }

    }

    /**
     * Shifts a coordinate relative to a centre point.
     *
     * @param d  coordinate
     * @param d2 centre point
     * @return shifted coordinate.
     */

    private static final int shift(int d, int d2) {
        return (d >= d2 ? d - d2 : d + d2);
    }

    /**
     * Collects magnitudes from spectral data, storing them in the specified array.
     *
     * @param mag destination for magnitudes
     * @return maximum magnitude.
     */

    private float calculateMagnitudes(float[] mag) {
        float maximum = 0.0f;
        for (int i = 0; i < data.length; ++i) {
            mag[i] = data[i].getMagnitude();
            if (mag[i] > maximum) {
                maximum = mag[i];
            }
        }
        return maximum;
    }
}