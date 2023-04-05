package com.app.spotify.model.repositories;

import com.app.spotify.model.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IArtistRepository extends JpaRepository<Artist, String>{
    @Query(
            value = "SELECT * FROM artists u WHERE u.name = :name",
            nativeQuery = true)
    List<Artist> findByName(@Param("name") String name);

    @Query(
            value="SELECT * FROM artists u WHERE u.name LIKE %:name%",
            nativeQuery = true
    )
    List<Artist> findByNameContaining(@Param("name") String name);

}
