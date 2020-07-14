<?php

//To call service : 

//Different cases: 
//Case 1 - case 2,   query = ?, startFrom = x.

require("DBInfoCancionero.inc");
include("loggerCancionero.php");

if($_GET['case'] == 1){
	//Busca en todas las canciones. Filtro opcional por coincidencia
	//http://localhost:8000/cancionero/get_songs.php?case=1&keyword=Amar&startFrom=0
	$query = "select * from cancionerocatolico.song  
			   where song_title like ".
				  "'%". $_GET['keyword']. "%' ".
				  "or song_artist like ".
				  "'%". $_GET['keyword']. "%' ".
				  "or song_lyrics like ".
				  "'%". $_GET['keyword']. "%' ".
				  " order by song_title asc".
					" limit 20 offset ".
					 $_GET['startFrom'].
					 ";";	
}
elseif($_GET['case'] == 2){
	//Busca todas las canciones asociadas a una lista en especifico
	//A SER USADA EN OTRO ARCHIVO DE SOLO LISTA>>> 
	//http://localhost:8000/cancionero/get_songs.php?case=2&listsong_id=1
	$query = "select listsong_id list_id, song_id, song_title, song_artist, song_lyrics, song_tags 
	          from cancionerocatolico.listsong_songs_view v
	          JOIN song s
	          ON v.listsong_song_id = s.song_id
	          WHERE listsong_id = ".
		  		$_GET['listsong_id']. 
			" ORDER BY song_title ".
			" limit 30 ;";
}
elseif($_GET['case'] == 3){
	//Busca una cancion en especifico 
	//http://localhost:8000/cancionero/get_songs.php?case=3&song_id=1
	$query = "select * from cancionerocatolico.song
	          WHERE song_id = ".
		  		$_GET['song_id']. ";";
}
elseif($_GET['case'] == 4){
	//Busca una cancion en especifico 
	//http://localhost:8000/cancionero/get_songs.php?case=4&song_tags=Ordinario,Salmos
	
	$song_tags = explode(",", $_GET['song_tags']);
	
	$query = "select * from cancionerocatolico.song
	          WHERE song_tags in ".
		  		$_GET['song_id']. ";";
}
// To use only when debugging
// var_dump($query);

wh_log($query);
$result = mysqli_query($connect, $query);

if(!$result){
	die('Error running query');
}

$songsInfo = array();

while( $row = mysqli_fetch_assoc($result)){
	$songsInfo[] = $row;
}


print("{ 'msg' : 'Loading songs - successful', 'songsInfo': ". json_encode($songsInfo)." }");

mysqli_free_result($result);
// mysqli_close($connect);

?>



