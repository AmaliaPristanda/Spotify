package com.app.spotify.view.DTO;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Setter
@Getter
public class ArtistDTO extends RepresentationModel<ArtistDTO> {

    private String uuid;
    private String name;
    private Boolean active;

    public ArtistDTO(String uuid, String name, Boolean active)
    {
        this.uuid = uuid;
        this.name = name;
        this.active = active;
    }
}