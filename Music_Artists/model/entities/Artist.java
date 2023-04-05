package com.app.spotify.model.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity //is a jpa annotation to make this object ready for storage in a jpa-based data store
@Table(name="artists")
@NoArgsConstructor
@Getter
@Setter
public class Artist {

    @Schema(description = "Unique identifier of the Artist.", example = "2a136c74-1270-4184-9a8a-83a97dd6dc98 ", required = true)
    private @Id @Column(name="uuid") String uuid;

    @Schema(description = "Artist's name", example = "John Smith", required = true)
    private @Column(name="name", unique = true, nullable = false) String name;

    @Schema(description = "Field in order to see whether an artist is still touring or not", example = "true", required = true)
    private @Column(name="active") Boolean active;

    //consider proprietarul ca fiind tabelul Artists
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "artists_music",
            joinColumns = { @JoinColumn(name = "artist_id") }, //@joinColumn este folosit pentru a specifica legatura cu tabela principala
            inverseJoinColumns = { @JoinColumn(name = "song_id") }
    )
    @JsonManagedReference
    @Schema(description = "Artist's songs")
    private Set<Music> songs = new HashSet<>();

    public Artist(String uuid, String name, boolean active)
    {
        this.uuid = uuid;
        this.name = name;
        this.active = active;
    }

    public void addSongToArtist(Music song)
    {
        if (!songs.contains(song))
        {
            songs.add(song);
        }
    }

    public void removeSong(Integer id)
    {
        Music song = this.songs.stream().filter(s -> Objects.equals(s.getId(), id)).findFirst().orElse((null));
        if (song != null) {
            song.getArtists().remove(this);
            this.songs.remove(song);
        }
    }

    public  void removeSongFromArtist(Music song)
    {
        if (songs.contains(song))
        {
            songs.remove(song);
        }
    }

    public void clearSongs()
    {
        songs.clear();
    }
}

