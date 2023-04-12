<?php
    $DB_SERVER="127.0.0.1"; #la dirección del servidor
    $DB_USER="Xpllano002"; #el usuario para esa base de datos
    $DB_PASS="hiQWqsrz"; #la clave para ese usuario
    $DB_DATABASE= "Xpllano002_Splip"; #la base de datos a la que hay que conectarse

    # Se establece la conexión:
    $con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

    #Comprobamos conexión
    if (mysqli_connect_errno()) {
        echo 'Error de conexion: ' . mysqli_connect_error();
        exit();
    }

    # Ejecutar la sentencia SQL
    $resultado = mysqli_query($con, "SELECT Token FROM Personas WHERE Token <> '' AND Token IS NOT NULL");

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

    #Acceder al resultado
    while( $fila = $resultado -> fetch_assoc()){
        $tokens[] = array(
                'Token' => $fila['Token']);
    } 

    $cabecera= array(
        'Authorization: key=BJrOrSpP-O0bKBl1gTlUw1NbjHPzdI6egt-8cFa4vRRYCXQUQUk3K49ttuIXMQwnHSVe6v2K6SiHzUR9WNHWiT0',
        'Content-Type: application/json'
        );

    $msg = array(
        'registration_ids'=> $tokens,
        'data' => array (
            "ejemplo" => "Este es mi mensaje de ejemplo"
        ),
        'notification' => array (
            "body" => "Este es el texto de la notificación!",
            "title" => "Título de la notificación",
            "icon" => "ic_stat_ic_notification",
            'click_action'=>'AvisoFirebase'
        )    
    );

    $msgJSON= json_encode ($msg);

    $ch = curl_init(); #inicializar el handler de curl
    #indicar el destino de la petición, el servicio FCM de google
    curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
    #indicar que la conexión es de tipo POST
    curl_setopt( $ch, CURLOPT_POST, true );
    #agregar las cabeceras
    curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
    #Indicar que se desea recibir la respuesta a la conexión en forma de string
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
    #agregar los datos de la petición en formato JSON
    curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
    #ejecutar la llamada
    $resultado= curl_exec( $ch );
    #cerrar el handler de curl
    curl_close( $ch );

    if (curl_errno($ch)) {
        print curl_error($ch);
    }
    echo $resultado;
        

?>