import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();

	static{
		nombreToken.add("(");
		nombreToken.add(")");
		nombreToken.add("+ -");
		nombreToken.add(";");
		nombreToken.add(":");
		nombreToken.add(":=");
		nombreToken.add("'var'");
		nombreToken.add("'real'");
		nombreToken.add("'entero'");
		nombreToken.add("'algoritmo'");
		nombreToken.add("'blq'");
		nombreToken.add("'fblq'");
		nombreToken.add("'si'");
		nombreToken.add("'entonces'");
		nombreToken.add("'fsi'");
		nombreToken.add("'mientras'");
		nombreToken.add("'hacer'");
		nombreToken.add("'escribir'");
		nombreToken.add("identificador");
		nombreToken.add("numero entero");
		nombreToken.add("numero real");
		nombreToken.add("fin de fichero");
	}

        // para imprimir la cadena asociada a un token 't' hay que poner:
        //  System.out.println(Token.nombreToken.get(t.tipo);

	public int tipo;		// tipo es: ID, ENTERO, REAL ...

	public static final int
		PARI 		= 0,
		PARD		= 1,
		OPAS		= 2,
		PYC		= 3,
		DOSP		= 4,
		ASIG		= 5,
		VAR		= 6,
		REAL		= 7,
		ENTERO		= 8,
		ALGORITMO	= 9,
		BLQ		= 10,
		FBLQ		= 11,
		SI		= 12,
		ENTONCES	= 13,
		FSI		= 14,
		MIENTRAS	= 15,
		HACER		= 16,
		ESCRIBIR	= 17,
		ID		= 18,
		NENTERO		= 19,
		NREAL		= 20,
		EOF		= 21;

	public String toString(){
		return "("+fila+","+columna+"): "+lexema+" es de tipo "+tipo+'\n'; 
	}

}

