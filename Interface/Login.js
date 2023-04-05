
import Dashboard from "./Dashboard";
import { useNavigate } from "react-router-dom";
import { useState } from "react";


const Login = () => {
    const navigate = useNavigate();
    const [username, setusername] = useState("");
    const [password, setpassword] = useState("");
    const [authenticated, setauthenticated] = useState(localStorage.getItem(localStorage.getItem("authenticated")|| false));
    const users = [{ username: "test", password: "test" }];
    const handleSubmit = (e) => {
        e.preventDefault()

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open('POST', 'http://127.0.0.1:8000', true);
        
        var sr = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:service="services.spotify.soap">' + 
        '<soap11env:Body>' +
            '<service:autentificare>' + 
                '<service:username>' +username+'</service:username>'+
                '<service:password>' +password+'</service:password>'+
            '</service:autentificare>' +
        '</soap11env:Body>' +
    '</soap11env:Envelope>';

        xmlhttp.onreadystatechange = function () {
            
            if (xmlhttp.status == 200) {
                //<tns:autentificareResult>Eroare: username/parola gresite</tns:autentificareResult>

                var raspuns = xmlhttp.responseText;
                var lungime = '<tns:autentificareResult>'.length;
                var indexStart = raspuns.indexOf('<tns:autentificareResult>');
                var indexFinal = raspuns.indexOf('</tns:autentificareResult>')
                if (indexStart != -1 && indexFinal != -1)
                {
                    var aux = xmlhttp.responseText.substring(indexStart + lungime, indexFinal);
                    if (aux.includes('Eroare') == false && aux.includes('Exceptie') == false)
                    {
                        //autentificare cu succes
                        setauthenticated(true)
                        localStorage.setItem("authenticated", true);
                        localStorage.setItem("token", aux)
                        navigate("/dashboard");
                    }
                    //alert(aux);
                }
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
            <p>Welcome Back</p>
            <form onSubmit={handleSubmit}>
                <div>
                <input
                    type="text"
                    name="Username"
                    value={username}
                    onChange={(e) => setusername(e.target.value)}
                />
                </div>
                <div>
                <input
                    type="password"
                    name="Password"
                    onChange={(e) => setpassword(e.target.value)}
                />
                </div>
                
                <input type="submit" value="Submit" />
            </form>
        </div>
    )
};


export default Login;