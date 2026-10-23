package com.studentanalysis;

public class Student {
    private String studentId;
    private String name;
    private double attendance;
    private double studyHours;
    private double previousMarks;
    private double finalMarks;

    public Student(String studentId, String name, double attendance,
                   double studyHours, double previousMarks, double finalMarks) {
        this.studentId = studentId;
        this.name = name;
        this.attendance = attendance;
        this.studyHours = studyHours;
        this.previousMarks = previousMarks;
        this.finalMarks = finalMarks;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public double getStudyHours() {
        return studyHours;
    }

    public void setStudyHours(double studyHours) {
        this.studyHours = studyHours;
    }

    public double getPreviousMarks() {
        return previousMarks;
    }

    public void setPreviousMarks(double previousMarks) {
        this.previousMarks = previousMarks;
    }

    public double getFinalMarks() {
        return finalMarks;
    }

    public void setFinalMarks(double finalMarks) {
        this.finalMarks = finalMarks;
    }
}
