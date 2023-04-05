package com.app.playlist.model.entities;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(collection = "cont")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cont {
    @Id
    private String id;
    private Integer userId;
    private Set<Playlist> playlists;

    public Cont(Integer userId, Set<Playlist> playlists)
    {
        this.userId = userId;
        this.playlists = playlists;
    }
}
