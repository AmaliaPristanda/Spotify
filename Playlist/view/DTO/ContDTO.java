package com.app.playlist.view.DTO;

import com.app.playlist.model.entities.Playlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ContDTO {
    private Integer userId;
    private Set<Playlist> playlists;
}
