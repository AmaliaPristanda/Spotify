import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import axios from 'axios';
import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import ContentManager from './pages/ContentManager'
import Artist from './pages/Artist'
import Client from './pages/Client'
import { Link } from "react-router-dom"; 

const Dashboard = () => 
{
    const [music, setMusic] = useState([]);
    const [authenticated, setauthenticated] = useState(null);
    useEffect(() => {
                        const loggedInUser = localStorage.getItem("authenticated");
                        if (loggedInUser) 
                        {
                            //afisez  playlist-uri
                            setauthenticated(loggedInUser);
                            //alert(localStorage.getItem("token"));
                        }
                    }, []);
    
    
    if (!authenticated) 
    {
        //returnez melodiile
        return (
            <div>
                Trebuie sa va autentificati
            </div>
          );


        //return <Navigate replace to="/login" />;
    } 
    else 
    {
        return (
            <>
            

            <div>
                <Link to="./pages/Home" className="btn btn-primary">Catre pagina cu melodii</Link>
            </div>
            <div>
                <Link to="./pages/Artist" className="btn btn-primary">Artist</Link>
            </div>
            <div>
                <Link to="./pages/ContentManager" className="btn btn-primary">ContentManager</Link>
            </div>
            <div>
                <Link to="./pages/Client" className="btn btn-primary">Client</Link>
            </div>
            <div>
                <Link to="./pages/Logout" className="btn btn-primary">Logout</Link>
            </div>
          </>
                );
    }
};
    
export default Dashboard;