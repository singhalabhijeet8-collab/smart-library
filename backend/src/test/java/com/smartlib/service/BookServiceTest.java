package com.smartlib.service;

import com.smartlib.entity.Book;
import com.smartlib.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repo;

    @InjectMocks
    private BookService service;

    @Test
    void addSavesBook() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");

        when(repo.save(book)).thenReturn(book);

        Book saved = service.add(book);

        assertEquals("Clean Code", saved.getTitle());
        verify(repo).save(book);
    }

    @Test
    void getAllReturnsBooks() {
        Book book = new Book();
        book.setTitle("Atomic Habits");

        when(repo.findAll()).thenReturn(List.of(book));

        List<Book> books = service.getAll();

        assertEquals(1, books.size());
        assertEquals("Atomic Habits", books.get(0).getTitle());
    }
}
