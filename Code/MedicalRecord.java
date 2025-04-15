import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a medical record for a patient.
 * A medical record contains the patient's ID, blood type, diagnoses, and treatments.
 * Implements `Serializable` for persistence and data transfer.
 */
public class MedicalRecord implements Serializable{
    private static final long serialVersionUID = 1L;
    /** The unique ID of the patient associated with this medical record. */
    private String patientID;
    /** The blood type of the patient. */
    private String bloodType;
    /** A list of diagnoses for the patient. */
    private List<String> diagnoses;
    /** A list of treatments administered to the patient. */
    private List<String> treatments;

    /**
     * Constructs a new `MedicalRecord` with the specified patient ID and blood type.
     *
     * @param patientID The unique ID of the patient.
     * @param bloodType The blood type of the patient.
     */
    public MedicalRecord(String patientID, String bloodType) {
        this.patientID = patientID;
        this.bloodType = bloodType;
        this.diagnoses = new ArrayList<>();
        this.treatments = new ArrayList<>();
    }

    /**
 * Adds a new diagnosis to the patient's medical record.
 * Diagnoses are stored as a list and can include multiple entries over time.
 *
 * @param diagnosis The diagnosis to be added to the medical record.
 */
    public void addDiagnosis(String diagnosis) {
        diagnoses.add(diagnosis);
    }

    /**
 * Adds a new treatment to the patient's medical record.
 * Treatments are stored as a list and can include multiple entries over time.
 *
 * @param treatment The treatment to be added to the medical record.
 */
    public void addTreatment(String treatment) {
        treatments.add(treatment);
    }

    /**
 * Provides a string representation of the patient's medical record.
 * The string includes the patient ID, blood type, list of diagnoses, and list of treatments.
 *
 * @return A formatted string containing the details of the medical record.
 */
    @Override
    public String toString() {
        return "Patient ID: " + this.patientID + 
        "\nBlood Type: " + this.bloodType + 
        "\nDiagnoses: " + diagnoses + 
        "\nTreatments: " + treatments;
    }
}