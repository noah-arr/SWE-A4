package com.rmit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    private Person person;

    @BeforeEach
    public void setUp() {
        person = new Person("23!@34abXY", "John", "Doe", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000");
    }

    // -------------------- AddPerson Tests --------------------

    @Test
    public void testAddPerson_ValidInput() {
        assertTrue(person.addPerson("John", "Doe", "23!@34abXY", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000"));
    }

    @Test
    public void testAddPerson_InvalidIDFormat() {
        assertFalse(person.addPerson("John", "Doe", "12ab#XY", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000"));
    }

    @Test
    public void testAddPerson_InvalidAddressFormat() {
        assertFalse(person.addPerson("John", "Doe", "23!@34abXY", "Main Street, VIC", "01-01-2000"));
    }

    @Test
    public void testAddPerson_InvalidState() {
        assertFalse(person.addPerson("John", "Doe", "23!@34abXY", "32|Highland Street|Melbourne|Queensland|Australia", "01-01-2000"));
    }

    @Test
    public void testAddPerson_InvalidBirthdayFormat() {
        assertFalse(person.addPerson("John", "Doe", "23!@34abXY", "45|Highland Street|Melbourne|Victoria|Australia", "1990-11-15"));
    }

    // -------------------- updatePersonDetails Tests --------------------

    @Test
    public void testUpdatePerson_ValidChange() {
        assertTrue(person.updatePersonDetails("Lebron", "James", "23!@34abXY", "41|Highland Street|Melbourne|Victoria|Australia", "01-01-2000"));
    }

    @Test
    public void testUpdatePerson_Under18ChangeAddress() {
        person = new Person("23!@34abXY", "John", "Doe", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2010");
        assertFalse(person.updatePersonDetails("John", "Doe", "23!@34abXY", "41|Highland Street|Melbourne|Victoria|Australia", "01-01-2010"));
    }

    @Test
    public void testUpdatePerson_BirthdayAndOtherChange() {
        assertFalse(person.updatePersonDetails("Donald", "Doe", "23!@34abXY", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000"));
    }

    @Test
    public void testUpdatePerson_EvenStartIDCannotChange() {
        person = new Person("46s_d%&fAB", "John", "Doe", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(person.updatePersonDetails("John", "Doe", "56s_d%&fAB", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000"));
    }

    @Test
    public void testUpdatePerson_ValidBirthdayChange() {
        person = new Person("23!@34abXY", "John", "Doe", "45|Highland Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertTrue(person.updatePersonDetails("John", "Doe", "23!@34abXY", "45|Highland Street|Melbourne|Victoria|Australia", "02-02-1999"));
    }

    
}
