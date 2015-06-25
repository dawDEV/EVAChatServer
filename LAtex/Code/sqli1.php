<?php
	if(isset($_POST['submit'])){
		$username = $_POST['username'];
		$password = $_POST['password'];
		$query = "SELECT * FROM logins WHERE username = '"
		.$username."' AND password = '".$password."';";
		mysql_connect("localhost", "root", "");
		mysql_select_db("sits");
		$result = mysql_query($query);
		if(mysql_num_rows($result) > 0){
			/** LOGIN RICHTIG
			* Cookies setzen & weiterleiten
			* In diesem Beispiel wird nur eine Ausgabe gemacht um 
			* auszugeben ob der Login geklappt hat
			*/
			die("Login richtig!");
		} else{
			/** LOGIN FALSCH
			* Login verweigern, dementsprechend Aktionen ausführen
			* z.B. Anzahl der fehlerhaften Logins erhöhen, 
			* Loginversuch protokollieren, ...
			*/
			die("Login falsch!");
		}
	}
?>
<form method="POST">
	Benutzername: <input type="text" name="username" /><br />
	Passwort: <input type="password" name="password" /><br />
	<input type="submit" name="submit">
</form>
