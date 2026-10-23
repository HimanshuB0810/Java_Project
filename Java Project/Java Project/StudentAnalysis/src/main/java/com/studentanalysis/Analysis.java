package com.studentanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Analysis {
    private final List<Student> students;
    private final Map<String, Integer> gradeDistribution;
    private final double averageFinalMarks;
    private final double medianFinalMarks;
    private final double highestFinalMarks;
    private final double lowestFinalMarks;
    private final double averageAttendance;
    private final double averageStudyHours;
    private final double populationStandardDeviation;
    private final boolean regressionAvailable;
    private final String regressionUnavailableReason;
    private final double slope;
    private final double intercept;
    private final double rSquared;
    private final double meanSquaredError;

    public Analysis(List<Student> students) {
        if (students == null) {
            throw new IllegalArgumentException("Student list cannot be null.");
        }

        this.students = new ArrayList<>(students);
        for (Student student : this.students) {
            if (student == null) {
                throw new IllegalArgumentException("Student list cannot contain null records.");
            }
            validateStudent(student);
        }

        gradeDistribution = createEmptyGradeDistribution();
        if (this.students.isEmpty()) {
            averageFinalMarks = 0.0;
            medianFinalMarks = 0.0;
            highestFinalMarks = 0.0;
            lowestFinalMarks = 0.0;
            averageAttendance = 0.0;
            averageStudyHours = 0.0;
            populationStandardDeviation = 0.0;
            regressionAvailable = false;
            regressionUnavailableReason = "At least two students are required for regression.";
            slope = 0.0;
            intercept = 0.0;
            rSquared = 0.0;
            meanSquaredError = 0.0;
            return;
        }

        double finalMarksTotal = 0.0;
        double attendanceTotal = 0.0;
        double studyHoursTotal = 0.0;
        double highest = this.students.get(0).getFinalMarks();
        double lowest = highest;
        List<Double> sortedFinalMarks = new ArrayList<>();

        for (Student student : this.students) {
            double finalMarks = student.getFinalMarks();
            finalMarksTotal += finalMarks;
            attendanceTotal += student.getAttendance();
            studyHoursTotal += student.getStudyHours();
            sortedFinalMarks.add(finalMarks);

            if (finalMarks > highest) {
                highest = finalMarks;
            }
            if (finalMarks < lowest) {
                lowest = finalMarks;
            }

            String grade = getGrade(finalMarks);
            gradeDistribution.put(grade, gradeDistribution.get(grade) + 1);
        }

        int totalStudents = this.students.size();
        averageFinalMarks = finalMarksTotal / totalStudents;
        averageAttendance = attendanceTotal / totalStudents;
        averageStudyHours = studyHoursTotal / totalStudents;
        highestFinalMarks = highest;
        lowestFinalMarks = lowest;

        Collections.sort(sortedFinalMarks);
        if (totalStudents % 2 == 1) {
            medianFinalMarks = sortedFinalMarks.get(totalStudents / 2);
        } else {
            double middleLeft = sortedFinalMarks.get(totalStudents / 2 - 1);
            double middleRight = sortedFinalMarks.get(totalStudents / 2);
            medianFinalMarks = (middleLeft + middleRight) / 2.0;
        }

        double squaredDifferenceTotal = 0.0;
        for (Student student : this.students) {
            double difference = student.getFinalMarks() - averageFinalMarks;
            squaredDifferenceTotal += difference * difference;
        }
        populationStandardDeviation = Math.sqrt(squaredDifferenceTotal / totalStudents);

        double[] regressionResults = calculateRegression();
        if (regressionResults == null) {
            regressionAvailable = false;
            regressionUnavailableReason = this.students.size() < 2
                    ? "At least two students are required for regression."
                    : "Regression is undefined when all study-hour values are identical.";
            slope = 0.0;
            intercept = 0.0;
            rSquared = 0.0;
            meanSquaredError = 0.0;
        } else {
            regressionAvailable = true;
            regressionUnavailableReason = "";
            slope = regressionResults[0];
            intercept = regressionResults[1];
            rSquared = regressionResults[2];
            meanSquaredError = regressionResults[3];
        }
    }

    private void validateStudent(Student student) {
        if (student.getStudentId() == null || student.getStudentId().isBlank()
                || student.getName() == null || student.getName().isBlank()
                || !Double.isFinite(student.getAttendance())
                || student.getAttendance() < 0 || student.getAttendance() > 100
                || !Double.isFinite(student.getStudyHours())
                || student.getStudyHours() < 0
                || !Double.isFinite(student.getPreviousMarks())
                || student.getPreviousMarks() < 0 || student.getPreviousMarks() > 100
                || !Double.isFinite(student.getFinalMarks())
                || student.getFinalMarks() < 0 || student.getFinalMarks() > 100) {
            throw new IllegalArgumentException(
                    "Student records must contain valid fields and values.");
        }
    }

    private Map<String, Integer> createEmptyGradeDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A+", 0);
        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);
        return distribution;
    }

    public static String getGrade(double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        }
        if (marks >= 90) {
            return "A+";
        } else if (marks >= 80) {
            return "A";
        } else if (marks >= 70) {
            return "B";
        } else if (marks >= 60) {
            return "C";
        } else if (marks >= 50) {
            return "D";
        }
        return "F";
    }

    private double[] calculateRegression() {
        if (students.size() < 2) {
            return null;
        }

        double averageX = 0.0;
        double averageY = 0.0;
        for (Student student : students) {
            averageX += student.getStudyHours();
            averageY += student.getFinalMarks();
        }
        averageX /= students.size();
        averageY /= students.size();

        double xDifferenceSquares = 0.0;
        double xyDifferenceProducts = 0.0;
        for (Student student : students) {
            double xDifference = student.getStudyHours() - averageX;
            double yDifference = student.getFinalMarks() - averageY;
            xDifferenceSquares += xDifference * xDifference;
            xyDifferenceProducts += xDifference * yDifference;
        }

        if (xDifferenceSquares == 0.0) {
            return null;
        }

        double calculatedSlope = xyDifferenceProducts / xDifferenceSquares;
        double calculatedIntercept = averageY - calculatedSlope * averageX;
        if (!Double.isFinite(calculatedSlope) || !Double.isFinite(calculatedIntercept)) {
            return null;
        }

        double residualSquares = 0.0;
        double totalSquares = 0.0;
        for (Student student : students) {
            double predictedMarks = calculatedSlope * student.getStudyHours()
                    + calculatedIntercept;
            double residual = student.getFinalMarks() - predictedMarks;
            double differenceFromAverage = student.getFinalMarks() - averageY;
            residualSquares += residual * residual;
            totalSquares += differenceFromAverage * differenceFromAverage;
        }

        if (!Double.isFinite(residualSquares) || !Double.isFinite(totalSquares)) {
            return null;
        }

        double calculatedRSquared;
        if (totalSquares == 0.0) {
            calculatedRSquared = residualSquares == 0.0 ? 1.0 : 0.0;
        } else {
            calculatedRSquared = 1.0 - residualSquares / totalSquares;
        }

        double calculatedMeanSquaredError = residualSquares / students.size();
        if (!Double.isFinite(calculatedRSquared)
                || !Double.isFinite(calculatedMeanSquaredError)) {
            return null;
        }

        return new double[]{
                calculatedSlope,
                calculatedIntercept,
                calculatedRSquared,
                calculatedMeanSquaredError
        };
    }

    private void requireRegression() {
        if (!regressionAvailable) {
            throw new IllegalStateException(regressionUnavailableReason);
        }
    }

    public int getTotalStudents() {
        return students.size();
    }

    public double getAverageFinalMarks() {
        return averageFinalMarks;
    }

    public double getMedianFinalMarks() {
        return medianFinalMarks;
    }

    public double getHighestFinalMarks() {
        return highestFinalMarks;
    }

    public double getLowestFinalMarks() {
        return lowestFinalMarks;
    }

    public double getAverageAttendance() {
        return averageAttendance;
    }

    public double getAverageStudyHours() {
        return averageStudyHours;
    }

    public double getPopulationStandardDeviation() {
        return populationStandardDeviation;
    }

    public Map<String, Integer> getGradeDistribution() {
        return new LinkedHashMap<>(gradeDistribution);
    }

    public boolean isRegressionAvailable() {
        return regressionAvailable;
    }

    public String getRegressionUnavailableReason() {
        return regressionUnavailableReason;
    }

    public double getSlope() {
        requireRegression();
        return slope;
    }

    public double getIntercept() {
        requireRegression();
        return intercept;
    }

    public String getRegressionEquation() {
        requireRegression();
        return String.format(java.util.Locale.ROOT, "y = %.4f * x + %.4f", slope, intercept);
    }

    public double getRSquared() {
        requireRegression();
        return rSquared;
    }

    public double getMeanSquaredError() {
        requireRegression();
        return meanSquaredError;
    }

    public double predictFinalMarks(double studyHours) {
        requireRegression();
        if (!Double.isFinite(studyHours) || studyHours < 0) {
            throw new IllegalArgumentException("Study hours must be a non-negative number.");
        }
        return slope * studyHours + intercept;
    }
}
