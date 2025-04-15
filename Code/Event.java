import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an event with a start time, optional end time, and a description.
 * The `Event` class is serializable to allow for persistent storage and retrieval.
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The start time of the event. */
    private LocalDateTime startTime;
    /** The end time of the event, or null if the event has no specified duration. */
    private LocalDateTime endTime;
    /** A brief description of the event's purpose or context. */
    private String description;

    /**
     * Constructs an `Event` with a start time and a description.
     * The end time is left null for events without a specified duration.
     *
     * @param startTime   The start time of the event.
     * @param description A brief description of the event.
     */
    public Event(LocalDateTime startTime, String description) {
        this.startTime = startTime;
        this.endTime = null;
        this.description = description;
    }

    /**
     * Constructs an `Event` with a start time, an end time, and a description.
     *
     * @param startTime   The start time of the event.
     * @param endTime     The end time of the event.
     * @param description A brief description of the event.
     */
    public Event(LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    /**
     * Retrieves the start time of the event.
     * The start time indicates when the event begins.
     *
     * @return The start time of the event, as a {@code LocalDateTime}.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Retrieves the end time of the event, if specified.
     * The end time indicates when the event concludes.
     *
     * @return The end time of the event, as a {@code LocalDateTime}, 
     *         or {@code null} if the event has no specified end time.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Retrieves a brief description of the event.
     * The description provides details about the event's purpose or context.
     *
     * @return A brief description of the event, as a {@code String}.
     */
    public String getDescription() {
        return description;
    }
}
