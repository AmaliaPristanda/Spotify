import { useNavigate } from "react-router-dom";
import { useState } from "react";


const Logout = () => {
    const navigate = useNavigate();
    
    
    const handleSubmit = (e) => {
        e.preventDefault()

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open('POST', 'http://127.0.0.1:8000', true);
        
        var sr = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:service="services.spotify.soap">' + 
        '<soap11env:Body>' +
            '<service:autentificare>' + 
                '<service:token>' +localStorage.getItem("token")+'</service:token>'+
            '</service:autentificare>' +
        '</soap11env:Body>' +
    '</soap11env:Envelope>';

        xmlhttp.onreadystatechange = function () {
            
            if (xmlhttp.status == 200) {
                //<tns:autentificareResult>Eroare: username/parola gresite</tns:autentificareResult>

                
                //alert(xmlhttp.responseText);
                
            }
            else
            {
                alert("s-a produs o eroare");
            }
        }
        xmlhttp.setRequestHeader('Content-Type', 'text/xml');
        xmlhttp.send(sr);
    };
    return (
        <div>
            <p>See you soon</p>
            <form onSubmit={handleSubmit}>
                
                <input type="submit" value="Logout" />
            </form>
        </div>
    )
};