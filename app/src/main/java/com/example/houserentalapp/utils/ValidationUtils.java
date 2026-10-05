package com.example.houserentalapp.utils;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        // Minimum 8 characters, at least one letter and one digit
        if (password == null || password.length() < 8) return false;
        boolean hasLetter = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String stripped = phone.replaceAll("[\\s\\-\\(\\)\\+]", "");
        return stripped.length() >= 7 && stripped.length() <= 15
                && stripped.matches("\\d+");
    }

    public static boolean isValidPrice(String price) {
        if (price == null || price.trim().isEmpty()) return false;
        try {
            double val = Double.parseDouble(price.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean passwordsMatch(String p1, String p2) {
        return p1 != null && p1.equals(p2);
    }

    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) return "Weak";
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        if (score <= 2) return "Weak";
        if (score <= 4) return "Moderate";
        return "Strong";
    }
}
