package com.app.spotify.model.exceptions;

public class GenreNotValidException extends RuntimeException{
    public GenreNotValidException(String gen) {super("Genre " + gen + " is not valid");}
}
