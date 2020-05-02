package com.sanskar.elasticsearch.springdata.example.service;

import com.sanskar.elasticsearch.springdata.example.model.Book;
import com.sanskar.elasticsearch.springdata.example.service.exception.BookNotFoundException;
import com.sanskar.elasticsearch.springdata.example.service.exception.DuplicateIsbnException;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<Book> getByIsbn(String isbn);

    List<Book> getAll();

    List<Book> findByAuthor(String authorName);

    List<Book> findByTitleAndAuthor(String title, String author);

    Book create(Book book) throws DuplicateIsbnException;

    void createMany (List<Book> books);

    void deleteById(String id);

    Book update(String id, Book book) throws BookNotFoundException;
}
