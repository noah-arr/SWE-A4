package com.rmit;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<Date, Integer> demeritPoints;
    private boolean isSuspended;

    Person(String personID, String firstName, String lastName, String address, String birthDate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthDate;
    }

    public boolean addPerson(String fname, String lname, String id, String residence, String birthd) {
        if (id.length() != 10) return false;
        if (id.charAt(0) < '2' || id.charAt(0) > '9') return false;
        if (id.charAt(1) < '2' || id.charAt(1) > '9') return false;

        int num_special_chars = 0;
        for (int i = 2; i < id.length() - 2; i++) {
            if (String.valueOf(id.charAt(i)).matches("[^a-zA-Z0-9]")) num_special_chars++;
        }

        if (num_special_chars < 2 ) return false;
        if (!Character.isUpperCase(id.charAt(8)) || !Character.isUpperCase(id.charAt(9))) return false;

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

        if (!checkBirthDate(birthd)) return false;

        String[] date_parts = birthd.split("-");
        int day = Integer.parseInt(date_parts[0]);
        int month = Integer.parseInt(date_parts[1]);

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;

        try {
            FileWriter fw = new FileWriter("Person.txt", false);
            fw.write("Person ID:" + id + "\n");
            fw.write("First Name: " + fname + "\n");
            fw.write("Last Name: " + lname + "\n");
            fw.write("Address: " + residence + "\n");
            fw.write("Birth date: " + birthd + "\n");
            fw.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        return true;
    }

    public boolean updatePersonDetails(String newFirstName, String newLastName, String newPersonID, String newAddress, String newBirthDate) {
        //if (!addPerson()) return false;
        String updatedFirstName = (newFirstName != null) ? newFirstName : this.firstName;
        String updatedLastName = (newLastName != null) ? newLastName : this.lastName;
        String updatedPersonID = (newPersonID != null) ? newPersonID : this.personID;
        String updatedAddress = (newAddress != null) ? newAddress : this.address;
        String updatedBirthDate = (newBirthDate != null) ? newBirthDate : this.birthDate;

        if (!addPerson(updatedFirstName, updatedLastName, updatedPersonID, updatedAddress, updatedBirthDate)) return false;

        //1 & 2
        int age = getAge(updatedBirthDate);
        if (age < 18) return false;
        else {
            // update age, then return.
        }

        //3
        char c = personID.charAt(0);
        if (Character.isDigit(c)) {
            int digit = Character.getNumericValue(c);
            if (digit % 2 == 0) return false;
        }




        return true;
    }

    private int getAge(String bd) {
        String[] date_parts = bd.split("-");
        int day = Integer.parseInt(date_parts[0]);
        int month = Integer.parseInt(date_parts[1]);
        int year = Integer.parseInt(date_parts[2]);

        Date now = new Date();
        int curr_year = now.getYear() + 1900;
        int curr_month = now.getMonth() + 1;
        int curr_day = now.getDate();
        int age = curr_year - year;

        if (curr_month < month || (curr_month == month && curr_day < day)) {
            age--;
        }
        return age;
    }

    public String addDemeritPoints() {
        return "Success";
    }

    public boolean checkBirthDate(String bday) {
        if (!bday.matches("\\d{2}-\\d{2}-\\d{4}")) return false;
        return true;
    }

    public static void main(String[] args) {
        String testID = "22$%1234AB";
        String testResidence = "123|Main-Street|Melbourne|Victoria|Australia";
        String testBirthDate = "15-08-2000";
        String firstName = "John";
        String lastName = "Smith";

        Person person = new Person(testID, firstName, lastName, testResidence, testBirthDate);

        boolean result = person.addPerson(firstName, lastName, testID, testResidence, testBirthDate);
        System.out.println("Add Person Result: " + result);
    }

}
