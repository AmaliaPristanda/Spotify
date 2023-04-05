package com.app.spotify.controller;

import com.app.spotify.model.entities.*;
import com.app.spotify.model.exceptions.*;
import com.app.spotify.model.services.MusicService;
import com.app.spotify.security.JwtUtils;
import com.app.spotify.view.DTO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.spotify.model.services.ArtistService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.app.spotify.security.JwtUtils.*;
import static com.app.spotify.view.mapper.Mapper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;
    @Autowired
    private MusicService musicService;

    private JwtUtils jwtUtils = new JwtUtils();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @GetMapping("/")
    public ResponseEntity<?> listArtists(
            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<String> match
    ) {
        if (name.isEmpty())
        {
            try {
                List<Artist> artists = artistService.listAllArtists(pageNo, pageSize);
                List<ArtistDTO> artistsDTO = new ArrayList<ArtistDTO>();

                for (Artist artist : artists) {
                    ArtistDTO artistDTO = DTOFromArtist(artist);

                    //self link
                    artistDTO.add(linkTo(methodOn(ArtistController.class).getArtist(artist.getUuid())).withSelfRel());
                    //link to artist's songs
                    artistDTO.add(linkTo(methodOn(ArtistController.class).listArtistSongs(artist.getUuid())).withRel("songs"));

                    artistsDTO.add(artistDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(artistsDTO, HttpStatus.OK);
            } catch (SetOfArtistsNotFoundException | PageNotFoundException e) {
                if (e.getClass() == PageNotFoundException.class)
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
        else if (name.isPresent() && match.isPresent())
        {
            try {
                List<Artist> artists = artistService.listAllArtistByName(name.get());
                List<ArtistDTO> artistsDTO = new ArrayList<ArtistDTO>();

                for (Artist artist : artists) {
                    ArtistDTO artistDTO = DTOFromArtist(artist);

                    //self link
                    artistDTO.add(linkTo(methodOn(ArtistController.class).getArtist(artist.getUuid())).withSelfRel());
                    //link to artist's songs
                    artistDTO.add(linkTo(methodOn(ArtistController.class).listArtistSongs(artist.getUuid())).withRel("songs"));

                    artistsDTO.add(artistDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(artistsDTO, HttpStatus.OK);
            } catch (SetOfArtistsNotFoundException|ArtistNotFoundByNameException e) {
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            try {
                List<Artist> artists = artistService.listAllArtistsByPartialName(name.get());
                List<ArtistDTO> artistsDTO = new ArrayList<ArtistDTO>();

                for (Artist artist : artists) {
                    ArtistDTO artistDTO = DTOFromArtist(artist);

                    //self link
                    artistDTO.add(linkTo(methodOn(ArtistController.class).getArtist(artist.getUuid())).withSelfRel());
                    //link to artist's songs
                    artistDTO.add(linkTo(methodOn(ArtistController.class).listArtistSongs(artist.getUuid())).withRel("songs"));

                    artistsDTO.add(artistDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(artistsDTO, HttpStatus.OK);
            } catch (SetOfArtistsNotFoundException|ArtistNotFoundByNameException e) {
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtists ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
            }
        }

    }

    @PutMapping("/") //clientul decide modul in care se creeaza resursele noi
    public ResponseEntity<?> addArtist(@RequestBody ArtistDTO artistDTO, @RequestHeader (name="Authorization") String token) {

        if (!jwtUtils.isTokenExpired(token))
        {
            if (jwtUtils.isUserContentManager(token))
            {
                Artist artist = ArtistFromDTO(artistDTO);
                artistService.saveArtist(artist);

                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addArtist ] - " + "[ " + HttpStatus.CREATED + " ]");
                return new ResponseEntity<>(linkTo(methodOn(ArtistController.class).getArtist(artist.getUuid())).withSelfRel(), HttpStatus.CREATED);
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addArtist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getArtist(@PathVariable String uuid) {
        try {
            Artist artist = artistService.getArtist(uuid);
            ArtistDTO artistDTO = DTOFromArtist(artist);

            //link to all artists
            artistDTO.add(linkTo(ArtistController.class).withRel("artists"));
            //link to artist's songs
            artistDTO.add(linkTo(methodOn(ArtistController.class).listArtistSongs(artist.getUuid())).withRel("songs"));
            //self link
            artistDTO.add(linkTo(methodOn(ArtistController.class).getArtist(artist.getUuid())).withSelfRel());

            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getArtist ] - " + "[ " + HttpStatus.OK + " ]");
            return new ResponseEntity<>(artistDTO, HttpStatus.OK);
        } catch (ArtistNotFoundException e) {
            //adaug un link catre toti artistii
            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getArtist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
            return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
        }
    }



    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteArtist(@PathVariable String uuid, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserContentManager(token)) {
                try {
                    Artist artist = artistService.getArtist(uuid);
                    Set<Music> music = artist.getSongs();

                    for (Music m : music) {
                        m.removeArtistFromSong(artist);
                    }

                    artist.clearSongs();
                    artistService.deleteArtist(uuid);
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteArtist ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } catch (ArtistNotFoundException e) {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteArtist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/{uuid}/songs")
    public ResponseEntity<?> addSongToArtist(@PathVariable String uuid, @RequestBody SongListDTO songs, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token))
        {
            if (jwtUtils.isUserContentManager(token))
            {
                try{
                    Artist artist = artistService.getArtist(uuid);
                    List<Integer> songIds = songs.getSongs();
                    for (Integer songId: songIds)
                    {
                        Music music = musicService.getMusic(songId);
                        artist.addSongToArtist(music);
                        artistService.saveArtist(artist);
                    }
                    //deoarece clientul are deja starea resursei alterate
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addSongToArtist ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }catch(ArtistNotFoundException|MusicNotFoundException e )
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addSongToArtist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{uuid}/songs")
    public ResponseEntity<?> listArtistSongs(
            @PathVariable String uuid)
    {
        try{
            Artist artist = artistService.getArtist(uuid);
            Set<Music> songs = artist.getSongs();

            if (!songs.isEmpty())
            {
                Set<MusicDTO> songsDTO = new HashSet<>();
                for (Music song: songs)
                {
                    MusicDTO musicDTO = DTOFromMusic(song);

                    //self link pentru melodia respectiva
                    musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());
                    //link to album
                    if (musicDTO.getAlbumID() != null)
                    {
                        musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(musicDTO.getAlbumID())).withRel("album"));
                    }

                    songsDTO.add(musicDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtistSongs ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(songsDTO, HttpStatus.OK);
            }
            else
            {
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtistSongs ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                return new ResponseEntity<>(List.of("Artist doesn't have any music", linkTo(methodOn(ArtistController.class).getArtist(artist.getUuid())).withSelfRel()), HttpStatus.NOT_FOUND);
            }

        }catch(ArtistNotFoundException e )
        {
            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listArtistSongs ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
            return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{uuid}/songs")
    public ResponseEntity<?> deleteSongFromArtist(@PathVariable String uuid, @RequestBody SongListDTO songs, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserContentManager(token)) {
                try {
                    Artist artist = artistService.getArtist(uuid);
                    Set<Music> music = artist.getSongs();
                    List<Integer> songIds = songs.getSongs();

                    for (Integer songId : songIds) {
                        Music song = musicService.getMusic(songId);
                        music.remove(song);
                    }
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromArtist ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } catch (ArtistNotFoundException | MusicNotFoundException e) {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromArtist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(List.of(e.getMessage(), linkTo(ArtistController.class).withRel("artists")), HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromArtist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
