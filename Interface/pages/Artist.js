import React from 'react';
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import axios from 'axios';


const Artist = () =>
{
  const navigate = useNavigate();
  const [uuid, setuuid] = useState("");
  const [idcantece, setidcantece] = useState("");
  
  const handleSubmit = (e) => {
    e.preventDefault()
  
    var idCanteceString = idcantece.split(",");
    idCanteceString.forEach( x => parseInt(x));
  
    const artist  = {uuid: uuid, songs: idCanteceString};
    const config = {
      headers: {
        "Content-Type": "application/json", 
        "Access-Control-Allow-Origin": "*",
        "Authorization": localStorage.getItem("token")
      }
    };
    var url = 'http://127.0.0.1:8080/api/artists/' + uuid + '/songs'
    axios.post(url, artist, config).then(res => console.log(res))
                                                                    .catch( function (error) {
                                                                      if (error.response){
                                                                        console.log(error.response.data);
                                                                        console.log(error.response.status);
                                                                        console.log(error.response.headers);
                                                                      }
                                                                    }); 
  };
  
  
    //adaug cantecele deja existente unui artist
    
    
      return (
        <div>
          <p>Add a song</p>
          <form onSubmit={handleSubmit}>
              <div>
              <input
                  type="text"
                  name="uuid"
                  value={uuid}
                  onChange={(e) => setuuid(e.target.value)}
              />
              </div>
              <div>
              <input
                  type="text"
                  name="idcantece"
                  value={idcantece}
                  onChange={(e) => setidcantece(e.target.value)}
                  defaultValue = "1,2,3"
              />
              </div>
              
              <input type="submit" value="Submit" />
          </form>
        </div>
  
  );
}


export default Artist