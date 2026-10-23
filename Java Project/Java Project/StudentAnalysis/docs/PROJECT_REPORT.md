# Student Performance Analysis System — Project Report

**Student name:** [Enter your name]  
**Course / laboratory:** [Enter course or laboratory name]  
**Institution:** [Enter institution name]

## Abstract

The Student Performance Analysis System is a Java desktop application that reads academic records from a CSV file. It validates records, displays accepted students in a Swing table, calculates descriptive statistics and grade counts, visualizes grades and attendance against final marks, and fits a simple linear regression model that estimates final marks from study hours. The system uses local files and does not require a database.

## Problem Statement

Student information stored in a flat file can be difficult to inspect and summarize manually. This project provides a small desktop tool to load a consistent CSV dataset, reject malformed or out-of-range records, and present summaries and visualizations that make the loaded data easier to review.

## Objectives

- Read student records from a local CSV file.
- Validate required fields, numeric values, ranges, and duplicate IDs.
- Display accepted records and report valid, skipped, and duplicate counts.
- Calculate descriptive statistics and grade distribution.
- Plot grade counts and attendance versus final marks.
- Use ordinary least squares to estimate final marks from study hours.

## Software and Hardware Requirements

### Software

- Java Development Kit 17 or later.
- Apache Maven.
- A desktop operating system with Java Swing support.
- JFreeChart 1.5.5, resolved through Maven.

### Hardware

A general-purpose computer capable of running Java 17 and displaying a Swing desktop window is sufficient. No specialized hardware is required.

## System Design and Workflow

The project uses package `com.studentanalysis`.

1. The user starts `Dashboard.main` and selects a CSV file with `JFileChooser`.
2. `CSVReader` checks the header, parses rows, validates their fields, detects repeated IDs, and returns a `ReadResult`.
3. `Dashboard` replaces the active `ArrayList<Student>` and updates the table and analysis after a successful load.
4. `Analysis` computes statistics, grade counts, and regression values from the active records.
5. `ChartGenerator` builds the grade distribution bar chart and attendance-versus-marks scatter plot from those same records.
6. The dashboard displays results in dialog windows and shows prediction estimates when regression is available.

If a selected file cannot be read, the dashboard displays an error and retains the previously loaded data. A successful subsequent load replaces the previous table and analysis data.

## Dataset and Preprocessing

The supplied `data/student_performance.csv` contains 20 sample records. Its header is:

```text
studentId,name,attendance,studyHours,previousMarks,finalMarks
```

Student IDs and names must not be blank. Attendance, previous marks, and final marks must be finite numbers in the range 0–100. Study hours must be a finite non-negative number. Rows with missing columns, invalid numbers, or invalid ranges are skipped. Duplicate IDs are skipped and counted separately. Blank lines are ignored. The reader expects the header exactly and uses basic comma splitting; commas inside quoted fields are not supported.

## Implementation Details

- **`Student`** stores an ID, name, attendance, study hours, previous marks, and final marks.
- **`CSVReader`** uses Java file handling and returns accepted records together with valid, skipped, and duplicate counts.
- **`Analysis`** uses loops and collections to compute statistics, grade counts, and regression metrics. It sorts a separate list of final marks to find the median.
- **`ChartGenerator`** uses JFreeChart datasets to create a bar chart with all six grade categories and a scatter plot with one point per student.
- **`Dashboard`** uses Swing controls, a non-editable `JTable`, dialogs, and a file chooser. It also contains the `main` method; there is no separate `Main.java`.

## Statistical Analysis and Regression Methodology

For a list of \(n\) final marks \(y_i\):

- Mean: \(\bar{y} = \sum y_i / n\).
- Median: middle value of a sorted copy, or the average of the two middle values when \(n\) is even.
- Population standard deviation: \(\sqrt{\sum (y_i - \bar{y})^2 / n}\).
- Grade counts use the final-mark boundaries A+ (90–100), A (80–below 90), B (70–below 80), C (60–below 70), D (50–below 60), and F (below 50).

For regression, \(x\) is daily study hours and \(y\) is final marks:

\[
m = \frac{\sum (x_i-\bar{x})(y_i-\bar{y})}{\sum (x_i-\bar{x})^2},
\qquad
b = \bar{y} - m\bar{x},
\qquad
\hat{y} = mx+b.
\]

R-squared is calculated as \(1-\mathrm{SSE}/\mathrm{SST}\), where SSE is the sum of squared residuals and SST is the sum of squared differences from the mean. MSE is SSE divided by the number of students. Regression is marked unavailable when there are fewer than two students or when study-hour values have zero variance. For constant final marks, the implementation returns R² = 1 for an exact constant fit and MSE = 0. Predictions remain raw model estimates and are not limited to the 0–100 marks range.

## Results and Discussion

The supplied sample dataset is accepted as 20 valid records with no skipped or duplicate rows. Java 17 compilation and headless integration checks were performed for CSV validation, descriptive statistics, grade boundaries, regression calculations and edge cases, prediction, and both chart datasets. The chart checks verified all six grade categories and one scatter point per sample student. The dashboard process also remained running during a brief startup check and closed cleanly afterward.

The checks confirmed expected results for representative statistical and regression inputs. The startup check does not constitute an interactive functional or visual test of the Swing windows. No claims are made about real student outcomes or predictive accuracy beyond the supplied sample data.

## Limitations

- The CSV parser is a basic comma-separated parser and does not support quoted values containing commas.
- Invalid rows are counted, but individual row numbers and rejection reasons are not displayed in the dashboard.
- Data exists only in the selected local CSV and application memory; no database or export feature is implemented.
- A one-variable linear regression does not account for other factors and does not establish causation.
- The Swing interface was briefly launched for a startup check, but its controls and chart windows have not been interactively or visually tested.
- Maven could not be run in the verification environment because it was unavailable on PATH.
- No application screenshots are included; capture them after launching the GUI on a desktop system if the submission requires them.

## Conclusion

The project demonstrates Java object-oriented data classes, file handling, collection-based validation and analysis, Swing user-interface components, JFreeChart visualization, and a manually calculated linear regression model. It provides an understandable starting point for reviewing a small student dataset while making its parsing and modeling limitations clear.

## References

1. Oracle, *Java Platform, Standard Edition 17 API Specification*: <https://docs.oracle.com/en/java/javase/17/docs/api/>.
2. Apache Maven Project, *Maven Documentation*: <https://maven.apache.org/guides/>.
3. JFreeChart project: <https://www.jfree.org/jfreechart/>.
