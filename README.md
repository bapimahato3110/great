package com.grace.rbac;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class RolesAccessScanner {

    // Patterns
    private static final Pattern HTML_ACCESS = Pattern.compile("(\\*access|\\[access\\])\\s*=\\s*['\"]([^'\"]+)['\"]");
    private static final Pattern ROLE_VAR = Pattern.compile("\\b(allowedRolesToEdit|allowedRoles|ALLOWED_ROLES)\\s*=\\s*\\[([^\\]]+)\\]");
    private static final Pattern HAS_ROLE = Pattern.compile("visibilityService\\.hasRole\\s*\\(\\s*\\[?([^\\]\\)]+)\\]?");

    public static void main(String[] args) throws IOException {

        // Base UI directory (change relative to RolesAccessScanner.java)
        Path baseDir = args.length > 0
                ? Paths.get(args[0])
                : Paths.get("../../../../operations/cockpit/ui/src/app");

        List<Result> htmlResults = new ArrayList<>();
        List<Result> tsResults = new ArrayList<>();

        // Walk all files recursively
        Files.walk(baseDir)
             .filter(Files::isRegularFile)
             .filter(path -> path.toString().endsWith(".html") || path.toString().endsWith(".ts"))
             .forEach(path -> {
                 try {
                     String content = Files.readString(path);

                     if (path.toString().endsWith(".html")) {
                         Matcher m = HTML_ACCESS.matcher(content);
                         while (m.find()) {
                             htmlResults.add(new Result(path.toString(), m.group(1), m.group(2)));
                         }
                     }

                     if (path.toString().endsWith(".ts")) {
                         // Role variables
                         Matcher v = ROLE_VAR.matcher(content);
                         while (v.find()) {
                             tsResults.add(new Result(path.toString(), v.group(1), v.group(2)));
                         }

                         // hasRole calls
                         Matcher h = HAS_ROLE.matcher(content);
                         while (h.find()) {
                             tsResults.add(new Result(path.toString(), "hasRole()", h.group(1)));
                         }
                     }

                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             });

        // Also include app.routes.ts specifically if needed
        Path routesFile = baseDir.resolve("app.routes.ts");
        if (Files.exists(routesFile)) {
            String content = Files.readString(routesFile);
            Matcher h = HAS_ROLE.matcher(content);
            while (h.find()) {
                tsResults.add(new Result(routesFile.toString(), "hasRole()", h.group(1)));
            }
        }

        writeHtmlReport(htmlResults, tsResults);
    }

    private static void writeHtmlReport(List<Result> html, List<Result> ts) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>Access Control Report</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                th, td { border: 1px solid #ccc; padding: 8px; }
                th { background: #f4f4f4; }
            </style>
        </head>
        <body>
        <h1>Access Control Report</h1>
        """);

        // HTML access table
        sb.append("<h2>HTML Access Usage</h2><table><tr><th>File</th><th>Directive</th><th>Roles</th></tr>");
        html.forEach(r -> sb.append("<tr><td>")
                .append(r.file).append("</td><td>")
                .append(r.type).append("</td><td>")
                .append(r.roles).append("</td></tr>"));
        sb.append("</table>");

        // TS roles table
        sb.append("<h2>TS Role Variables</h2><table><tr><th>File</th><th>Variable</th><th>Roles</th></tr>");
        ts.forEach(r -> sb.append("<tr><td>")
                .append(r.file).append("</td><td>")
                .append(r.type).append("</td><td>")
                .append(r.roles).append("</td></tr>"));
        sb.append("</table></body></html>");

        Path output = Paths.get("target/access-report.html");
        Files.createDirectories(output.getParent());
        Files.writeString(output, sb.toString());
        System.out.println("✅ Report written to " + output.toAbsolutePath());
    }

    // Java 21 record
    record Result(String file, String type, String roles) {
        Result {
            roles = roles.replaceAll("[\"'\\s]", ""); // normalize roles
        }
    }
}
