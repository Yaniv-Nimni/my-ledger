import express from "express";
import axios from "axios";
const app = express();
const port = 3333;

// define a route handler for the default home page
app.get( "/", ( req, res ) => {
    // render the index template
    res.send( "index" );
} );

app.get( "/test", async ( req, res ) => {
    // render the index template
    const response = await axios.post('http://localhost:3334/example', JSON.stringify({'test': 'skeleton'}))
    res.send( response.data );
} );

// start the express server
app.listen( port, () => {
    // tslint:disable-next-line:no-console
    console.log( `server started at http://localhost:${ port }` );
} );