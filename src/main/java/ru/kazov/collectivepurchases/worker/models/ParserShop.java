package ru.kazov.collectivepurchases.worker.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParserShop {
    private boolean needLogin;
    private String login;
    private String password;
}
