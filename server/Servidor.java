package laboratorio3.server;

import laboratorio3.client.Estado;
import laboratorio3.client.Libro;
import laboratorio3.client.Usuario;
import laboratorio3.persistence.Persistencia;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    private static final int PUERTO = 3400;
    private static List<Usuario>usuarios = new ArrayList<>();
    private static List<Libro>libros = new ArrayList<>();
    private static String username ;
    public void iniciarServidor() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);



            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new HiloCliente(clientSocket).start();
                } catch (IOException e) {
                    System.err.println("Error al aceptar la conexión: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static class HiloCliente extends Thread {
        private Socket socket;

        public HiloCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream())) {
                while (true) {

                    String operacion = (String) in.readLine();
                    System.out.println("Operación recibida: " + operacion);
                    String[] lista = operacion.split(",");

                    switch (lista[0]) {

                        case "iniciarsesion":

                            boolean usuarioAutenticacion =  autenticarUsuario(lista[1],lista[2]);
                            username = lista[1];
                            out.println(usuarioAutenticacion+"\n");
                            break;

                        case "reservarlibro":
                            boolean libroReservado = reservarLibro(lista[1]);
                            out.println(libroReservado);
                            break;

                        case "buscarlibros":
                            String coincidencias = buscarLibros(lista[1]);
                            System.out.println(coincidencias);
                            out.println(coincidencias);
                            break;

                        case "cargarlibros":

                            String datos = listarLibros(libros);
                            System.out.println(datos);
                            out.println(datos);
                            break;

                        case "cambiarpassword":

                            boolean contrasenaActualizada = actualizarContrasena(username,lista[1]);
                            System.out.println(contrasenaActualizada);
                            out.println(contrasenaActualizada);
                            break;

                        default:
                            out.println("Operación no reconocida");
                            break;
                    }
                    out.flush();
                }

            } catch (IOException e) {
                System.err.println("cliente desconectado " );
                try {
                    Persistencia.guardarLibros(libros);
                    Persistencia.guardarUsuarios(usuarios);
                } catch (IOException ex) {
                    System.out.println("no se han guardado las listas");
                }


            }
            catch (NullPointerException exception){
                try {
                    Persistencia.guardarLibros(libros);
                    Persistencia.guardarUsuarios(usuarios);
                } catch (IOException e) {

                    throw new RuntimeException(e);
                }
                System.out.println("Petición null");
            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }

        private String buscarLibros(String palabraClave) {
            String resultados = "";

            for (Libro libro : libros) {
                if (libro.getNombre().toLowerCase().contains(palabraClave.toLowerCase()) ||
                        libro.getAutor().toLowerCase().contains(palabraClave.toLowerCase()) ||
                        libro.getEstado().toString().toLowerCase().contains(palabraClave.toLowerCase()) ||
                        libro.getTema().toLowerCase().contains(palabraClave.toLowerCase())) {
                    resultados += libro.toString();
                }
            }

            return resultados;
        }


        private String listarLibros(List<Libro> libros) {

            String listaLibros = "";
            for (Libro libro : libros){
                listaLibros+= libro.toString();
            }
            return listaLibros;
        }


        private boolean reservarLibro(String nombre) {


            for (Libro libro:libros){

                if (libro.getNombre().equals(nombre) && libro.getEstado()==Estado.DISPONIBLE){
                    libro.setEstado(Estado.RESERVADO);
                    return true;
                }
            }
            return false;
        }


        public static boolean autenticarUsuario(String username, String password) throws IOException {

            for (Usuario usuario : usuarios) {
                if (usuario.getUser().equals(username) && usuario.verificarPassword(password)) {
                    return true;
                }
            }

            return false;
        }

        public static boolean actualizarContrasena(String user,String newPassword) throws IOException {

            for (Usuario usuario : usuarios) {
                if (usuario.getUser().equals(user)) {
                    usuario.setPassword(newPassword);
                    return true;
                }
            }
            return false;
        }

    }

    public static void main(String[] args) throws IOException {
        usuarios = Persistencia.leerUsuarios();
        libros = Persistencia.leerLibros();

        Servidor servidor = new Servidor();
        servidor.iniciarServidor();
    }

}
