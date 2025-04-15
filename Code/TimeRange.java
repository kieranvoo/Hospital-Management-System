import java.io.Serializable;
import java.time.LocalTime;

/**
 * Represents a time range with a start time, end time, and an optional event name.
 * Implements {@code Serializable} for persistence.
 */
public class TimeRange implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The start time of the time range. */
    private LocalTime start;
    /** The end time of the time range. */
    private LocalTime end;
    /** The name of the event associated with this time range, if any. */
    private String eventName;

    /**
     * Constructs a {@code TimeRange} with the specified start and end times.
     * The event name is set to {@code null} by default.
     *
     * @param start the start time of the range.
     * @param end   the end time of the range.
     * @throws IllegalArgumentException if the start time is after the end time.
     */
    public TimeRange(LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        this.start = start;
        this.end = end;
        this.eventName = null; // Default to no event name
    }

    /**
     * Constructs a {@code TimeRange} with the specified start time, end time, and event name.
     *
     * @param start     the start time of the range.
     * @param end       the end time of the range.
     * @param eventName the name of the event associated with this time range.
     * @throws IllegalArgumentException if the start time is after the end time.
     */
    public TimeRange(LocalTime start, LocalTime end, String eventName) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        this.start = start;
        this.end = end;
        this.eventName = eventName; // Allow optional event name
    }

    /**
     * Gets the start time of the time range.
     *
     * @return the start time.
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Gets the end time of the time range.
     *
     * @return the end time.
     */
    public LocalTime getEnd() {
        return end;
    }

    /**
     * Gets the event name associated with this time range.
     *
     * @return the event name, or {@code null} if no event name is set.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the event name for this time range.
     *
     * @param eventName the name of the event to associate with this time range.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Checks if this time range contains another time range.
     * A time range contains another if its start time is before or equal to the other range's start time,
     * and its end time is after or equal to the other range's end time.
     *
     * @param other the {@code TimeRange} to check.
     * @return {@code true} if this range contains the other range, {@code false} otherwise.
     */
    public boolean contains(TimeRange other) {
        return !this.start.isAfter(other.start) && !this.end.isBefore(other.end);
    }

    /**
     * Checks if this time range overlaps with another time range.
     * Two ranges overlap if they share any portion of time.
     *
     * @param other the {@code TimeRange} to check.
     * @return {@code true} if the ranges overlap, {@code false} otherwise.
     */
    public boolean overlaps(TimeRange other) {
        return !this.end.isBefore(other.start) && !this.start.isAfter(other.end);
    }

    /**
     * Returns a string representation of this time range.
     * Includes the start time, end time, and the event name if it is set.
     *
     * @return a string representation of this time range.
     */
    @Override
    public String toString() {
        return start + " - " + end + (eventName != null ? " (" + eventName + ")" : "");
    }
}
