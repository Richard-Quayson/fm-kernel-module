package controllers.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Helper functions for handling dates and times.
 * 
 * @author Thomas Quarshie
 */
public class DateUtil {
    
    /** The date and time pattern that is used for conversion. Change as you wish. */
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    
    /** The date and time formatter. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    
    /**
     * Returns the given date and time as a well formatted String.
     * 
     * @param dateTime the date and time to be returned as a string
     * @return formatted string
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    /**
     * Converts a String in the format of the defined {@link DateTimeUtil#DATE_TIME_PATTERN} 
     * to a {@link LocalDateTime} object.
     * 
     * Returns null if the String could not be converted.
     * 
     * @param dateTimeString the date and time as String
     * @return the date and time object or null if it could not be converted
     */
    public static LocalDateTime parse(String dateTimeString) {
        try {
            return DATE_TIME_FORMATTER.parse(dateTimeString, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Checks the String whether it is a valid date and time.
     * 
     * @param dateTimeString
     * @return true if the String is a valid date and time
     */
    public static boolean validDateTime(String dateTimeString) {
        // Try to parse the String.
        return DateUtil.parse(dateTimeString) != null;
    }
}