import React from 'react'

import { useState } from "react";
import { useEffect } from "react";
import SongList from './SongList';

function Home() {

  const [songs, setSongs] = useState([]);

  const fetchData = () => {
    return fetch("http://127.0.0.1:8080/api/songs/")
          .then((response) => response.json())
          .then((data) => setSongs(data));
  }

  useEffect(() => {
    fetchData();
  },[])

  return (
    <div className="Home">
      <header className="Home-header">
        
        <div>
          <h1>Song List</h1>
          <SongList songs={songs} />
        </div>
      </header>
    </div>
  );
}

export default Home