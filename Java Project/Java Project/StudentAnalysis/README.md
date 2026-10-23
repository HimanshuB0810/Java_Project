# Student Performance Analysis System

## Project Overview

This desktop application helps summarize student academic performance from a CSV file. It loads attendance, daily study hours, previous marks, and final marks, then displays the accepted records and calculates statistics. It also displays two charts and uses simple linear regression to estimate final marks from study hours.

The application is intended as a small Java laboratory project. Regression shows an association in the loaded sample and does not prove that study hours cause a change in marks.

## Objectives

- Load and validate student records from a CSV file.
- Calculate descriptive statistics and grade counts.
- Visualize grade distribution and attendance compared with final marks.
- Calculate a simple linear regression model using study hours to estimate final marks.

## Technology Stack

- Java 17
- Java Swing
- Maven
- JFreeChart 1.5.5
- CSV file handling with Java's standard file APIs

## Features

- Choose and load a CSV file using a file chooser.
- Validate required fields, numeric values, attendance and marks ranges, and non-negative study hours.
- Detect duplicate student IDs; show valid, skipped, and duplicate record counts.
- Display valid records in a non-editable table.
- Display mean, median, highest and lowest marks, population standard deviation, average attendance, average study hours, and grade counts.
- Display a grade distribution bar chart with all six grade categories.
- Display an attendance-versus-final-marks scatter plot.
- Display linear regression equation, slope, intercept, R-squared, and mean squared error.
- Predict final marks from entered study hours when regression can be calculated. Estimates outside 0–100 are shown as extrapolated results, not clamped.
- Load another file to replace the active table and analysis data.

## Project Structure

```text
StudentAnalysis/
├── pom.xml
├── README.md
├── data/
│   └── student_performance.csv
├── docs/
│   ├── PROJECT_REPORT.md
│   └── VIVA_QUESTIONS.md
└── src/
    └── main/
        └── java/
            └── com/
                └── studentanalysis/
                    ├── Analysis.java
                    ├── ChartGenerator.java
                    ├── CSVReader.java
                    ├── Dashboard.java
                    └── Student.java
```

- `Student.java` stores one student's six data fields.
- `CSVReader.java` reads the CSV and returns the accepted records with skipped and duplicate counts in `ReadResult`.
- `Analysis.java` calculates descriptive statistics, grades, regression metrics, and predictions.
- `ChartGenerator.java` creates the grade bar chart and attendance scatter plot.
- `Dashboard.java` contains the Swing interface and the application's `main` method.
- `data/student_performance.csv` is the supplied sample dataset.

There is no separate `Main.java`. The application entry point is `com.studentanalysis.Dashboard`.

## Dataset Description

The supplied file contains 20 sample records with this exact header:

```text
studentId,name,attendance,studyHours,previousMarks,finalMarks
```

| Column | Meaning |
|---|---|
| `studentId` | Student identifier; must be non-empty and unique in the file |
| `name` | Student name; must be non-empty |
| `attendance` | Attendance percentage from 0 to 100 |
| `studyHours` | Daily study hours; must be non-negative |
| `previousMarks` | Previous marks from 0 to 100 |
| `finalMarks` | Final marks from 0 to 100 |

Numeric values must be finite. Invalid records, records with the wrong number of columns, and repeated IDs are skipped. Repeated IDs are also included in the skipped count and reported separately as duplicates. Blank lines are ignored. A missing file or incorrect header is reported as an error.

The CSV reader is intentionally basic: it splits each data row on commas and does not support commas inside quoted fields. Keep names and other fields free of embedded commas.

Grades are assigned from final marks as follows:

| Marks | Grade |
|---|---|
| 90–100 | A+ |
| 80–below 90 | A |
| 70–below 80 | B |
| 60–below 70 | C |
| 50–below 60 | D |
| Below 50 | F |

## Installation and Execution

### Prerequisites

- JDK 17 or later, with `java` and `javac` available.
- Maven installed and available as `mvn` on the command line.
- A desktop environment capable of displaying Swing windows.

From the project root, build the project and resolve its dependencies:

```powershell
mvn clean package
```

The POM configures Java release 17 and declares JFreeChart 1.5.5. It does not configure an executable JAR or the Maven Exec plugin. To run the actual entry point from PowerShell after the build, create a classpath containing Maven's resolved dependencies:

```powershell
mvn dependency:build-classpath "-Dmdep.outputFile=target\classpath.txt"
$dependencies = (Get-Content target\classpath.txt -Raw).Trim()
java -cp "target\classes;$dependencies" com.studentanalysis.Dashboard
```

Alternatively, open the folder as a Maven project in an IDE, wait for dependency resolution, and run `Dashboard.main`.

When the dashboard opens, click **Load CSV** and select `data\student_performance.csv` (or another CSV with the same header). If the chooser does not start in the `data` folder, navigate there from the project root. After a successful load, use the statistics, regression, prediction, and chart controls.

The chart library and Maven build plugins must be available locally or downloaded during the build. The application itself reads a local CSV and does not make network requests at runtime.

## Statistical Methods

- **Mean:** sum of the values divided by the number of students.
- **Median:** middle final mark after sorting a separate list; for an even number of students, the two middle values are averaged. The table's record order is not changed.
- **Population standard deviation:** square root of the average squared difference from the mean. The implementation divides by the number of loaded students, treating the loaded dataset as the population.
- **Grade distribution:** counts students in each grade category using the final-mark boundaries above.

### Linear Regression

Study hours per day are the independent variable \(x\), and final marks are the dependent variable \(y\). The implementation uses ordinary least squares:

```text
slope     = sum((x - meanX) * (y - meanY)) / sum((x - meanX)^2)
intercept = meanY - slope * meanX
estimate  = slope * x + intercept
```

- **R-squared (R²)** describes how much of the variation in final marks is explained by the fitted line for this dataset.
- **Mean Squared Error (MSE)** is the average squared difference between actual and predicted marks; smaller values indicate smaller residuals on the data used to fit the line.

Regression is unavailable when fewer than two records exist or all study-hour values are identical. Predictions are not clamped. A prediction outside the marks range is an extrapolated estimate.

## Results and Interpretation

The statistics summarize the loaded records; the grade chart counts students in each category, and each scatter-plot point corresponds to one student's attendance and final marks. Regression describes the association between study hours and final marks in the loaded sample. It should not be interpreted as proof of cause and effect or as a guaranteed individual outcome.

The supplied CSV has 20 sample records. No separate experimental study or real-world outcome is claimed.

## Verification Notes

All Java source files were compiled with `javac --release 17` using the declared JFreeChart dependency. Headless integration checks exercised CSV loading and validation, duplicate detection, statistics, grade boundaries, regression edge cases and prediction, and both chart datasets. The dashboard was also launched briefly and remained running during a startup check, then closed cleanly. Its controls and chart windows were not interactively exercised or visually reviewed. The Maven build was not run because Maven is unavailable in the checked environment.

## Limitations and Future Improvements

- The CSV parser does not implement quoted-field escaping or support commas inside fields.
- Only the first occurrence of a repeated student ID is kept; matching is case-sensitive.
- The dashboard displays record counts for invalid data but does not show a detailed error for every skipped row.
- Data is held in memory for the current session; there is no database or report export.
- Regression is a simple one-variable model and may not represent other influences on student performance.
- The GUI's controls and chart windows have not been interactively or visually tested.

Possible future improvements include reporting skipped row numbers and reasons, adding automated tests to the repository, and performing interactive GUI testing on a desktop system.

## Author

- Student name: **Himanshu Borikar**
- Registration Number : **24070014**
- Roll Number: **141**
- Subject: **Java Lab**
