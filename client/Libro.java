package laboratorio3.client;

public class Libro {

    private String nombre;
    private String autor;
    private String tema;
    private Estado estado;

    public Libro(String titulo, String autor, String categoria, Estado estado) {
        this.nombre = titulo;
        this.autor = autor;
        this.tema = categoria;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return
                nombre+","+autor+","+tema+","+estado;
    }
}
