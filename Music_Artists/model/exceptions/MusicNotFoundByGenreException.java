package com.app.spotify.model.exceptions;

import com.app.spotify.model.enums.Genre;

public class MusicNotFoundByGenreException extends RuntimeException{
    public MusicNotFoundByGenreException(String genre) {super("Could not find any music from the " + genre + " genre");}
}
