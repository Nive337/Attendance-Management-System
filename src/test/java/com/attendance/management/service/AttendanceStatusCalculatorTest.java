package com.attendance.management.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttendanceStatusCalculatorTest {

    @Test
    void zeroClasses_isNoData_notZeroPercentWarning() {
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(0, 0);
        assertEquals("NO_DATA", result.status());
        assertEquals(0.0, result.percentage());
    }

    @Test
    void exactlySeventyFivePercent_isGood_notWarning() {
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(4, 3); // 75.0%
        assertEquals(75.0, result.percentage());
        assertEquals("GOOD", result.status());
    }

    @Test
    void justBelowSeventyFivePercent_isWarning() {
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(100, 74); // 74.0%
        assertEquals("WARNING", result.status());
    }

    @Test
    void julyExample_eightyPercent_isGood() {
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(5, 4); // 80.0%
        assertEquals(80.0, result.percentage());
        assertEquals("GOOD", result.status());
    }

    @Test
    void augustExample_seventyPercent_isWarning() {
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(10, 7); // 70.0%
        assertEquals(70.0, result.percentage());
        assertEquals("WARNING", result.status());
    }

    @Test
    void septemberExample_roundsToTwoDecimalPlaces() {
        AttendanceStatusCalculator.Result result = AttendanceStatusCalculator.calculate(15, 10); // 66.666...%
        assertEquals(66.67, result.percentage());
        assertEquals("WARNING", result.status());
    }
}