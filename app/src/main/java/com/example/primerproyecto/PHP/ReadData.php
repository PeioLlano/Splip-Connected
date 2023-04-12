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
    $resultado = mysqli_query($con, "SELECT * FROM Personas");

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

    #Acceder al resultado
    // switch ($tabla) {
    switch ('Personas') {
        case 'Usuarios':
            while( $fila = $resultado -> fetch_assoc()){
                $resultado_final[] = array(
                        'Usuario' => $fila['Usuario'],
                        'Contraseña' => $fila['Contraseña']);
            }            
            break;
        case 'Personas':
            while( $fila = $resultado -> fetch_assoc()){
                $resultado_final[] = array(
                        'Usuario' => $fila['Usuario'],
                        'Grupo' => $fila['Grupo'],
                        'Nombre' => $fila['Nombre']);
            }            
            break;
        case 'Grupos':
            while( $fila = $resultado -> fetch_assoc()){
                $resultado_final[] = array(
                        'Usuario' => $fila['Usuario'],
                        'Titulo' => $fila['Titulo'],
                        'Divisa' => $fila['Divisa']);
            }            
            break;
        case 'Pagos':
            while( $fila = $resultado -> fetch_assoc()){
                $resultado_final[] = array(
                        'Codigo' => $fila['Codigo'],
                        'Usuario' => $fila['Usuario'],
                        'Grupo' => $fila['Grupo'],
                        'PersonaAutora' => $fila['PersonaAutora'],
                        'PersonaDestinataria' => $fila['PersonaDestinataria'],
                        'Cantidad' => $fila['Cantidad'],
                        'Fecha' => $fila['Fecha']);
            }            
            break;
        case 'Gastos':
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
            break;
    }

    #Devolver el resultado en formato JSON
    echo json_encode($resultado_final);
?>