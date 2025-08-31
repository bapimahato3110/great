package com.grace.rbac;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RolesAccessScanner {

    // HTML pattern to capture normal tags with closing or self-closing tags
    private static final Pattern HTML_ACCESS = Pattern.compile(
        "<(\\w[\\w-]*)[^>]*?(?:\\*access|\\[access\\])\\s*=\\s*\"([^\"]+)\"[^>]*>([\\s\\S]*?)</\\1>|" +
        "<(\\w[\\w-]*)[^>]*?(?:\\*access|\\[access\\])\\s*=\\s*\"([^\"]+)\"[^>]*/>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
);

    // TS patterns for role variables
    private static final List<Pattern> TS_ROLE_PATTERNS = List.of(
            Pattern.compile("(allowedRoles|allowedRolesToEdit|ALLOWED_ROLES)\\s*=\\s*([^;]+);"),
            Pattern.compile("visibilityService\\.hasRole\\(([^)]+)\\)")
    );

    public static void main(String[] args) throws IOException {
        Path baseDir = Paths.get("operations/cockpit/ui/src/app").toAbsolutePath().normalize();
        System.out.println("Scanning directory: " + baseDir);

        List<Result> results = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> processFile(file, results));
        }

        exportReport(results);
    }

    private static void processFile(Path file, List<Result> results) {
        String filePath = file.toString();
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);

            if (filePath.endsWith(".html")) {
                scanHtml(filePath, content, results);
            } else if (filePath.endsWith(".ts")) {
                scanTs(filePath, content, results);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " -> " + e.getMessage());
        }
    }

    private static void scanHtml(String filePath, String content, List<Result> results) {
        Matcher m = HTML_ACCESS.matcher(content);
        while (m.find()) {
            String element, roles, innerText;

            // Normal tag with closing
            if (m.group(1) != null) {
                element = m.group(1).trim();
                roles = m.group(2).trim();
                innerText = m.group(3) == null ? "" : m.group(3).trim();
            } else { // Self-closing tag
                element = m.group(4).trim();
                roles = m.group(5).trim();
                innerText = "";
            }

            results.add(new Result(filePath, element, roles, innerText));
        }
    }

    private static void scanTs(String filePath, String content, List<Result> results) {
        for (Pattern p : TS_ROLE_PATTERNS) {
            Matcher m = p.matcher(content);
            while (m.find()) {
                String varName = m.group(1).trim();
                String value = m.group(2).trim();
                results.add(new Result(filePath, "TS:" + varName, value, ""));
            }
        }
    }

    private static void exportReport(List<Result> results) throws IOException {
        String html = """
                <html>
                <head>
                  <meta charset='UTF-8'>
                  <title>RBAC Access Report</title>
                  <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border: 1px solid #ddd; padding: 8px; }
                    th { background: #f4f4f4; }
                    code { font-family: monospace; }
                  </style>
                </head>
                <body>
                  <h1>RBAC Access Report</h1>
                  <table>
                    <tr><th>File</th><th>Element / Variable</th><th>Roles</th><th>Inner Text</th></tr>
                """ + results.stream()
                .map(r -> "<tr><td><code>" + r.file + "</code></td><td>" + r.type +
                          "</td><td>" + r.roles + "</td><td>" + r.innerText + "</td></tr>")
                .collect(Collectors.joining("\n"))
                + """
                  </table>
                </body>
                </html>
                """;

        Path out = Paths.get("target/rbac-report.html");
        Files.createDirectories(out.getParent());
        Files.writeString(out, html, StandardCharsets.UTF_8);

        System.out.println("Report generated at: " + out.toAbsolutePath());
    }

    private record Result(String file, String type, String roles, String innerText) {}
}
