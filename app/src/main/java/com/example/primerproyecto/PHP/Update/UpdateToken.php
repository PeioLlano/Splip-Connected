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

    $token = $_POST["token"];
    $usuario= $_POST["usuario"];

    try {
        $resultado = mysqli_query($con, "UPDATE Usuarios SET Token='$token' WHERE Usuario = '$usuario'");
    } catch (Exception $e) { }

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

?>