package com.monaum.Rapid_Global.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberToWordsUtil {

    private static final String[] tensNames = {
            "", " Ten", " Twenty", " Thirty", " Forty",
            " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"
    };

    private static final String[] numNames = {
            "", " One", " Two", " Three", " Four", " Five", " Six",
            " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve",
            " Thirteen", " Fourteen", " Fifteen", " Sixteen",
            " Seventeen", " Eighteen", " Nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
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

    public static String convert(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "Zero Taka Only";
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        long takaPart = amount.longValue();
        int paisaPart = amount
                .subtract(BigDecimal.valueOf(takaPart))
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        StringBuilder sTaka = new StringBuilder();

        long billions = takaPart / 1_000_000_000;
        if (billions > 0) {
            sTaka.append(convertLessThanOneThousand((int) billions)).append(" Billion");
            takaPart %= 1_000_000_000;
        }

        long millions = takaPart / 1_000_000;
        if (millions > 0) {
            sTaka.append(convertLessThanOneThousand((int) millions)).append(" Million");
            takaPart %= 1_000_000;
        }

        long thousands = takaPart / 1_000;
        if (thousands > 0) {
            sTaka.append(convertLessThanOneThousand((int) thousands)).append(" Thousand");
            takaPart %= 1_000;
        }

        if (takaPart > 0) {
            sTaka.append(convertLessThanOneThousand((int) takaPart));
        }

        String result = sTaka.toString().trim() + " Taka";

        if (paisaPart > 0) {
            result += " and " +
                    convertLessThanOneThousand(paisaPart).trim() +
                    " Paisa";
        }

        return result + " Only";
    }
}
