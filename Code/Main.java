import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The `Main` class serves as the entry point for the Hospital Management System (HMS).
 * It manages the application's lifecycle, including user authentication, menu navigation,
 * and role-specific functionality for patients, doctors, pharmacists, and administrators.
 */
public class Main {
    private static final String USER_DATA_FILE = "user_data.ser";
    
    private static Main instance;
    
    /**
     * Constructs the singleton instance of the `Main` class.
     * Prevents accidental creation of multiple instances.
     */
    public Main() {
        if (instance == null) {
            instance = this; // Assign this object to the static instance
        } else {
            throw new IllegalStateException("Main instance already exists!"); // Prevent accidental re-creation
        }
    }

    /**
     * Retrieves the singleton instance of the `Main` class.
     *
     * @return The `Main` instance.
     */
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main(); // Lazy initialization if not created yet
        }
        return instance;
    }
    
    public List<Patient> patients = new ArrayList<>();
    public List<Doctor> doctors = new ArrayList<>();
    public List<Pharmacist> pharmacists = new ArrayList<>();
    public List<Administrator> administrators = new ArrayList<>();
    public Inventory inventory = new Inventory();
    private static SchedulingSystem schedulingSystem = new SchedulingSystem();
    private String[] roles = {"Patient", "Doctor", "Pharmacist", "Administrator"};

    /**
 * The entry point for the Hospital Management System (HMS).
 * Handles user login, authentication, and role-specific functionalities.
 *
 * @param args Command-line arguments (not used).
 */
    public static void main(String[] args) {
        Main mainInstance = Main.getInstance();

        // Load serialized users if available, otherwise load from CSV
        mainInstance.loadUserData();

        Scanner sc = new Scanner(System.in);

        // Login and authentication process
        User currentUser = null;
        int option = 0;

        while (option != 2) {
                System.out.println("""
                    Welcome to HMS,
                    1. Login
                    2. Exit """);
            option = sc.nextInt();

            switch (option) {
                case 1: // Login
                    while (true) {
                        System.out.println("Enter User ID: ");
                        String userID = sc.next();
                        System.out.println("Enter Password: ");
                        String password = sc.next();

                        currentUser = mainInstance.authenticateUser(userID, password);

                        if (currentUser != null) {
                            System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
                            break;
                        } else {
                            System.out.println("Invalid credentials. Please try again.");
                        }
                    }
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            // Example menu handling
            boolean loggedIn = true;
            while (loggedIn) {
                try {
                    currentUser.displayMenu();
                    int choice = sc.nextInt();

                    if (currentUser instanceof Patient) {
                        loggedIn = mainInstance.handlePatientOptions((Patient) currentUser, loggedIn, choice, sc);
                    } else if (currentUser instanceof Doctor) {
                        loggedIn = mainInstance.handleDoctorOptions((Doctor) currentUser, loggedIn, choice, sc);
                    } else if (currentUser instanceof Pharmacist) {
                        loggedIn = mainInstance.handlePharmacistOptions((Pharmacist) currentUser, loggedIn, choice, sc);
                    } else if (currentUser instanceof Administrator) {
                        loggedIn = mainInstance.handleAdminOptions((Administrator) currentUser, loggedIn, choice, sc);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    sc.nextLine(); // Clear the invalid input from the scanner buffer
                }
            }

            if (!loggedIn) {
                System.out.println("Successfully logged out.");
            }        
        }

        sc.close();
        mainInstance.saveUserData(); // Save user data before exiting
    }

    /**
 * Saves user data to the serialized file immediately after any change.
 * This method ensures that all updates to the user data (e.g., additions, deletions, or modifications)
 * are persisted without waiting for the application to exit.
 *
 * Usage:
 * - Call this method whenever a change is made to the user data to avoid losing updates.
 */
    public static void saveDataOnChange() {
        Main.getInstance().saveUserData();
    }

    /**
    * Loads user data from a serialized file if it exists; otherwise, initializes
    * data from CSV files and saves it for future use.
    */
    @SuppressWarnings("unchecked")
    private void loadUserData() {
        File file = new File(USER_DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<User> allUsers = (List<User>) ois.readObject();
                for (User user : allUsers) {
                    if (user instanceof Patient) patients.add((Patient) user);
                    else if (user instanceof Doctor) doctors.add((Doctor) user);
                    else if (user instanceof Pharmacist) pharmacists.add((Pharmacist) user);
                    else if (user instanceof Administrator) administrators.add((Administrator) user);
                }
                System.out.println("User data loaded successfully from file.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading user data: " + e.getMessage());
            }
        } else {
            // Load from CSV if no serialized data file exists
            try {
                CSVLoader.loadPatientsFromCSV("Patient_List.csv", this);
                CSVLoader.loadStaffFromCSV("Staff_List.csv", this);
                CSVLoader.loadMedicineFromCSV("Medicine_List.csv", this);
                System.out.println("User data loaded from CSV files.");
                saveUserData(); // Save to serialized file for future runs
            } catch (IOException e) {
                System.err.println("Error loading data from CSV files: " + e.getMessage());
            }
        }
    }

    /**
    * Saves all user data to a serialized file for persistence.
    */
    private void saveUserData() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(patients);
        allUsers.addAll(doctors);
        allUsers.addAll(pharmacists);
        allUsers.addAll(administrators);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(allUsers);
            System.out.println("User data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    /**
    * Authenticates a user by checking their credentials against existing users.
    *
    * @param userID   The user's unique ID.
    * @param password The user's password.
     * @return The authenticated `User` object if credentials are valid, otherwise `null`.
    */
    private User authenticateUser(String userID, String password) {
        for (Patient patient : patients) {
            if (patient.login(userID, password)) return patient;
        }
        for (Doctor doctor : doctors) {
            if (doctor.login(userID, password)) return doctor;
        }
        for (Pharmacist pharmacist : pharmacists) {
            if (pharmacist.login(userID, password)) return pharmacist;
        }
        for (Administrator admin : administrators) {
            if (admin.login(userID, password)) return admin;
        }
        return null;
    }

    /**
 * Handles menu options for a logged-in patient.
 *
 * @param patient   The logged-in `Patient`.
 * @param loggedIn  The current login state.
 * @param choice    The menu option selected by the patient.
 * @param sc        The scanner instance for input.
 * @return `false` if the patient logs out, otherwise `true`.
 */
    private boolean handlePatientOptions(Patient patient, boolean loggedIn, int choice, Scanner sc) {
        switch (choice) {
            case 1 -> patient.viewPatientInformation();
            case 2 -> patient.updateContactInfo();
            case 3 -> patient.scheduleAppointment(schedulingSystem);
            case 4 -> patient.rescheduleAppointment(schedulingSystem);
            case 5 -> patient.cancelAppointment(schedulingSystem);
            case 6 -> patient.viewScheduledAppointments(schedulingSystem);
            case 7 -> patient.viewPastAppointmentOutcomeRecords();
            case 8 -> {
                loggedIn = false;
                System.out.println("Logging out.");
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
        return loggedIn;
    }

    /**
 * Handles the menu options available to a doctor after login.
 *
 * @param doctor    The logged-in `Doctor` object.
 * @param loggedIn  The current login state of the doctor.
 * @param choice    The menu option selected by the doctor.
 * @param sc        The `Scanner` instance for input.
 * @return `false` if the doctor chooses to log out, otherwise `true`.
 */
    private boolean handleDoctorOptions(Doctor doctor, boolean loggedIn, int choice, Scanner sc) {
        switch (choice) {
            case 1 -> {
                doctor.viewAllPatients(patients);
                System.out.println("Enter patient ID to view medical record: ");
                String patientID = sc.next();
                Patient patient = findPatientByID(patientID);
                if (patient != null) {
                    doctor.viewPatientMedicalRecord(patient);
                } else {
                    System.out.println("Patient not found.");
                }
            }
            case 2 -> {
                doctor.viewAllPatients(patients);
                System.out.println("Enter patient ID to update medical record: ");
                String patientID = sc.next();
                Patient patient = findPatientByID(patientID);
                if (patient == null) {
                    System.out.println("Patient Not Found. Please Try Again.");
                } else {
                    doctor.updatePatientMedicalRecord(patient);
                }
            }
            case 3 -> doctor.viewPersonalSchedule();
            case 4 -> doctor.setAvailability();
            case 5 -> doctor.respondToAppointmentRequests(schedulingSystem);
            case 6 -> doctor.viewUpcomingAppointments();
            case 7 -> doctor.recordAppointmentOutcome(inventory);
            case 8 -> {
                loggedIn = false;
                System.out.println("Logging out.");
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
        return loggedIn;
    }

    /**
 * Handles the menu options for a pharmacist.
 * Depending on the user's choice, this method allows the pharmacist to view appointment outcomes,
 * update prescription statuses, manage inventory, or submit replenishment requests.
 * The method also provides an option to log out.
 *
 * @param pharmacist The `Pharmacist` object representing the logged-in pharmacist.
 * @param loggedIn   A boolean indicating whether the user is logged in.
 * @param choice     The menu choice selected by the pharmacist.
 * @param sc         The `Scanner` object for user input.
 * @return `true` if the pharmacist remains logged in; `false` if the pharmacist logs out.
 */
private boolean handlePharmacistOptions(Pharmacist pharmacist, boolean loggedIn, int choice, Scanner sc) {
    switch (choice) {
        case 1 -> pharmacist.viewAppointmentOutcomeRecord(patients);
        case 2 -> pharmacist.updatePrescriptionStatus(patients, inventory);
        case 3 -> pharmacist.viewInventory(inventory);
        case 4 -> pharmacist.submitReplenishmentRequest(inventory);
        case 5 -> {
            loggedIn = false;
            System.out.println("Logging out.");
        }
        default -> System.out.println("Invalid option. Please try again.");
    }
    return loggedIn;
}


    /**
 * Handles the menu options available to an administrator after login.
 *
 * @param admin     The logged-in `Administrator` object.
 * @param loggedIn  The current login state of the administrator.
 * @param choice    The menu option selected by the administrator.
 * @param sc        The `Scanner` instance for input.
 * @return `false` if the administrator chooses to log out, otherwise `true`.
 */
    private boolean handleAdminOptions(Administrator admin, boolean loggedIn, int choice, Scanner sc) {
        switch (choice) {
            case 1 -> admin.viewAndManageHospitalStaff(doctors, pharmacists, administrators, roles);
            case 2 -> admin.viewAllAppointments(schedulingSystem);
            case 3-> admin.viewAllUpcomingConfirmedAppointments(schedulingSystem);
            case 4->admin.viewPendingAppointments(schedulingSystem);
            case 5->admin.displayCompletedAppointments(schedulingSystem);    
            case 6 -> admin.viewAndManageMedicationInventory(inventory);
            case 7 -> admin.approveReplenishmentRequest(inventory);
            case 8 -> {
                loggedIn = false;
                System.out.println("Logging out.");
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
        return loggedIn;
    }

    /**
 * Finds a patient in the system by their unique ID.
 *
 * @param patientID The unique ID of the patient.
 * @return The `Patient` object if found, otherwise `null`.
 */
    private Patient findPatientByID(String patientID) {
        for (Patient patient : patients) {
            if (patient.getuserID().equals(patientID)) {
                return patient;
            }
        }
        return null;
    }

    /**
 * Retrieves the singleton instance of the scheduling system.
 *
 * @return The `SchedulingSystem` instance.
 */
    public static SchedulingSystem getSchedulingSystem(){
        return schedulingSystem;
    }
}
