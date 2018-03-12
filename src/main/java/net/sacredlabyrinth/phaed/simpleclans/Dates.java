package net.sacredlabyrinth.phaed.simpleclans;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author phaed
 */

public class Dates {

    private Dates() {
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInMonths(Timestamp date1, Timestamp date2) {
        return differenceInMonths(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInYears(Timestamp date1, Timestamp date2) {
        return differenceInYears(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInDays(Timestamp date1, Timestamp date2) {
        return differenceInDays(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInHours(Timestamp date1, Timestamp date2) {
        return differenceInHours(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInMinutes(Timestamp date1, Timestamp date2) {
        return differenceInMinutes(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInSeconds(Timestamp date1, Timestamp date2) {
        return differenceInSeconds(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    private static double differenceInMilliseconds(Timestamp date1, Timestamp date2) {
        return differenceInMilliseconds(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInMonths(Date date1, Date date2) {
        return differenceInYears(date1, date2) * 12;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInYears(Date date1, Date date2) {
        double days = differenceInDays(date1, date2);
        return days / 365.2425;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInDays(Date date1, Date date2) {
        return differenceInHours(date1, date2) / 24.0;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInHours(Date date1, Date date2) {
        return differenceInMinutes(date1, date2) / 60.0;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInMinutes(Date date1, Date date2) {
        return differenceInSeconds(date1, date2) / 60.0;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static double differenceInSeconds(Date date1, Date date2) {
        return differenceInMilliseconds(date1, date2) / 1000.0;
    }

    private static double differenceInMilliseconds(Date date1, Date date2) {
        return Math.abs(getTimeInMilliseconds(date1) - getTimeInMilliseconds(date2));
    }

    private static long getTimeInMilliseconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());
    }

    /**
     * Returns the string representation of the amount of time labeled as days, hours, minutes, seconds
     * @author RoboMWM, LaxWasHere
     * @param seconds the time in seconds
     * @param depth Max amount of detail (e.g. only display days and hours if set to 1 and seconds > 1 day)
     * @return
     */
    public static String formatTime(Long seconds, int depth)
    {
        if (seconds == null || seconds < 1) {
            return "moments";
        }

        if (seconds < 60) {
            return seconds + " seconds";
        }

        if (seconds < 3600) {
            Long count = (long) Math.ceil(seconds / 60);
            String res;
            if (count > 1) {
                res = count + " minutes";
            } else {
                res = "1 minute";
            }
            Long remaining = seconds % 60;
            if (depth > 0 && remaining >= 5) {
                return res + ", " + formatTime(remaining, --depth);
            }
            return res;
        }
        if (seconds < 86400) {
            Long count = (long) Math.ceil(seconds / 3600);
            String res;
            if (count > 1) {
                res = count + " hours";
            } else {
                res = "1 hour";
            }
            if (depth > 0) {
                return res + ", " + formatTime(seconds % 3600, --depth);
            }
            return res;
        }
        Long count = (long) Math.ceil(seconds / 86400);
        String res;
        if (count > 1) {
            res = count + " days";
        } else {
            res = "1 day";
        }
        if (depth > 0) {
            return res + ", " + formatTime(seconds % 86400, --depth);
        }
        return res;
    }
}
