package com.grace.rbac;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;

public class RolesAccessScanner {

    // Patterns
    private static final Pattern HTML_ACCESS = Pattern.compile(
            "<(\\w[\\w-]*)\\s+[^>]*?(\\*access|\\[access\\])\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>(.*?)</\\1>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ROLE_VAR = Pattern.compile(
            "\\b(allowedRolesToEdit|allowedRoles|ALLOWED_ROLES)\\s*=\\s*\\[([^\\]]+)\\]"
    );

    private static final Pattern HAS_ROLE = Pattern.compile(
            "visibilityService\\.hasRole\\s*\\(\\s*\\[?([^\\]\\)]+)\\]?\\)"
    );

    public static void main(String[] args) {
        try {
            // Default base dir: append UI folder to current working directory
            Path baseDir = Paths.get("")
                    .toAbsolutePath()
                    .resolve("operations/cockpit/ui/src/app")
                    .normalize();

            if (args.length > 0) {
                baseDir = Paths.get(args[0]).toAbsolutePath().normalize();
            }

            System.out.println("Scanning UI base directory: " + baseDir);

            if (!Files.exists(baseDir)) {
                System.err.println("ERROR: UI folder not found -> " + baseDir);
                return;
            }

            List<Result> htmlResults = new ArrayList<>();
            List<Result> tsResults = new ArrayList<>();

            // Scan all files recursively
            try (Stream<Path> paths = Files.walk(baseDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".html") || p.toString().endsWith(".ts"))
                        .forEach(path -> processFile(path, htmlResults, tsResults));
            }

            // Optionally check app.routes.ts for hasRole
            Path routes = baseDir.resolve("app.routes.ts");
            if (Files.exists(routes)) {
                System.out.println("Scanning app.routes.ts for hasRole()...");
                try {
                    String content = Files.readString(routes);
                    Matcher m = HAS_ROLE.matcher(content);
                    while (m.find()) {
                        tsResults.add(new Result(routes.toString(), "hasRole()", m.group(1)));
                    }
                } catch (IOException e) {
                    System.err.println("Failed to read app.routes.ts: " + e.getMessage());
                }
            }

            // Generate HTML report
            writeHtmlReport(htmlResults, tsResults);
            System.out.println("Scan complete. Report generated: target/access-report.html");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void processFile(Path path, List<Result> htmlResults, List<Result> tsResults) {
        try {
            String content = Files.readString(path);
            String filePath = path.toString();

            if (filePath.endsWith(".html")) {
                Matcher m = HTML_ACCESS.matcher(content);
                while (m.find()) {
                    String element = m.group(1);       // element type e.g., m4o-button
                    String directive = m.group(2);     // *access or [access]
                    String roles = m.group(3).trim();  // roles string ['ADMIN']
                    String innerText = m.group(4).trim(); // inner text of element

                    htmlResults.add(new Result(
                            filePath,
                            element + " (" + directive + ")",
                            roles + " → " + innerText
                    ));
                }
            } else if (filePath.endsWith(".ts")) {
                // role variables
                Matcher v = ROLE_VAR.matcher(content);
                while (v.find()) {
                    tsResults.add(new Result(filePath, v.group(1), v.group(2)));
                }
                // visibilityService.hasRole()
                Matcher h = HAS_ROLE.matcher(content);
                while (h.find()) {
                    tsResults.add(new Result(filePath, "hasRole()", h.group(1)));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed reading " + path + ": " + e.getMessage());
        }
    }

    private static void writeHtmlReport(List<Result> html, List<Result> ts) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8"/>
                    <title>Access Control Report</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                        th, td { border: 1px solid #ddd; padding: 8px; }
                        th { background: #f4f4f4; text-align: left;}
                        code { font-family: monospace; }
                    </style>
                </head>
                <body>
                <h1>Access Control Report</h1>
                """);

        sb.append("<h2>HTML Access Usage</h2>");
        sb.append("<table><tr><th>File</th><th>Element/Directive</th><th>Roles / Inner Text</th></tr>");
        for (Result r : html) {
            sb.append("<tr><td><code>").append(escapeHtml(r.file())).append("</code></td>")
                    .append("<td>").append(escapeHtml(r.type())).append("</td>")
                    .append("<td>").append(escapeHtml(r.roles())).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<h2>TS Role Variables / hasRole()</h2>");
        sb.append("<table><tr><th>File</th><th>Variable / Method</th><th>Roles</th></tr>");
        for (Result r : ts) {
            sb.append("<tr><td><code>").append(escapeHtml(r.file())).append("</code></td>")
                    .append("<td>").append(escapeHtml(r.type())).append("</td>")
                    .append("<td>").append(escapeHtml(r.roles())).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("</body></html>");

        Path out = Paths.get("target/access-report.html");
        Files.createDirectories(out.getParent());
        Files.writeString(out, sb.toString());
    }

    private static String escapeHtml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // record class for results
    private static record Result(String file, String type, String roles) {
        Result {
            roles = roles == null ? "" : roles.replaceAll("[\"'\\s]+", " ").trim();
        }
    }
}
