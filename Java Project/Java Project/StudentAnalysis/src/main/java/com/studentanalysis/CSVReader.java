package com.studentanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CSVReader {
    private static final String EXPECTED_HEADER =
            "studentId,name,attendance,studyHours,previousMarks,finalMarks";

    // This basic parser does not support commas inside quoted CSV fields.
    public static ReadResult readStudents(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        Set<String> studentIds = new HashSet<>();
        int skippedCount = 0;
        int duplicateCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(filePath), StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header != null && header.startsWith("\uFEFF")) {
                header = header.substring(1);
            }
            if (!EXPECTED_HEADER.equals(header)) {
                throw new IOException("Unexpected or missing CSV header in: " + filePath);
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                String[] values = line.split(",", -1);
                if (values.length != 6) {
                    skippedCount++;
                    continue;
                }

                try {
                    String studentId = values[0].trim();
                    String name = values[1].trim();
                    double attendance = Double.parseDouble(values[2].trim());
                    double studyHours = Double.parseDouble(values[3].trim());
                    double previousMarks = Double.parseDouble(values[4].trim());
                    double finalMarks = Double.parseDouble(values[5].trim());

                    if (studentId.isEmpty() || name.isEmpty()
                            || !Double.isFinite(attendance)
                            || !Double.isFinite(studyHours)
                            || !Double.isFinite(previousMarks)
                            || !Double.isFinite(finalMarks)
                            || attendance < 0 || attendance > 100
                            || studyHours < 0
                            || previousMarks < 0 || previousMarks > 100
                            || finalMarks < 0 || finalMarks > 100) {
                        skippedCount++;
                        continue;
                    }

                    if (!studentIds.add(studentId)) {
                        skippedCount++;
                        duplicateCount++;
                        continue;
                    }

                    students.add(new Student(studentId, name, attendance, studyHours,
                            previousMarks, finalMarks));
                } catch (NumberFormatException exception) {
                    skippedCount++;
                }
            }
        }

        return new ReadResult(students, skippedCount, duplicateCount);
    }

    public static class ReadResult {
        private final List<Student> students;
        private final int skippedCount;
        private final int duplicateCount;

        private ReadResult(List<Student> students, int skippedCount, int duplicateCount) {
            this.students = students;
            this.skippedCount = skippedCount;
            this.duplicateCount = duplicateCount;
        }

        public List<Student> getStudents() {
            return new ArrayList<>(students);
        }

        public int getSkippedCount() {
            return skippedCount;
        }

        public int getDuplicateCount() {
            return duplicateCount;
        }

        public int getValidCount() {
            return students.size();
        }
    }
}
