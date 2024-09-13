package laboratorio3.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Scanner teclado;

    public Cliente(String host, int puerto) {
        try {
            // Conectarse al servidor
            socket = new Socket(host, puerto);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            teclado = new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar texto al servidor
    public void enviarTexto(String texto) {
        if (salida != null) {
            salida.println(texto);
            salida.flush(); // Asegúrate de que el texto se envíe inmediatamente
        }
    }

    // Método para recibir respuestas del servidor y capturarlas en un String
    public String recibirRespuesta() {
        if (entrada == null) return null;

        StringBuilder respuesta = new StringBuilder();
        String linea;
        try {
            while ((linea = entrada.readLine()) != null && !linea.trim().isEmpty()) {
                respuesta.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return respuesta.toString().trim(); // Elimina posibles espacios en blanco finales
    }

    // Método para interactuar con el servidor desde la consola
    public void iniciar() {
        System.out.println("Escribe tus mensajes. Escribe 'salir' para terminar.");
        String mensaje;

        while (true) {
            System.out.print("Tú: ");
            mensaje = teclado.nextLine();

            if (mensaje.equalsIgnoreCase("salir")) {
                break;
            }

            enviarTexto(mensaje);

            // Recibir respuesta del servidor
            String respuesta = (String) recibirRespuesta();
            if (respuesta != null ) {
                System.out.println("Servidor:\n" + respuesta);
            } else {
                System.out.println("No se recibió respuesta del servidor.");
            }
        }

        cerrarConexion();
    }

    // Método para cerrar la conexión
    public void cerrarConexion() {
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
            if (teclado != null) teclado.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente("localhost", 8082);
        cliente.iniciar();
    }
}
