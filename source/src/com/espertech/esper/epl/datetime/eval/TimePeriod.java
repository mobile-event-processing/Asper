/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.epl.datetime.eval;

public class TimePeriod {
    private Integer years;
    private Integer months;
    private Integer weeks;
    private Integer days;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
    private Integer milliseconds;

    public TimePeriod(Integer years, Integer months, Integer weeks, Integer days, Integer hours, Integer minutes, Integer seconds, Integer milliseconds) {
        this.years = years;
        this.months = months;
        this.weeks = weeks;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }

    public Integer getYears() {
        return years;
    }

    public Integer getMonths() {
        return months;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public Integer getDays() {
        return days;
    }

    public Integer getHours() {
        return hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public Integer getMilliseconds() {
        return milliseconds;
    }
}
