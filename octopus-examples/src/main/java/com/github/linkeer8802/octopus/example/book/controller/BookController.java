package com.github.linkeer8802.octopus.example.book.controller;

import com.github.linkeer8802.octopus.example.book.application.BookService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping
public class BookController {

    @Resource
    private BookService bookService;

    public void createBook() {

    }
}
