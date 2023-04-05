package com.app.playlist.model.repository;

import com.app.playlist.model.entities.Cont;
import com.app.playlist.model.entities.Playlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContRepository extends MongoRepository<Cont, String> {
    Cont getContByUserId(Integer userId);
}
