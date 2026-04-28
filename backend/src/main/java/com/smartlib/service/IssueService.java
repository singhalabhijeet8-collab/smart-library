package com.smartlib.service;

import com.smartlib.entity.Book;
import com.smartlib.entity.Issue;
import com.smartlib.repository.BookRepository;
import com.smartlib.repository.IssueRepository;
import com.smartlib.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public IssueService(IssueRepository issueRepo, BookRepository bookRepo, UserRepository userRepo) {
        this.issueRepo = issueRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    public Issue issueBook(Long bookId, Long userId) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❌ prevent issuing if already issued
        if (!book.isAvailable()) {
            throw new RuntimeException("Book already issued");
        }

        // ✅ mark unavailable
        book.setAvailable(false);
        bookRepo.save(book);

        Issue issue = new Issue();
        issue.setBookId(bookId);
        issue.setUserId(userId);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(7));
        issue.setReturned(false);

        return issueRepo.save(issue);
    }

    public void returnBook(Long issueId) {

        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        Book book = bookRepo.findById(issue.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // ✅ mark available again
        book.setAvailable(true);
        bookRepo.save(book);

        issue.setReturned(true);
        issue.setReturnDate(LocalDate.now());
        issueRepo.save(issue);
    }

    public List<Issue> getAllIssues() {
        return issueRepo.findAll();
    }

    public List<Issue> getIssuesByUser(Long userId) {
        return issueRepo.findAll()
                .stream()
                .filter(i -> i.getUserId().equals(userId) && !i.isReturned())
                .toList();
    }

    public List<Issue> getOverdueBooks() {
        return issueRepo.findAll()
                .stream()
                .filter(i -> !i.isReturned() && i.getDueDate().isBefore(LocalDate.now()))
                .toList();
    }
}
