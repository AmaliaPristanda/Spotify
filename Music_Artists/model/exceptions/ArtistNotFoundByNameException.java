package com.app.spotify.model.exceptions;

public class ArtistNotFoundByNameException extends RuntimeException{
    public ArtistNotFoundByNameException(String name) {super("Cannot find artist with name(containig): " + name);}
}
