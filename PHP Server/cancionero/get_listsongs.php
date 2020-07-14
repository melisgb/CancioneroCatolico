<?php

//To call service : 

require("DBInfoCancionero.inc");
include("loggerCancionero.php");

//Busca todas las listas asociadas con el usuario
//http://localhost:8000/cancionero/get_listsongs.php?user_id=1
$query = "select listsong_id, listsong_name 
			from cancionerocatolico.listsong 
		   where listsong_user_id = ". $_GET['user_id'].
		    "  order by listsong_name asc
			limit 20 ;";


// To use only when debugging
// var_dump($query);

wh_log($query);
$result = mysqli_query($connect, $query);

if(!$result){
	die('Error running query');
}

$listsongs = array();

while( $row = mysqli_fetch_assoc($result)){
	$listsongs[] = $row;
}

if($listsongs){
	print("{ 'msg' : 'Loading summary lists - successful', 'listsongs': ". json_encode($listsongs)." }");

} else {
	print("{ 'msg' : 'Loading summary lists - 0 lists found' }");
}

mysqli_free_result($result);
// mysqli_close($connect);

?>



