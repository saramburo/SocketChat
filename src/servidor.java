/*
El siguiente codigo funciona con la siguiente logica:
1. Se usan la libreria .net de java que contiene los objectos y constructores de la clase Socket y SocketServer
2. Se definen los metodos para:
    a. Establecer una conexion con un equipo remoto dentro de la misma red al conocer el puerto de conexion
    b. Se crean las funciones de flujo de datos hacia el servidor (inputStream) y hacia el cliente (outputStream)
    c. Se crea funcion para aceptar los datos recibidos por el servidor en formato UTF. Si esta funcion, se establece la conexion pero no se aceptan los datos de entrada
    d. Se crea funcion para enviar los datos en formato UTF al cliente
    e. Se crea una funcion para escribir los datos, transformados en String en la consola, tipo chat.
    f. Se crea funcion para terminar la conexion y cerrar el programa
    g. Se crea la funcion del hilo que permite usar el metodo run, ejecutando la conexion, la recepcion/envio de datos y la escritura de los mismos en el chat
 3. Se establece el public static void que ejecuta las acciones
*/

//Librerias utilizadas
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class servidor {
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream datosEntrada=null;
    private DataOutputStream datosSalida=null;
    Scanner EscanerServidor = new Scanner(System.in);

    final String FINALIZACION = "finalizar()";

    //funcion que establece la conexion con el puerto del cliente
    public void establecerConexion(int puerto) {
        try {
            //utilizamos el constructor de la libreria java.net en la que se crea un ServerSocket atada a un puerto en especifico y lo asignamos a la variable serverSocket
            serverSocket = new ServerSocket(puerto);
            System.out.print("Estableciendo conexión con el puerto "+ String.valueOf(puerto));
            //el metodo accept escucha una conexion a ser hecha con este socket y la acepta
            socket=serverSocket.accept();
            //el metodo getInetAddress devuelve la direccion local del socket servidor acompanado del nombre del host
            System.out.print("Conexión generada satisfactoriamente con el puerto: "+ socket.getInetAddress().getHostName()+"\n\n\n");
        } catch (Exception e) {
            System.out.print("Error en establecer Conexion "+e.getMessage());
            System.exit(0);
        }
    }
    //este funcion crea los flujos de datos de entrada y salida del servidor
    public void flujoInformacion(){
        try{
            //Aqui usamos dos constructores DataInput y DataOutput que crean un flujo de datos a ser recibidos y compartidos por el socket. Estos flujos de datos son guardados en dos variables.
            datosEntrada=new DataInputStream(socket.getInputStream());
            datosSalida=new DataOutputStream(socket.getOutputStream());
            //el metodo flush obliga a escribir los datos de salida en el flujo de salida. no se mantienen datos en ningun buffer y todos deben ser escritos y enviados
            datosSalida.flush();
        }catch(IOException e){
            System.out.print("Error en la gestion de flujos de entrada y salida");

        }

    }
    public void aceptarDatos(){
         String texto="";
         try{
             do{
                 //transforma los datos de entrada a formato UTF
                 texto= (String) datosEntrada.readUTF();
                 //escribe en consola los datos de entrada
                 System.out.print("\n[Cliente]: "+ texto);
                 System.out.print("\n[Servidor]: ");
                 //se mantiene el chat hasta que el texto sea igual al comando de finalizacion definido arriba
             }while(!texto.equals(FINALIZACION));
         }catch(IOException e){
             terminarConexion();
         }
}
    public void enviarDatos(String textoEnviar){
        try{
            //el metodo writeUTF escribe un String en el outputStream y lo codifica en UTF-8
            datosSalida.writeUTF(textoEnviar);
            datosSalida.flush();

        }catch(IOException e){
            System.out.print("Error al enviar datos "+ e.getMessage());
        }

    }

    public void escribirDatosChat(){
        while(true){
            System.out.print("[Servidor]: ");
            enviarDatos(EscanerServidor.nextLine());
        }
    }
    public void terminarConexion(){
        try {
            datosEntrada.close();
            datosSalida.close();
            socket.close();
        }catch(IOException e){
            System.out.print("Excepcion en funcion ...error: "+e.getMessage());
        }finally {
            System.out.print("Cerrando chat..");
            System.exit(0);
        }
    }

    public void activarConexion(int puerto){
        Thread hilo =new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                       establecerConexion(puerto);
                       flujoInformacion();
                       aceptarDatos();
                    }finally {
                        terminarConexion();
                    }
                }
            }
        });
        hilo.start();
    }

    public static void main(String[] args) throws IOException {
        servidor servidorPrincipal=new servidor();
        Scanner scanner1= new Scanner(System.in);

        System.out.print("Ingrese el puerto de conexion [el puerto 5050 se selecciona por defecto al estar vacio]:");
        String puerto= scanner1.nextLine();

        if(puerto.length()<=0) puerto ="5050";
        servidorPrincipal.activarConexion(Integer.parseInt(puerto));
        servidorPrincipal.escribirDatosChat();
    }
}