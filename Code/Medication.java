import java.io.Serializable;
import java.util.List;

/**
 * Represents a medication in the hospital inventory system.
 * Each medication has a unique ID, name, quantity, low stock alert level,
 * and a replenishment request amount. Implements `Serializable` for persistence.
 */
public class Medication implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The unique ID of the medication. */
    private String medicationID;
    /** The name of the medication. */
    private String name;
    /** The current quantity of the medication in stock. */
    private int quantity;
    /** The current replenishment request amount for the medication. */
    private int replenishmentRequest;
    /** The low stock alert threshold for the medication. */
    private int lowStockAlert;

    /**
     * Constructs a new `Medication` object with the specified parameters.
     *
     * @param medicationID     The unique ID of the medication.
     * @param name             The name of the medication.
     * @param quantity         The initial quantity of the medication in stock.
     * @param lowStockAlert    The low stock alert threshold for the medication.
     */
    public Medication(String medicationID, String name, int quantity, int lowStockAlert) {
        this.medicationID = medicationID;
        this.name = name;
        this.quantity = quantity;
        this.replenishmentRequest = 0; // default
        this.lowStockAlert = lowStockAlert;
    }

    /**
 * Retrieves the name of the medication.
 *
 * @return The name of the medication.
 */
    public String getName() {
        return this.name;
    }

    /**
 * Retrieves the unique ID of the medication.
 *
 * @return The unique ID of the medication.
 */
    public String getMedicationID() {
        return this.medicationID;
    }

    /**
 * Retrieves the current quantity of the medication in stock.
 *
 * @return The current stock quantity.
 */
    public int getQuantity() {
        return this.quantity;
    }

    /**
 * Retrieves the current replenishment request amount for the medication.
 *
 * @return The replenishment request amount.
 */
    public int getReplenishmentRequest() {
        return this.replenishmentRequest;
    }

    /**
 * Retrieves the low stock alert threshold for the medication.
 *
 * @return The low stock alert threshold.
 */
    public int getLowStockAlert() {
        return this.lowStockAlert;
    }

    /**
 * Updates the quantity of the medication in stock.
 *
 * @param newQuantity The new stock quantity for the medication.
 */
    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    /**
 * Updates the low stock alert threshold for the medication.
 *
 * @param newQuantity The new low stock alert threshold.
 */
    public void updateLowStockAlert(int newQuantity) {
        this.lowStockAlert = newQuantity;
    }

    /**
 * Updates the replenishment request amount for the medication.
 *
 * @param replenishmentAmount The new replenishment request amount.
 */
    public void updateReplenishmentRequest(int replenishmentAmount) {
        this.replenishmentRequest = replenishmentAmount;
    }

    /**
 * Checks if the medication quantity is below the low stock alert threshold.
 *
 * @return `true` if the stock is below the alert threshold, otherwise `false`.
 */
    public boolean getLevelAlert() {
        if (this.quantity < this.getLowStockAlert()) {
            return true;
        }
        
        return false;
    }

    /**
 * Finds a medication in a list by its unique ID.
 *
 * @param medicationID The unique ID of the medication to find.
 * @param medications  The list of medications to search in.
 * @return The `Medication` object if found, otherwise `null`.
 */
    public static Medication findMedicationByID(String medicationID, List<Medication> medications) {
        for (Medication medication : medications) {
            if (medication.getMedicationID().equals(medicationID)) {
                return medication;
            }
        }
        return null;
    }

}
