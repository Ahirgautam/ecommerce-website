package com.ecommerce.ecommerce_site.utils;

public class HtmlSanitizer {
    public static String sanitize(String html) {
        if (html == null) return null;

        // Remove script tags and their content
        html = html.replaceAll("(?i)<script.*?>.*?</script>", "");

        // Remove any on* attributes like onclick, onerror, etc.
        html = html.replaceAll("(?i)on\\w+\\s*=\\s*['\"].*?['\"]", "");

        // Remove javascript: from href or src attributes
        html = html.replaceAll("(?i)javascript:", "");

        return html;
    }
}
