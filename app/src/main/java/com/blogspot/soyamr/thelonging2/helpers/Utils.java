package com.blogspot.soyamr.thelonging2.helpers;


import android.util.Log;

public class Utils {
    private static final int TARGET_PIXELS_X = 2260;
    private static final int TARGET_PIXELS_Y = 1080;

    public static int screenHeight;
    public static int screenWidth;

    public static final int TOP_TO_BOTTOM = 0;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int LEFT_TO_RIGHT = 2;
    public static final int BOTTOM_TO_TOP = 3;


    public static int INITIAL_RIGHT_POSITION = 2000;
    public static int INITIAL_LEFT_POSITION = 186;


    public static int characterWidth = 194;
    public static int characterHeight = 420;

    public static double SCALING_FACTOR_X;
    public static double SCALING_FACTOR_Y;

    public static int FLOOR_Y_END = 840;


    public static void setXScalingFactor(int screenHeight, int screenWidth) {

        Utils.screenHeight = 1080;
        Utils.screenWidth = 2260;

        //target pixels on x axis / real pixels on x axis
        SCALING_FACTOR_X = TARGET_PIXELS_X / (double) screenWidth;
        SCALING_FACTOR_Y = TARGET_PIXELS_Y / (double) screenHeight;

        Utils.screenHeight /= SCALING_FACTOR_Y;
        Utils.screenWidth /= SCALING_FACTOR_X;


        //adapt initial character sizes to the screen
        characterWidth /= SCALING_FACTOR_X;
        characterHeight /= SCALING_FACTOR_Y;

        //adapt initial character positions on the screen
        INITIAL_LEFT_POSITION /= SCALING_FACTOR_X;
        INITIAL_RIGHT_POSITION /= SCALING_FACTOR_X;

        //adapt FLOOR positions on the screen
        FLOOR_Y_END /= SCALING_FACTOR_Y;

        print();
    }

    private static void print() {
        Log.e("amora ", p("\ncharacter width", characterWidth) + p("characterHeight", characterHeight) + "" +
                "" + p("Utils.screenHeight", Utils.screenHeight) + "" +
                "" + p("Utils.screenWidth", Utils.screenWidth) + "" +
                "" + p("SCALING_FACTOR_X", SCALING_FACTOR_X) + "" +
                "" + p("SCALING_FACTOR_Y", SCALING_FACTOR_Y) + "" +
                p("floorYEND", FLOOR_Y_END) +
                p("inital left", INITIAL_LEFT_POSITION) +
                p("inital right", INITIAL_RIGHT_POSITION));
    }

    private static String p(String str, int v) {
        return " " + str + " = " + v + "\n";
    }

    private static String p(String str, double v) {
        return " " + str + " = " + v + "\n";
    }

    public static int power2(int a) {
        return a * a;
    }

    public static int appluScallingX(int value) {
        return (int) (value / SCALING_FACTOR_X);
    }

    public static int appluScallingY(int value) {
        return (int) (value / SCALING_FACTOR_Y);
    }
}
