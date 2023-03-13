package ru.kazov.collectivepurchases.worker.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ParserItem {
    private String name = "";
    private String description = "";
    private String url = "";
    private List<String> pictures = new ArrayList<>();
    private Map<String, Double> prices = new HashMap<>();
    private Map<String, String> properties = new HashMap<>();
}
