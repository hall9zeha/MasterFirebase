# MasterFirebase

Proyecto comprendido por varias aplicaciones entre complejas y más sencillas, usando los servicios de Firebase de google y los patrones de arquitectura MVC y MVP  

## Software :hammer_and_wrench:

* Android Studio

## Servicios  	:gear:

* [Firebase Firestore](https://firebase.google.com/docs/firestore/quickstart) -- Base de datos NoSQL alojada en la nube, en tiempo real.
* [Firebase Realtime Database](https://firebase.google.com/docs/database/android/start) -- Base de datos NoSQL alojada en la nube, en tiempo real.
* [Firebase Storage](https://firebase.google.com/docs/storage/android/start) -- Servicio de almacenamiento de objetos (fotos, videos).
* [Firebase Authentication](https://firebase.google.com/docs/auth?hl=es-419) -- Autenticación de usuarios con diversos proveedores.
* [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging?hl=es-419) -- Mensajería multiplataforma que te permite enviar mensajes de forma segura.

## Librerías :books:
 
* [FirebaseUI](https://github.com/firebase/FirebaseUI-Android) -- Librería recomendada por google para manejar el sdk de firestore  con mayor simplicidad y eficiencia.
* [Glide](https://github.com/bumptech/glide) -- Librería para el manejo de imágenes.
* [Volley](https://google.github.io/volley/) -- Librería HTTP para peticiones web. 
* [Circle Image View](https://github.com/hdodenhof/CircleImageView) -- Librería para convertir una Imageview normal en circular.
* [Event Bus](https://github.com/greenrobot/EventBus) -- Librería para simplificar la comunicación entre componentes en android y java.
* [Gson](https://github.com/google/gson) -- Librería para convertir objetos java en su correspondiente representación en JSON.
* [MaterialShowCaseView](https://github.com/deano2390/MaterialShowcaseView) -- Librería diseñada para resaltar y mostrar partes específicas de las aplicaciones al usuario con una superposición distintiva y atractiva.

## Aplicaciones del proyecto :card_index_dividers:

* FireChat :iphone:
* FirebaseStorageApp :iphone:
* InventarioFirestore :iphone:
* MasterDetailCloud :iphone:
* MultiLoginFirebase :iphone:
* OfertasCloudMessage :iphone:
* RemoteConfigApp :iphone:

## Importante 

Para que la aplicación funcione correctamente al conectarla a su Cuenta de Firebase y activar la autenticación con google, debe proporcionar el código SHA-256 o SHA-1 generadas desde su IDE android studio indroduciendo en la terminal de Android Studio el comando: ```graddle signingReport```
y presionando ```ctrl``` ```+``` ```enter```.

También deberá agregar su propio archivo ```google-services.json``` generado en la configuración de su proyecto de firebase, dentro de la aplicación en android studio.

Para manejar las notificaciones desde un servidor externo pero usando el servicio de firebase cloud messaging, se ha proporcionado dos archivos PHP 
* [TextingRS](https://github.com/hall9zeha/MasterFirebase/blob/main/Extras/TextingRS.php)
* [Notification](https://github.com/hall9zeha/MasterFirebase/blob/main/Extras/Notification.php)
 
Estos archivos pueden ser utilizados y modificados a conveniencia, y cargados al servidor que utilice. Solo se debe agregar la llave de su proyecto de firebase.

## Capturas de FireChat :framed_picture:

<img src="https://github.com/hall9zeha/MasterFirebase/blob/main/Screenshots/Screenshot_20220303-130141~2.jpg" alt="drawing" width="300"/>|
<img src="https://github.com/hall9zeha/MasterFirebase/blob/main/Screenshots/Screenshot_20220303-130149~2.jpg" alt="drawing" width="300"/>|
<img src="https://github.com/hall9zeha/MasterFirebase/blob/main/Screenshots/Screenshot_20220303-130157~2.jpg" alt="drawing" width="300"/>|
<img src="https://github.com/hall9zeha/MasterFirebase/blob/main/Screenshots/Screenshot_20220303-130222~2.jpg" alt="drawing" width="300"/>



