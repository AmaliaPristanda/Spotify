package com.app.spotify.view.mapper;

import com.app.spotify.model.entities.*;
import com.app.spotify.view.DTO.*;


public class Mapper {
    public static Music MusicFromDTO(MusicDTO musicDTO, Music album)
    {
        return new Music(musicDTO.getName(), musicDTO.getGenre(), musicDTO.getReleaseYear(), musicDTO.getType(), album);
    }

    public static MusicDTO DTOFromMusic(Music music)
    {
        if (music.getAlbum() != null)
        {
            return new MusicDTO(music.getName(), music.getGenre(), music.getReleaseYear(), music.getType(), music.getAlbum().getId());
        }
        else
        {
            return new MusicDTO(music.getName(), music.getGenre(), music.getReleaseYear(), music.getType(), null);
        }
    }

    public static Artist ArtistFromDTO(ArtistDTO artistDTO)
    {
        return new Artist(artistDTO.getUuid(), artistDTO.getName(), artistDTO.getActive());
    }

    public static ArtistDTO DTOFromArtist(Artist artist)
    {
        return new ArtistDTO(artist.getUuid(), artist.getName(), artist.getActive());
    }
}
