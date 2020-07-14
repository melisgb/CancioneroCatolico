<?php

//To call service : 
//case 	1 - create
//		2 - edit
//		3 - delete
//		4 - addSongs to List
//		5 - removeSongs from List

require("DBInfoCancionero.inc");
include("loggerCancionero.php");

if($_GET['case'] == 1){
	///Inserta una nueva lista 
	//http://localhost:8000/cancionero/listsongs.php?case=1&list_name=NewList&user_id=1
	$query = "insert into cancionerocatolico.listsong
				(listsong_name, listsong_user_id)
				values (".
				"'".$_GET['list_name']. "', ". 
				 $_GET['user_id']. ");"; 
	wh_log($query);
	$result = mysqli_query($connect, $query);
	wh_log($result);
}
elseif($_GET['case'] == 2){
	//Actualiza el nombre de la lista 
	//http://localhost:8000/cancionero/listsongs.php?case=2&list_id=4&list_name=Nueva Luz
	$query = "update cancionerocatolico.listsong  
		         set listsong_name = '".$_GET['list_name']. "'
			   where listsong_id = ".$_GET['list_id']. ";";
	wh_log($query);
	$result = mysqli_query($connect, $query);
	wh_log($result);
}
elseif($_GET['case'] == 3){
	//Eliminar la lista usando ID
	//http://localhost:8000/cancionero/listsongs.php?case=3&list_id=3
	$query = "delete from cancionerocatolico.listsong_songs 
				where listsong_id = ". $_GET['list_id']. ";";
	$result = mysqli_query($connect, $query);

	$query = "delete from cancionerocatolico.listsong  
				where listsong_id = ". $_GET['list_id']. ";";
	wh_log($query);
	$result = mysqli_query($connect, $query);

	wh_log($result);
}
elseif($_GET['case'] == 4){
	//Agregar canciones a esta lista
	//http://localhost:8000/cancionero/listsongs.php?case=4&list_id=1&songs_ids=10,11,12

	$songsArray = explode(",", $_GET['songs_ids']);

	foreach ($songsArray as $song_id){
		$query = "insert ignore into cancionerocatolico.listsong_songs
				(listsong_id, listsong_song_id)
					values (".$_GET['list_id'].",". $song_id.");";
		wh_log($query);
		$result = mysqli_query($connect, $query);
		wh_log($result);
	}
}
elseif($_GET['case'] == 5){
	//Eliminar cancion de esta lista
	//http://localhost:8000/cancionero/listsongs.php?case=5&list_id=1&songs_ids=2,3

	$songsArray = explode(",", $_GET['songs_ids']);

	foreach ($songsArray as $song_id){
		$query = "delete from cancionerocatolico.listsong_songs
					where listsong_id = ".$_GET['list_id'].
					" and listsong_song_id = ".$song_id.";";
		wh_log($query);
		$result = mysqli_query($connect, $query);
		wh_log($result);
	}
}
// To use only when debugging
// var_dump($query);

if(!$result){
	//wh_log(mysqli_error($connect)); //to print the error returned
	die('Error running query');
}
else{
	if($_GET['case'] == 1){
		//get the ID generated for new listsong
		print("{ 'msg' : 'Adding listsongs - successful', 'listsongID' : ". $connect->insert_id ."} ");
	}
	elseif($_GET['case'] == 2){
		//to return when listsong is updated
		print("{ 'msg' : 'Updating listsongs - successful' }");
	}
	elseif($_GET['case'] == 3){
		//to return when listsong is deleted
		print("{ 'msg' : 'Deleting listsongs - successful' }");
	}
	elseif($_GET['case'] == 4){
		//to return when the song is added into listsong
		print("{ 'msg' : 'Adding songs into listsongs - successful' }");
	}
	elseif($_GET['case'] == 5){
		//to return when the song is removed from listsong
		print("{ 'msg' : 'Removing songs from listsongs - successful' }");
	}
}
// mysqli_close($connect);

?>



