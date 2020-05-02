package com.sanskar.elasticsearch.springdata.example.service.impl;

import com.sanskar.elasticsearch.springdata.example.model.Book;
import com.sanskar.elasticsearch.springdata.example.repository.BookRepository;
import com.sanskar.elasticsearch.springdata.example.service.BookService;
import com.sanskar.elasticsearch.springdata.example.service.exception.BookNotFoundException;
import com.sanskar.elasticsearch.springdata.example.service.exception.DuplicateIsbnException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultBookService implements BookService {

    private final BookRepository bookRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public DefaultBookService(BookRepository bookRepository, ElasticsearchTemplate elasticsearchTemplate) {
        this.bookRepository = bookRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Optional<Book> getByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(book -> books.add(book));
        return books;
    }

    @Override
    public List<Book> findByAuthor(String authorName) {
        return bookRepository.findByAuthorName(authorName);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        BoolQueryBuilder criteria = QueryBuilders.boolQuery();
        criteria.must().addAll(Arrays.asList(QueryBuilders.matchQuery("authorName", author), QueryBuilders.matchQuery("title", title)));
        return elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(criteria).build(), Book.class);
    }

    @Override
    public Book create(Book book) throws DuplicateIsbnException {
        if (!getByIsbn(book.getIsbn()).isPresent()) {
            return bookRepository.save(book);
        }
        throw new DuplicateIsbnException(String.format("The provided ISBN: %s already exists. Use update instead!", book.getIsbn()));
    }

    @Override
    public void createMany(List<Book> books)  {
        bookRepository.saveAll(books);
    }


    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book update(String id, Book book) throws BookNotFoundException {
        Book oldBook = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("There is not book associated with the given id"));
        oldBook.setIsbn(book.getIsbn());
        oldBook.setAuthorName(book.getAuthorName());
        oldBook.setPublicationYear(book.getPublicationYear());
        oldBook.setTitle(book.getTitle());
        return bookRepository.save(oldBook);
    }
}
