package com.app.playlist.model.entities;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(collection = "playlist")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Playlist {

    @Id
    private String id;
    private String name;
    @JsonIgnore
    private Set<Song> songs;

    public Playlist(String name, Set<Song> songs)
    {
        this.name = name;
        this.songs = songs;
    }

}