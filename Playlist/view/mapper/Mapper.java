package com.app.playlist.view.mapper;

import com.app.playlist.model.entities.Cont;
import com.app.playlist.model.entities.Playlist;
import com.app.playlist.model.entities.Song;
import com.app.playlist.view.DTO.ContDTO;
import com.app.playlist.view.DTO.PlaylistDTO;
import com.app.playlist.view.DTO.SongDTO;

public class Mapper {

    public static PlaylistDTO DTOFromPlaylist(Playlist playlist)
    {
        return new PlaylistDTO(playlist.getName(), playlist.getSongs());
    }

    public static Playlist PlaylistFromDTO(PlaylistDTO playlistDTO)
    {
        return new Playlist(playlistDTO.getName(), playlistDTO.getSongs());
    }

    public static SongDTO DTOFromSong(Song song)
    {
        return new SongDTO(song.getName(), song.getSelfLink());
    }

    public static ContDTO DTOFromCont(Cont cont) {return new ContDTO(cont.getUserId(), cont.getPlaylists());}

    public static Cont ContFromDTO(ContDTO contDTO) {return new Cont(contDTO.getUserId(), contDTO.getPlaylists());}
}
