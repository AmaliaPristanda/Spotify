package com.app.spotify.model.exceptions;

public class MusicNotFoundByGenreAndReleaseYearException extends RuntimeException{
    public MusicNotFoundByGenreAndReleaseYearException(Integer releaseYear, String genre) {super("Could not find any music from the year " + releaseYear +" and of " + genre + " genre ");}
}
