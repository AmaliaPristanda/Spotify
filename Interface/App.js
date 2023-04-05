import logo from './logo.svg';
import React, { Component } from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import { Navigate } from "react-router-dom";
import { Link } from "react-router-dom"; 
import './App.css';

class App extends Component{
  
  render() {
    return(
      <div>
        <Link to="./Login" className="btn btn-primary">Catre pagina de login</Link>
     </div>
    );
 }
}

export default App;
