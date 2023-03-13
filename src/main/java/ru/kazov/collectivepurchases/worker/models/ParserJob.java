package ru.kazov.collectivepurchases.worker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParserJob {
    private Long id;
    private String url;
    private ParserShop shop;
}
