var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var config = {
    host : '127.0.0.1',
    user : process.env.CCDB_USER || 'dbuser',
    password : process.env.CCDB_PASSWORD || 'catolico', 
    database : 'cancionerocatolico'
    };

//SELECT ALL SONGS WITH KEYWORD
//Call-> http://127.0.0.1:3000/songs/?keyword=&startFrom=0&tags=Salmo
router.get('/', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;
    
    var connection = mysql.createConnection(config);
    connection.connect();

    if(querydata.tags ==null) querydata.tags = "";
    
    function formatTags(tag){
        return ` song_tags like '%${tag}%'`;
    }
    var tagsArr = querydata.tags.split(',');
    var tagsFormatted = tagsArr.map(formatTags);
    var tagsConcat = tagsFormatted.join(" or ");
    if(tagsConcat != "") tagsConcat = " and (" + tagsConcat + ")";

    var myQuery =  `
                    select song_id, song_title, song_artist, "" as song_lyrics, song_tags from cancionerocatolico.song 
                     where (song_title like '%${querydata.keyword||""}%' 
                        or song_artist like '%${querydata.keyword||""}%' 
                        or song_lyrics like '%${querydata.keyword||""}%')
                        ${tagsConcat} 
                     order by song_id asc limit 400 offset ${querydata.startFrom||0};
                    `;
    
    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Error running query'});
            }
            else{
                res.send({
                    'msg' : 'Loading songs - successful', 
                    'songsInfo': rows 
                });
            }
        });
        connection.end();
    //res.send('Welcome node');
});


//SELECT SONGS FROM LISTSONG
//Call -> http://127.0.0.1:3000/songs/list?listsong_id=2
router.get('/list', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    select listsong_id list_id, song_id, song_title, song_artist, song_lyrics, song_tags 
                      from cancionerocatolico.listsong_songs_view v
                      join song s
                        on v.listsong_song_id = s.song_id
                     where listsong_id = '${querydata.listsong_id}'  
                     order by song_title
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
                    'msg' : 'Loading songs - successful', 
                    'songsInfo': rows 
                });
            }
        });
        connection.end();
});

//SELECT SPECIFIC SONG
//Call -> http://127.0.0.1:3000/songs/song?song_id=15
router.get('/song', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    select * 
                      from cancionerocatolico.song
	                 where song_id = ${querydata.song_id};                   
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Error running query'});
            }
            else{
                res.send({
                    'msg' : 'Loading songs - successful', 
                    'songsInfo': rows 
                });
            }
        });
        connection.end();
});


//SELECT SONGS FILTERED BY TAG (deprecated)
//Call -> http://127.0.0.1:3000/songs/tags?song_tags=Ordinario,Salmo
router.get('/tags', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    function formatTags(tag){
        return ` song_tags like '%${tag}%'`;
    }
    var tagsArr = querydata.song_tags.split(',');
    var tagsFormatted = tagsArr.map(formatTags)
    var tagsConcat = tagsFormatted.join(" or ")

    var myQuery =  `
                    select * 
                      from cancionerocatolico.song
                     where ${tagsConcat}
                     order by song_title;                   
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Error running query'});
            }
            else{
                res.send({
                    'msg' : 'Loading songs - successful', 
                    'songsInfo': rows 
                });
            }
        });
        connection.end();
});


//CREATE SONG
//Call -> http://127.0.0.1:3000/songs/create?song_title=La paz te doy a ti mi hermano&song_artist=Aleluya&song_lyrics=para ser santo hay que ser sencillo&song_tags=Ordinario
router.get('/create', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    insert into cancionerocatolico.song (song_title, song_artist, song_lyrics, song_tags)
                    values	('${querydata.song_title}', '${querydata.song_artist}',
                    '${querydata.song_lyrics}', '${querydata.song_tags}');                  
                    `;


    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Song failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Song saved', 
                    'songInfo': rows.insertId
                });
            }
        });
        connection.end();
});


//EDIT SONG
//Call -> http://127.0.0.1:3000/songs/edit?song_id=6&song_title=La paz te doy a ti mi hermano&song_artist=Aleluya&song_lyrics=para ser santo hay que ser sencillo&song_tags=Ordinario
router.get('/edit', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    update cancionerocatolico.song set
                     song_title = '${querydata.song_title}', 
                     song_artist = '${querydata.song_artist}',
                     song_lyrics = '${querydata.song_lyrics}',
                     song_tags = '${querydata.song_tags}'
    	            where song_id = ${querydata.song_id};                  
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Song failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Song updated'
                });
            }
        });
        connection.end();
});


//DELETE SONG
//Call -> http://127.0.0.1:3000/songs/delete?song_id=30
router.get('/delete', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    delete from cancionerocatolico.song
    	             where song_id = ${querydata.song_id};                  
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'Song failed to edit'});
            }
            else{
                res.send({
                    'msg' : 'Song deleted'
                });
            }
        });
        connection.end();
});


module.exports = router; //available to use from user.