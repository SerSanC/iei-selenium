package iei.entrega2;

/**
 *
 * @author sergisanz
 */
public class Libro {
    String titulo;
    String autor;
    double precio;
    double descuento;
    
    Libro (String titulo, String autor,double precio,double descuento){
        this.titulo=titulo;
        this.autor = autor;
        this.precio = precio;
        this.descuento = descuento;
    }
    
    public String getTitulo(){return titulo;}
    public String getAutor(){return autor;}
    public double getPrecio(){return precio;}
    public double getDescuento(){return descuento;}
}
