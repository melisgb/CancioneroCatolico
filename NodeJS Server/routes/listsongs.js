var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var config = {
        host : '127.0.0.1',
        user : 'dbuser',
        password : 'catolico', 
        database : 'cancionerocatolico'
    };

//Case 4 - Insert songs into songlist
//Call-> http://127.0.0.1:3000/listsongs/insert?list_id=29&songs_ids=10,11,12
router.get('/insert', function(req, res, next){
    console.log("h")
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    function formatInsert(song_id){
        return ` (${querydata.list_id}, ${song_id} )`;
    }

    var fullQuery = `insert ignore into cancionerocatolico.listsong_songs
                    (listsong_id, listsong_song_id) values`;

    var songsIDS = querydata.songs_ids.split(',');                
    var idsFormatted = songsIDS.map(formatInsert)
    var idsConcat = idsFormatted.join(", ")              
    fullQuery += idsConcat +";"
    
    connection.query(fullQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.send({'msg' : 'Listsong failed to edit'});
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


//CREATE LISTSONG
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
                res.send({'msg' : 'Listsong failed to edit'});
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


//EDIT LISTSONG
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
                res.send({'msg' : 'Listsong failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Updating listsongs - successful'
                });
            }
        });
        connection.end();
});


//DELETE LISTSONG
//Call -> http://127.0.0.1:3000/listsongs/delete?list_id=50
router.get('/delete', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQueryDet =  `
                    delete from cancionerocatolico.listsong_songs
    	             where listsong_id = ${querydata.list_id};                  
                    `;

    connection.query(myQueryDet);

    var myQuery =   `
                        delete from cancionerocatolico.listsong
                            where listsong_id = ${querydata.list_id};                  
                        `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.send({'msg' : 'Listsong failed to edit'});
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