<?php
/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

include '../yek/db_info.php';
$pdo = db_connect($db_user, $db_pwd);

$method = $_POST["method"];

if($method == "write_review"){
	$cultcode = $_POST["cultcode"];
	$author = $_POST["author"];
	$rating = $_POST["rating"];
	$content = $_POST["content"];
	$end_date = $_POST["end_date"];

	try{
		$sql = "INSERT INTO review(cultcode,author,rating,content,end_date,reg_date) VALUES(:cultcode,:author,:rating,:content,:end_date,now())";
		$stmh = $pdo->prepare($sql);
		$stmh->bindValue(':cultcode',$cultcode, PDO::PARAM_INT);
		$stmh->bindValue(':author',$author, PDO::PARAM_STR);
		$stmh->bindValue(':rating',$rating, PDO::PARAM_INT);
		$stmh->bindValue(':content',$content, PDO::PARAM_STR);
		$stmh->bindValue(':end_date',$end_date, PDO::PARAM_STR);
		$stmh->execute();
	}
	catch(Exception $e){
		exit;
	}
	$json = array("success" => 1);
	echo json_encode($json, JSON_UNESCAPED_UNICODE);
	exit;
}
else if($method == "read_review"){
	$cultcode = $_POST["cultcode"];

	try{
		$sql = "SELECT author,rating,content,reg_date FROM review WHERE cultcode = :cultcode ORDER BY id DESC";
		$stmh = $pdo->prepare($sql);
		$stmh->bindValue(':cultcode',$cultcode, PDO::PARAM_INT);
		$stmh->execute();
		$result = $stmh->fetchAll();
	}
	catch(Exception $e){
		exit;
	}
	$json = array('items' => $result);
	echo json_encode($json, JSON_UNESCAPED_UNICODE);
	exit;
}
else if($method == "get_rating"){
	$cultcode = $_POST["cultcode"];
	try{
		$sql = "SELECT sum(rating) as sum,count(id) as count FROM review WHERE cultcode = :cultcode";
		$stmh = $pdo->prepare($sql);
		$stmh->bindValue(':cultcode',$cultcode, PDO::PARAM_INT);
		$stmh->execute();
		$result = $stmh->fetchAll();
	}
	catch(Exception $e){
		exit;
	}
	$json = array('rating' => $result);
	echo json_encode($json, JSON_UNESCAPED_UNICODE);
	exit;
}
else if($method == "review_count"){
	$subjcode = $_POST["subjcode"];

	try{
		if($subjcode == 0){//ALL
			$sql = "SELECT CULTCODE, count(CULTCODE) as COUNT FROM review  WHERE end_date >= now() GROUP BY CULTCODE ORDER BY COUNT DESC LIMIT 10";
			$stmh = $pdo->prepare($sql);
			$stmh->execute();
		}else{
			$sql = "SELECT CULTCODE, count(CULTCODE) as COUNT FROM review WHERE end_date >= now() AND SUBJCODE = :SUBJCODE GROUP BY CULTCODE ORDER BY COUNT DESC LIMIT 10";
			$stmh = $pdo->prepare($sql);
			$stmh->bindValue(':subjcode',$subjcode, PDO::PARAM_INT);
			$stmh->execute();
		}
		$result = $stmh->fetchAll();
	}
	catch(Exception $e){
		exit;
	}
	$json = array('reviews' => $result);
	echo json_encode($json, JSON_UNESCAPED_UNICODE);
	exit;
}

function db_connect($db_user, $db_pwd){
	try{
		$pdo = new PDO('mysql:host=localhost;dbname=enjoy;charset=utf8',$db_user,$db_pwd);
		$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		$pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
	}catch(Exception $e){
		exit;
	}
	return $pdo;
}

?>