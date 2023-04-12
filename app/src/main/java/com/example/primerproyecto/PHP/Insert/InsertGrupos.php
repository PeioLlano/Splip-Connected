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

    $parametros = json_decode( file_get_contents( 'php://input' ), true );

    $tabla = $parametros["tabla"];
    #$tabla = 'Usuarios';
    #$condicion = $parametros["condicion"];

    # Ejecutar la sentencia SQL
    # $resultado = mysqli_query($con, "SELECT * FROM '$tabla' '$condicion'");
    # $resultado = mysqli_query($con, "SELECT * FROM '$tabla'");
    try {
        $resultado = mysqli_query($con, "INSERT INTO Grupos VALUES ('admin','Viaje a Malaga', '€')");
    } catch (Exception $e) { }

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

?>