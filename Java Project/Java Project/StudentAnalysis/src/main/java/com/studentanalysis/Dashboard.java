package com.studentanalysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JFrame {
    private final ArrayList<Student> students;
    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final JButton statisticsButton;
    private final JButton regressionButton;
    private final JButton gradeChartButton;
    private final JButton attendanceChartButton;
    private final JButton predictButton;
    private final JTextField studyHoursField;
    private final JLabel statusLabel;
    private final JLabel predictionLabel;
    private Analysis analysis;

    public Dashboard() {
        super("Student Performance Analysis System");
        students = new ArrayList<>();
        analysis = new Analysis(students);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JButton loadButton = new JButton("Load CSV");
        statisticsButton = new JButton("Statistics");
        regressionButton = new JButton("Regression Results");
        gradeChartButton = new JButton("Grade Distribution Chart");
        attendanceChartButton = new JButton("Attendance vs Final Marks");
        statisticsButton.setEnabled(false);
        regressionButton.setEnabled(false);
        gradeChartButton.setEnabled(false);
        attendanceChartButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttonPanel.add(loadButton);
        buttonPanel.add(statisticsButton);
        buttonPanel.add(regressionButton);
        buttonPanel.add(gradeChartButton);
        buttonPanel.add(attendanceChartButton);

        statusLabel = new JLabel("No dataset loaded.");
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(buttonPanel, BorderLayout.NORTH);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Student ID", "Name", "Attendance", "Study Hours",
                        "Previous Marks", "Final Marks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setAutoCreateRowSorter(true);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        studyHoursField = new JTextField(8);
        predictButton = new JButton("Predict Final Marks");
        predictButton.setEnabled(false);
        predictionLabel = new JLabel("Enter study hours after loading data.");

        JPanel predictionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        predictionPanel.add(new JLabel("Study hours per day:"));
        predictionPanel.add(studyHoursField);
        predictionPanel.add(predictButton);
        predictionPanel.add(predictionLabel);
        add(predictionPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(event -> chooseAndLoadCsv());
        statisticsButton.addActionListener(event -> showStatistics());
        regressionButton.addActionListener(event -> showRegressionResults());
        gradeChartButton.addActionListener(event -> showGradeDistributionChart());
        attendanceChartButton.addActionListener(event -> showAttendanceChart());
        predictButton.addActionListener(event -> predictFinalMarks());
    }

    private void chooseAndLoadCsv() {
        JFileChooser fileChooser = new JFileChooser(new File("data"));
        fileChooser.setDialogTitle("Select student performance CSV file");
        int choice = fileChooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        try {
            CSVReader.ReadResult readResult =
                    CSVReader.readStudents(selectedFile.getAbsolutePath());
            ArrayList<Student> loadedStudents =
                    new ArrayList<>(readResult.getStudents());
            Analysis loadedAnalysis = new Analysis(loadedStudents);

            students.clear();
            students.addAll(loadedStudents);
            analysis = loadedAnalysis;
            updateStudentTable();
            updateControls();
            statusLabel.setText("Loaded: " + selectedFile.getName()
                    + " | Valid: " + readResult.getValidCount()
                    + " | Skipped: " + readResult.getSkippedCount()
                    + " | Duplicates: " + readResult.getDuplicateCount());
            predictionLabel.setText("Prediction result will appear here.");

            String message = "Valid records: " + readResult.getValidCount()
                    + "\nSkipped records: " + readResult.getSkippedCount()
                    + "\nDuplicate records: " + readResult.getDuplicateCount();
            int messageType = readResult.getValidCount() == 0
                    ? JOptionPane.WARNING_MESSAGE
                    : JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog(this, message, "CSV Load Results", messageType);
        } catch (IOException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this,
                    "Could not load the selected CSV file.\n" + exception.getMessage(),
                    "CSV Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudentTable() {
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    student.getAttendance(),
                    student.getStudyHours(),
                    student.getPreviousMarks(),
                    student.getFinalMarks()
            });
        }
    }

    private void updateControls() {
        boolean hasStudents = !students.isEmpty();
        statisticsButton.setEnabled(hasStudents);
        regressionButton.setEnabled(hasStudents);
        gradeChartButton.setEnabled(hasStudents);
        attendanceChartButton.setEnabled(hasStudents);
        predictButton.setEnabled(hasStudents && analysis.isRegressionAvailable());
    }

    private void showStatistics() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Load a CSV file containing valid student records first.",
                    "No Student Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Total students: ").append(analysis.getTotalStudents()).append('\n');
        message.append("Average final marks: ")
                .append(format(analysis.getAverageFinalMarks())).append('\n');
        message.append("Median final marks: ")
                .append(format(analysis.getMedianFinalMarks())).append('\n');
        message.append("Highest final marks: ")
                .append(format(analysis.getHighestFinalMarks())).append('\n');
        message.append("Lowest final marks: ")
                .append(format(analysis.getLowestFinalMarks())).append('\n');
        message.append("Population standard deviation: ")
                .append(format(analysis.getPopulationStandardDeviation())).append('\n');
        message.append("Average attendance: ")
                .append(format(analysis.getAverageAttendance())).append("%\n");
        message.append("Average study hours per day: ")
                .append(format(analysis.getAverageStudyHours())).append("\n\n");
        message.append("Grade counts:\n");

        Map<String, Integer> gradeDistribution = analysis.getGradeDistribution();
        for (Map.Entry<String, Integer> grade : gradeDistribution.entrySet()) {
            message.append(grade.getKey()).append(": ")
                    .append(grade.getValue()).append('\n');
        }

        JOptionPane.showMessageDialog(this, message.toString(),
                "Student Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRegressionResults() {
        if (!analysis.isRegressionAvailable()) {
            JOptionPane.showMessageDialog(this,
                    analysis.getRegressionUnavailableReason(),
                    "Regression Unavailable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String message = "Regression equation: " + analysis.getRegressionEquation()
                + "\nSlope: " + format(analysis.getSlope())
                + "\nIntercept: " + format(analysis.getIntercept())
                + "\nR-squared: " + format(analysis.getRSquared())
                + "\nMean Squared Error: " + format(analysis.getMeanSquaredError())
                + "\n\nRegression shows an association in this sample; it does not prove "
                + "that study hours cause higher marks.";
        JOptionPane.showMessageDialog(this, message,
                "Regression Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showGradeDistributionChart() {
        if (students.isEmpty()) {
            showNoStudentDataMessage();
            return;
        }
        showChart("Grade Distribution",
                ChartGenerator.createGradeDistributionChart(analysis));
    }

    private void showAttendanceChart() {
        if (students.isEmpty()) {
            showNoStudentDataMessage();
            return;
        }
        showChart("Attendance vs Final Marks",
                ChartGenerator.createAttendanceVsFinalMarksChart(students));
    }

    private void showNoStudentDataMessage() {
        JOptionPane.showMessageDialog(this,
                "Load a CSV file containing valid student records first.",
                "No Student Data", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showChart(String title, JFreeChart chart) {
        JFrame chartFrame = new JFrame(title);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setContentPane(new ChartPanel(chart));
        chartFrame.pack();
        chartFrame.setSize(800, 550);
        chartFrame.setLocationRelativeTo(this);
        chartFrame.setVisible(true);
    }

    private void predictFinalMarks() {
        try {
            double studyHours = Double.parseDouble(studyHoursField.getText().trim());
            double predictedMarks = analysis.predictFinalMarks(studyHours);
            String result = "Predicted final marks: " + format(predictedMarks);

            if (predictedMarks < 0 || predictedMarks > 100) {
                result += " (extrapolated estimate outside the 0–100 marks range)";
            }
            predictionLabel.setText(result);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this,
                    "Enter study hours as a valid number.",
                    "Invalid Study Hours", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Prediction Unavailable", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
            }
        });
    }
}
