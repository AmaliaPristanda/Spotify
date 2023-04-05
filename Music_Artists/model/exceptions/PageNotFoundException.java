package com.app.spotify.model.exceptions;

public class PageNotFoundException extends RuntimeException{

    public PageNotFoundException() {super("Invalid page number");}
}
