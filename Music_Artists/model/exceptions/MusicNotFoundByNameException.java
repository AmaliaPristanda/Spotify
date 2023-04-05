package com.app.spotify.model.exceptions;

public class MusicNotFoundByNameException extends RuntimeException{
    public MusicNotFoundByNameException(String name) {super("Could not find any music with name(containing): " + name);}
}
