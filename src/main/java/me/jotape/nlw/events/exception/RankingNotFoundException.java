package me.jotape.nlw.events.exception;

public class RankingNotFoundException extends RuntimeException {
    public RankingNotFoundException(String message) {
        super(message);
    }
}
