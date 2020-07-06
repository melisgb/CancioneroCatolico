<?php

$host = "127.0.0.1";
$user = "root";
$password = "admin007";
$database = "cancionerocatolico";

$connect = mysqli_connect($host, $user, $password, $database);

if(mysqli_connect_errno()){
	die("cannot connect to db". mysqli_connect_error());
}

?>