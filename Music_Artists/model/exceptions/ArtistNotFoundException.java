package com.app.spotify.model.exceptions;

public class ArtistNotFoundException extends RuntimeException{
    public ArtistNotFoundException(String uuid) {super("Invalid artist id: " + uuid);}
}
