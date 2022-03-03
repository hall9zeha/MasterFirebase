<?php
class Notification{
//*****************************************************************************************************
//*************************************   class to send push notifications
//*****************************************************************************************************
	function sendNotificationByTopic($title, $message, $topic, $uid, $email, $photoUrl, $username){
		$path_to_firebase_cm = 'https://fcm.googleapis.com/fcm/send'; 
		
		$fields = array(
            'to' => "/topics/$topic",
            'notification' => array('title' => $title, 'body' => $message, 'click_action' => 'OPEN_CHAT','icon' => 'ic_texting_notify', 'color' => '#2196f3', 'tag' => $email, 'sound' => 'default'),
			'time_to_live' => 604800,
            'data' => array('uid' => $uid, 'email' => $email, 'photoUrl' => $photoUrl, 'username' => $username)
        );

        $headers = array('Authorization:key=[your key of project in firebase/delete brackets]',
            'Content-Type:application/json'
        );
		
		$ch = curl_init();
		
		curl_setopt($ch, CURLOPT_URL, $path_to_firebase_cm);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_IPRESOLVE, CURL_IPRESOLVE_V4 );
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
		
        $result = curl_exec($ch);

        if(!$result) {
          $response["success"]=100;
          $response["Error"]='Error: "' . curl_error($ch) . '" - Code: ' . curl_errno($ch);
        } else {
          $response["success"]=3;
          $response["StatusCode"]= curl_getinfo($ch, CURLINFO_HTTP_CODE);
          $response["message"]='Notificacion enviada correctamente.';
          $response["Response HTTP Body"]= " - " .$result ." -";
        }

		curl_close($ch);

        return ($response);
	}
}
?>
