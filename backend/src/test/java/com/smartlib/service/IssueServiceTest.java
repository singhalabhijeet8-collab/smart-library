package com.smartlib.service;

import com.smartlib.entity.Book;
import com.smartlib.entity.Issue;
import com.smartlib.entity.User;
import com.smartlib.repository.BookRepository;
import com.smartlib.repository.IssueRepository;
import com.smartlib.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepo;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private IssueService service;

    @Test
    void issueBookMarksBookUnavailableAndSavesIssue() {
        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        User user = new User();

        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(issueRepo.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Issue issue = service.issueBook(1L, 2L);

        assertFalse(book.isAvailable());
        assertFalse(issue.isReturned());
        verify(bookRepo).save(book);
        verify(issueRepo).save(any(Issue.class));
    }

    @Test
    void issueBookFailsWhenUserDoesNotExist() {
        Book book = new Book();
        book.setAvailable(true);

        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.issueBook(1L, 2L));
    }

    @Test
    void returnBookMarksIssueReturnedAndBookAvailable() {
        Issue issue = new Issue();
        issue.setBookId(1L);
        issue.setReturned(false);

        Book book = new Book();
        book.setAvailable(false);

        when(issueRepo.findById(5L)).thenReturn(Optional.of(issue));
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));

        service.returnBook(5L);

        assertTrue(book.isAvailable());
        assertTrue(issue.isReturned());
        verify(bookRepo).save(book);
        verify(issueRepo).save(issue);
    }
}
