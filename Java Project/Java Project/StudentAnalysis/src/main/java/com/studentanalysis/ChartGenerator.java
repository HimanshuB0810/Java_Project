package com.studentanalysis;

import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartGenerator {
    public static JFreeChart createGradeDistributionChart(Analysis analysis) {
        if (analysis == null) {
            throw new IllegalArgumentException("Analysis data cannot be null.");
        }
        if (analysis.getTotalStudents() == 0) {
            throw new IllegalArgumentException(
                    "Load at least one valid student record before creating a chart.");
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> gradeCounts = analysis.getGradeDistribution();
        String[] grades = {"A+", "A", "B", "C", "D", "F"};
        for (String grade : grades) {
            dataset.addValue(gradeCounts.get(grade), "Students", grade);
        }

        return ChartFactory.createBarChart(
                "Student Grade Distribution",
                "Grade",
                "Number of Students",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
    }

    public static JFreeChart createAttendanceVsFinalMarksChart(List<Student> students) {
        if (students == null || students.isEmpty()) {
            throw new IllegalArgumentException(
                    "Load at least one valid student record before creating a chart.");
        }

        XYSeries studentResults = new XYSeries("Students");
        for (Student student : students) {
            if (student == null) {
                throw new IllegalArgumentException(
                        "Student data cannot contain null records.");
            }
            studentResults.add(student.getAttendance(), student.getFinalMarks());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(studentResults);
        return ChartFactory.createScatterPlot(
                "Attendance vs Final Marks",
                "Attendance (%)",
                "Final Marks",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
    }
}
