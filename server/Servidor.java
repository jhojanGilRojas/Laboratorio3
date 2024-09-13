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

    private static final int PUERTO = 8082;
    private static List<Usuario>usuarios = new ArrayList<>();
    private static List<Libro>libros = new ArrayList<>();

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
                    String [] lista = operacion.split(",");

                    switch (lista[0]) {


                        case "iniciarsesion":
                            boolean usuarioAutenticacion =  autenticarUsuario(lista[1],lista[2]);
                            out.println(usuarioAutenticacion+"\n"+"");
                            break;
                        case "registrarusuario":
                            boolean usuarioRegistrado = registraUsuario(lista[1],lista[2]);
                            out.println(usuarioRegistrado+"\n"+"");
                            break;

                        case "reservarlibro":
                            boolean libroReservado = reservarLibro(lista[1],lista[2]);
                            out.println(libroReservado+"\n"+"");
                            break;

                        case "buscarlibros":
                            String coincidencias = buscarLibros(lista[1]);
                            out.println(coincidencias);
                            break;

                        case "cargarlibros":
                            String datos = listarLibros(libros);
                            out.println(datos);
                            break;

                        case "actualizarcontrasena":
                            boolean contrasenaActualizada = actualizarContrasena(lista[1],lista[2]);
                            out.println(contrasenaActualizada+"\n"+"");
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

            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }

        private String buscarLibros(String palabraClave) {
            StringBuilder resultados = new StringBuilder();

            for (Libro libro : libros) {
                if (libro.getNombre().toLowerCase().contains(palabraClave.toLowerCase()) ||
                        libro.getAutor().toLowerCase().contains(palabraClave.toLowerCase()) ||
                        libro.getEstado().toString().toLowerCase().contains(palabraClave.toLowerCase()) ||
                        libro.getTema().toLowerCase().contains(palabraClave.toLowerCase())) {
                    resultados.append(libro.toString()).append("\n");
                }
            }

            return resultados.toString();
        }


        private String listarLibros(List<Libro> libros) {

            StringBuilder listaLibros = new StringBuilder();
            for (Libro libro : libros) {
                listaLibros.append(libro.toString()).append("\n");
            }
            return listaLibros.toString();
        }


        private boolean reservarLibro(String nombre, String autor) {


            for (Libro libro:libros){

                if (libro.getNombre().equals(nombre) && libro.getAutor().equals(autor) && libro.getEstado()==Estado.DISPONIBLE){
                    libro.setEstado(Estado.RESERVADO);
                    return true;
                }
            }
            return false;
        }

        private boolean registraUsuario(String user, String password) {

            for (Usuario usuario:usuarios){
                if(usuario.getUser().equals(user)) {
                    return false;
                }
            }
            Usuario usuario = new Usuario(user,password);
            usuarios.add(usuario);
            return true;

        }

        public static boolean autenticarUsuario(String username, String password) throws IOException {

            for (Usuario usuario : usuarios) {
                if (usuario.getUser().equals(username) && usuario.verificarPassword(password)) {
                    return true;
                }
            }

            return false;
        }

        public static boolean actualizarContrasena(String user,String newPassword) throws IOException {;

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
