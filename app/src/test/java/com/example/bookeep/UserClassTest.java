package com.example.bookeep;

import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserClassTest {

    //Tests the constructor when given arguments, also tests relevant getter and setters
    @Test
    public void constructorTest(){
        String firstName1 = "Nolan";
        String lastName1 = "Brost";
        String email1 = "nbrost@email.com";
        String firstName2 = "Nafee";
        String lastName2 = "Khan";
        String email2 = "nkhan@email.com";
        User user1 = new User(email1, firstName1, lastName1);
        User user2 = new User(email2, firstName2, lastName2);
        assertNotEquals(user1.getUserId(),user2.getUserId());//Checks given unique ids

        assertEquals(firstName1,user1.getFirstname());
        user1.setFirstname("New");
        assertEquals("New",user1.getFirstname());//Tests get and set first name

        assertEquals(lastName1,user1.getLastname());
        user1.setLastname("New1");
        assertEquals("New1",user1.getLastname());//Tests get and set last name

        assertEquals(email1, user1.getEmail());
        user1.setEmail("email");
        assertEquals("email",user1.getEmail());//Tests get and set email

    }

    //Tests getters and setters not tested in the constructorTest
    @Test
    public void getterAndSetterTest() {
        User user = new User("email", "firstName", "lastName");
        String url = "ImageUrl.com";
        user.setImageURL(url);
        assertEquals(url, user.getImageURL());//Tests the getter and setter for image url

        UUID uuid = UUID.randomUUID();
        user.setUserId(uuid);
        assertEquals(uuid, user.getUserId());//Tests the getter and setter for user id's

        UUID bookID1 = UUID.randomUUID();
        UUID bookID2 = UUID.randomUUID();
        UUID bookID3 = UUID.randomUUID();
        ArrayList<UUID> borrowed = user.getBorrowedIds();
        assertEquals(0, borrowed.size());//tests to make sure list currently empty
        user.addToBorrowed(bookID1);
        user.addToBorrowed(bookID2);
        user.addToBorrowed(bookID3);
        borrowed.add(bookID1);
        borrowed.add(bookID2);
        borrowed.add(bookID3);
        assertEquals(borrowed, user.getBorrowedIds());//Tests the get and add Borrowed Ids

        ArrayList<UUID> owned = new ArrayList<UUID>();
        assertEquals(0, user.getOwnedId().size());//tests to make sure list is currently empty
        user.addToOwned(bookID1);
        user.addToOwned(bookID2);
        user.addToOwned(bookID3);
        owned.add((bookID1));
        owned.add(bookID2);
        owned.add(bookID3);
        assertEquals(owned, user.getOwnedId());//Tests the get and add owned ids

        Address address = new Address();
        assertFalse(address == user.getAddress());
        user.setAddress(address);
        assertEquals(address, user.getAddress());//Tests the get and set address

        PhoneNumber phone = new PhoneNumber(123, 456, 7890);
        assertFalse(phone == user.getPhoneNumber());
        user.setPhoneNumber(phone);
        assertEquals(phone, user.getPhoneNumber());//Tests the get and set phone number
    }
}
