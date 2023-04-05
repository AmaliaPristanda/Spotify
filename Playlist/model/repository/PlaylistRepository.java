package com.app.playlist.model.repository;

import com.app.playlist.model.entities.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String>
{
    @Query("{nume:'?0'}")
    Playlist findUserByUsername(String nume);

    Playlist findPlaylistById(String id);
}
