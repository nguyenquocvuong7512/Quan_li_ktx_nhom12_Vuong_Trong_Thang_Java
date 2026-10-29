package com.example.NguyenQuocVuong_chuong4.security;

import java.util.regex.Pattern;

/**
 * Tiện ích làm sạch và mã hóa dữ liệu nhập vào nhằm ngăn chặn tấn công Cross-Site Scripting (XSS).
 */
public final class XssProtectionUtil {

    private static final Pattern SCRIPT_TAG = Pattern.compile("<script[^>]*>(.*?)</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern JAVASCRIPT_PROTO = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern ON_EVENTS = Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern IFRAME_TAG = Pattern.compile("<iframe[^>]*>(.*?)</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private XssProtectionUtil() {
    }

    /**
     * Làm sạch chuỗi, loại bỏ các đoạn mã độc JavaScript và HTML nguy hiểm.
     */
    public static String sanitize(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        String cleaned = SCRIPT_TAG.matcher(input).replaceAll("");
        cleaned = IFRAME_TAG.matcher(cleaned).replaceAll("");
        cleaned = JAVASCRIPT_PROTO.matcher(cleaned).replaceAll("");
        cleaned = ON_EVENTS.matcher(cleaned).replaceAll("");
        return cleaned.trim();
    }

    /**
     * Escape HTML đặc biệt (&, <, >, ", ') để an toàn khi xuất ra giao diện.
     */
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
