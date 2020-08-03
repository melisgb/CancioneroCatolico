var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var config = {
        host : '127.0.0.1',
        user : 'dbuser',
        password : 'catolico', 
        database : 'cancionerocatolico'
    };

//GET ALL LISTSONGS (get_listsongs)
//Call-> http://127.0.0.1:3000/listsongs/?user_id=2
router.get('/', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();


    var myQuery =  `
                    select listsong_id, listsong_name 
                      from cancionerocatolico.listsong
                     where listsong_user_id = ${querydata.user_id} 
                     order by listsong_name asc 
                     limit 30;
                    `;
    
    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Error running query'});
            }
            else{
                res.send({
                    'msg' : 'Loading summary lists - successful', 
                    'listsongs': rows 
                });
            }
        });
        connection.end();
});


//INSERT SONGS into songlist  (case 4 listsongs)
//Call-> http://127.0.0.1:3000/listsongs/insert?list_id=49&songs_ids=26,27,28
router.get('/insert', function(req, res, next){
    console.log("h")
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    function formatSong(song_id){
        return ` (${querydata.list_id}, ${song_id} )`;
    }

    var fullQuery = `insert ignore into cancionerocatolico.listsong_songs
                    (listsong_id, listsong_song_id) values`;

    var songsIDS = querydata.songs_ids.split(',');                
    var idsFormatted = songsIDS.map(formatSong)
    var idsConcat = idsFormatted.join(", ")              
    fullQuery += idsConcat +";"
    
    connection.query(fullQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Listsong failed to edit'});
            }
            else{
                console.log(rows)
                res.send({
                    'msg' : 'Adding songs into listsongs - successful'
                });
            }
        });
        connection.end();
});

//DELETE SONG from listsong (case 5 listsongs)
//Call -> http://127.0.0.1:3000/listsongs/remove?list_id=50&songs_ids=26,27,28
router.get('/remove', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =   `
                        delete from cancionerocatolico.listsong_songs
                         where listsong_id = ${querydata.list_id}
                           and listsong_song_id in (${querydata.songs_ids} );             
                        `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Listsong failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Removing songs from listsongs - successful'
                });
            }
        });
        connection.end();
});


//CREATE LISTSONG (case 1 listsongs)
//Call -> http://127.0.0.1:3000/listsongs/create?list_name=ListaPrueba&user_id=1
router.get('/create', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    insert into cancionerocatolico.listsong
                    (listsong_name, listsong_user_id)
                    values ('${querydata.list_name}', '${querydata.user_id}');                  
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Listsong failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Adding listsongs - successful', 
                    'listsongID': rows.insertId
                });
            }
        });
        connection.end();
});


//EDIT LISTSONG (case 2 listsongs)
//Call -> http://127.0.0.1:3000/listsongs/edit?list_id=29&list_name=Nueva Luz
router.get('/edit', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    update cancionerocatolico.listsong set
                     listsong_name = '${querydata.list_name}'
                     where listsong_id = ${querydata.list_id};                  
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Listsong failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Updating listsongs - successful'
                });
            }
        });
        connection.end();
});


//DELETE LISTSONG (case 3 listsongs)
//Call -> http://127.0.0.1:3000/listsongs/delete?list_id=50
router.get('/delete', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =   `
                        delete from cancionerocatolico.listsong
                            where listsong_id = ${querydata.list_id};                  
                        `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Listsong failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Deleting listsongs - successful'
                });
            }
        });
        connection.end();
});


module.exports = router; //available to use from user.