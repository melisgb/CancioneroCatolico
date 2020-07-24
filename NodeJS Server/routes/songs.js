var express = require('express');
var router = express.Router();
var mysql = require('mysql');

//assembling the request
router.get('/', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;
    var config = {
        host : '127.0.0.1',
        user : 'root',
        password : '',
        database : 'cancionerocatolico'
    }

    var connection = mysql.createConnection(config);
    connection.connect();

    var myQuery =  `
                    select * from cancionerocatolico.song 
                    where song_title like '%${querydata.kewyword}%' 
                    or song_artist like '%${querydata.kewyword}%' 
                    or song_lyrics like '%${querydata.kewyword}%'
                    order by song_title asc limit 30 offset ${querydata.start_from};
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.send("{'msg' : 'Error running query'}");
            }
            else{
                res.send("{'msg' : 'Loading songs successful', 'songsInfo': " +
                    rows + "}");
                //in the case we have more than one result row: 
                //res.send(rows);
            }
        });
        connection.end();
    //res.send('Welcome node');
});
//available to use from user.
module.exports = router;