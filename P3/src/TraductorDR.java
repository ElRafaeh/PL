import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TraductorDR
{
        private boolean flagNumerosReglas;
        private AnalizadorLexico al;
        private StringBuilder numerosRegla = new StringBuilder();
        private Token token;
        public TablaSimbolos tsActual;

        /**
         * Constructor de la clase
         * @param al analizador lexico para sacar tokens
         */
        TraductorDR(AnalizadorLexico al)
        {
                this.al = al;
                this.flagNumerosReglas = true;
                this.token = al.siguienteToken();
                this.tsActual = new TablaSimbolos(null);
        }

        /**
         * Debajo de este comentario se van a poner las reglas de la gramatica
         */
        public String S()
        {
                String devolver = "";
                if(token.tipo == Token.ALGORITMO)
                {
                        añadirRegla(1);
                        emparejar(Token.ALGORITMO);
                        devolver += "//algoritmo ";
                        devolver += token.lexema + "\n";
                        emparejar(Token.ID);
                        emparejar(Token.PYC);
                        devolver += Vsp("main");
                        devolver += "int main() " + Bloque();
                }
                else errorSintaxis(Token.ALGORITMO);

                return devolver;
        }

        public String Vsp(String th)
        {
                String devolver = "";
                if(token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        añadirRegla(2);
                        devolver += Unsp(th);
                        devolver += Vspp(th);
                }
                else errorSintaxis(Token.VAR, Token.FUNCION);

                return devolver;
        }

        public String Vspp(String th)
        {
                String devolver = "";
                if(token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        añadirRegla(3);
                        devolver += Unsp(th);
                        devolver += Vspp(th);
                }
                else if(token.tipo == Token.BLQ)
                {
                        añadirRegla(4);
                        // Regla epsilon
                }
                else errorSintaxis(Token.VAR, Token.FUNCION, Token.BLQ);

                return devolver;
        }

        public String Unsp(String th)
        {
                String devolver = "";
                if(token.tipo == Token.FUNCION)
                {
                        añadirRegla(5);
                        emparejar(Token.FUNCION);
                        String nombre_ambito = ((th == "main") ? "" : th+"_") + token.lexema;

                        if (tsActual.buscarAmbito(token.lexema)!=null)
                                errorSemantico(ERR_YA_EXISTE, token.fila, token.columna, token.lexema);

                        tsActual.nuevoSimbolo(new Simbolo(token.lexema, token.lexema, Simbolo.FUNCION));
                        emparejar(Token.ID);
                        emparejar(Token.DOSP);
                        String Tipo_trad = Tipo();
                        emparejar(Token.PYC);
                        tsActual = new TablaSimbolos(tsActual);
                        devolver += Vsp(nombre_ambito) + Tipo_trad + " " + nombre_ambito + "()";
                        devolver += Bloque();
                        devolver += "\n";
                        emparejar(Token.PYC);
                        tsActual = tsActual.getAmbitoAnterior();
                }
                else if (token.tipo == Token.VAR)
                {
                        añadirRegla(6);
                        emparejar(Token.VAR);
                        devolver += LV(th);
                }
                else errorSintaxis(Token.FUNCION, Token.VAR);

                return devolver;
        }

        public String LV(String th)
        {
                String devolver = "";
                if(token.tipo == Token.ID)
                {
                        añadirRegla(7);
                        devolver += V(th);
                        devolver += LVp(th);
                }
                else errorSintaxis(Token.ID);

                return devolver;
        }

        public String LVp(String th)
        {
                String devolver = "";
                if(token.tipo == Token.ID)
                {
                        añadirRegla(8);
                        devolver += V(th);
                        devolver += LVp(th);
                }
                else if (token.tipo == Token.VAR || token.tipo == Token.FUNCION || token.tipo == Token.BLQ)
                {
                        añadirRegla(9);
                        // Regla epsilon
                }
                else errorSintaxis(Token.ID, Token.VAR, Token.FUNCION, Token.BLQ);

                return devolver;
        }

        public String V(String th)
        {
                String devolver = "";
                if(token.tipo == Token.ID)
                {
                        añadirRegla(10);
                        String id_lexema = token.lexema;
                        if (tsActual.buscarAmbito(id_lexema)!=null)
                                errorSemantico(ERR_YA_EXISTE, token.fila, token.columna, id_lexema);
                        emparejar(Token.ID);

                        String[] Lid_trad = Lid(th);
                        emparejar(Token.DOSP);
                        String tipo_trad = Tipo();
                        tsActual.nuevoSimbolo(new Simbolo(id_lexema, th+"_"+id_lexema, ((tipo_trad.equals("int")) ? Simbolo.ENTERO : Simbolo.REAL)));

                        String ids_lid_trad[] = Lid_trad[1].split(" ");
                        String pos_lid_trad[] = Lid_trad[2].split(" ");
                        for (int i = 0; i < ids_lid_trad.length; i++) {
                                if (ids_lid_trad[i].isEmpty())
                                        break;
                                if (tsActual.buscarAmbito(ids_lid_trad[i]) != null) {
                                        String posiciones[] = pos_lid_trad[i].split(",");
                                        errorSemantico(ERR_YA_EXISTE, Integer.parseInt(posiciones[0]), Integer.parseInt(posiciones[1]), id_lexema);
                                }
                                else
                                        tsActual.nuevoSimbolo(new Simbolo(ids_lid_trad[i], th + "_" + ids_lid_trad[i], ((tipo_trad.equals("int")) ? Simbolo.ENTERO : Simbolo.REAL)));
                        }

                        devolver += tipo_trad + " ";
                        emparejar(Token.PYC);
                        devolver += th + "_" + id_lexema + Lid_trad[0] + ";\n";
                }
                else errorSintaxis(Token.ID);

                return devolver;
        }

        public String[] Lid(String th)
        {
                String devolver[] = {"","",""};
                if(token.tipo == Token.COMA)
                {
                        añadirRegla(11);
                        emparejar(Token.COMA);
                        Token id_lexema = token;
                        emparejar(Token.ID);
                        String[] Lid1_trad = Lid(th);
                        devolver[0] += "," + th + "_" + id_lexema.lexema + Lid1_trad[0];
                        devolver[1] += id_lexema.lexema + " " + Lid1_trad[1];
                        devolver[2] += id_lexema.fila + "," + id_lexema.columna + " " + Lid1_trad[2];
                }
                else if(token.tipo == Token.DOSP)
                {
                        añadirRegla(12);
                        // Regla epsilon
                }
                else errorSintaxis(Token.COMA, Token.DOSP);

                return devolver;
        }

        public String Tipo()
        {
                String devolver = "";
                if(token.tipo == Token.ENTERO)
                {
                        añadirRegla(13);
                        emparejar(Token.ENTERO);
                        devolver += "int";
                }
                else if(token.tipo == Token.REAL)
                {
                        añadirRegla(14);
                        emparejar(Token.REAL);
                        devolver += "double";
                }
                else errorSintaxis(Token.ENTERO, Token.REAL);
                return devolver;
        }

        public String Bloque()
        {
                String devolver = "";
                if(token.tipo == Token.BLQ)
                {
                        añadirRegla(15);
                        emparejar(Token.BLQ);
                        String SInstr_trad = SInstr();
                        emparejar(Token.FBLQ);
                        devolver += "{\n" + SInstr_trad + "}\n";
                }
                else errorSintaxis(Token.BLQ);

                return devolver;
        }

        public String SInstr()
        {
                String devolver = "";
                if (token.tipo == Token.BLQ || token.tipo == Token.ID || token.tipo == Token.SI || token.tipo == Token.MIENTRAS || token.tipo == Token.ESCRIBIR)
                {
                        añadirRegla(16);
                        devolver += Instr();
                        devolver += SInstrp();
                }
                else errorSintaxis(Token.BLQ, Token.ID, Token.SI, Token.MIENTRAS, Token.ESCRIBIR);

                return devolver;
        }

        public String SInstrp()
        {
                String devolver = "";
                if (token.tipo == Token.PYC)
                {
                        añadirRegla(17);
                        emparejar(Token.PYC);
                        devolver += Instr();
                        devolver += SInstrp();
                }
                else if (token.tipo == Token.FBLQ)
                {
                        añadirRegla(18);
                        // Regla epsilon
                }
                else errorSintaxis(Token.PYC, Token.FBLQ);

                return devolver;
        }

        public String Instr()
        {
                String devolver = "";
                if(token.tipo == Token.BLQ)
                {
                        añadirRegla(19);
                        devolver += Bloque();
                }
                else if (token.tipo == Token.ID)
                {
                        añadirRegla(20);
                        Token id_tok = token;
                        Simbolo id = tsActual.buscar(token.lexema);

                        if(id == null)
                                errorSemantico(ERR_NO_DECL, token.fila, token.columna, token.lexema);
                        else if(id.tipo == Simbolo.FUNCION)
                                errorSemantico(ERR_NO_VARIABLE, token.fila, token.columna, token.lexema);

                        emparejar(Token.ID);
                        Token asig_token = token;
                        emparejar(Token.ASIG);

                        Pair E_trad = E();
                        if (id.tipo == Simbolo.ENTERO && E_trad.tipo == Simbolo.REAL)
                                errorSemantico(ERR_ASIG_REAL, id_tok.fila, id_tok.columna, id.nombre);
                        else if (E_trad.tipo == 0)
                                errorSemantico(ERR_NO_BOOL, asig_token.fila, asig_token.columna, asig_token.lexema);

                        Pair opera_trad = opera("=", new Pair(id.nombreCompleto, id.tipo), E_trad);

                        devolver += opera_trad.trad + " ;\n";
                }
                else if (token.tipo == Token.SI)
                {
                        añadirRegla(21);
                        Token err = token;
                        String si_lexema = token.lexema;
                        emparejar(Token.SI);
                        Pair E_trad = E();

                        if(E_trad.tipo != 0)
                                errorSemantico(ERR_SIMIENTRAS, err.fila, err.columna, si_lexema);

                        emparejar(Token.ENTONCES);
                        devolver += "if ( " + E_trad.trad + " )\n";
                        devolver += Instr();
                        devolver += Instrp();
                }
                else if (token.tipo == Token.MIENTRAS)
                {
                        añadirRegla(24);
                        Token err = token;
                        String mientras_lexema= token.lexema;
                        emparejar(Token.MIENTRAS);
                        Pair E_trad = E();

                        if(E_trad.tipo != 0)
                                errorSemantico(ERR_SIMIENTRAS, err.fila, err.columna, mientras_lexema);

                        devolver += "while ( " + E_trad.trad + " )\n";
                        emparejar(Token.HACER);
                        devolver += Instr();
                }
                else if (token.tipo == Token.ESCRIBIR)
                {
                        añadirRegla(25);
                        Token escribir_token = token;
                        emparejar(Token.ESCRIBIR);
                        emparejar(Token.PARI);
                        Pair E_trad = E();

                        if(E_trad.tipo == 0)
                                errorSemantico(ERR_NO_BOOL, escribir_token.fila, escribir_token.columna, escribir_token.lexema);

                        devolver += "printf(\"";
                        if (E_trad.tipo == Simbolo.ENTERO)
                                devolver += "%d\\n\",";
                        else if (E_trad.tipo == Simbolo.REAL)
                                devolver += "%g\\n\",";

                        devolver += E_trad.trad + ");\n";

                        emparejar(Token.PARD);
                }
                else errorSintaxis(Token.BLQ, Token.ID, Token.SI, Token.MIENTRAS, Token.ESCRIBIR);

                return devolver;
        }

        public String Instrp()
        {
                String devolver = "";
                if (token.tipo == Token.FSI)
                {
                        añadirRegla(22);
                        emparejar(Token.FSI);
                }
                else if (token.tipo == Token.SINO)
                {
                        añadirRegla(23);
                        emparejar(Token.SINO);
                        devolver += "else\n";
                        devolver += Instr();
                        emparejar(Token.FSI);
                }
                else errorSintaxis(Token.FSI, Token.SINO);

                return devolver;
        }

        public Pair E()
        {
                Pair devolver = new Pair(null, null);
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {

                        añadirRegla(26);
                        Pair Expr_trad = Expr();
                        devolver = Ep(Expr_trad);
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);

                return devolver;
        }

        public Pair Ep(Pair th)
        {
                Pair devolver = th;
                if (token.tipo == Token.OPREL)
                {
                        añadirRegla(27);
                        String oprel_lexema = token.lexema;

                        if(oprel_lexema.equals("=")) oprel_lexema = "==";
                        if(oprel_lexema.equals("<>")) oprel_lexema = "!=";

                        emparejar(Token.OPREL);
                        Pair Expr_trad = Expr();
                        devolver = opera(oprel_lexema, th, Expr_trad);
                        devolver.tipo = 0;
                }
                else if (token.tipo == Token.HACER || token.tipo == Token.PARD || token.tipo == Token.ENTONCES || token.tipo == Token.FSI || token.tipo == Token.PYC || token.tipo == Token.FBLQ || token.tipo == Token.SINO)
                {
                        añadirRegla(28);
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPREL, Token.HACER, Token.PARD, Token.ENTONCES, Token.FSI, Token.PYC, Token.FBLQ ,Token.SINO);

                return devolver;
        }

        public Pair Expr()
        {
                Pair devolver = null;
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        añadirRegla(29);
                        Pair Term_trad = Term();
                        devolver = Exprp(Term_trad);
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);

                return devolver;
        }


        public Pair Exprp(Pair th)
        {
                Pair devolver = th;
                if (token.tipo == Token.OPAS)
                {
                        añadirRegla(30);
                        String opas_lexema = token.lexema;
                        emparejar(Token.OPAS);
                        Pair Term_trad = Term();
                        Pair Opera_th = opera(opas_lexema, th, Term_trad);
                        Pair Exprp_trad = Exprp(Opera_th);
                        devolver = Exprp_trad;
                }
                else if (token.tipo == Token.PARD || token.tipo == Token.OPREL || token.tipo == Token.ENTONCES || token.tipo == Token.HACER || token.tipo == Token.FSI || token.tipo == Token.SINO || token.tipo == Token.PYC || token.tipo == Token.FBLQ)
                {
                        añadirRegla(31);
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPAS, Token.PARD, Token.OPREL, Token.ENTONCES, Token.HACER, Token.FSI, Token.SINO, Token.PYC, Token.FBLQ);

                return devolver;
        }

        public Pair Term()
        {
                Pair devolver = null;
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        añadirRegla(32);
                        Pair Factor_trad = Factor();
                        Pair Termp_trad = Termp(Factor_trad);
                        devolver = Termp_trad;
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);

                return devolver;
        }

        public Pair Termp(Pair th)
        {
                Pair devolver = th;
                if (token.tipo == Token.OPMD)
                {
                        añadirRegla(33);
                        Token opmd_token = token;
                        emparejar(Token.OPMD);
                        Pair Factor_trad = Factor();

                        if(opmd_token.lexema.equals("//") && (th.tipo != Simbolo.ENTERO || Factor_trad.tipo != Simbolo.ENTERO)) {
                                errorSemantico(ERR_DIVENTERA, opmd_token.fila, opmd_token.columna, opmd_token.lexema);
                        }

                        Pair Opera_th = opera(opmd_token.lexema, th, Factor_trad);
                        Pair Termp_trad = Termp(Opera_th);
                        devolver = Termp_trad;
                }
                else if (token.tipo == Token.OPAS || token.tipo == Token.PARD || token.tipo == Token.OPREL || token.tipo == Token.ENTONCES || token.tipo == Token.HACER || token.tipo == Token.FSI || token.tipo == Token.SINO || token.tipo == Token.PYC || token.tipo == Token.FBLQ)
                {
                        añadirRegla(34);
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPMD, Token.OPAS, Token.PARD, Token.OPREL, Token.ENTONCES, Token.HACER, Token.FSI, Token.SINO, Token.PYC, Token.FBLQ);

                return devolver;
        }

        /**l
         * Devolveremos un array de dos posiciones en la que la posición 0 contendra la cadena de traduccion y la 1 el tipo de dato
         */
        public Pair Factor()
        {
                Pair devolver = new Pair("",-1);
                if(token.tipo == Token.ID)
                {
                        añadirRegla(35);
                        Simbolo id = tsActual.buscar(token.lexema);
                        if(id == null)
                                errorSemantico(ERR_NO_DECL, token.fila, token.columna, token.lexema);
                        devolver.trad = id.nombreCompleto;
                        devolver.tipo = id.tipo;
                        emparejar(Token.ID);
                }
                else if (token.tipo == Token.NENTERO)
                {
                        añadirRegla(36);
                        devolver.trad = token.lexema;
                        emparejar(Token.NENTERO);
                        devolver.tipo = Simbolo.ENTERO;
                }
                else if (token.tipo == Token.NREAL)
                {
                        añadirRegla(37);
                        devolver.trad = token.lexema;
                        emparejar(Token.NREAL);
                        devolver.tipo = Simbolo.REAL;
                }
                else if (token.tipo == Token.PARI)
                {
                        añadirRegla(38);
                        devolver.trad = token.lexema;
                        emparejar(Token.PARI);
                        Pair Expr_Trad = Expr();
                        devolver.trad += Expr_Trad.trad;
                        devolver.tipo = Expr_Trad.tipo;
                        devolver.trad += token.lexema;
                        emparejar(Token.PARD);
                }
                else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);

                return devolver;
        }

        public Pair opera(String op, Pair Izq, Pair Der)
        {
                Pair devolver = new Pair("",-1);
                if (!op.equals("/") && !op.equals("//")) {
                        if (Izq.tipo == Simbolo.ENTERO && Der.tipo == Simbolo.ENTERO)
                        {
                                devolver.tipo = Simbolo.ENTERO;
                                devolver.trad = Izq.trad + " " + op + "i " + Der.trad;
                        }
                        else if (Izq.tipo == Simbolo.REAL && Der.tipo == Simbolo.ENTERO)
                        {
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = Izq.trad + " " + op + "r " + "itor(" + Der.trad + ")";
                        }
                        else if (Izq.tipo == Simbolo.ENTERO && Der.tipo == Simbolo.REAL)
                        {
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = "itor(" + Izq.trad + ") " + op + "r " + Der.trad;
                        }
                        else
                        { // REAL && REAL
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = Izq.trad + " " + op + "r " + Der.trad;
                        }
                }
                else if(op.equals("/"))
                {
                        if (Izq.tipo == Simbolo.ENTERO && Der.tipo == Simbolo.ENTERO)
                        {
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = "itor(" + Izq.trad + ") " + op + "r " + "itor(" + Der.trad + ")";
                        }
                        else if (Izq.tipo == Simbolo.REAL && Der.tipo == Simbolo.ENTERO)
                        {
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = Izq.trad + " " + op + "r " + "itor(" + Der.trad + ")";
                        }
                        else if (Izq.tipo == Simbolo.ENTERO && Der.tipo == Simbolo.REAL)
                        {
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = "itor(" + Izq.trad + ") " + op + "r " + Der.trad;
                        }
                        else
                        { // REAL && REAL
                                devolver.tipo = Simbolo.REAL;
                                devolver.trad = Izq.trad + " " + op + "r " + Der.trad;
                        }
                }
                else if(op.equals("//"))
                {
                        devolver.tipo = Simbolo.ENTERO;
                        devolver.trad = Izq.trad + " /i " + Der.trad;
                }

                return devolver;
        }

        /**
         * Funcion que empareja los siguientes tokens en la regla
         * @param tokEsperado token esperado para emparejar
         */
        public void emparejar(int tokEsperado)
        {
                if (token.tipo == tokEsperado)
                        token = al.siguienteToken();
                else
                        errorSintaxis(tokEsperado);
        }

        /**
         * Funcuon para comprobar si el último token leido es el fin de fichero
         */
        public void comprobarFinFichero()
        {
                if(token.tipo != Token.EOF) errorSintaxis(Token.EOF);
                //if(flagNumerosReglas) System.out.println(numerosRegla);
        }

        /**
         * Función para pintar por pantalla y parar la ejecución cuando ha existido un fallo sintáctico en una regla
         * @param tokensError tokens que se esperaban al darse el error
         */
        public void errorSintaxis(int ... tokensError)
        {
                if(token.tipo != Token.EOF)
                        System.err.print("Error sintactico ("+token.fila+","+token.columna+"): encontrado '"+token.lexema+"'");
                else
                        System.err.print("Error sintactico: encontrado " + Token.nombreToken.get(token.tipo));

                System.err.print(", esperaba");

                TreeSet<Integer> tokens = Arrays.stream(tokensError).boxed().collect(Collectors.toCollection(TreeSet::new));

                for(Integer tokenErr: tokens)
                {
                        System.err.print(" " + Token.nombreToken.get(tokenErr));
                }
                System.err.println();
                System.exit(-1);
        }

        /**
         * Función para añadir la regla al stringBuilder de las reglas
         * @param numero numero de regla a añadir
         */
        private void añadirRegla(int numero)
        {
                numerosRegla.append(" ").append(numero);
        }

        /**
         * Tipos de errores semanticos que pueden ser causados
         */
        private final int ERR_YA_EXISTE=1, ERR_NO_VARIABLE=2, ERR_NO_DECL=3, ERR_NO_BOOL=4, ERR_ASIG_REAL=5, ERR_SIMIENTRAS=6, ERR_DIVENTERA=7;

        /**
         * Función que se llega al encontrar un error semántico
         * @param nerr tipo de error
         * @param fila fila donde se producce el error
         * @param columna columna donde se produce el error
         * @param lexema lexema que causa el error
         */
        private void errorSemantico(int nerr,int fila,int columna,String lexema) {
                System.err.print("Error semantico ("+fila+","+columna+"): ");
                switch (nerr) {
                        case ERR_YA_EXISTE:
                                System.err.println("'"+lexema+"' ya existe en este ambito");
                                break;
                        case ERR_NO_VARIABLE:
                                System.err.println("'"+lexema+"' no es una variable");
                                break;
                        case ERR_NO_DECL:
                                System.err.println("'"+lexema+"' no ha sido declarado");
                                break;
                        case ERR_NO_BOOL:
                                System.err.println("'"+lexema+"' no admite expresiones booleanas");
                                break;
                        case ERR_ASIG_REAL:
                                System.err.println("'"+lexema+"' debe ser de tipo real");
                                break;
                        case ERR_SIMIENTRAS:
                                System.err.println("en la instruccion '"+lexema+"' la expresion debe ser relacional");
                                break;
                        case ERR_DIVENTERA:
                                System.err.println("los dos operandos de '"+lexema+"' deben ser enteros");
                                break;
                }
                System.exit(-1);
        }
}


/**
 * Cosas a verificar todavía en el código
 * - No se puede asignar un booleano a una variable
 * - No se puede asignar un real a una variable entera
 * - Las expresión que se debe de devolver en la instrucción SI o MIENTRAS debe ser de tipo booleana
 */