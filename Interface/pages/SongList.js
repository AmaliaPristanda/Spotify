import React from 'react';

const SongList = ({ songs }) => (
  <ul>
    {songs.map((song) => (
      <li>
        <h3>{song.name}</h3>
        <p>{song.genre}</p>
        <p>{song.releaseYear}</p>
      </li>
    ))}
  </ul>
);

export default SongList;