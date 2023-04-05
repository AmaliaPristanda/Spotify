package com.app.playlist.view.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

//TODO de vazut cum sa modelez sa includ si self link-ul
//TODO nu cred ca am nevoie de el
@Data
@Getter
@Setter
public class MusicDTO extends RepresentationModel<MusicDTO> {
    private String name;
    private Integer releaseYear;

    private Integer albumID;


}
