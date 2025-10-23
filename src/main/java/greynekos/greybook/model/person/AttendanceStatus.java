package greynekos.greybook.model.person;

import static greynekos.greybook.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's attendance status in the GreyBook. Guarantees:
 * immutable; is valid as declared in {@link #isValidStatus(Status)}
 */
public class AttendanceStatus {
    public static final String MESSAGE_CONSTRAINTS =
            "Attendance status should be one of the following: PRESENT, ABSENT, LATE, EXCUSED, NONE";

    /**
     * Enum representing the possible attendance status values.
     */
    public enum Status {
        PRESENT("Present"), ABSENT("Absent"), LATE("Late"), EXCUSED("Excused"), NONE("None");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public final Status value;

    /**
     * Constructs an {@code AttendanceStatus} with default value
     * {@link Status#NONE}.
     */
    public AttendanceStatus() {
        this.value = Status.NONE;
    }

    /**
     * Constructs an {@code AttendanceStatus} with the given {@link Status}.
     *
     * @param status
     *            A valid attendance status.
     * @throws NullPointerException
     *             if {@code status} is null.
     * @throws IllegalArgumentException
     *             if {@code status} is invalid.
     */
    public AttendanceStatus(Status status) {
        requireNonNull(status);
        checkArgument(isValidStatus(status), MESSAGE_CONSTRAINTS);
        this.value = status;
    }

    /**
     * Constructs an {@code AttendanceStatus} from a string.
     *
     * @param statusString
     *            The string representation of the status.
     * @throws NullPointerException
     *             if {@code statusString} is null.
     * @throws IllegalArgumentException
     *             if {@code statusString} does not match any Status.
     */
    public AttendanceStatus(String statusString) {
        requireNonNull(statusString);
        this.value = parseStatus(statusString);
    }

    /**
     * Returns true if a given {@link Status} is valid.
     *
     * @param testStatus
     *            The status to test.
     * @return true if valid, false otherwise.
     */
    public static boolean isValidStatus(Status testStatus) {
        return testStatus != null;
    }

    /**
     * Returns true if a given string corresponds to a valid {@link Status}.
     *
     * @param test
     *            The string to test.
     * @return true if valid, false otherwise.
     */
    public static boolean isValidStatus(String test) {
        if (test == null) {
            return false;
        }
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a string to its corresponding {@link Status}.
     *
     * @param statusString
     *            The string to parse.
     * @return The corresponding {@link Status}.
     * @throws IllegalArgumentException
     *             if no matching status is found.
     */
    private static Status parseStatus(String statusString) {
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(statusString)) {
                return s;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AttendanceStatus)) {
            return false;
        }

        AttendanceStatus otherStatus = (AttendanceStatus) other;
        return value == otherStatus.value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
