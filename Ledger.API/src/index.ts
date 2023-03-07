import express from "express";
import api from './api/index'
import bodyParser from "body-parser";
const app = express();
const port = 3333;

app.use(bodyParser.urlencoded({extended: false}))
app.use(bodyParser.json())

// define a route handler for the default home page
app.get( "/", ( req, res ) => {
    // render the index template
    res.send( "index" );
});

app.use(api)

// start the express server
app.listen( port, () => {
    // tslint:disable-next-line:no-console
    console.log( `server started at http://localhost:${ port }` );
} );