import java.net.*;
import java.io.*;
import java.util.Scanner;

public class servidor {
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream datosEntrada;
    private DataOutputStream datosSalida;
    Scanner scanner = new Scanner(System.in);

    final String FINALIZACION = "finalizar()";

    //funcion que establece la conexion con el puerto del cliente
    public void establecerConexion(int puerto) {
        try {
            //utilizamos el constructor de la libreria java.net en la que se crea un ServerSocket atada a un puerto en especifico y lo asignamos a la variable serverSocket
            serverSocket = new ServerSocket(puerto);
            System.out.print("Estableciendo conexión con el puerto "+ String.valueOf(puerto));

            socket=serverSocket.accept();
            System.out.print("Conexión generada satisfactoriamente con el puerto: "+ socket.getInetAddress().getHostName()+"\n\n\n");
        } catch (Exception e) {
            System.out.print("Error en establecer Conexion "+e.getMessage());
            System.exit(0);
        }
    }


}