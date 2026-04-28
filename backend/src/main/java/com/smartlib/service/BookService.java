package com.smartlib.service;

import com.smartlib.entity.Book;
import com.smartlib.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public Book add(Book b) { return repo.save(b); }

    public List<Book> getAll() { return repo.findAll(); }

    public void delete(Long id) { repo.deleteById(id); }
}
