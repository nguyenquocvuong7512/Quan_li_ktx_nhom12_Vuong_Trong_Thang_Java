package com.example.NguyenQuocVuong_chuong4.util;

import java.math.BigDecimal;

public class MoneyToWordsUtil {

    private static final String[] DIGITS = {
            "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };

    private static final String[] UNITS = {
            "", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"
    };

    public static String convertToVietnameseWords(BigDecimal amount) {
        if (amount == null) return "Không đồng";
        long number = amount.longValue();
        if (number == 0) return "Không đồng";
        if (number < 0) return "Âm " + convertToVietnameseWords(amount.abs());

        StringBuilder result = new StringBuilder();
        int unitIndex = 0;

        while (number > 0) {
            int block = (int) (number % 1000);
            if (block > 0) {
                String blockText = readThreeDigits(block, number >= 1000);
                String unit = UNITS[unitIndex];
                if (!unit.isEmpty()) {
                    blockText += " " + unit;
                }
                if (result.length() > 0) {
                    result.insert(0, blockText + " ");
                } else {
                    result.insert(0, blockText);
                }
            }
            number /= 1000;
            unitIndex++;
        }

        String res = result.toString().trim();
        if (res.isEmpty()) {
            return "Không đồng";
        }
        res = Character.toUpperCase(res.charAt(0)) + res.substring(1) + " đồng";
        return res.replaceAll("\\s+", " ");
    }

    private static String readThreeDigits(int number, boolean hasHigherGroup) {
        int tram = number / 100;
        int chuc = (number % 100) / 10;
        int donVi = number % 10;

        StringBuilder sb = new StringBuilder();

        if (tram > 0 || hasHigherGroup) {
            sb.append(DIGITS[tram]).append(" trăm");
        }

        if (chuc > 1) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(DIGITS[chuc]).append(" mươi");
            if (donVi == 1) {
                sb.append(" mốt");
            } else if (donVi == 5) {
                sb.append(" lăm");
            } else if (donVi > 0) {
                sb.append(" ").append(DIGITS[donVi]);
            }
        } else if (chuc == 1) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("mười");
            if (donVi == 5) {
                sb.append(" lăm");
            } else if (donVi > 0) {
                sb.append(" ").append(DIGITS[donVi]);
            }
        } else if (chuc == 0 && donVi > 0) {
            if (sb.length() > 0) {
                sb.append(" lẻ ").append(DIGITS[donVi]);
            } else {
                sb.append(DIGITS[donVi]);
            }
        }

        return sb.toString();
    }
}
