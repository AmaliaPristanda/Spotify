package com.app.spotify.controller;

import com.app.spotify.model.entities.Artist;
import com.app.spotify.model.entities.Music;
import com.app.spotify.model.enums.Genre;
import com.app.spotify.model.enums.Type;
import com.app.spotify.model.exceptions.*;
import com.app.spotify.model.services.ArtistService;
import com.app.spotify.model.services.MusicService;
import com.app.spotify.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.app.spotify.view.mapper.Mapper.*;
import static com.app.spotify.view.util.CheckData.checkGenre;
import static com.app.spotify.view.util.CheckData.checkReleaseYear;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.app.spotify.view.DTO.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/songs")
public class MusicController {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Autowired
    private MusicService musicService;

    @Autowired
    private ArtistService artistService;

    private JwtUtils jwtUtils = new JwtUtils();

    @GetMapping("/")
    public ResponseEntity<?> listMusic(
            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<String> gen,
            @RequestParam(required = false) Optional<Integer> releaseYear,
            @RequestParam(required = false) Optional<String> match
    ) {
        if (name.isEmpty()) {
            //vad daca se cauta dupa gen sau releaseYear
            if (gen.isEmpty())
            {
                if (releaseYear.isEmpty())
                {
                    //caut fara niciun criteriu
                    try {

                        List<Music> songs = musicService.listAllMusic(pageNo, pageSize);
                        List<MusicDTO> songsDTO = new ArrayList<MusicDTO>();

                        for (Music song : songs) {
                            MusicDTO musicDTO = DTOFromMusic(song);

                            //self link
                            musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());

                            songsDTO.add(musicDTO);
                        }
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.OK + " ]");
                        //logger.info("listMusic " + HttpStatus.OK);
                        return new ResponseEntity<>(songsDTO, HttpStatus.OK);
                    } catch (SetOfMusicNotFoundException | PageNotFoundException e) {
                        if (e.getClass() == PageNotFoundException.class)
                        {
                            //logger.info("listMusic " + HttpStatus.NOT_FOUND);
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                            return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                        }
                        System.out.println();

                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                    }
                }
                else
                {
                    //caut doar dupa release Year
                    try {
                        checkReleaseYear(releaseYear.get());
                        List<Music> songs = musicService.listAllMusicByReleaseYear(releaseYear.get());
                        List<MusicDTO> songsDTO = new ArrayList<MusicDTO>();

                        for (Music song : songs) {
                            MusicDTO musicDTO = DTOFromMusic(song);

                            //self link
                            musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());

                            songsDTO.add(musicDTO);
                        }
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.OK + " ]");
                        return new ResponseEntity<>(songsDTO, HttpStatus.OK);
                    } catch (SetOfMusicNotFoundException | MusicNotFoundByNameException | ReleaseYearNotValidException e) {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                    }
                }
            }
            else
            {
                if (releaseYear.isEmpty())
                {
                    //caut doar dupa genre
                    try {
                        checkGenre(gen.get());
                        List<Music> songs = musicService.listAllMusicByGenre((gen.get()));
                        List<MusicDTO> songsDTO = new ArrayList<MusicDTO>();

                        for (Music song : songs) {
                            MusicDTO musicDTO = DTOFromMusic(song);

                            //self link
                            musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());

                            songsDTO.add(musicDTO);
                        }
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.OK + " ]");
                        return new ResponseEntity<>(songsDTO, HttpStatus.OK);
                    } catch (SetOfMusicNotFoundException | PageNotFoundException | GenreNotValidException e) {
                        if (e.getClass() == PageNotFoundException.class)
                        {
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                            return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                        }
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                    }
                }
                else
                {
                    //caut dupa genre +  release Year
                    try {
                        checkGenre(gen.get());
                        checkReleaseYear(releaseYear.get());
                        List<Music> songs = musicService.listAllMusicByReleaseYearAndGenre(releaseYear.get(), gen.get());
                        List<MusicDTO> songsDTO = new ArrayList<MusicDTO>();

                        for (Music song : songs) {
                            MusicDTO musicDTO = DTOFromMusic(song);

                            //self link
                            musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());

                            songsDTO.add(musicDTO);
                        }
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.OK + " ]");
                        return new ResponseEntity<>(songsDTO, HttpStatus.OK);
                    } catch (SetOfMusicNotFoundException | MusicNotFoundByNameException | GenreNotValidException | ReleaseYearNotValidException | MusicNotFoundByGenreAndReleaseYearException e) {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                    }
                }
            }

        }
        else if (name.isPresent() && match.isPresent()) {
            try {
                List<Music> songs = musicService.listAllMusicByName(name.get());
                List<MusicDTO> songsDTO = new ArrayList<MusicDTO>();

                for (Music song : songs) {
                    MusicDTO musicDTO = DTOFromMusic(song);

                    //self link
                    musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());

                    songsDTO.add(musicDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(songsDTO, HttpStatus.OK);
            } catch (SetOfMusicNotFoundException | MusicNotFoundByNameException e) {
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
            }
        }
        else //atunci name is present and match is empty
        {
            try {
                List<Music> songs = musicService.listAllMusicByPartialName(name.get());
                List<MusicDTO> songsDTO = new ArrayList<MusicDTO>();

                for (Music song : songs) {
                    MusicDTO musicDTO = DTOFromMusic(song);

                    //self link
                    musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withSelfRel());

                    songsDTO.add(musicDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(songsDTO, HttpStatus.OK);
            } catch (SetOfMusicNotFoundException| MusicNotFoundByNameException e) {
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addMusic(@RequestBody MusicDTO musicDTO, @RequestHeader (name="Authorization") String token) {
        if (!jwtUtils.isTokenExpired(token))
        {
            if (jwtUtils.isUserContentManager(token) || jwtUtils.isUserArtist(token))
            {

                if (musicDTO.getAlbumID() != null) {
                    try {
                        Music album = musicService.getMusic(musicDTO.getAlbumID());
                        if (musicDTO.getType() == Type.song)
                        {
                            if (album.getType() == Type.album)
                            {
                                Music music = MusicFromDTO(musicDTO, album);
                                musicService.saveMusic(music);
                                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addMusic ] - " + "[ " + HttpStatus.CREATED + " ]");
                                return new ResponseEntity<>(linkTo(methodOn(MusicController.class).getMusic(music.getId())).withSelfRel(), HttpStatus.CREATED);
                            }
                            else
                            {
                                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addMusic ] - " + "[ " + HttpStatus.BAD_REQUEST + " ]");
                                return new ResponseEntity<>("Only albums can have songs", HttpStatus.BAD_REQUEST);
                            }
                        }
                        else
                        {
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addMusic ] - " + "[ " + HttpStatus.BAD_REQUEST + " ]");
                            return new ResponseEntity<>("Only songs can belong to an album", HttpStatus.BAD_REQUEST);
                        }
                    } catch (MusicNotFoundException e) {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                    }
                }

                Music music = MusicFromDTO(musicDTO, null);
                musicService.saveMusic(music);
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addMusic ] - " + "[ " + HttpStatus.CREATED + " ]");
                return new ResponseEntity<>(music, HttpStatus.CREATED);
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ addMusic ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMusic(@PathVariable Integer id) {
        try {

            Music music = musicService.getMusic(id);
            MusicDTO musicDTO = DTOFromMusic(music);

            //self link
            musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(music.getId())).withSelfRel());

            //link to all songs
            musicDTO.add(linkTo(MusicController.class).withRel("music"));

            //link to album
            if (music.getAlbum() != null)
            {
                musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(music.getAlbum().getId())).withRel("album"));
            }

            //links to all album songs
            if (music.getType() == Type.album)
            {
                Set<Music> songs = music.getAlbumSongs();
                for(Music song: songs)
                {
                    musicDTO.add(linkTo(methodOn(MusicController.class).getMusic(song.getId())).withRel("songs"));
                }
            }
            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.OK + " ]");
            return new ResponseEntity<>(musicDTO, HttpStatus.OK);
        } catch (MusicNotFoundException e) {
            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
            return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
        }
    }

    private boolean doesMusicBelongToArtist(Integer music_id, String token)
    {
        String artistName = jwtUtils.getUserNameFromToken(token);
        Music music = musicService.getMusic(music_id);
        Set<Artist> music_artists = music.getArtists();
        for(Artist artist:music_artists)
        {
            if (artist.getName() == artistName)
            {
                return true;
            }
        }
        return false;
    }
    
    @PostMapping("/{id}") //post si nu put pentru ca adaug ceva in resursa
    public ResponseEntity<?> addSongToAlbum(@PathVariable Integer id, @RequestBody Integer albumID, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token))
        {
            //atat albumul cat si melodia trebuie sa fie a artistului
            if (jwtUtils.isUserContentManager(token) || jwtUtils.isUserArtist(token))
            {
                try{
                    Music music = musicService.getMusic(id);
                    Music album = musicService.getMusic(albumID);

                    if (album.getType() == Type.album)
                    {
                        if (music.getType() == Type.song)
                        {
                            album.addSongToAlbum(music);
                            musicService.saveMusic(album);
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                        }
                        else
                        {
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.BAD_REQUEST + " ]");
                            return new ResponseEntity<>(List.of("Only songs can be in an album", linkTo(methodOn(MusicController.class).getMusic(music.getId())).withSelfRel()), HttpStatus.BAD_REQUEST);
                        }
                    }
                    else
                    {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.BAD_REQUEST + " ]");
                        return new ResponseEntity<>(List.of("Only albums can have multiple songs", linkTo(methodOn(MusicController.class).getMusic(music.getId())).withSelfRel()), HttpStatus.BAD_REQUEST);
                    }
                }catch(MusicNotFoundException e)
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.OK + " ]");
                    return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteMusic(@PathVariable Integer id, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            //atat albumul cat si melodia trebuie sa fie a artistului
            if (jwtUtils.isUserContentManager(token) || jwtUtils.isUserArtist(token) ) {
                try {
                    Music music = musicService.getMusic(id);
                    music.getArtists().forEach(u -> u.getSongs().remove(music));
                    music.getArtists().forEach(u -> artistService.saveArtist(u));

                    //trebuie sa am grija sa sterg si din tabela de join
                    if (music.getType() == Type.song) {
                        if (music.getAlbum() != null) {
                            //trebuie sa-l sterg din albumul din care face parte
                            Music album = musicService.getMusic(music.getAlbum().getId());
                            album.getAlbumSongs().remove(music);
                            musicService.saveMusic(album);
                        }
                    }

                    musicService.deleteMusic(music);
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

                } catch (MusicNotFoundException e) {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(List.of(e.getMessage(), linkTo(MusicController.class).withRel("music")), HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ getMusic ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
