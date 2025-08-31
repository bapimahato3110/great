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
        System.out.println("File\tAssignedTo\tRoles");
        for (Result r : results) {
            System.out.println(r.fileName() + "\t" + r.assignedTo() + "\t" + r.roles());
        }
    }

    private static void processFile(Path file, List<Result> results) {
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            String fileName = file.getFileName().toString(); // only file name, not full path

            scanAssignmentEquals(fileName, content, results);
            scanAssignmentObject(fileName, content, results);
            scanMethodCall(fileName, content, results);

        } catch (IOException e) {
            System.err.println("Error reading file: " + file + " -> " + e.getMessage());
        }
    }

    private static void scanAssignmentEquals(String fileName, String content, List<Result> results) {
        Matcher m = ASSIGNMENT_EQUALS.matcher(content);
        while
