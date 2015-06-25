<?php
	mysql_connect("localhost", "root", "");
	mysql_select_db("sits");

	if(isset($_POST['submit'])){
		$name = $_POST['name'];
		$text = $_POST['text'];
		$query = "INSERT INTO guestbook(name, text) VALUES('"
		.$name."', '".$text."');";
		mysql_query($query);
		echo("Eintrag gespeichert!<br />");
	}
?>
<form method="POST">
	Ihr Name: <input type="text" name="name" /><br />
	Ihr Text:<br />
	<textarea name="text"></textarea><br />
	<input type="submit" name="submit"><br /><br />
</form>
<h1>Die bisherigen Eintr&auml;ge</h1>
<?php
	$query = "SELECT name, text FROM guestbook ORDER BY id ASC";
	$result = mysql_query($query);
	while($row = mysql_fetch_array($result)){
		echo("<b>".$row[0]."</b><br />".$row[1]."<br /><br />");
	}
?>
