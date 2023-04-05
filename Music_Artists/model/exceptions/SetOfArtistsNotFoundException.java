package com.app.spotify.model.exceptions;

public class SetOfArtistsNotFoundException extends RuntimeException{
    public SetOfArtistsNotFoundException() { super("Artists collection is empty");}
}
