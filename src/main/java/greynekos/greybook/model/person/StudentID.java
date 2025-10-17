package greynekos.greybook.model.person;

import static greynekos.greybook.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's student ID in the GreyBook. Guarantees: immutable; is
 * valid as declared in {@link #isValidStudentID(String)}
 */
public class StudentID implements PersonIdentifier {

    public static final String MESSAGE_CONSTRAINTS =
            "Student IDs should be in the format A0000000Y, where the first letter must be 'A', "
                    + "followed by exactly 7 digits, and ending with any English letter (A-Z or a-z)";
    public static final String VALIDATION_REGEX = "^(?:A\\d{7}|U\\d{6,7})[YXWURNMLJHEAB]$";
    public final String value;

    /**
     * Constructs a {@code StudentID}.
     *
     * @param studentID
     *            A valid student ID.
     */
    public StudentID(String studentID) {
        requireNonNull(studentID);
        checkArgument(isValidStudentID(studentID), MESSAGE_CONSTRAINTS);
        value = studentID;
    }

    /**
     * Returns true if a given string is a valid student ID.
     */
    public static boolean isValidStudentID(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }
        return StudentID.isValidStudentIDChecksum(test);
    }

    /**
     * Returns true if a given student ID checksum is valid.
     */
    private static boolean isValidStudentIDChecksum(String test) {
        int checksumIndex = test.length() - 1;
        String values = test.substring(0, checksumIndex);
        char checksum = test.charAt(checksumIndex);

        // Discard 3rd digit from U-prefixed NUSNET ID (e.g., U1x45678 → U145678)
        if (values.charAt(0) == 'U' && values.length() == 8) {
            values = values.substring(0, 3) + values.substring(4);
        }

        int[] weights;
        if (values.charAt(0) == 'U') {
            weights = new int[]{0, 1, 3, 1, 2, 7};
        } else { // 'A'
            weights = new int[]{1, 1, 1, 1, 1, 1};
        }

        String digits = values.substring(values.length() - 6);
        int sum = 0;

        for (int i = 0; i < 6; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += weights[i] * digit;
        }

        char checkChar = "YXWURNMLJHEAB".charAt(sum % 13);
        return checksum == checkChar;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentID)) {
            return false;
        }

        StudentID otherStudentID = (StudentID) other;
        return value.equals(otherStudentID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
