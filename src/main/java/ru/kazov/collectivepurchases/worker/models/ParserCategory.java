package ru.kazov.collectivepurchases.worker.models;

import lombok.Data;

import java.util.Date;

@Data
public class ParserCategory {
    private Long id;
    private String name;
    private String shop;
    private Date date;
}
