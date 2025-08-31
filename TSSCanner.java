package com.grace.rbac;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TSRolesScanner {

    // Scenario a: variable assignment with =
    private static final Pattern ASSIGNMENT_EQUALS = Pattern.compile(
            "(.+?)=\\s*(\\[.*ROLE_MBAA.*?\\]);",
            Pattern.DOTALL
    );

    // Scenario b: object literal with roles inside { }
    private static final Pattern ASSIGNMENT_OBJECT = Pattern.compile(
            "([\\w\\d_]+)\\s*=\\s*\\{([\\s\\S]*?ROLE_MBAA[\\s\\S]*?)\\};",
            Pattern.DOTALL
    );

    // Scenario c: method calls with roles
    private static final Pattern METHOD_CALL = Pattern.compile(
            "([\\w\\.]+)\\s*\\(\\s*(\\[.*ROLE_MBAA.*?\\])\\s*\\)",
            Pattern.DOTALL
    );

    public static void main(String[] args) throws IOException {
        Path baseDir = Paths.get("operations/cockpit/ui/src/app").toAbsolutePath().normalize();
        System.out.println("Scanning TS files in: " + baseDir);

        List<Result> results = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".ts") && !path.toString().endsWith(".spec.ts"))
                 .forEach(file -> processFile(file, results));
        }

        // Print results for testing
        for (Result r : results) {
            System.out.println("File: " + r.file);
            System.out.println("AssignedTo: " + r.assignedTo);
            System.out.println("Roles: " + r.roles);
            System.out.println("------------------------");
        }
    }

    private static void processFile(Path file, List<Result> results) {
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);

            scanAssignmentEquals(file.toString(), content, results);
            scanAssignmentObject(file.toString(), content, results);
            scanMethodCall(file.toString(), content, results);

        } catch (IOException e) {
            System.err.println("Error reading file: " + file + " -> " + e.getMessage());
        }
    }

    private static void scanAssignmentEquals(String filePath, String content, List<Result> results) {
        Matcher m = ASSIGNMENT_EQUALS.matcher(content);
        while (m.find()) {
            String assignedTo = m.group(1).trim();
            String roles = m.group(2).trim();
            results.add(new Result(filePath, assignedTo, roles));
        }
    }

    private static void scanAssignmentObject(String filePath, String content, List<Result> results) {
        Matcher m = ASSIGNMENT_OBJECT.matcher(content);
        while (m.find()) {
            String assignedTo = m.group(1).trim();
            String body = m.group(2).trim();
            // extract roles line inside body
            Pattern rolesLine = Pattern.compile(".*(ROLE_MBAA.*?);", Pattern.DOTALL);
            Matcher rm = rolesLine.matcher(body);
            String roles = "";
            if (rm.find()) {
                roles = rm.group(1).trim();
            }
            results.add(new Result(filePath, assignedTo, roles));
        }
    }

    private static void scanMethodCall(String filePath, String content, List<Result> results) {
        Matcher m = METHOD_CALL.matcher(content);
        while (m.find()) {
            String assignedTo = m.group(1).trim();
            String roles = m.group(2).trim();
            results.add(new Result(filePath, assignedTo, roles));
        }
    }

    // Simple record to hold results
    private record Result(String file, String assignedTo, String roles) {}
}
