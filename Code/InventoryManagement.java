/**
 * Defines the core operations for managing medications in an inventory system.
 * The interface provides methods to add, remove, and update the stock of medications.
 */
public interface InventoryManagement {
    /**
     * Adds a new medication to the inventory.
     * Implementations should ensure that medication IDs and names are unique.
     */
    void addMedication();
    /**
     * Removes a medication from the inventory based on its unique ID.
     * Implementations should handle scenarios where the medication is not found.
     */
    void removeMedication();

    /**
     * Updates the stock quantity of a specific medication.
     * Implementations should handle scenarios where the medication is not found.
     */
    void updateStock();
}