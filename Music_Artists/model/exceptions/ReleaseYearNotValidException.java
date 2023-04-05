package com.app.spotify.model.exceptions;

public class ReleaseYearNotValidException extends RuntimeException{
    public ReleaseYearNotValidException(Integer year) {super("Invalid release year: " + year);}
}
