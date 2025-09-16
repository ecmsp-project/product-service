package com.ecmsp.productservice.testutil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {

    private static final Random RANDOM = new Random();

    private static final String[] ADJECTIVES = {
            "Red", "Green", "Sharp", "Smart", "Cool", "Fresh", "Big", "Huge", "Fancy", "Bright"
    };

    private static final String[] NOUNS = {
            "phone", "laptop", "iPhone", "door", "window", "house decoration", "bubble tea", "toy"
    };

    public static final String[] DESCRIPTIONS = {
            "High-quality and durable product designed for everyday use.",
            "Eco-friendly and made from sustainable materials.",
            "Compact design with excellent performance.",
            "Perfect choice for professionals and hobbyists alike.",
            "Innovative technology combined with sleek aesthetics.",
            "Lightweight and portable, ideal for travel.",
            "User-friendly interface with advanced features.",
            "Reliable and tested to meet industry standards.",
            "Elegant design that complements any environment.",
            "Offers exceptional value for the price."
    };

    public static String randomName() {
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[RANDOM.nextInt(NOUNS.length)];
        return adjective + " " + noun + " " + UUID.randomUUID().toString().substring(0, 5);
    }

    public static BigDecimal randomPrice() {
        return BigDecimal.valueOf(RANDOM.nextDouble() * 1000 + 10)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static String randomDescription() {
        return DESCRIPTIONS[RANDOM.nextInt(DESCRIPTIONS.length)];
    }

    public static String randomString(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    public static Map<String, Object> randomInfo() {
        return Map.ofEntries(
                Map.entry("productionCountry", "CHINA"),
                Map.entry("quality", "GOOD"),
                Map.entry("satisfactionRate", "5")
        );
    }
}
