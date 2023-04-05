package com.app.spotify.model.exceptions;

public class MusicNotFoundException extends RuntimeException{
    public MusicNotFoundException(Integer id) {super("Invalid music id: " + id);}
}
