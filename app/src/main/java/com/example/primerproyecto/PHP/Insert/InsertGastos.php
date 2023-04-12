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
        $resultado = mysqli_query($con, "INSERT INTO Gastos(Usuario, Grupo, Persona, Titulo, Cantidad, Fecha, Latitud, Longitud) VALUES ('admin','Viaje a Malaga', 'Peio', 'Cena pizzeria', '56.8', '2023-03-11', null, null)");
    } catch (Exception $e) { }

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }
    else{
        $resultado = mysqli_query($con, "SELECT * FROM Gastos ORDER BY Codigo DESC LIMIT 1");

        while( $fila = $resultado -> fetch_assoc()){
            $resultado_final[] = array(
                    'Codigo' => $fila['Codigo'],
                    'Usuario' => $fila['Usuario'],
                    'Grupo' => $fila['Grupo'],
                    'Persona' => $fila['Persona'],
                    'Titulo' => $fila['Titulo'],
                    'Cantidad' => $fila['Cantidad'],
                    'Fecha' => $fila['Fecha'],
                    'Latitud' => $fila['Latitud'],
                    'Latitud' => $fila['Latitud']);
        }            

        echo json_encode($resultado_final);
    }

?>