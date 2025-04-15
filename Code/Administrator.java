import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The Administrator class represents an administrator in the hospital management system.
 * Administrators have the ability to view and manage hospital staff, medication inventory,
 * appointments, and handle replenishment requests.
 */
public class Administrator extends User {
    private static Scanner sc = new Scanner(System.in);

    /**
     * Constructor for Administrator.
     *
     * @param userID   The unique ID of the administrator.
     * @param name     The name of the administrator.
     * @param gender   The gender of the administrator.
     */
    public Administrator(String userID, String name, String gender) {
        super(userID, null, name, gender, "Administrator"); // Default password is null
    }

    /**
     * Displays and manages hospital staff such as doctors, pharmacists, and administrators.
     *
     * @param doctors        The list of doctors.
     * @param pharmacists    The list of pharmacists.
     * @param administrators The list of administrators.
     * @param roles          Array of roles for role-based operations.
     */
    public void viewAndManageHospitalStaff(List<Doctor> doctors, List<Pharmacist> pharmacists, List<Administrator> administrators, String[] roles) {
        try {
            System.out.println("""
                    Hospital Staff Menu: 
                    1. View Hospital Staff
                    2. Manage Hospital Staff
                    Choose options (1-2): """);
            int choice1 = sc.nextInt();
            int choice2, i;

            switch(choice1) {
                case 1: // View Hospital Staff
                    System.out.println(""" 
                            View Hospital Staff:
                            1. Doctors
                            2. Pharmacists
                            3. Administrators
                            Choose which hospital staff to view (1-3): """);
                    choice2 = sc.nextInt();

                    switch (choice2) {
                        case 1:
                            i = 1;
                            if (doctors.isEmpty()) {
                                System.out.println("No doctors in the system.");
                                break;
                            }
                            for (Doctor doctor : doctors) {
                                System.out.println(i++ + ". Doctor ID: " + doctor.getuserID() + ", Name: " + doctor.getName());
                            }
                            break;
                        case 2:
                            i = 1;
                            if (pharmacists.isEmpty()) {
                                System.out.println("No pharmacists in the system.");
                                break;
                            }
                            for (Pharmacist pharmacist : pharmacists) {
                                System.out.println(i++ + ". Pharmacist ID: " + pharmacist.getuserID() + ", Name: " + pharmacist.getName());
                            }
                            break;
                        case 3:
                            i = 1;
                            if (administrators.isEmpty()) {
                                System.out.println("No administrators in the system.");
                                break;
                            }
                            for (Administrator administrator : administrators) {
                                System.out.println(i++ + ". Administrator ID: " + administrator.getuserID() + ", Name: " + administrator.getName());
                            }
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            break;
                    }
                    break;
                case 2: // Manage Hospital Staff
                    System.out.println(""" 
                            1. Add Staff
                            2. Update Staff
                            3. Remove Staff
                            Enter action (1-3): """);
                    choice2 = sc.nextInt();

                    switch(choice2) {
                        case 1: // Add Staff
                            addStaff(doctors, pharmacists, administrators, roles);
                            break;
                        case 2: // Update Staff
                            updateStaffRole(doctors, pharmacists, administrators, roles);
                            break;
                        case 3: // Remove Staff
                            removeStaff(doctors, pharmacists, administrators);
                            break;
                        default:
                            System.out.println("Invalid action. Please try again.");
                            break;
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            sc.nextLine(); // Clear the invalid input from the scanner buffer
        }
    }
    
    /**
     * Adds new staff to the hospital system based on the role.
     *
     * @param doctors        The list of doctors.
     * @param pharmacists    The list of pharmacists.
     * @param administrators The list of administrators.
     * @param roles          Array of roles for role-based operations.
     */
    public void addStaff(List<Doctor> doctors, List<Pharmacist> pharmacists, List<Administrator> administrators, String[] roles) {
        System.out.println("Enter Staff ID: ");
        String staffID = sc.next();
        System.out.println("Enter Name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.println("Enter Gender: ");
        String gender = sc.next();

        System.out.println("""
                Enter Staff Role:
                1. Doctor
                2. Pharmacist
                3. Administrator
                Choose options (1-3): """);
        int role = sc.nextInt();

        switch (role) {
            case 1:
                System.out.println("Enter Specialty: ");
                sc.nextLine();
                String specialty = sc.nextLine();
                Doctor doctor = new Doctor(staffID, name, gender, specialty);
                doctors.add(doctor);
                break;
            case 2:
                Pharmacist pharmacist = new Pharmacist(staffID, name, gender);
                pharmacists.add(pharmacist);
                break;
            case 3:
                Administrator administrator = new Administrator(staffID, name, gender);
                administrators.add(administrator);
                break;
            default:
                System.out.println("Invalid Option. Please try again.");
                break;
        }

        System.out.println("Staff " + name + " with ID " + staffID + " added as " + roles[role]);
    }

    /**
     * Updates the role of an existing staff member in the hospital system.
     *
     * @param doctors        The list of doctors.
     * @param pharmacists    The list of pharmacists.
     * @param administrators The list of administrators.
     * @param roles          Array of roles for role-based operations.
     */
    public void updateStaffRole(List<Doctor> doctors, List<Pharmacist> pharmacists, List<Administrator> administrators, String[] roles) {
        System.out.println("Enter Staff ID: ");
        String staffID = sc.next();
        System.out.println("""
                Enter Original Staff Role:
                1. Doctor
                2. Pharmacist
                3. Administrator
                Choose options (1-3): """);
        int role = sc.nextInt();

        System.out.println("""
                Enter New Staff Role:
                1. Doctor
                2. Pharmacist
                3. Administrator
                Choose options (1-3): """);
        int newRole = sc.nextInt();
        
        if (role == newRole) {
            System.out.println("Staff is already of role " + roles[role]);
        } else {
            switch (role) {
                case 1:
                    Doctor doctor = Doctor.findDoctorByID(staffID, doctors);
                    
                    if (doctor == null) {
                        System.out.println("Doctor not found");
                        return;
                    }

                    doctor.updateRole(doctor, newRole, pharmacists, administrators);
                    doctors.remove(doctor);
                    break;
                case 2:
                    Pharmacist pharmacist = Pharmacist.findPharmacistByID(staffID, pharmacists);
                    
                    if (pharmacist == null) {
                        System.out.println("Pharmacist not found");
                        return;
                    }
                    
                    pharmacist.updateRole(pharmacist, newRole, doctors, administrators);
                    pharmacists.remove(pharmacist);
                    break;
                case 3:
                    Administrator administrator = Administrator.findAdministratorByID(staffID, administrators);
                    
                    if (administrator == null) {
                        System.out.println("Administrator not found");
                        return;
                    }
                    
                    administrator.updateRole(administrator, newRole, doctors, pharmacists);
                    administrators.remove(administrator);
                    break;
                default:
                    System.out.println("Invalid Option. Please try again.");
                    break;
            }

            System.out.println("Staff ID " + staffID + " role updated to: " + roles[newRole]);
        }
    }

    /**
     * Removes a staff member from the hospital system based on their ID and role.
     *
     * @param doctors        The list of doctors.
     * @param pharmacists    The list of pharmacists.
     * @param administrators The list of administrators.
     */
    public void removeStaff(List<Doctor> doctors, List<Pharmacist> pharmacists, List<Administrator> administrators) {
        System.out.println("Enter Staff ID: ");
        String staffID = sc.next();
        System.out.println("""
                Enter Staff Role:
                1. Doctor
                2. Pharmacist
                3. Administrator
                Choose options (1-3): """);
        int role = sc.nextInt();

        switch (role) {
            case 1:
                Doctor doctor = Doctor.findDoctorByID(staffID, doctors);
                
                if (doctor == null) {
                    System.out.println("Doctor not found");
                    return;
                }
                
                doctors.remove(doctor);
                break;
            case 2:
                Pharmacist pharmacist = Pharmacist.findPharmacistByID(staffID, pharmacists);
                
                if (pharmacist == null) {
                    System.out.println("Pharmacist not found");
                    return;
                }
                
                pharmacists.remove(pharmacist);
                break;
            case 3:
                Administrator administrator = Administrator.findAdministratorByID(staffID, administrators);
                
                if (administrator == null) {
                    System.out.println("Administrator not found");
                    return;
                }
                
                administrators.remove(administrator);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }

        System.out.println("Staff ID " + staffID + " has been removed.");
    }

    /**
     * Displays all upcoming confirmed appointments in the scheduling system.
     *
     * @param schedulingSystem The scheduling system instance.
     */
    public void viewAllUpcomingConfirmedAppointments(SchedulingSystem schedulingSystem) {
        schedulingSystem.displayUpcomingConfirmedAppointments();
    }

    /**
     * Displays all appointments in the scheduling system.
     *
     * @param schedulingSystem The scheduling system instance.
     */
    public void viewAllAppointments(SchedulingSystem schedulingSystem) {
        schedulingSystem.displayAllAppointments();
    }

    /**
     * Displays all pending appointments in the scheduling system.
     *
     * @param schedulingSystem The scheduling system instance.
     */
    public void viewPendingAppointments(SchedulingSystem schedulingSystem) {
        schedulingSystem.displayPendingAppointments();
    }
    
    /**
     * Displays all completed appointments in the scheduling system.
     *
     * @param schedulingSystem The scheduling system instance.
     */
    public void displayCompletedAppointments(SchedulingSystem schedulingSystem){
        schedulingSystem.displayCompletedAppointments();
    }

    /**
     * Allows viewing and management of the medication inventory.
     *
     * @param inventory The inventory instance.
     */
    public void viewAndManageMedicationInventory(Inventory inventory) {
        try {
            System.out.println("""
                    Medication Inventory Menu: 
                    1. View Medication Inventory
                    2. Manage Medication Inventory
                    Choose options (1-2): """);
            int choice1 = sc.nextInt();
            int choice2;

            switch(choice1) {
                case 1: // View Medication Inventory
                    inventory.displayInventory(false);
                    break;
                case 2: // Manage Medication Inventory
                    System.out.println(""" 
                        1. Add New Medication to Inventory
                        2. Remove Existing Medication from Inventory
                        3. Update Existing Inventory Stock
                        4. Update Low Stock Alert Levels
                        Enter action (1-4): """);
                    choice2 = sc.nextInt();

                    switch (choice2) {
                        case 1: // Add New Medication to Inventory
                            System.out.println("Add New Medication to Inventory: ");
                            inventory.addMedication();
                            break;
                        case 2: // Remove Existing Medication from Inventory
                            System.out.println("Remove Existing Medication from Inventory: ");
                            inventory.displayInventory(false);
                            inventory.removeMedication();
                            break;
                        case 3: // Update Existing Inventory Stock
                            System.out.println("Update Existing Inventory Stock: ");
                            inventory.displayInventory(false);
                            inventory.updateStock();
                            break;
                        case 4: // Update Low Stock Levels
                            System.out.println("Update Low Stock Levels: ");
                            inventory.displayInventory(false);
                            inventory.updateLowStockAlert();
                        default:
                            break;
                    }
                    break;
                default:
                    System.out.println("Invalid Option. Please try again.");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            sc.nextLine(); // Clear the invalid input from the scanner buffer
        }

        
    }

    /**
     * Approves medication replenishment requests from the inventory.
     *
     * @param inventory The inventory instance.
     */
    public void approveReplenishmentRequest(Inventory inventory) {
        boolean empty = inventory.viewReplenishmentRequests();
        if (empty == false) {
            System.out.println("Medication ID to Replenish: ");
            String medicationID = sc.next();
            inventory.fulfillReplenishmentRequest(medicationID);
        } else {
            System.out.println("No Replenishment Requests.");
        }
    }

    /**
     * Updates the role of an administrator to a new role.
     *
     * @param administrator The administrator to update.
     * @param newRole       The new role to assign.
     * @param doctors       The list of doctors.
     * @param pharmacists   The list of pharmacists.
     */
    public void updateRole(Administrator administrator, int newRole, List<Doctor> doctors, List<Pharmacist> pharmacists) {
        switch (newRole) {
            case 1:
                this.role = "Doctor";
                System.out.println("Enter Doctor Specialty: ");
                sc.nextLine();
                String specialty = sc.nextLine();
                Doctor doctor = new Doctor(administrator.getuserID(), administrator.getName(), administrator.getGender(), specialty);
                doctors.add(doctor);
                break;
            case 2:
                this.role = "Pharmacist";
                Pharmacist pharmacist = new Pharmacist(administrator.getuserID(), administrator.getName(), administrator.getGender());
                pharmacists.add(pharmacist);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                return;
        }
    }

    /**
     * Finds an administrator by their ID.
     *
     * @param administratorID The ID of the administrator to find.
     * @param administrators  The list of administrators.
     * @return The administrator instance if found, otherwise null.
     */
    public static Administrator findAdministratorByID(String administratorID, List<Administrator> administrators) {
        for (Administrator administrator : administrators) {
            if (administrator.getuserID().equals(administratorID)) {
                return administrator;
            }
        }
        return null;
    }


     /**
     * Displays the menu options for an administrator.
     */
    @Override
    public void displayMenu() {
        System.out.println("""
                
                Administrator Display Menu: 
                1. View and Manage Hospital Staff
                2. View All Appointments 
                3. View Only Upcoming Confirmed Appointments
                4. View Only Pending Appointments
                5. View All Completed Appointments
                6. View and Manage Medication Inventory
                7. Approve Replenishment Requests
                8. Logout
                Choose options (1-8): """);
    }
}
