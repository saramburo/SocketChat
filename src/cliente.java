import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class cliente {
    private Socket socket=null;
    private DataInputStream datosEntrada =null;
    private DataOutputStream datosSalida=null;
     Scanner Escaner2= new Scanner(System.in);
    final String FINALIZACION= "finalizar()";
    public void establecerConexion(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            System.out.print("Se está conectando a: " + socket.getInetAddress().getHostName());

        } catch (IOException e) {
            System.out.print("Excepcion al crear la conexión: " + e.getMessage());
            System.exit(0);

        }
    }

    public void establecerFlujos(){
        try{
            datosEntrada=new DataInputStream(socket.getInputStream());
            datosSalida= new DataOutputStream((socket.getOutputStream()));
            datosSalida.flush();

        }catch(IOException e) {
            System.out.print("No es posible intercambiar datos entre equipos: "+ e.getMessage());
        }
    }

    public void enviarDatos(String texto){
        try {
            datosSalida.writeUTF(texto);
            datosSalida.flush();
        }catch(IOException e) {
            System.out.print("Error IO al enviar");
        }
    }
    public void recibirDatos() {
        String st = "";
        try {
            do {
                st = (String) datosEntrada.readUTF();
               System.out.print("\n[Servidor] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(FINALIZACION));
        } catch (IOException e) {}
    }

    public void terminarConexion(){
        try{
            datosSalida.close();
            datosEntrada.close();
            socket.close();
            System.out.print("Conexión Finalizada Exitósamente");

        }catch(IOException e){
            System.out.print("Error al finalizar conexión: "+e.getMessage());
        }finally {
            System.exit(0);
        }
    }

    public void activarConexion(String ip, int puerto){
        Thread hilo=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    establecerConexion(ip,puerto);
                    establecerFlujos();
                    recibirDatos();

                }finally {
                    terminarConexion();
                }
            }
        });
        hilo.start();

    }

    public void escribirDatos() {
        String textoEntrada="";
        while (true) {
            System.out.print("[Usted] => ");
            textoEntrada = Escaner2.nextLine();
            if(textoEntrada.length() > 0)
                enviarDatos(textoEntrada);
        }
    }

    public static void main(String[] argumentos) {
        cliente cliente = new cliente();
        Scanner scanner1 = new Scanner(System.in);
        System.out.print("Ingrese la IP del Host: [localhost por defecto] ");
        String ip = scanner1.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        System.out.print("Puerto: [5050 por defecto] ");
        String puerto = scanner1.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        cliente.activarConexion(ip, Integer.parseInt(puerto));
        cliente.escribirDatos();
    }
}


