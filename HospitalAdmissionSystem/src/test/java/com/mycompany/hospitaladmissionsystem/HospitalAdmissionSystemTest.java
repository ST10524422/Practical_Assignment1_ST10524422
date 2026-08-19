package com.mycompany.hospitaladmissionsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HospitalAdmissionSystemTest {

    @BeforeEach
    public void setUp() {
        HospitalAdmissionSystem.patientList.clear();
        HospitalAdmissionSystem.initializeBeds();
    }

    @Test
    public void testRegisterPatientSuccess() {
        Patient p = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        assertTrue(HospitalAdmissionSystem.registerPatient(p));
        assertEquals(1, HospitalAdmissionSystem.patientList.size());
    }

    @Test
    public void testPreventDuplicatePatientID() {
        Patient p1 = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "Jane", "Smith", 25, "Female", "Fever", PatientCategory.EMERGENCY);
        HospitalAdmissionSystem.registerPatient(p1);
        assertFalse(HospitalAdmissionSystem.registerPatient(p2));
    }

    @Test
    public void testSearchPatientFound() {
        Patient p = new Patient("P002", "Alice", "Brown", 40, "Female", "Asthma", PatientCategory.OUTPATIENT);
        HospitalAdmissionSystem.registerPatient(p);
        assertNotNull(HospitalAdmissionSystem.findPatientById("P002"));
    }

    @Test
    public void testUpdatePatientDetails() {
        Patient p = new Patient("P003", "Bob", "Marley", 50, "Male", "Cardiac", PatientCategory.OUTPATIENT);
        HospitalAdmissionSystem.registerPatient(p);
        assertTrue(HospitalAdmissionSystem.updatePatientDetails("P003", "Robert", "Marley", "Recovered"));
        assertEquals("Robert", HospitalAdmissionSystem.findPatientById("P003").getFirstName());
    }

    @Test
    public void testDeletePatient() {
        Patient p = new Patient("P004", "Charlie", "Day", 35, "Male", "Fracture", PatientCategory.OUTPATIENT);
        HospitalAdmissionSystem.registerPatient(p);
        assertTrue(HospitalAdmissionSystem.deletePatient("P004"));
        assertNull(HospitalAdmissionSystem.findPatientById("P004"));
    }

    @Test
    public void testBedAllocationAndOccupiedCheck() {
        Inpatient inp = new Inpatient("P005", "David", "Miller", 60, "Male", "Surgery", "Ward A", "B801");
        HospitalAdmissionSystem.registerPatient(inp);
        assertTrue(HospitalAdmissionSystem.allocateBed("P005", "B801"));
        assertTrue(HospitalAdmissionSystem.isBedOccupied("B801"));
        assertFalse(HospitalAdmissionSystem.allocateBed("P005", "B801"));
    }

    @Test
    public void testReleaseBed() {
        Inpatient inp = new Inpatient("P006", "Eva", "Green", 28, "Female", "Observation", "Ward A", "B802");
        HospitalAdmissionSystem.registerPatient(inp);
        HospitalAdmissionSystem.allocateBed("P006", "B802");
        assertTrue(HospitalAdmissionSystem.releaseBedByNumber("B802"));
        assertFalse(HospitalAdmissionSystem.isBedOccupied("B802"));
    }

    @Test
    public void testFullWardCondition() {
        int bedNum = 801;
        for (int i = 1; i <= 20; i++) {
            Inpatient inp = new Inpatient("P" + i, "Name" + i, "Last", 20, "M", "Condition", "W1", "B" + bedNum);
            HospitalAdmissionSystem.registerPatient(inp);
            HospitalAdmissionSystem.allocateBed("P" + i, "B" + bedNum);
            bedNum++;
        }
        assertTrue(HospitalAdmissionSystem.isWardFull());

        Inpatient extraInp = new Inpatient("P21", "Extra", "User", 22, "F", "Checkup", "W1", "B801");
        HospitalAdmissionSystem.registerPatient(extraInp);
        assertFalse(HospitalAdmissionSystem.allocateBed("P21", "B801"));
    }

    @Test
    public void testSortPatientsBySurname() {
        Patient p1 = new Patient("P002", "Adam", "Zebra", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "Brian", "Alpha", 25, "Male", "Fever", PatientCategory.OUTPATIENT);
        HospitalAdmissionSystem.registerPatient(p1);
        HospitalAdmissionSystem.registerPatient(p2);

        HospitalAdmissionSystem.sortPatients(1);
        assertEquals("Alpha", HospitalAdmissionSystem.patientList.get(0).getLastName());
    }
}