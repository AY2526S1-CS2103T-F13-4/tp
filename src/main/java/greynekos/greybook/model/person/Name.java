package greynekos.greybook.model.person;

import static greynekos.greybook.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's name in the GreyBook. Guarantees: immutable; is valid
 * as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphabets, spaces, and certain special characters, and it should not be blank";

    /*
     * The first character of the greybook must not be a whitespace, otherwise " "
     * (a blank string) becomes a valid input. Follows
     * "https://partnersupport.singpass.gov.sg/hc/en-sg/articles/32733563138585-What-are-the-special-characters-allowed-in-Myinfo-Name-data-item"
     */
    public static final String VALIDATION_REGEX = "[a-zA-Z,()/.@\\-'][a-zA-Z,()/.@\\-' ]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name
     *            A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
