package com.volasoftware.tinder.utility;

import java.text.DecimalFormat;

public class RandomCoordinatesGenerator {
    public static double generate(double minValue, double maxValue) {
        int decimalPlaces = 5;

        double randomValue = Math.random();

        double coordinates = minValue + randomValue * (maxValue - minValue);

        coordinates = round(coordinates, decimalPlaces);

        return coordinates;
    }

    private static double round(double value, int decimalPlaces) {
        String pattern = "#." + "0".repeat(decimalPlaces);
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return Double.parseDouble(decimalFormat.format(value));
    }
}
