public class Simbolo {
    public String nombre; 
    public String nombreCompleto; 
    public int tipo;
  
    // Tipos de datos: 
    public static final int 
	ENTERO  = 1,
	REAL = 2,
	FUNCION = 3;

    public Simbolo(String nombre, String nombreCompleto, int tipo) { 
        this.nombre = nombre; 
        this.nombreCompleto = nombreCompleto; 
        this.tipo = tipo; 
    }

    public String toString() {
        return nombre+"\t"+nombreCompleto+"\t"+tipo+"\n";
    }
} 

