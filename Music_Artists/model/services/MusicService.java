package com.app.spotify.model.services;


import com.app.spotify.model.entities.Artist;
import com.app.spotify.model.entities.Music;
import com.app.spotify.model.enums.Genre;
import com.app.spotify.model.exceptions.*;
import com.app.spotify.model.repositories.IMusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MusicService {

    @Autowired
    private IMusicRepository musicRepository;

    public List<Music> listAllMusic(Integer pageNo, Integer pageSize)
    {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Music> pagedResult = musicRepository.findAll(paging);

        if (pagedResult.hasContent())
        {
            return pagedResult.getContent();
        }
        else
        {
            if (!musicRepository.findAll().isEmpty())
            {
                //pagina nu exista
                throw new PageNotFoundException();
            }
            else
            {
                throw new SetOfMusicNotFoundException();
            }
        }
    }


    public void saveMusic(Music music) { musicRepository.save(music); }


    public Music getMusic(Integer id)
    {
        if (musicRepository.findById(id).isPresent())
        {
            return musicRepository.findById(id).get();
        }
        else
        {
            throw new MusicNotFoundException(id);
        }
    }

    public void deleteMusic(Music music)
    {
        musicRepository.delete(music);
    }

    public List<Music> listAllMusicByName(String name)
    {
        if (musicRepository.findByName(name).isEmpty())
        {
            throw new MusicNotFoundByNameException(name);
        }
        else
        {
            return musicRepository.findByName(name);
        }
    }

    public List<Music> listAllMusicByPartialName(String name)
    {
        if (musicRepository.findByNameContaining(name).isEmpty())
        {
            throw new MusicNotFoundByNameException(name);
        }
        else
        {
            return musicRepository.findByNameContaining(name);
        }
    }

    public List<Music> listAllMusicByReleaseYear(Integer year)
    {
        if (musicRepository.findByReleaseYear(year).isEmpty())
        {
            throw new MusicNotFoundByReleaseYearException(year);
        }
        else
        {
            return musicRepository.findByReleaseYear(year);
        }
    }

    public List<Music> listAllMusicByGenre(String genre)
    {
        if (musicRepository.findByGenre(genre).isEmpty())
        {
            throw new MusicNotFoundByGenreException(genre);
        }
        else
        {
            return musicRepository.findByGenre(genre);
        }
    }

    public List<Music> listAllMusicByReleaseYearAndGenre(Integer year, String genre)
    {
        if (musicRepository.findByReleaseYearAndGenre(year,genre).isEmpty())
        {
            throw new MusicNotFoundByGenreAndReleaseYearException(year, genre);
        }
        else
        {
            return musicRepository.findByReleaseYearAndGenre(year,genre);
        }
    }
}
