import React from 'react';
import axios from 'axios';

class Client extends React.Component{
  state={
    name:''
  };

  handleChange = (event) => {
    const target = event.target;
    const name = target.name;
    const value  = target.value;

    this.setState({
        [name]:value
    });
  }

  submit = (event) =>{
    event.preventDefault();

    axios({
      url:"http://localhost:8081/api/playlist",
      method: 'POST',
      headers :{
        "Content-Type": "application/json", 
        "Access-Control-Allow-Origin": "*",
        "Authorization": localStorage.getItem("token")
      },
      data: JSON.stringify(this.state.name)
    }).then(()=>{
      console.log("Data sent");
    }

    ).catch((error)=>{
      console.log("Internal server error " + error);
    }
    );
    }

    render(){
      return (
        <div>
          <h3 >New playlist name:</h3>
          <form onSubmit={this.submit}>
            <div>
                <label>
                  Playlist Name : &nbsp;
                  <input 
                        type="text" 
                        name="name"
                        value={this.state.name} 
                        onChange={(event)=>this.handleChange(event)} />
                </label>
            </div>
            <br/>
            <br/>
            <button>Show</button>
          </form>
        </div>
      );
      }
}

export default Client