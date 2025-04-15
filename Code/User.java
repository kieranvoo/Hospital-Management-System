import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Abstract class representing a generic user in the system.
 * Implements {@code Serializable} to allow user objects to be saved and loaded.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Fields
    /** The unique ID of the user. */
    protected String userID;
     /** The hashed password of the user. */
    protected String passwordHash; // Store hashed password
    /** The name of the user. */
    protected String name;
    /** The gender of the user. */
    protected String gender;
    /** The role of the user in the system. */
    protected String role;
     /** Indicates whether this is the user's first login. */
    protected boolean isFirstLogin;
    /** The default password for users. */
    private static final String DEFAULT_PASSWORD = "password";

    /**
     * Constructor for the {@code User} class.
     *
     * @param userID   the user ID of the user.
     * @param password the password for the user (can be null, default password will be used if null).
     * @param name     the name of the user.
     * @param gender   the gender of the user.
     * @param role     the role of the user in the system.
     */
    public User(String userID, String password, String name, String gender, String role) {
        this.userID = userID;
        this.passwordHash = hashPassword(password != null ? password : DEFAULT_PASSWORD);
        this.name = name;
        this.gender = gender;
        this.role = role;
        this.isFirstLogin = password == null || password.equals(DEFAULT_PASSWORD);
    }

    /**
     * Gets the user ID of the user.
     *
     * @return the user ID.
     */
    public String getuserID() {
        return userID;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return the hashed password.
     */
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the gender of the user.
     *
     * @return the gender of the user.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Hashes the given password using SHA-256.
     *
     * @param password the password to be hashed.
     * @return the hashed password as a hexadecimal string.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Authenticates the user by checking the provided user ID and password.
     *
     * @param userID   the user ID provided during login.
     * @param password the password provided during login.
     * @return {@code true} if authentication is successful, {@code false} otherwise.
     */
    public boolean login(String userID, String password) {
        if (this.userID.equals(userID) && this.passwordHash.equals(hashPassword(password))) {
            if (isFirstLogin) {
                System.out.println("Welcome, " + name + "! Since this is your first login, please change your password.");
                if (changePassword()) {
                    Main.saveDataOnChange(); // Save data after password change
                }
                isFirstLogin = false;
            }
            return true;
        }
        return false;
    }

    /**
     * Allows the user to change their password.
     *
     * @return {@code true} if the password was successfully changed.
     */
    public boolean changePassword() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        while (newPassword.equals("password") || newPassword.length() < 6) {
            System.out.println("Password must be different from 'password' and at least 6 characters long.");
            System.out.print("Enter new password: ");
            newPassword = sc.nextLine();
        }
        this.passwordHash = hashPassword(newPassword);
        System.out.println("Password successfully changed!");
        return true; // Indicate password was changed
    }

    /**
     * Abstract method to display a menu for the user.
     * This method must be implemented by subclasses.
     */
    public abstract void displayMenu();
}
