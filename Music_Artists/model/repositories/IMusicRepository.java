package com.app.spotify.model.repositories;

import com.app.spotify.model.entities.Music;
import com.app.spotify.model.enums.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMusicRepository extends JpaRepository<Music, Integer> {

    @Query(
            value = "SELECT * FROM music u WHERE u.name = :name",
            nativeQuery = true)
    List<Music> findByName(@Param("name") String name);

    @Query(
            value="SELECT * FROM music u WHERE u.name LIKE %:name%",
            nativeQuery = true
    )
    List<Music> findByNameContaining(@Param("name") String name);

    @Query(
            value="SELECT * FROM music u WHERE u.genre = :genre",
            nativeQuery = true
    )
    List<Music> findByGenre(@Param("genre") String genre);

    @Query(
            value="SELECT * FROM music u WHERE u.releaseyear = :releaseyear",
            nativeQuery = true
    )
    List<Music> findByReleaseYear(@Param("releaseyear") Integer releaseyear);

    @Query(
            value="SELECT * FROM music u WHERE u.releaseyear = :releaseyear AND u.genre = :genre",
            nativeQuery = true
    )
    List<Music> findByReleaseYearAndGenre(@Param("releaseyear") Integer releaseyear, @Param("genre") String genre);

}
