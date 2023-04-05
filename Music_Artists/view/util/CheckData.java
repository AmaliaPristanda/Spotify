package com.app.spotify.view.util;

import com.app.spotify.model.enums.Genre;
import com.app.spotify.model.exceptions.GenreNotValidException;
import com.app.spotify.model.exceptions.ReleaseYearNotValidException;
import com.app.spotify.view.DTO.ArtistDTO;
import com.app.spotify.view.DTO.MusicDTO;
import org.yaml.snakeyaml.util.EnumUtils;

public class CheckData {

    public static void checkGenre(String gen)
    {
       Boolean isInGenreEnum = false;
       for (Genre genre: Genre.values())
       {
           if (genre.name().equals(gen))
           {
               isInGenreEnum = true;
           }
       }
       if (isInGenreEnum == false)
       {
           throw new GenreNotValidException(gen);
       }
    }

    public static void checkReleaseYear(Integer year)
    {
        if (year < 1600 || year > 2100)
        {
            throw new ReleaseYearNotValidException(year);
        }
    }
}
