package com.app.spotify.model.exceptions;

public class SetOfMusicNotFoundException extends RuntimeException{
    public SetOfMusicNotFoundException() { super("Music collection is empty");}
}
