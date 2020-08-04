var express = require('express');
var router = express.Router();
var mysql = require('mysql');


//SELECT SPECIFIC USER by email
//Call -> http://127.0.0.1:3000/users/?email=prueba@ccca
router.get('/', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;
    var config = {
        host : '127.0.0.1',
        user : 'dbuser',
        password : 'catolico', 
        database : 'cancionerocatolico'
    }
    
    if(!querydata.email ){
        //checks if parameters are null or empty
        res.status(400).send({'msg' : 'Email required'});
        return;
    }
    var connection = mysql.createConnection(config);
    connection.connect();


    var myQuery =  `
                    select user_id, user_name, user_email
                      from cancionerocatolico.user
                     where user_email = '${querydata.email}';                   
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(500).send({'msg' : 'Error running query'});
            }
            else if(rows.length==0){
                console.log(err);
                res.status(401).send({'msg' : 'No user matches'});
            }
            else{
                res.send({
                    'msg' : 'Loading user - successful', 
                    'userInfo': rows
                });
            }
        });
        connection.end();
});

//CREATE USER
//Call -> http://127.0.0.1:3000/users/add?username=Prueba&email=prueba@ccc
router.get('/add', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;
    var config = {
        host : '127.0.0.1',
        user : 'dbuser',
        password : 'catolico', 
        database : 'cancionerocatolico'
    }

    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    insert into cancionerocatolico.user(user_name, user_email)
	                 values ('${querydata.username}', '${querydata.email}' );                   
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'User failed to insert'});
            }
            else{
                res.send({
                    'msg' : 'User created - successful', 
                    'userID': rows.insertId 
                });
            }
        });
        connection.end();
});


//UPDATE USER
//Call -> http://127.0.0.1:3000/users/update?id=4&password=100055
router.get('/update', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;
    var config = {
        host : '127.0.0.1',
        user : 'dbuser',
        password : 'catolico', 
        database : 'cancionerocatolico'
    }
    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    update cancionerocatolico.user
                       set user_password = '${querydata.password}'
                     where user_id = ${querydata.id};                   
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'User failed to update'});
            }
            else{
                res.send({
                    'msg' : 'User updated - successful'
                });
            }
        });
        connection.end();
});

//DELETE USER
//Call -> http://127.0.0.1:3000/users/delete?id=3
router.get('/delete', function(req, res, next){
    res.header('Access-Control-Allow-Origin', '*'); //security
    var querydata = req.query;
    var config = {
        host : '127.0.0.1',
        user : 'dbuser',
        password : 'catolico', 
        database : 'cancionerocatolico'
    }
    var connection = mysql.createConnection(config);
    connection.connect();
    
    var myQuery =  `
                    delete from cancionerocatolico.user
                     where user_id = ${querydata.id};                   
                    `;

    connection.query(myQuery,
        function(err, rows, fields){
            if(err){
                console.log(err);
                res.status(400).send({'msg' : 'User failed to delete'});
            }
            else{
                res.send({
                    'msg' : 'User deleted - successful'
                });
            }
        });
        connection.end();
});
//available to use from user.
module.exports = router;