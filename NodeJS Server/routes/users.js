var express = require('express');
var router = express.Router();
var mysql = require('mysql');

//assembling the request
router.get('/add', function(req, res, next){
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

    var myQuery = "insert into instasocial.post(post_user_id, post_content, post_image_url) "+
        "values ("+ 
        querydata.post_user_id + 
        ", '" +
        querydata.post_content +
        "', '" +
        querydata.post_image_url +
        "'); "; 

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.send("{'msg' : 'Error cannot add'}");
            }
            else{
                res.send("{'msg' : 'Post is added'}");
                //in the case we have more than one result row: 
                //res.send(rows);
            }
        });
        connection.end();
    //res.send('Welcome node');
});
//available to use from user.
module.exports = router;