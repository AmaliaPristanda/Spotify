package com.app.spotify.model.services;

import com.app.spotify.model.entities.Artist;
import com.app.spotify.model.entities.Music;
import com.app.spotify.model.exceptions.*;
import com.app.spotify.model.repositories.IArtistRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ArtistService {
    @Autowired
    private IArtistRepository artistRepository;

    public List<Artist> listAllArtists(Integer pageNo, Integer pageSize)
    {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Artist> pagedResult = artistRepository.findAll(paging);

        if (pagedResult.hasContent())
        {
            return pagedResult.getContent();
        }
        else
        {
            if (!artistRepository.findAll().isEmpty())
            {
                //pagina nu exista
                throw new PageNotFoundException();
            }
            else
            {
                throw new SetOfArtistsNotFoundException();
            }
        }
    }
    public void saveArtist(Artist artist) {
        artistRepository.save(artist);}

    public Artist getArtist(String uuid)
    {
        if (artistRepository.findById(uuid).isPresent())
        {
            return artistRepository.findById(uuid).get();
        }
        else
        {
            throw new ArtistNotFoundException(uuid);
        }
    }

    public void deleteArtist(String uuid)
    {
        if (artistRepository.findById(uuid).isPresent())
        {
            artistRepository.deleteById(uuid);
        }
        else
        {
            throw new ArtistNotFoundException(uuid);
        }
    }

    public List<Artist> listAllArtistByName(String name)
    {
        if (artistRepository.findByName(name).isEmpty())
        {
            throw new ArtistNotFoundByNameException(name);
        }
        else
        {
            return artistRepository.findByName(name);
        }
    }

    public List<Artist> listAllArtistsByPartialName(String name)
    {
        if (artistRepository.findByNameContaining(name).isEmpty())
        {
            throw new ArtistNotFoundByNameException(name);
        }
        else
        {
            return artistRepository.findByNameContaining(name);
        }
    }
}
