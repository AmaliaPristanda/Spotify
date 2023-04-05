package com.app.spotify.view.DTO;

import com.app.spotify.model.enums.Genre;
import com.app.spotify.model.enums.Type;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SongListDTO {
    List<Integer> songs;
}
