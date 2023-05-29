import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class cliente {
    /*
    Se crean las variables para:
    1. El socket: Escribe y lee mensajes desde el cliente
    2. DataInputStream y DataOutPutStream: Dejan leer y escribir datos desde y hacia los flujos de los sockets4
    3. Escaner de escritura
    4. Comando de finalización del chat.*/
    private Socket socket;
    private DataInputStream datosEntrada =null;
    private DataOutputStream datosSalida=null;
     Scanner scannerCliente= new Scanner(System.in);
    final String FINALIZACION= "finalizar()";

    //funcion que establece la conexion con el puerto del Servidor
    public void establecerConexion(String ip, int puerto) {
        try {
            //Constructor que crea el socket en el lado del cliente. Necesita como entradas la ip y el puerto del servidor. Se usa localhost porque está en la misma máquina
            socket = new Socket(ip, puerto);
            //el metodo getInetAddress devuelve la direccion local del socket servidor acompanado del nombre del host
            System.out.print("Se está conectando a: " + socket.getInetAddress().getHostName());

        } catch (IOException e) {
            System.out.print("Excepcion al crear la conexión: " + e.getMessage());
            System.exit(0);

        }
    }
    //esta funcion crea los flujos de datos de entrada y salida del servidor y nos ayuda a definir la lectura y escritura
    public void establecerFlujos(){
        try{
            //Aqui usamos dos constructores DataInput y DataOutput que crean un flujo de datos a ser recibidos y compartidos por el socket. Estos flujos de datos son guardados en dos variables
            datosEntrada=new DataInputStream(socket.getInputStream());
            datosSalida= new DataOutputStream((socket.getOutputStream()));
            //el metodo flush obliga a escribir los datos de salida en el flujo de salida. no se mantienen datos en ningun buffer y todos deben ser escritos y enviados
            datosSalida.flush();

        }catch(IOException e) {
            System.out.print("No es posible intercambiar datos entre equipos: "+ e.getMessage());
        }
    }

    public void enviarDatos(String texto){
        try {
            //el metodo writeUTF escribe un String en el outputStream y lo codifica en UTF-8. El método flush obliga a escribir los datos en el flujo de salida
            datosSalida.writeUTF(texto);
            datosSalida.flush();
        }catch(IOException e) {
            System.out.print("Error IO al enviar");
        }
    }
    public void aceptarDatos() {
        String texto = "";
        try {
            //La conversación se mantiene con un bucle do/while que establece que se realizar la aceptación y escritura mientras no se escriba el comando para finalizar
            do {
                //transforma los datos de entrada a formato UTF y los almacena en una variable de texto
                texto = (String) datosEntrada.readUTF();
                //escribe en consola los datos de entrada y son asignados al "Servidor".  Se crea una linea vacía cada vez que hay un dato de entrada para poder escribir sobre esta
               System.out.print("\n[Servidor] => " + texto);
               System.out.print("\n[Usted] => ");
            //Se cierra la lectura y escritura de datos con el comando de finalización
            } while (!texto.equals(FINALIZACION));
        } catch (IOException e) {}
    }
    //Función usada para cerrar el flujo de datos y cerrar el socket en el servidor
    public void terminarConexion(){
        try{
//El metodo close aplica para la clase inputStream y para la clase socket, para cerrar los flujos de información abiertos
            datosSalida.close();
            datosEntrada.close();
            socket.close();
//Mensaje informativo en la cosola luego de cerrar los flujos
            System.out.print("Conexión Finalizada Exitósamente");

        }catch(IOException e){
            System.out.print("Error al finalizar conexión: "+e.getMessage());
        }finally {
            System.exit(0);
        }
    }

    public void activarConexion(String ip, int puerto){
        //La interfaz runnable ejecuta el hilo usando el método vacío run
        Thread hilo=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Se definen los métodos que debe ejecutarse simultaneamete junto al método Main()
                    establecerConexion(ip,puerto);
                    establecerFlujos();
                    aceptarDatos();

                }finally {
                    //Se inicia el hilo al activar la conexión
                    terminarConexion();
                }
            }
        });
        //Se inicia el hilo al activar la conexión
        hilo.start();

    }

    public void escribirDatosChat() {
        String textoEntrada="";
        while (true) {
            //Se usa el escaner creado para poder registrar las palabras en la consola y luego usa el método anterior (enviar datos) para enviarlos al servidor
            System.out.print("[Usted] => ");
            textoEntrada = scannerCliente.nextLine();
            //Verificamos que se envíen mensajes no vacios.
            if(textoEntrada.length() > 0)
                enviarDatos(textoEntrada);
        }
    }

    public static void main(String[] argumentos) {
        //Creación de los objetos con sus respectivos  constructores
        cliente cliente = new cliente();
        Scanner scanner1 = new Scanner(System.in);
        //Mensaje para ingresar la IP del host
        System.out.print("Ingrese la IP del Host: [localhost por defecto] ");
        String ip = scanner1.nextLine();
        if (ip.length() <= 0) ip = "localhost";
        //Mensaje para ingresar el host
        System.out.print("Puerto: [5050 por defecto] ");
        String puerto = scanner1.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        //Ejecución de los métodos para iniciar la conexión, transmisión de datos y escritura.
        cliente.activarConexion(ip, Integer.parseInt(puerto));
        cliente.escribirDatosChat();
    }
}


