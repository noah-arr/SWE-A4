package com.rmit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<Date, Integer> demeritPoints;
    private boolean isSuspended;

    public boolean addPerson() {
        if (personID.length() != 10) return false;
        if (personID.charAt(0) < '2' || personID.charAt(0) > '9') return false;
        if (personID.charAt(1) < '2' || personID.charAt(1) > '9') return false;

        int num_special_chars = 0;
        for (int i = 2; i < personID.length() - 2; i++) {
            if (String.valueOf(personID.charAt(i)).matches("[^a-zA-Z0-9]")) num_special_chars++;
        }

        if (num_special_chars < 2 ) return false;
        if (!Character.isUpperCase(personID.charAt(8)) || !Character.isUpperCase(personID.charAt(9))) return false;

        String[] sections = address.split("\\|");
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


        if (!checkBirthDate(birthDate)) return false;

        String[] date_parts = birthDate.split("-");
        int day = Integer.parseInt(date_parts[0]);
        int month = Integer.parseInt(date_parts[1]);

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;

        return true;
    }

    public boolean updatePersonDetails() {
        return true;
    }

    public String addDemeritPoints() {
        return "Success";
    }

    public boolean checkBirthDate(String bday) {
        if (!bday.matches("\\d{2}-\\d{2}-\\d{4}")) return false;
        return true;
    }
}
