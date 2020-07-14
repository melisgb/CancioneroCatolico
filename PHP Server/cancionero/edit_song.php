<?php

//To call service : 
require("DBInfoCancionero.inc");
include("loggerCancionero.php");
/*
	1 - Insert
	2 - Update
*/

if($_GET['case'] == 1){
//http://localhost:8000/cancionero/edit_song.php?case=1&song_title=La paz te doy a ti mi hermano&song_artist=Aleluya&song_lyrics=para ser santo hay que ser sencillo&song_tags=Ordinario
	$query = "insert into cancionerocatolico.song
				(song_title, song_artist, song_lyrics, song_tags)
				values	(".
				"'".$_GET['song_title']."',".
				"'".$_GET['song_artist']."',".
				"'".$_GET['song_lyrics']."',".			
				"'".$_GET['song_tags']."');";
}
elseif($_GET['case'] == 2){
//http://localhost:8000/cancionero/edit_song.php?case=2&song_id=6&song_title=La paz te doy&song_artist=Aleluya&song_lyrics=para ser santo hay que ser sencillo&song_tags=Ordinario
	$query = "update cancionerocatolico.song set ".
				"song_title =". "'".$_GET['song_title']."', ".
				"song_artist =". "'".$_GET['song_artist']."', ".
				"song_lyrics =". "'".$_GET['song_lyrics']."', ".
				"song_tags =". "'".$_GET['song_tags']."' ".
				"where song_id =".$_GET['song_id'].";";

}
elseif($_GET['case'] == 3){
//http://localhost:8000/cancionero/edit_song.php?case=3&song_id=6
	$query = "delete from cancionerocatolico.song ".
				"where song_id =".$_GET['song_id'].";";

}


$result = mysqli_query($connect, $query);

wh_log($connect->insert_id);
if(!$result){
	print("{ 'msg' : 'Song failed to edit'} ");
}
else{
	if($_GET['case'] == 1){
		$select_query = "select * from cancionerocatolico.song where song_id =".
				 $connect->insert_id .";";

		$result = mysqli_query($connect, $select_query);
		$songInfo = array();
		
		while( $row = mysqli_fetch_assoc($result)){
			$songInfo[] = $row;
			break;
		}
		print("{ 'msg' : 'Song saved', 'songInfo': '". json_encode($songInfo)."' }");
	}
	elseif($_GET['case'] == 2){
		
		print("{ 'msg' : 'Song updated'}");
	}
	elseif($_GET['case'] == 3){
		
		print("{ 'msg' : 'Song deleted'}");
	}
}

// mysqli_close($connect);

?>

