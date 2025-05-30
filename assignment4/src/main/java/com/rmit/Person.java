package com.rmit;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<String, Integer> demeritPoints;
    private boolean isSuspended;

    Person(String personID, String firstName, String lastName, String address, String birthDate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthDate;
    }

    public boolean addPerson(String fname, String lname, String id, String residence, String birthd) {
        if (!isValidID(id)) return false;

        if (!isValidAddress(residence)) return false;

        if (!isValidBirthdate(birthd)) return false;

        this.firstName = fname;
        this.lastName = lname;
        this.personID = id;
        this.address = residence;
        this.birthDate = birthd;

        writePersonDetails(fname, lname, id, residence, birthd, null, false, true, false);
        return true;
    }

    public boolean updatePersonDetails(String newFirstName, String newLastName, String newPersonID, String newAddress, String newBirthDate) {
        String updatedFirstName = (newFirstName != null) ? newFirstName : this.firstName;
        String updatedLastName = (newLastName != null) ? newLastName : this.lastName;
        String updatedPersonID = (newPersonID != null) ? newPersonID : this.personID;
        String updatedAddress = (newAddress != null) ? newAddress : this.address;
        String updatedBirthDate = (newBirthDate != null) ? newBirthDate : this.birthDate;

        if (!isValidID(updatedPersonID)) return false;

        if (!isValidAddress(updatedAddress)) return false;

        if (!checkDateFormat(updatedBirthDate)) return false;

        if (!isValidBirthdate(updatedBirthDate)) return false;

        int age = getAge(updatedBirthDate);
        if (age < 18) return false;

        // if bday changed, update it in file
        if (!updatedBirthDate.equals(this.birthDate)) {
            File file = new File("Person.txt");
            List<String> lines = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("Birth date: "))
                        lines.add("Birth date: " + updatedBirthDate);
                    else
                        lines.add(line);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return false;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            catch (IOException exception) {
                exception.printStackTrace();
                return false;
            }

            this.birthDate = updatedBirthDate;
            return true;
        }

        // only update ID if first digit is even
        if (!updatedPersonID.equals(this.personID)) {
            char c = updatedPersonID.charAt(0);
            if (Character.isDigit(c)) {
                int digit = Character.getNumericValue(c);
                if (digit % 2 == 0) {
                    this.personID = updatedPersonID;
                }
                else {
                    System.out.println("Cant update person ID");
                    return false;
                }
            }
        }

        this.firstName = updatedFirstName;
        this.lastName = updatedLastName;
        this.address = updatedAddress;
        this.personID = updatedPersonID;

        //write info without demerit points
        writePersonDetails(updatedFirstName, updatedLastName, updatedPersonID, updatedAddress, updatedBirthDate, null, false, true, false);

        return true;
    }

    public String addDemeritPoints(HashMap<String, Integer> demerits) {
        if (demerits == null || demerits.isEmpty()) return "Failed";
        List<LocalDate> offenseDates = new ArrayList<>();
        Map<LocalDate, Integer> demeritsMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Map.Entry<String, Integer> entry : demerits.entrySet()) {
            String dateStr = entry.getKey();
            int points = entry.getValue();

            if (!checkDateFormat(dateStr)) {
                return "Failed";
            }

            LocalDate offenseDate;
            try {
                offenseDate = LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException exception) {
                return "Failed";
            }

            if (points < 1 || points > 6)
                return "Failed";

            offenseDates.add(offenseDate);
            demeritsMap.put(offenseDate, points);
        }

        int age;
        try {
            age = getAge(this.birthDate);
        } catch (DateTimeParseException exception) {
            return "Failed";
        }

        // counting demerits from the last 2 years since last offense
        LocalDate lastOffense = Collections.max(offenseDates);
        int total = 0;

        for (LocalDate offenseDate : offenseDates) {
            if (!offenseDate.isAfter(lastOffense)) {
                var years = ChronoUnit.YEARS.between(offenseDate, lastOffense);
                if (years < 2)
                    total += demeritsMap.get(offenseDate);
            }
        }

        isSuspended = false;
        if (age < 21 && total > 6)
            isSuspended = true;
        else if (age >= 21 && total > 12)
            isSuspended = true;

        this.demeritPoints = new HashMap<>(demerits);

        // write all details including suspension and demerits
        writePersonDetails(this.firstName, this.lastName, this.personID, this.address, this.birthDate, this.demeritPoints, isSuspended, false, true);

        return "Success";
    }

    // writes person data to a file, with flags to include/exclude certain elements
    private void writePersonDetails(String firstName, String lastName, String personID, String address, String birthDate, HashMap<String, Integer> demerits, boolean isSuspended, boolean writeBasicInfo, boolean writeDemeritInfo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Person.txt", true))) {
            if (writeBasicInfo) {
                bw.write("\n---- New Person ----\n");
                bw.write("Person ID: " + personID + "\n");
                bw.write("First Name: " + firstName + "\n");
                bw.write("Last Name: " + lastName + "\n");
                bw.write("Address: " + address + "\n");
                bw.write("Birth date: " + birthDate + "\n");
            }

            if (writeDemeritInfo && demerits != null) {
                bw.write("Current demerits:\n");
                int total = 0;
                for (Map.Entry<String, Integer> entry : demerits.entrySet()) {
                    bw.write("  - Date: " + entry.getKey() + ", Points: " + entry.getValue() + "\n");
                    total += entry.getValue();
                }
                bw.write("Total demerit points: " + total + "\n");
                bw.write("Suspended: " + (isSuspended ? "Yes\n" : "No\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // helpers
    private int getAge(String bd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(bd, formatter);
        LocalDate now = LocalDate.now();
        return Period.between(birthDate, now).getYears();
    }

    public boolean checkDateFormat(String date) {
        if (!date.matches("\\d{2}-\\d{2}-\\d{4}")) return false;
        return true;
    }

    private boolean isValidID(String id) {
        if (id.length() != 10) return false;
        if (id.charAt(0) < '2' || id.charAt(0) > '9') return false;
        if (id.charAt(1) < '2' || id.charAt(1) > '9') return false;

        int num_special_chars = 0;
        for (int i = 2; i < id.length() - 2; i++) {
            if (String.valueOf(id.charAt(i)).matches("[^a-zA-Z0-9]")) num_special_chars++;
        }

        if (num_special_chars < 2 ) return false;
        if (!Character.isUpperCase(id.charAt(8)) || !Character.isUpperCase(id.charAt(9))) return false;

        return true;
    }

    private boolean isValidAddress(String residence) {
        String[] sections = residence.split("\\|");
        // section 0 = street num. 1 = street, 2 = city, 3 = state, 4 = country
        if (sections.length != 5) return false;
        for (int i = 0; i < sections.length; i++) {
            for (char c : sections[i].toCharArray()) {
                if (i == 0)  // street num
                    if (!Character.isDigit(c)) return false;
                if (i == 1 || i == 2 || i == 3 || i == 4) {
                    if (!Character.isAlphabetic(c) && c != '-' && c != ' ') return false;
                }
            }
            if (i == 3) {
                if (!sections[i].equals("Victoria")) return false;
            }
        }
        return true;
    }

    private boolean isValidBirthdate(String birthd) {
        if (!checkDateFormat(birthd)) return false;

        String[] date_parts = birthd.split("-");
        int day = Integer.parseInt(date_parts[0]);
        int month = Integer.parseInt(date_parts[1]);

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;

        int age = getAge(birthd);
        return age >= 18;
    }

    public static void main(String[] args) {


    }





}
