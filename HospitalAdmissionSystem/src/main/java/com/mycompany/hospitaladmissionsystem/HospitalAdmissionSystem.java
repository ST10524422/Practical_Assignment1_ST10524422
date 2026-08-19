package com.mycompany.hospitaladmissionsystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class HospitalAdmissionSystem {
    // Shared state accessible by unit tests[cite: 1]
    public static ArrayList<Patient> patientList = new ArrayList<>();
    public static String[][] bedGrid = new String[4][5]; 
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeBeds();
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n==================================================");
            System.out.println("   MEDICARE HOSPITAL ADMISSION SYSTEM - MENU");
            System.out.println("==================================================");
            System.out.println("1. Register New Patient");
            System.out.println("2. Search Patient by ID");
            System.out.println("3. Update Patient Details");
            System.out.println("4. Delete Patient");
            System.out.println("5. Allocate Bed to Inpatient");
            System.out.println("6. Release Bed (Discharge)");
            System.out.println("7. Display Ward Bed Layout");
            System.out.println("8. Generate Ward Reports");
            System.out.println("9. Sort Patients (Surname / ID)");
            System.out.println("0. Exit System");
            System.out.println("==================================================");
            System.out.print("Select an option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: registerPatientConsole(); break;
                    case 2: searchPatientConsole(); break;
                    case 3: updatePatientConsole(); break;
                    case 4: deletePatientConsole(); break;
                    case 5: allocateBedConsole(); break;
                    case 6: releaseBedConsole(); break;
                    case 7: displayWardLayout(); break;
                    case 8: generateReports(); break;
                    case 9: sortPatientsConsole(); break;
                    case 0: System.out.println("System terminated successfully."); break;
                    default: System.out.println("Error: Invalid menu choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid numeric input.");
            }
        }
    }

    public static void initializeBeds() {
        int bedNum = 801;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                bedGrid[r][c] = "B" + bedNum++;
            }
        }
    }

    // --- PATIENT MANAGEMENT ---
    public static boolean registerPatient(Patient p) {
        if (findPatientById(p.getPatientId()) != null) {
            return false;
        }
        patientList.add(p);
        return true;
    }

    public static Patient findPatientById(String id) {
        for (Patient p : patientList) {
            if (p.getPatientId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public static boolean updatePatientDetails(String id, String fname, String lname, String condition) {
        Patient p = findPatientById(id);
        if (p != null) {
            p.setFirstName(fname);
            p.setLastName(lname);
            p.setMedicalCondition(condition);
            return true;
        }
        return false;
    }

    public static boolean deletePatient(String id) {
        Patient p = findPatientById(id);
        if (p != null) {
            if (p instanceof Inpatient) {
                releaseBedByNumber(((Inpatient) p).getBedNumber());
            }
            patientList.remove(p);
            return true;
        }
        return false;
    }

    // --- BED MANAGEMENT ---
    public static boolean allocateBed(String patientId, String bedNo) {
        Patient p = findPatientById(patientId);
        if (p == null || !(p instanceof Inpatient)) return false;
        if (isWardFull() || isBedOccupied(bedNo)) return false;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].equalsIgnoreCase(bedNo)) {
                    bedGrid[r][c] = bedNo + " [OCCUPIED]";
                    ((Inpatient) p).setBedNumber(bedNo);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean releaseBedByNumber(String bedNo) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].equalsIgnoreCase(bedNo + " [OCCUPIED]")) {
                    bedGrid[r][c] = bedNo;
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBedOccupied(String bedNo) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].equalsIgnoreCase(bedNo + " [OCCUPIED]")) return true;
            }
        }
        return false;
    }

    public static boolean isWardFull() {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (!bedGrid[r][c].contains("[OCCUPIED]")) return false;
            }
        }
        return true;
    }

    public static void displayWardLayout() {
        System.out.println("\n--- WARD BED LAYOUT (4x5 GRID) ---");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                System.out.print(bedGrid[r][c] + "\t");
            }
            System.out.println();
        }
    }

    // --- REPORTS & SORTING ---
    public static void generateReports() {
        int occupied = 0;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].contains("[OCCUPIED]")) occupied++;
            }
        }
        double occupancyRate = (occupied / 20.0) * 100.0;

        System.out.println("\n==================================================");
        System.out.println("            HOSPITAL WARD OCCUPANCY REPORT        ");
        System.out.println("==================================================");
        System.out.println("Total Registered Patients: " + patientList.size());
        System.out.println("Occupied Beds           : " + occupied);
        System.out.println("Available Beds          : " + (20 - occupied));
        System.out.printf("Ward Occupancy Rate     : %.2f%%\n", occupancyRate);
        System.out.println("==================================================");
    }

    public static void sortPatients(int option) {
        if (option == 1) {
            patientList.sort(Comparator.comparing(Patient::getLastName));
        } else {
            patientList.sort(Comparator.comparing(Patient::getPatientId));
        }
    }

    // --- CONSOLE INTERACTION HANDLERS ---
    private static void registerPatientConsole() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        if (findPatientById(id) != null) {
            System.out.println("Error: Duplicate Patient ID!");
            return;
        }
        System.out.print("Enter First Name: ");
        String fname = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lname = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter Medical Condition: ");
        String cond = scanner.nextLine();

        System.out.println("Select Category: 1. Inpatient | 2. Outpatient | 3. Emergency");
        int cat = Integer.parseInt(scanner.nextLine());

        if (cat == 1) {
            System.out.print("Enter Ward Number: ");
            String ward = scanner.nextLine();
            displayWardLayout();
            System.out.print("Select Bed Number (e.g. B801): ");
            String bed = scanner.nextLine();

            Inpatient inp = new Inpatient(id, fname, lname, age, gender, cond, ward, "Unassigned");
            registerPatient(inp);
            if (allocateBed(id, bed)) {
                System.out.println("Inpatient registered & bed allocated successfully.");
            } else {
                System.out.println("Registered, but bed allocation failed (Occupied/Full).");
            }
        } else if (cat == 2) {
            registerPatient(new Patient(id, fname, lname, age, gender, cond, PatientCategory.OUTPATIENT));
            System.out.println("Outpatient registered.");
        } else {
            registerPatient(new Patient(id, fname, lname, age, gender, cond, PatientCategory.EMERGENCY));
            System.out.println("Emergency patient registered.");
        }
    }

    private static void searchPatientConsole() {
        System.out.print("Enter Patient ID: ");
        Patient p = findPatientById(scanner.nextLine());
        if (p != null) p.displayDetails();
        else System.out.println("Patient not found.");
    }

    private static void updatePatientConsole() {
        System.out.print("Enter Patient ID to update: ");
        String id = scanner.nextLine();
        System.out.print("New First Name: ");
        String fn = scanner.nextLine();
        System.out.print("New Last Name: ");
        String ln = scanner.nextLine();
        System.out.print("New Condition: ");
        String cond = scanner.nextLine();

        if (updatePatientDetails(id, fn, ln, cond)) System.out.println("Update successful.");
        else System.out.println("Patient ID not found.");
    }

    private static void deletePatientConsole() {
        System.out.print("Enter Patient ID to delete: ");
        if (deletePatient(scanner.nextLine())) System.out.println("Record deleted successfully.");
        else System.out.println("Patient ID not found.");
    }

    private static void allocateBedConsole() {
        System.out.print("Enter Inpatient ID: ");
        String id = scanner.nextLine();
        displayWardLayout();
        System.out.print("Enter Bed Number: ");
        String bed = scanner.nextLine();
        if (allocateBed(id, bed)) System.out.println("Bed allocated successfully.");
        else System.out.println("Allocation failed (Invalid Bed/Occupied/Not Inpatient).");
    }

    private static void releaseBedConsole() {
        System.out.print("Enter Bed Number to release: ");
        if (releaseBedByNumber(scanner.nextLine())) System.out.println("Bed released.");
        else System.out.println("Bed was not occupied.");
    }

    private static void sortPatientsConsole() {
        System.out.print("Sort by: 1. Surname | 2. Patient ID: ");
        int opt = Integer.parseInt(scanner.nextLine());
        sortPatients(opt);
        System.out.println("\n--- SORTED PATIENT LIST ---");
        for (Patient p : patientList) p.displayDetails();
    }
}