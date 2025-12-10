package com.monaum.Rapid_Global.model;

public class NumberToWordsUtil {

    private static final String[] tensNames = {
        "", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"
    };

    private static final String[] numNames = {
        "", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine",
        " Ten", " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen",
        " Seventeen", " Eighteen", " Nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;
        if (number % 100 < 20){
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;
            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + " Hundred" + soFar;
    }

    public static String convert(Double amount) {
        if (amount == 0) { return "Zero Taka Only"; }

        long takaPart = amount.longValue();
        long paisaPart = Math.round((amount - takaPart) * 100);

        String sTaka = "";
        long billions = takaPart / 1000000000;
        if (billions > 0) {
            sTaka += convertLessThanOneThousand((int) billions) + " Billion";
            takaPart %= 1000000000;
        }

        long millions = takaPart / 1000000;
        if (millions > 0) {
            sTaka += convertLessThanOneThousand((int) millions) + " Million";
            takaPart %= 1000000;
        }

        long thousands = takaPart / 1000;
        if (thousands > 0) {
            sTaka += convertLessThanOneThousand((int) thousands) + " Thousand";
            takaPart %= 1000;
        }

        if (takaPart > 0) {
            sTaka += convertLessThanOneThousand((int) takaPart);
        }

        String result = sTaka.trim() + " Taka";

        if (paisaPart > 0) {
            result += " and " + convertLessThanOneThousand((int) paisaPart).trim() + " Paisa";
        }

        result += " Only";
        return result;
    }
}
