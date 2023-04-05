package com.app.spotify.model.exceptions;

public class MusicNotFoundByReleaseYearException extends RuntimeException{
    public MusicNotFoundByReleaseYearException(Integer year) {super("Could not find any music from the year " + year);}
}
