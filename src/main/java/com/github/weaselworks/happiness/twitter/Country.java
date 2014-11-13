package com.github.weaselworks.happiness.twitter;

/**
 * Created by nick on 11/11/2014.
 */
public class Country {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Country.class);

    private Number currentAverage = 0;
    private String code;

    public Country(String alpha3Code, Number score) {

        this.code = alpha3Code;
        this.currentAverage = score;

    }

    public String getAlpha3Code() {
        return code;
    }

    public void updateAverage(Number score) {

        currentAverage = (currentAverage.doubleValue() + score.doubleValue()) / 2;

    }

    public Number getCurrentAverage() {
        return currentAverage;
    }

}
