import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();

	static{
		nombreToken.add("(");
		nombreToken.add(")");
		nombreToken.add("* / //");
		nombreToken.add("+ -");
		nombreToken.add("< > <= >= = <>");
		nombreToken.add(";");
		nombreToken.add(":");
		nombreToken.add(",");
		nombreToken.add(":=");
		nombreToken.add("'var'");
		nombreToken.add("'real'");
		nombreToken.add("'entero'");
		nombreToken.add("'algoritmo'");
		nombreToken.add("'blq'");
		nombreToken.add("'fblq'");
		nombreToken.add("'funcion'");
		nombreToken.add("'si'");
		nombreToken.add("'entonces'");
		nombreToken.add("'sino'");
		nombreToken.add("'fsi'");
		nombreToken.add("'mientras'");
		nombreToken.add("'hacer'");
		nombreToken.add("'escribir'");
		nombreToken.add("identificador");
		nombreToken.add("numero entero");
		nombreToken.add("numero real");
		nombreToken.add("final de fichero");
	}

        // para imprimir la cadena asociada a un token 't' hay que poner:
        //  System.out.println(Token.nombreToken.get(t.tipo);

	public int tipo;		// tipo es: ID, ENTERO, REAL ...

	public static final int
		PARI 		= 0,
		PARD		= 1,
		OPMD		= 2,
		OPAS		= 3,
		OPREL		= 4,
		PYC		= 5,
		DOSP		= 6,
		COMA		= 7,
		ASIG		= 8,
		VAR		= 9,
		REAL		= 10,
		ENTERO		= 11,
		ALGORITMO	= 12,
		BLQ		= 13,
		FBLQ		= 14,
		FUNCION 	= 15,
		SI		= 16,
		ENTONCES	= 17,
		SINO		= 18,
		FSI		= 19,
		MIENTRAS	= 20,
		HACER		= 21,
		ESCRIBIR	= 22,
		ID		= 23,
		NENTERO		= 24,
		NREAL		= 25,
		EOF		= 26;

	public String toString(){
		return "("+fila+","+columna+"): "+lexema+" es de tipo "+tipo+'\n'; 
	}

}

