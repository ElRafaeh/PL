import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TooManyListenersException;

public class AnalizadorLexico {
    private RandomAccessFile fichero;
    private ArrayList<Integer> estadosFinales = new ArrayList<>(Arrays.asList(27,15,17,18,21,23,26,27,28,31));
    private int posicionFichero = 0, filaFichero = 1, columnaFichero = 1, finalDeEstado = -1, errorEstado = -2;
    private static char EOF = (char)-1;

    /**
     * Constructor de la clase
     * @param fichero fichero a leer para analizar
     */
    public AnalizadorLexico(RandomAccessFile fichero)
    {
        this.fichero = fichero;
    }

    /**
     * Se vuelve una posición atrás en el fichero de donde ya se ha leido
     * @param token token leido
     */
    public void volverCaracterAtras(Token token)
    {
        try
        {
            posicionFichero--;
            columnaFichero--;
            fichero.seek(posicionFichero);
            token.lexema = token.lexema.substring(0, token.lexema.length()-1);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Funcion para verificar si un id es una palabra reservada en nuestro compilador
     * @param token token a ver si es una palabra reservada
     * @return devuelve el identificador de la palabra reservada
     */
    public int esPalabraReservada(Token token)
    {
        switch (token.lexema)
        {
            case "var":
                return Token.VAR;
            case "real":
                return Token.REAL;
            case "entero":
                return Token.ENTERO;
            case "algoritmo":
                return Token.ALGORITMO;
            case "blq":
                return Token.BLQ;
            case "fblq":
                return Token.FBLQ;
            case "si":
                return Token.SI;
            case "entonces":
                return Token.ENTONCES;
            case "fsi":
                return Token.FSI;
            case "mientras":
                return Token.MIENTRAS;
            case "hacer":
                return Token.HACER;
            case "escribir":
                return Token.ESCRIBIR;
            default:
                return Token.ID;
        }
    }

    /**
     * Funcion para leer el siguiente token del fichero y realizar su analisis lexico
     * @return el token leido analizado lexicamente
     */
    public Token siguienteToken()
    {
        Token token = new Token();
        int estado = 0;
        char c = readChar();
        token.lexema = "";
        estado = delta(estado, c);

        do
        {
            if (estado == -1)
            {
                switch (c)
                {
                    case '\n':
                        filaFichero++;
                        columnaFichero = 1;
                    case ' ':
                    case '\t':
                        c = readChar();
                        estado = delta(0, c);
                        break;
                    case (char)-1:
                        token.tipo = Token.EOF;
                        token.columna = columnaFichero;
                        token.fila = filaFichero;
                        return token;
                    default:
                        token.fila = filaFichero;
                        token.columna = columnaFichero - token.lexema.length()-1;
                        System.err.println("Error lexico ("+token.fila+","+token.columna+"): caracter '" + c +"' incorrecto");
                        System.exit(-1);
                }
            }
            else if(estado == -2)
            {
                System.err.println("Error lexico: " + Token.nombreToken.get(Token.EOF) +  " inesperado");
                System.exit(-1);
            }
            else
            {
                if(EsFinal(estado))
                {
                    token.lexema += c;
                    token.fila = filaFichero;

                    switch (estado)
                    {
                        case 2:
                            token.tipo = Token.PARD;
                            break;
                        case 7:
                            token.tipo = Token.OPAS;
                            break;
                        case 15:
                            token.tipo = Token.PYC;
                            break;
                        case 17:
                            token.tipo = Token.ASIG;
                            break;
                        case 18:
                            token.tipo = Token.DOSP;
                            volverCaracterAtras(token);
                            break;
                        case 21:
                            volverCaracterAtras(token);
                            token.tipo = esPalabraReservada(token);
                            break;
                        case 23:
                            token.tipo = Token.NENTERO;
                            volverCaracterAtras(token);
                            break;
                        case 27:
                            token.tipo = Token.NREAL;
                            volverCaracterAtras(token);
                            break;
                        case 28:
                            token.tipo = Token.PARI;
                            volverCaracterAtras(token);
                            break;
                        case 26:
                            token.tipo = Token.NENTERO;
                            volverCaracterAtras(token);
                            volverCaracterAtras(token);
                            break;
                        case 31:
                            token.lexema = "";
                            estado = 0;
                            continue;
                    }
                    token.columna = columnaFichero - token.lexema.length();
                    return token;
                }
                else
                {
                    if(estado == 0) token.lexema = "";
                    else            token.lexema += c;

                    c = readChar();
                    estado = delta(estado, c);
                }
            }
        } while (true);
    }

    /**
     * Función para ver si un estado es estado final
     * @param estado estado a analizar
     * @return true si es estado final y false si no
     */
    public boolean EsFinal(int estado)
    {
        if(estadosFinales.contains(estado))
        {
            return true;
        }
        return  false;
    }

    /**
     * Funcion delta que dado un estado y un caracter leido, devuelve a que estado saltamos
     * @param estado estado en el que nos encontramos
     * @param c caracter leido
     * @return estado al que hemos saltado
     */
    public int delta(int estado, int c)
    {
        int dev;

        switch (estado) {
            case 0:
                if(c=='(')                      return 1;
                else if(c == ')')               return 2;
                else if(c == '+' || c == '-')   return 7;
                else if(c == '=')               return 14;
                else if(c == ';')               return 15;
                else if(c == ':')               return 16;
                else if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) return  20;
                else if(c >= '0' && c <= '9')   return 22;
                else                            return -1;
            case 1:
                if(c == '*')    return 29;
                else            return 28;
            case 2:
                return -1;
            case 7:
                return -1;
            case 15:
                return -1;
            case 16:
                if (c == '=')   return 17;
                else            return 18;
            case 17:
                return -1;
            case 18:
                return -1;
            case 20:
                if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))  return 20;
                else                                                                            return 21;
            case 21:
                return -1;
            case 22:
                if(c >= '0' && c <= '9')    return 22;
                else if(c == '.')           return 24;
                else                        return 23;
            case 23:
                return -1;
            case 24:
                if(c >= '0' && c <= '9')    return 25;
                else                        return 26;
            case 25:
                if(c >= '0' && c <= '9')    return 25;
                else                        return 27;
            case 26:
                return -1;
            case 27:
                return -1;
            case 28:
                return -1;
            case 29:
                if (c == '*')       return 30;
                else if(c == EOF)   return -2;
                else
                {
                    if (c == '\n') { filaFichero++; columnaFichero = 1; }
                    return 29;
                }
            case 30:
                if (c == ')')       return 31;
                else if (c == EOF)  return -2;
                else if (c == '*')  return 30;
                else
                {
                    if (c == '\n') { filaFichero++; columnaFichero = 1; }
                    return 29;
                }
            case 31:
                return -1;
            default: //Error lexico
                return -1;
        }
    }

    /**
     * Funcion para leer caracteres del fichero
     * @return el caracter leido
     */
    public char readChar()
    {
        char c;
        try {
            c = (char)fichero.readByte();
            posicionFichero++;
            columnaFichero++;
            if (c == '\r')
            {
                c = (char)fichero.readByte();
                posicionFichero++;
            }
            return c;
        } catch (EOFException e) {
            return EOF;
        } catch (IOException e) { }

        return  ' ';
    }
}
