package com.app.spotify.model.entities;

import com.app.spotify.model.enums.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name="music")
@NoArgsConstructor
@Getter
@Setter
public class Music {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name="id") Integer id;
    private @Column(name="name", nullable = false) String name;
    private @Column(name="genre", nullable = false) @Enumerated(EnumType.STRING) Genre genre;
    private @Column(name="releaseyear", nullable = false) Integer releaseYear;
    private @Column(name="type", nullable = false) @Enumerated(EnumType.STRING) Type type;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "albumid", referencedColumnName = "id")
    @JsonBackReference
    private Music album;

    /*
      LAZY = fetch when needed
      EAGER = fetch immediately
    */

    @OneToMany(
            mappedBy = "album",
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    //@JoinColumn(name = "albumid")
    @JsonManagedReference
    private Set<Music> albumSongs = new HashSet<>();


    @Getter @Setter @ManyToMany(mappedBy = "songs", fetch = FetchType.EAGER) @JsonBackReference
//aceasta anotare imi arata ca, colectia artists este mapata de colectia songs din partea proprietarului
    private Set<Artist> artists = new HashSet<>();

    public Music(String name, Genre genre, Integer releaseYear, Type type, Music album)
    {
        this.name = name;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.type = type;
        this.album = album;
    }

    public void addSongToAlbum(Music song)
    {
        if (!albumSongs.contains(song))
        {
            albumSongs.add(song);
        }
    }


    public void removeArtistFromSong(Artist artist)
    {
        if (artists.contains(artist))
        {
            artists.remove(artist);
        }
    }

    @PreRemove
    public void removeMusicFromArtist()
    {
        for (Artist a: artists)
        {
            a.getSongs().remove(this);
        }
    }


    //pentru a seta o melodie unui artist atunci trebuie sa introduc in tabela de join

}