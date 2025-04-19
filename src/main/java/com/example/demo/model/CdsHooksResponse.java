package com.example.demo.model;

import lombok.Data;
import java.util.List;

@Data
public class CdsHooksResponse {
    private List<Card> cards;

    @Data
    public static class Card {
        private String summary;
        private String detail;
        private String indicator;
        private Source source;
        private List<Suggestion> suggestions;
    }

    @Data
    public static class Source {
        private String label;
        private String url;
    }

    @Data
    public static class Suggestion {
        private String label;
        private List<Action> actions;
    }

    @Data
    public static class Action {
        private String type;
        private String description;
        private String resource;
    }
} 