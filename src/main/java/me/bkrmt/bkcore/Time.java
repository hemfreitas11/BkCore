package me.bkrmt.bkcore;

import java.util.concurrent.TimeUnit;

public class Time {
    private final long milliseconds;
    private final long seconds;
    private final long minutes;
    private final long hours;
    private final long days;

    public Time(long milliseconds) {
        this.milliseconds = milliseconds;
        seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        days = TimeUnit.MILLISECONDS.toDays(milliseconds);
    }

    public String getFormatedTime() {
        return String.format(
                "%s:%s:%s",
                getHours() < 10 ? "0" + getHours() : getHours(),
                getMinutes() < 10 ? "0" + getMinutes() : getMinutes(),
                getSeconds() < 10 ? "0" + getSeconds() : getSeconds()
        );
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getHours() {
        return hours;
    }

    public long getDays() {
        return days;
    }

    public static Time ticksToTime(long ticks) {
        long milliseconds = 50 * ticks;
        return new Time(milliseconds);
    }
}
