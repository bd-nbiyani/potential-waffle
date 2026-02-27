package com.search;

import java.util.List;

/**
 * Generates a self-contained, styled HTML page that displays
 * Google search results in a clean, card-based layout.
 */
public class HtmlGenerator {

    /**
     * Builds an HTML string for the given query and list of results.
     *
     * @param query   the original search query
     * @param results the parsed search results (may be empty)
     * @return a complete HTML document as a string
     */
    public String generate(String query, List<SearchResult> results) {
        String escapedQuery = escapeHtml(query);
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\">\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("  <title>Search Results: ").append(escapedQuery).append("</title>\n");
        sb.append(buildStyles());
        sb.append("</head>\n");
        sb.append("<body>\n");

        // Header / search bar
        sb.append("  <header>\n");
        sb.append("    <div class=\"header-inner\">\n");
        sb.append("      <span class=\"logo\">&#x1F50D; Google Search Results</span>\n");
        sb.append("      <div class=\"query-badge\">").append(escapedQuery).append("</div>\n");
        sb.append("    </div>\n");
        sb.append("  </header>\n");

        sb.append("  <main>\n");

        if (results.isEmpty()) {
            sb.append("    <div class=\"no-results\">\n");
            sb.append("      <p>&#x26A0; No results found for <strong>")
              .append(escapedQuery).append("</strong>.</p>\n");
            sb.append("      <p>Google may have changed its page structure, or the network request was blocked.</p>\n");
            sb.append("    </div>\n");
        } else {
            sb.append("    <p class=\"result-count\">Top ")
              .append(results.size()).append(" result")
              .append(results.size() == 1 ? "" : "s").append("</p>\n");

            sb.append("    <div class=\"results-grid\">\n");
            int index = 1;
            for (SearchResult r : results) {
                sb.append(buildResultCard(index++, r));
            }
            sb.append("    </div>\n");
        }

        sb.append("  </main>\n");
        sb.append("  <footer>\n");
        sb.append("    <p>Results fetched via Google &mdash; Java Search App</p>\n");
        sb.append("  </footer>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private String buildResultCard(int index, SearchResult result) {
        String title   = escapeHtml(result.getTitle());
        String url     = escapeHtml(result.getUrl());
        String snippet = escapeHtml(result.getSnippet());
        // Shorten the displayed URL for readability
        String displayUrl = result.getUrl()
                .replaceFirst("^https?://", "")
                .replaceFirst("^www\\.", "");
        if (displayUrl.length() > 60) {
            displayUrl = displayUrl.substring(0, 57) + "...";
        }
        displayUrl = escapeHtml(displayUrl);

        return "      <div class=\"card\">\n" +
               "        <div class=\"card-number\">" + index + "</div>\n" +
               "        <div class=\"card-body\">\n" +
               "          <a class=\"card-title\" href=\"" + url + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + title + "</a>\n" +
               "          <div class=\"card-url\">" + displayUrl + "</div>\n" +
               (snippet.isEmpty() ? "" :
               "          <p class=\"card-snippet\">" + snippet + "</p>\n") +
               "        </div>\n" +
               "      </div>\n";
    }

    private String buildStyles() {
        return "  <style>\n" +
               "    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }\n" +
               "    body {\n" +
               "      font-family: 'Segoe UI', Arial, sans-serif;\n" +
               "      background: #f0f4f8;\n" +
               "      color: #1a202c;\n" +
               "      min-height: 100vh;\n" +
               "      display: flex;\n" +
               "      flex-direction: column;\n" +
               "    }\n" +
               "    header {\n" +
               "      background: linear-gradient(135deg, #4285F4 0%, #34A853 50%, #EA4335 100%);\n" +
               "      padding: 20px 32px;\n" +
               "      box-shadow: 0 2px 8px rgba(0,0,0,0.2);\n" +
               "    }\n" +
               "    .header-inner {\n" +
               "      max-width: 860px;\n" +
               "      margin: 0 auto;\n" +
               "      display: flex;\n" +
               "      align-items: center;\n" +
               "      gap: 16px;\n" +
               "      flex-wrap: wrap;\n" +
               "    }\n" +
               "    .logo {\n" +
               "      font-size: 1.5rem;\n" +
               "      font-weight: 700;\n" +
               "      color: #fff;\n" +
               "      letter-spacing: 0.5px;\n" +
               "    }\n" +
               "    .query-badge {\n" +
               "      background: rgba(255,255,255,0.25);\n" +
               "      color: #fff;\n" +
               "      padding: 6px 14px;\n" +
               "      border-radius: 20px;\n" +
               "      font-size: 0.95rem;\n" +
               "      font-style: italic;\n" +
               "    }\n" +
               "    main {\n" +
               "      flex: 1;\n" +
               "      max-width: 860px;\n" +
               "      width: 100%;\n" +
               "      margin: 32px auto;\n" +
               "      padding: 0 16px;\n" +
               "    }\n" +
               "    .result-count {\n" +
               "      font-size: 0.85rem;\n" +
               "      color: #718096;\n" +
               "      margin-bottom: 16px;\n" +
               "      text-transform: uppercase;\n" +
               "      letter-spacing: 0.8px;\n" +
               "    }\n" +
               "    .results-grid {\n" +
               "      display: flex;\n" +
               "      flex-direction: column;\n" +
               "      gap: 16px;\n" +
               "    }\n" +
               "    .card {\n" +
               "      background: #fff;\n" +
               "      border-radius: 12px;\n" +
               "      padding: 20px 24px;\n" +
               "      box-shadow: 0 1px 4px rgba(0,0,0,0.08);\n" +
               "      display: flex;\n" +
               "      gap: 18px;\n" +
               "      align-items: flex-start;\n" +
               "      transition: box-shadow 0.2s;\n" +
               "    }\n" +
               "    .card:hover {\n" +
               "      box-shadow: 0 4px 16px rgba(66,133,244,0.18);\n" +
               "    }\n" +
               "    .card-number {\n" +
               "      min-width: 32px;\n" +
               "      height: 32px;\n" +
               "      background: #4285F4;\n" +
               "      color: #fff;\n" +
               "      border-radius: 50%;\n" +
               "      display: flex;\n" +
               "      align-items: center;\n" +
               "      justify-content: center;\n" +
               "      font-weight: 700;\n" +
               "      font-size: 0.9rem;\n" +
               "      flex-shrink: 0;\n" +
               "      margin-top: 2px;\n" +
               "    }\n" +
               "    .card-body { flex: 1; min-width: 0; }\n" +
               "    .card-title {\n" +
               "      display: block;\n" +
               "      font-size: 1.1rem;\n" +
               "      font-weight: 600;\n" +
               "      color: #1a0dab;\n" +
               "      text-decoration: none;\n" +
               "      margin-bottom: 4px;\n" +
               "      word-break: break-word;\n" +
               "    }\n" +
               "    .card-title:hover { text-decoration: underline; }\n" +
               "    .card-url {\n" +
               "      font-size: 0.8rem;\n" +
               "      color: #188038;\n" +
               "      margin-bottom: 8px;\n" +
               "      word-break: break-all;\n" +
               "    }\n" +
               "    .card-snippet {\n" +
               "      font-size: 0.93rem;\n" +
               "      color: #4a5568;\n" +
               "      line-height: 1.5;\n" +
               "    }\n" +
               "    .no-results {\n" +
               "      background: #fff3cd;\n" +
               "      border: 1px solid #ffc107;\n" +
               "      border-radius: 10px;\n" +
               "      padding: 24px;\n" +
               "      text-align: center;\n" +
               "      color: #856404;\n" +
               "      line-height: 1.8;\n" +
               "    }\n" +
               "    footer {\n" +
               "      text-align: center;\n" +
               "      padding: 16px;\n" +
               "      font-size: 0.8rem;\n" +
               "      color: #a0aec0;\n" +
               "    }\n" +
               "    @media (max-width: 600px) {\n" +
               "      .card { padding: 14px 16px; gap: 12px; }\n" +
               "      .logo { font-size: 1.1rem; }\n" +
               "    }\n" +
               "  </style>\n";
    }

    /**
     * Escapes characters that have special meaning in HTML.
     */
    static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
