package com.redcat.tutorials.bookhub.repository;

import com.redcat.tutorials.bookhub.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
