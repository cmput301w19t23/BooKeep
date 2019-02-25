package com.example.bookeep;

import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;


//Tests the methods of the book class
public class BookClassTest {
    //Tests the various getters and setters for the book class
    @Test
    public void testBookGettersAndSetters(){
        Book book = new Book();

        String testUrl = "https://test.url";
        book.setImageURL(testUrl);
        assertEquals(testUrl,book.getImageURL());//Tests the get/setimageurl methods

        UUID requester1 = UUID.randomUUID();
        UUID requester2 = UUID.randomUUID();
        UUID requester3 = UUID.randomUUID();
        ArrayList<UUID> requesters = new ArrayList<UUID>();
        requesters.add(requester1);
        requesters.add(requester2);
        requesters.add(requester3);
        book.addRequest(requester1);
        book.addRequest(requester2);
        book.addRequest(requester3);
        int len = book.getRequesterIds().size();
        assertEquals(3,len);//Tests whether duplicate requesters can be added
        ArrayList<UUID> requesters1 = book.getRequesterIds();
        for (int i = 0; i < 3 ; i++) {
            assertEquals(requesters.get(i),requesters1.get(i));//Tests the addRequester and getRequesters
        }

        String author1 = "Nolan";
        String author2 = "Nafee";
        String author3 = "Kyle";
        String author4 = "Dusan";
        ArrayList<String> authorList1 = new ArrayList<String>();
        ArrayList<String> authorList2;
        authorList1.add(author1);
        authorList1.add(author2);
        authorList1.add(author3);
        authorList1.add(author4);
        book.setAuthor(authorList1);
        authorList2 = book.getAuthors();
        for (int i = 0; i < 4; i++) {
            assertEquals(authorList1.get(i),authorList2.get(i));//Tests the get/setAuthors
        }

        String title = "The Shadow Kingdom";
        book.setTitle(title);
        assertEquals(title,book.getTitle());//Tests the get/set Title methods

        String desc = "In the background on contemporary New York...";
        book.setDescription(desc);
        assertEquals(desc,book.getDescription());//Tests the get/setDescriptions

        int isbn = 1234;
        book.setISBN(isbn);
        assertEquals(true,1234 == book.getISBN());//Test the get/setISBN

        UUID bookID = UUID.randomUUID();
        book.setBookId(bookID);
        assertEquals(bookID,book.getBookId());//Tests the get/setBookID

        UUID ownerID = UUID.randomUUID();
        book.setOwner(ownerID);
        assertEquals(ownerID,book.getOwner());//Tests the get/setOwner

        BookStatus current = BookStatus.BORROWED;
        assertEquals(BookStatus.AVAILABLE,book.getStatus());
        book.setStatus(current);
        assertEquals(current,book.getStatus());//Tests the get/setStatus

        UUID borrower = UUID.randomUUID();
        book.setCurrentBorrower(borrower);
        assertEquals(borrower,book.getCurrentBorrowerId());//tests the get/setCurrentBorrower


    }


    //Tests to make sure a book created with no arguments gets a unique id and a status of available
    @Test
    public void testBookNoArguments(){
        Book book1 = new Book();
        Book book2 = new Book();
        assertFalse(book1.getBookId().equals(book2.getBookId()));
        assertEquals(BookStatus.AVAILABLE,book1.getStatus());
    }

    //Tests to make sure a book is created properly when given arguments
    @Test
    public void testBookWithArguments(){
        int isbn = 1;
        UUID uuid =  UUID.randomUUID();
        Book book1 = new Book(isbn, uuid);
        Book book2 = new Book(isbn +1, uuid);
        assertFalse(book1.getBookId().equals(book2.getBookId())); //checks to make sure given unique ID's
        assertEquals(BookStatus.AVAILABLE, book1.getStatus());//Ensures new book status is available
        assertEquals(true, isbn == book1.getISBN());//checks isbn is same as given isbn
        assertEquals(uuid,book1.getOwner());//checks the owners uuid is equal to the given uuid

    }
}
