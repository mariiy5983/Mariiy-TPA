package com.mariiy.tpa;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side i18n loaded from {@code assets/mariiy_tpa/lang/<locale>.json}.
 * Locale follows each player's Minecraft client language (e.g. {@code zh_tw}, {@code en_us}).
 */
public final class TpaI18n {

    private static final String PATH = "assets/mariiy_tpa/lang/";
    private static final String FALLBACK = "en_us";
    private static final Map<String, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    private TpaI18n() {
    }

    public static String normalize(String locale) {
        if (locale == null || locale.isBlank()) {
            return FALLBACK;
        }
        String s = locale.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        int hash = s.indexOf('#');
        if (hash >= 0) {
            s = s.substring(0, hash);
            while (s.endsWith("_")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        String[] parts = s.split("_+");
        if (parts.length >= 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
            return parts[0] + "_" + parts[1];
        }
        if (parts.length >= 1 && !parts[0].isEmpty()) {
            return parts[0];
        }
        return FALLBACK;
    }

    public static String translate(String locale, String key, Object... args) {
        String normalized = normalize(locale);
        String template = lookup(normalized, key);
        if (template == null) {
            String langOnly = languageOnly(normalized);
            if (langOnly != null) {
                template = lookup(langOnly, key);
            }
        }
        if (template == null) {
            template = lookup(FALLBACK, key);
        }
        if (template == null) {
            return key;
        }
        return format(template, args);
    }

    private static String languageOnly(String locale) {
        int i = locale.indexOf('_');
        if (i <= 0) {
            return null;
        }
        return locale.substring(0, i);
    }

    private static String lookup(String locale, String key) {
        Map<String, String> map = CACHE.computeIfAbsent(locale, TpaI18n::load);
        String v = map.get(key);
        if (v != null) {
            return v;
        }
        // Alias files: zh -> try zh_tw then zh_cn
        if ("zh".equals(locale)) {
            String tw = CACHE.computeIfAbsent("zh_tw", TpaI18n::load).get(key);
            if (tw != null) {
                return tw;
            }
            return CACHE.computeIfAbsent("zh_cn", TpaI18n::load).get(key);
        }
        return null;
    }

    private static Map<String, String> load(String locale) {
        String path = PATH + locale + ".json";
        try (InputStream in = TpaI18n.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                return Collections.emptyMap();
            }
            String raw = readAll(in).trim();
            if (raw.isEmpty()) {
                return Collections.emptyMap();
            }
            return parseFlatJson(raw);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static String readAll(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    /** Minimal flat JSON object parser: {"key":"value", ...} */
    static Map<String, String> parseFlatJson(String raw) {
        Map<String, String> out = new LinkedHashMap<>();
        int i = 0;
        int n = raw.length();
        while (i < n && raw.charAt(i) != '{') {
            i++;
        }
        i++;
        while (i < n) {
            while (i < n && Character.isWhitespace(raw.charAt(i))) {
                i++;
            }
            if (i >= n || raw.charAt(i) == '}') {
                break;
            }
            if (raw.charAt(i) == ',') {
                i++;
                continue;
            }
            if (raw.charAt(i) != '"') {
                break;
            }
            String key = readJsonString(raw, i);
            i = skipJsonString(raw, i) + 1;
            while (i < n && (Character.isWhitespace(raw.charAt(i)) || raw.charAt(i) == ':')) {
                i++;
            }
            if (i >= n || raw.charAt(i) != '"') {
                break;
            }
            String value = readJsonString(raw, i);
            i = skipJsonString(raw, i) + 1;
            out.put(key, value);
        }
        return out;
    }

    private static String readJsonString(String raw, int startQuote) {
        StringBuilder sb = new StringBuilder();
        for (int i = startQuote + 1; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\' && i + 1 < raw.length()) {
                char n = raw.charAt(++i);
                switch (n) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case 'u' -> {
                        if (i + 4 < raw.length()) {
                            sb.append((char) Integer.parseInt(raw.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                    }
                    default -> sb.append(n);
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int skipJsonString(String raw, int startQuote) {
        for (int i = startQuote + 1; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\') {
                i++;
            } else if (c == '"') {
                return i;
            }
        }
        return raw.length() - 1;
    }

    static String format(String template, Object... args) {
        if (args == null || args.length == 0) {
            return template;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < template.length(); ) {
            char c = template.charAt(i);
            if (c == '{' && i + 2 < template.length()) {
                int end = template.indexOf('}', i);
                if (end > i) {
                    String idx = template.substring(i + 1, end);
                    try {
                        int n = Integer.parseInt(idx);
                        if (n >= 0 && n < args.length) {
                            sb.append(args[n] == null ? "" : args[n]);
                            i = end + 1;
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }
}
