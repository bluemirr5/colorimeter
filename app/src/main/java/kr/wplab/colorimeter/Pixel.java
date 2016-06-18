package kr.wplab.colorimeter;

import android.graphics.Color;

/**
 * Created by gs.won on 2016. 6. 18..
 *
 */
public class Pixel {
    public static Pixel WHITE_PIXEL = new Pixel(0, 255, 255, 255);

    private int alpha = 0;
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    public Pixel(int pixel) {
        alpha = Color.alpha(pixel);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
    }

    public Pixel(int alpha, int red, int green, int blue) {
        this.alpha = valid(alpha);
        this.red = valid(red);
        this.green = valid(green);
        this.blue = valid(blue);
    }

    public Pixel(int red, int green, int blue) {
        this(0, red, green, blue);
    }

    private int valid(int value) {
//        if(value > 255) return 255;
//        if(value < 0) return Math.abs(value);
        return value;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    @Override
    public String toString() {
        return "A:" + alpha + ", R:" + red + ", G:"+ green + ", B:" + blue;
    }

    public Pixel diff(Pixel org) {
        int alpha = this.alpha - org.alpha;
        int blue = this.blue - org.blue;
        int green = this.green - org.green;
        int red = this.red - org.red;
        return new Pixel(alpha, red, green, blue);
    }

    public int toRGBValue() {
        return Math.abs(red) + Math.abs(green) + Math.abs(blue);
    }
}
