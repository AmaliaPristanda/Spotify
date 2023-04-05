package com.app.playlist.view.DTO;

import com.app.playlist.model.entities.Song;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO extends RepresentationModel<PlaylistDTO> {
    private String name;
    private Set<Song> songs;
}
