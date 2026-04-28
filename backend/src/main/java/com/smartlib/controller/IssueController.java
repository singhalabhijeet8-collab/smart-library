package com.smartlib.controller;

import com.smartlib.entity.Issue;
import com.smartlib.service.IssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueService service;

    public IssueController(IssueService service) {
        this.service = service;
    }

    // ✅ ISSUE BOOK
    @PostMapping
    public Issue issue(@RequestParam Long bookId, @RequestParam Long userId) {
        return service.issueBook(bookId, userId);
    }

    // ✅ RETURN BOOK (FIXED)
    @GetMapping
    public List<Issue> all() {
        return service.getAllIssues();
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        service.returnBook(id);
        return ResponseEntity.ok("Book returned successfully");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
