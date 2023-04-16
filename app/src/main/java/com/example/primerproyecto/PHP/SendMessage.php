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
    $resultado = mysqli_query($con, "SELECT Token FROM Token WHERE Token <> '' AND Token IS NOT NULL");

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
        'Authorization: key=MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCzztIqxquHOGFORNHuqPAMIwKMTazJDZDF4eGBqaya2EORrhLcVM8U7j5qKvpDvxvw22JwgF2qlsbDy2HnaZxBlcwatiKw7trumJjiOPQ2K1nKqt6Aa+ERl/ISHoEbEeaEvzE0tgVHT8pm/mxFFAeqxoWw5oDcK+/0IiADItF5fP4yIilxGqL5omPJ014Vp23evY5Su1sZ+l5GXlJhXXWYZ8i9zaV9HsZn7meoJgvVu9Z6kLonJNF6mB00S/Zi83bI3/ZYZhR4D7mBxIdNL/hCyVfrTkAD6LniF1tQZ9nIPOm+4U0k2+c62CwmPy66gx9hGvTNGN/l1Sb/5cLz9V9PAgMBAAECggEADVoru9wfm12nHCt6y2uQJ9aRtzQtbeCsAjB75n0LFunoWpu/8CH8HrnHuL/oRbpIq4zJtUD9s6VEAqt1DGeIoEuCFJm3NHGy9Z+EpYfv2qCYJ7dN6TYZneItVQXJizu7uoUtdOnvX771G3Yujr1QtHB5jQwQ6vNNplDCankzb6n7NpZkEo+KKCdTrQmvwA+5rHLSVkVr49jG4HSM4ofb+1cfPysEv1rbybd4V5aa7DwG0uTqaJOdZomnbsrkp2sDNaU9wh34UEntUeW/8VE8h0YiMhR2XpnyXcHuCZEXVCHLF5JD0hvxz0gZVoOCZxPdocOPfVSjagB+vpLuZFwtgQKBgQDnlyR/6kX9s+n71HljJEjb+fSgliFfcbvSlvkWEzRSlch1EHgwITkR1mkUM9fd3wvkuXWnTfFKzvX1f5fLnR0M8h5Ok9pmsZlRoS82fc/pLOKmjbDNl8HCTFX0WZ/6Xzgse4Sfwvrao+rUatSAmpNCZ++iCRJtKSA+4b+WjlOMWQKBgQDGwna4EKuT5P5XdP/QET0705NEgkKectXCC++vhMIdFzg6zFsA8m6Wcwzc1QJwlaxG0S5K8x2cBOW7z8tpWoumQyoe5OSf+s6fpTnmiA3U2FI0pqDKNo0JIoE81pBG8C8AqcLCMX82XJzu4VD2OtIEg9ZkQMrK5pVOSUPuSvwz5wKBgCNgUCAGDlj50aU5SK2fTk3j+b1KEaD9w54gl74KgvSWkr9nG6Tmfkw+P2DppnDq8Gso9VLwMgqmYKX2rp5YyqY+meiQ50zGllAE2ixtvYO16o1cmYReSSe/92VTB3/8aZ1J5PFunFknruhxxTI76oC7VbyfvCPWo2lXnKeH6DVJAoGARbuaTU/D8rpnpaVtzsENm7zKMUIGP3MInfD0Ib9RH0WzO6XQ0886j1xECgJNvdQ1Qg9Hz5HmNIPfWfo4Ynka/7UWxQoHUD19WrNsUf4v5BoVFVXUmb1z092gPAkHS2wfAXuN0fXtrNPnV0QD1K+kWWMrx7aXr4Igpfu8cpEqVMcCgYAp3SSWge2ir4fUwdpKeIYkCSGwlcId820gl4n9wqE8AVKgbbRrYe/dpNhwRhTRd4zz89sFRVpPbElVdBneP7foDJwtQ6rvTJQMi6rB8nKeBYUShGlAIAyiep3SrgyWS/UXC8lzQ7tbS0Rb61CbTVSiivbBRhKmgi1n2b4gQIPCSw==',
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

    echo $msgJSON;
    echo json_encode($cabecera);

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