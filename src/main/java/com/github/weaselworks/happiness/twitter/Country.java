package com.github.weaselworks.happiness.twitter;

/**
 * Created by nick on 11/11/2014.
 */
public class Country {

    private double totalScore;
    private long numReadings;
    private String code;

    public Country(String alpha3Code, double score) {

        this.code = alpha3Code;
        this.totalScore = score;
        this.numReadings = 1;

    }

    public String getAlpha3Code() {
        return code;
    }

    public double getAverageScore() {

        return totalScore / numReadings;

    }

    public void addScore(double score) {
        totalScore += score;
        numReadings++;
    }




}
