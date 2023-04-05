package com.app.spotify.view.DTO;

import com.app.spotify.model.enums.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;


@Data
@Getter
@Setter
public class MusicDTO extends RepresentationModel<MusicDTO> {
    private String name;
    private Genre genre;
    private Integer releaseYear;
    private Type type;
    private Integer albumID;

    public MusicDTO(String name, Genre genre, Integer releaseYear, Type type, Integer albumID) {
        this.name = name;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.type = type;
        this.albumID = albumID;
    }
}