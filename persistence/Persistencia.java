package laboratorio3.persistence;

import laboratorio3.client.Estado;
import laboratorio3.client.Libro;
import laboratorio3.client.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Persistencia {

    private static final String RUTA_USUARIO = "src/laboratorio3/persistence/usuariosData.txt";
    private static final String RUTA_LIBRO = "src/laboratorio3/persistence/librosData.txt";

    public static List<Usuario> leerUsuarios() throws IOException {
        List<Usuario> usuarios = new ArrayList<>();
        try (FileReader fileReader = new FileReader(RUTA_USUARIO);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 2) {
                    Usuario usuario = new Usuario(datos[0], datos[1]);
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }

    public static List<Libro> leerLibros() throws IOException {

        List<Libro> libros = new ArrayList<>();
        try (FileReader fileReader = new FileReader(RUTA_LIBRO);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    Libro libro = new Libro(datos[0], datos[1], datos[2], Estado.valueOf(datos[3]));
                    libros.add(libro);
                }
            }
        }
        return libros;
    }



    public static void guardarLibros(List<Libro> libros) throws IOException {
        try (FileWriter fileWriter = new FileWriter(RUTA_LIBRO);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (Libro libro : libros) {
                String linea = libro.getNombre() + "," + libro.getAutor() + "," + libro.getTema() + "," + libro.getEstado().name();
                bufferedWriter.write(linea);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar los libros: " + e.getMessage());
            throw e;
        }
    }
    public static void guardarUsuarios(List<Usuario>usuarios) throws IOException{
        try (FileWriter fileWriter = new FileWriter(RUTA_USUARIO);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (Usuario usuario : usuarios) {
                String linea = usuario.getUser() + "," + usuario.getPassword() ;
                bufferedWriter.write(linea);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar los usuarios: " + e.getMessage());
            throw e;
        }
    }
}
