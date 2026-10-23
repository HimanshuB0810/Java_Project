# Viva Questions and Answers

## Java and Object-Oriented Programming

1. **What is the purpose of the `Student` class?**  
   It represents one record and stores the ID, name, attendance, study hours, previous marks, and final marks.

2. **How does the project use encapsulation?**  
   `Student` keeps its fields private and provides getters and setters to access or update them.

3. **How are Swing and JFreeChart used in the project?**  
   `Dashboard` extends `JFrame` to provide the desktop interface. `ChartGenerator` creates the JFreeChart bar and scatter plots, which the dashboard displays in separate windows using `ChartPanel`.

4. **What is the application's entry point?**  
   `Dashboard.main` starts Swing and displays a new dashboard. There is no separate `Main.java`.

## Swing and User Interface

5. **What is `JFileChooser` used for?**  
   It lets the user select the CSV file to load.

6. **How do buttons perform actions?**  
   The dashboard attaches action listeners to buttons; their event handlers load data, display analysis, open charts, or make predictions.

7. **Why is a `JTable` used?**  
   It presents the accepted student records in rows and columns. The dashboard's table is not editable.

8. **What happens when another CSV is loaded successfully?**  
   The dashboard replaces its active student list, table rows, and `Analysis`, so subsequent results use the newly loaded data.

## CSV Processing and Collections

9. **How does `CSVReader` read a file?**  
   It uses `Files.newBufferedReader` to read the header and process one line at a time.

10. **What does `CSVReader.ReadResult` contain?**  
    It provides the accepted student list and counts for valid, skipped, and duplicate records.

11. **How are duplicate student IDs detected?**  
    A `HashSet` stores each accepted ID. A later occurrence is skipped and counted as a duplicate.

12. **What validation rules are applied to a row?**  
    ID and name must not be blank; attendance and marks must be finite and within 0–100; study hours must be finite and non-negative; and each row must have six columns.

13. **Why is `ArrayList<Student>` used?**  
    It stores a changeable list of accepted student records in memory after loading the CSV.

14. **What is a limitation of the CSV parser?**  
    It splits on commas and does not support commas inside quoted fields.

## Statistics and Regression

15. **How does the program calculate the median?**  
    It sorts a separate list of final marks. It selects the middle value, or averages the two middle values for an even-sized list.

16. **Why does the program use population standard deviation?**  
    It treats all currently loaded records as the full dataset being summarized and divides the squared differences by the number of students.

17. **What do the grade categories represent?**  
    They group final marks: A+ from 90, A from 80, B from 70, C from 60, D from 50, and F below 50.

18. **What are X and Y in the regression model?**  
    X is daily study hours, and Y is final marks. The model estimates final marks from study hours.

19. **What do slope and intercept mean in the regression equation?**  
    The slope is the model's estimated change in marks for one more study hour; the intercept is the model's estimated marks when study hours are zero.

20. **What do R² and MSE tell us, and what are the model's limitations and possible improvements?**  
    R² summarizes how much variation in marks the fitted line accounts for, while MSE is the average squared prediction error. This simple model uses only study hours and does not prove causation. Future improvements could support quoted CSV fields, explain individual skipped rows, and include interactive GUI testing.
