import React from 'react';
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

const ContentManager = () => {

  const navigate = useNavigate();
  const [uuid, setuuid] = useState("");
  const [name, setname] = useState("");
  const [active, setactive] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault()
    var isActiveSet = (active === 'true');
    const artist  = {uuid: uuidv4(), name:name, active:isActiveSet};
    const config = {
      headers: {
        "Content-Type": "application/json", 
        "Access-Control-Allow-Origin": "*",
        "Authorization": localStorage.getItem("token")
      }
    };
    axios.put('http://127.0.0.1:8080/api/artists/', artist, config).then(res => console.log(res))
                                                                    .catch( function (error) {
                                                                      if (error.response){
                                                                        console.log(error.response.data);
                                                                        console.log(error.response.status);
                                                                        console.log(error.response.headers);
                                                                      }
                                                                    }); 
    
  };



  return (
          <div>
            <p>Add an artist</p>
            <form style ={{marginTop:"100px"}} onSubmit={handleSubmit}>
                
                
                <div>
                <input
                    type="text"
                    name="name"
                    value={name}
                    onChange={(e) => setname(e.target.value)}
                />
                </div>
                <div>
                <input
                    type="text"
                    name="active"
                    onChange={(e) => setactive(e.target.value)}
                />
                </div>
                
                <input type="submit" value="Submit" />
            </form>
          </div>
    
  );
}



export default ContentManager