<?php
	header("Access-Control-Allow-Origin: *");
	header("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");

	$data = json_decode(file_get_contents('php://input'), true);

	require('Notification.php');

	$metodoBl = $data["method"];
	$tituloBl = $data["title"];
	$mensajeBl = $data["message"];
	$topicBl = $data["topic"];
	$uidBL = $data["uid"];
	$emailBl = $data["email"];
	$photoUrlBl = $data["photoUrl"];
	$usernameBl = $data["username"];
	
	//***********************************************************************************************************
	//*****   AQUI INICIA LA DEFINICIÓN DE FUNCIONES QUE A SU VEZ ACCEDERAN A LOS MÉTODOS DEL OBJETO NOTIFICATION
	//***********************************************************************************************************
	function sendNotification($titulo, $mensaje, $topic, $uid, $email, $photoUrl, $username){
		$notification = new Notification();
		$response=$notification->sendNotificationByTopic($titulo, $mensaje, $topic, $uid, $email, $photoUrl, $username);
		
		return $response;
	}
	
	//************************************************************************************************************
	//*****   SWICH UTILIZADO PARA FILTRAR Y MANDAR A LLAMAR EL MÉTODO CORRESPONDIENTE
	//************************************************************************************************************
	switch ($metodoBl) {
		case "sendNotification":{
			$response=sendNotification($tituloBl, $mensajeBl, $topicBl, $uidBL, $emailBl, $photoUrlBl, $usernameBl);
			break;
		}
			
		default:{
			$response["success"]=104;
			$response["message"]='El método indicado no se encuentra registrado';
		}
	}
	
	echo json_encode ($response)
?>
