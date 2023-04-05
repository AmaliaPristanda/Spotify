package com.app.playlist.controller;

import com.app.playlist.model.entities.Cont;
import com.app.playlist.model.entities.Playlist;
import com.app.playlist.model.entities.Song;
import com.app.playlist.model.repository.ContRepository;
import com.app.playlist.model.repository.PlaylistRepository;
import com.app.playlist.security.JwtUtils;
import com.app.playlist.view.DTO.ContDTO;
import com.app.playlist.view.DTO.PlaylistDTO;
import com.app.playlist.view.DTO.SongDTO;
import com.google.gson.Gson;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.app.playlist.view.mapper.Mapper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@EnableMongoRepositories(basePackageClasses = {ContRepository.class, PlaylistRepository.class})
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/cont")
public class PlaylistController {
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private ContRepository contRepository;

    private JwtUtils jwtUtils = new JwtUtils();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @GetMapping("/playlist")
    public ResponseEntity<?> listAllUserPlaylists(@RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userID = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userID);
                Set<Playlist> playlists = cont.getPlaylists();

                if (playlists == null)
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllUserPlaylists ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>("User doesn't have ant playlists yet", HttpStatus.NOT_FOUND);
                }

                List<PlaylistDTO> playlistsDTO = new ArrayList<PlaylistDTO>();

                for (Playlist playlist:playlists)
                {
                    PlaylistDTO playlistDTO = DTOFromPlaylist(playlist);
                    playlistsDTO.add(playlistDTO);
                }
                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllUserPlaylists ] - " + "[ " + HttpStatus.OK + " ]");
                return new ResponseEntity<>(playlistsDTO, HttpStatus.OK);
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllUserPlaylists ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);


    }

    @GetMapping("/playlist/{playlistID}/songs")
    public ResponseEntity<?> listAllSongsFromUserPlaylist(@PathVariable String playlistID, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userID = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userID);
                if (cont != null)
                {
                    //ma asigur ca playlistul este al utilizatorului
                    Playlist playlist = playlistRepository.findPlaylistById(playlistID);

                    if (playlist != null)
                    {
                        Set<Playlist> userPlaylists = cont.getPlaylists();
                        if (isPlaylistInUserPlaylists(userPlaylists, playlistID))
                        {
                            Set<Song> songs = playlist.getSongs();
                            Set<SongDTO> songsDTO = new HashSet<>();

                            for (Song song: songs)
                            {
                                songsDTO.add(DTOFromSong(song));
                            }
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllSongsFromUserPlaylist ] - " + "[ " + HttpStatus.OK + " ]");
                            return new ResponseEntity<>(songsDTO, HttpStatus.OK);
                        }
                        else
                        {
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllSongsFromUserPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        }
                    }
                    else
                    {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllSongsFromUserPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>("Could not find playlist",HttpStatus.NOT_FOUND);
                    }
                }
                else
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllSongsFromUserPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>("Could not find account", HttpStatus.NOT_FOUND);

                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listAllSongsFromUserPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/playlist/{playlistID}")
    public ResponseEntity<?> listUserPlaylist(@PathVariable String playlistID, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userID = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userID);
                if (cont != null)
                {
                    //ma asigur ca playlistul este al utilizatorului
                    Playlist playlist = playlistRepository.findPlaylistById(playlistID);

                    if (playlist != null)
                    {
                        Set<Playlist> userPlaylists = cont.getPlaylists();
                        if (isPlaylistInUserPlaylists(userPlaylists, playlistID))
                        {
                            PlaylistDTO playlistDTO= DTOFromPlaylist(playlist);
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listUserPlaylist ] - " + "[ " + HttpStatus.OK + " ]");
                            return new ResponseEntity<>(playlistDTO, HttpStatus.OK);
                        }
                        else
                        {
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listUserPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        }
                    }
                    else
                    {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listUserPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                        return new ResponseEntity<>("Could not find playlist",HttpStatus.NOT_FOUND);
                    }
                }
                else
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listUserPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>("Could not find account", HttpStatus.NOT_FOUND);

                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ listUserPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/playlist")
    public ResponseEntity<?> createNewPlaylist(@RequestBody PlaylistDTO playlistDTO, @RequestHeader (name="Authorization") String token)
    {

        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userID = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userID);
                if (cont != null)
                {
                    Set<Playlist> playlists = cont.getPlaylists();

                    if (playlists == null)
                    {
                        playlists = new HashSet<Playlist>();
                    }
                    Playlist newPlaylist = playlistRepository.save(PlaylistFromDTO(playlistDTO));
                    playlists.add(newPlaylist);
                    cont.setPlaylists(playlists);
                    contRepository.save(cont);

                    playlistDTO.add(linkTo(methodOn(PlaylistController.class).listUserPlaylist(newPlaylist.getId(), token)).withSelfRel());
                    playlistDTO.add(linkTo(methodOn(PlaylistController.class).listAllSongsFromUserPlaylist(newPlaylist.getId(), token)).withRel("songs"));

                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ createNewPlaylist ] - " + "[ " + HttpStatus.OK + " ]");
                    return new ResponseEntity<>(playlistDTO, HttpStatus.OK);
                }
                else
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ createNewPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>("Could not find the account", HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ createNewPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/playlist/{playlistId}")
    public ResponseEntity<?> deletePlaylistFromUser(@PathVariable String playlistId, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userId = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userId);
                if (cont != null)
                {
                    //trebuie sa ma asigur ca playlistul apartine contului respectiv
                    Playlist playlist = playlistRepository.findPlaylistById(playlistId);

                    Set<Playlist> userPlaylists = cont.getPlaylists();
                    if (isPlaylistInUserPlaylists(userPlaylists, playlistId))
                    {
                        userPlaylists.remove(playlist);
                        cont.setPlaylists(userPlaylists);
                        contRepository.save(cont);
                        playlistRepository.delete(playlist);
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deletePlaylistFromUser ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                    else
                    {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deletePlaylistFromUser ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }
                else
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deletePlaylistFromUser ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deletePlaylistFromUser ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/playlist/{playlistId}/songs/{songId}")
    public ResponseEntity<?> deleteSongFromUserPlaylist(@PathVariable String playlistId, @PathVariable Integer songId, @RequestHeader (name="Authorization") String token)
    {
        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userId = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userId);
                if (cont != null)
                {
                    //trebuie sa ma asigur ca playlistul apartine contului respectiv
                    Playlist playlist = playlistRepository.findPlaylistById(playlistId);

                    Set<Playlist> userPlaylists = cont.getPlaylists();
                    if (isPlaylistInUserPlaylists(userPlaylists, playlistId))
                    {
                        Set<Song> playlistSongs = playlist.getSongs();
                        Boolean found = false;
                        for (Song s: playlistSongs)
                        {
                            if (Objects.equals(s.getId(), songId))
                            {
                                found = true;
                                playlistSongs.remove(s);
                                playlist.setSongs(playlistSongs);
                                playlistRepository.save(playlist);
                            }
                        }
                        if (found)
                        {
                            Link link = linkTo(methodOn(PlaylistController.class).listAllSongsFromUserPlaylist(playlistId, token)).withRel("songs");
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromUserPlaylist ] - " + "[ " + HttpStatus.OK + " ]");
                            return new ResponseEntity<>(link, HttpStatus.OK);
                        }
                        else
                        {
                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromUserPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                            return new ResponseEntity<>("Could not find the song", HttpStatus.NOT_FOUND);
                        }
                    }
                    else
                    {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromUserPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }
                else
                {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromUserPlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deleteSongFromUserPlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/playlist/{playlistId}")
    public HttpEntity<?> updatePlaylist(@PathVariable String playlistId,
                                        @RequestParam(required = false) Optional<String> newPlaylistName,
                                        @RequestParam(required = false) Optional<Integer> songID, @RequestHeader (name="Authorization") String token) throws IOException, ParseException {

        if (!jwtUtils.isTokenExpired(token)) {
            if (jwtUtils.isUserClient(token)) {
                Integer userId = jwtUtils.getUserIdFromToken(token);
                Cont cont = contRepository.getContByUserId(userId);


                if (cont != null) {
                    Playlist playlist = playlistRepository.findPlaylistById(playlistId);

                    Set<Playlist> userPlaylists = cont.getPlaylists();

                    if (isPlaylistInUserPlaylists(userPlaylists, playlistId))
                    {
                        //presupun ca ori adaug o melodie ori schimb numele
                        if (newPlaylistName.isPresent())
                        {
                            for (Playlist p:userPlaylists)
                            {
                                if (Objects.equals(p.getId(), playlistId))
                                {
                                    p.setName(newPlaylistName.get());
                                    break;
                                }
                            }
                            cont.setPlaylists(userPlaylists);
                            contRepository.save(cont);

                            playlist.setName(newPlaylistName.get());
                            Playlist newPlaylist = playlistRepository.save(playlist);

                            PlaylistDTO playlistDTO = DTOFromPlaylist(newPlaylist);

                            playlistDTO.add(linkTo(methodOn(PlaylistController.class).listUserPlaylist(newPlaylist.getId(), token)).withSelfRel());
                            playlistDTO.add(linkTo(methodOn(PlaylistController.class).listAllSongsFromUserPlaylist(newPlaylist.getId(), token)).withRel("songs"));

                            System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ deletePlaylistFromUser ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
                            return new ResponseEntity<>(playlistDTO, HttpStatus.OK);

                        }
                        if (songID.isPresent())
                        {
                            String url = "http://localhost:8080/api/songs/" + songID.get();
                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("GET");

                            int responseCode = con.getResponseCode();

                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                String inputLine;
                                StringBuffer response = new StringBuffer();

                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                in.close();

                                JSONParser parser = new JSONParser();
                                JSONObject json = (JSONObject) parser.parse(response.toString());

                                //System.out.println(json.get("_links"));
                                JSONObject jsonLinks = (JSONObject) parser.parse(json.get("_links").toString());
                                JSONObject jsonLinksSelf = (JSONObject) parser.parse(jsonLinks.get("self").toString());
                                System.out.println(jsonLinksSelf.get("href").toString());

                                Song song = new Song(songID.get(), json.get("name").toString(), jsonLinksSelf.get("href").toString());

                                //adaug cantecul in playlist
                                Set<Song> playlistSongs = playlist.getSongs();
                                if (playlistSongs == null)
                                {
                                    playlistSongs = new HashSet<>();
                                }
                                playlistSongs.add(song);
                                playlist.setSongs(playlistSongs);

                                //salvez playlistul
                                Playlist newPlaylist = playlistRepository.save(playlist);

                                PlaylistDTO playlistDTO = DTOFromPlaylist(newPlaylist);

                                playlistDTO.add(linkTo(methodOn(PlaylistController.class).listUserPlaylist(newPlaylist.getId(), token)).withSelfRel());
                                playlistDTO.add(linkTo(methodOn(PlaylistController.class).listAllSongsFromUserPlaylist(newPlaylist.getId(), token)).withRel("songs"));

                                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ updatePlaylist ] - " + "[ " + HttpStatus.OK + " ]");
                                return new ResponseEntity<>(playlistDTO, HttpStatus.OK);
                            } else {
                                System.out.println("GET request did not work.");
                                System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ updatePlaylist ] - " + "[ " + HttpStatus.INTERNAL_SERVER_ERROR + " ]");
                                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        }
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ updatePlaylist ] - " + "[ " + HttpStatus.NO_CONTENT + " ]");
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else {
                        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ updatePlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ updatePlaylist ] - " + "[ " + HttpStatus.NOT_FOUND + " ]");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        }
        System.out.println("[ " + dtf.format(LocalDateTime.now()) + " ] - [ updatePlaylist ] - " + "[ " + HttpStatus.UNAUTHORIZED + " ]");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping("/")
    public ResponseEntity<?> createNewCont(@RequestBody ContDTO contDTO)
    {
        Cont cont = contRepository.getContByUserId(contDTO.getUserId());
        if (cont == null)
        {
            Cont returnCont = contRepository.save(ContFromDTO(contDTO));
            return new ResponseEntity<>(DTOFromCont(returnCont), HttpStatus.OK);
        }
        else
        {
            //TODO nu cred ca ar trebui sa fie conflict aici - de vazut
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    private Boolean isPlaylistInUserPlaylists(Set<Playlist> userPlaylists, String playlistId)
    {
        Boolean found = false;
        for(Playlist p:userPlaylists)
        {
            if (Objects.equals(p.getId(), playlistId))
            {
                found = true;
                break;
            }
        }
        return found;
    }
}
