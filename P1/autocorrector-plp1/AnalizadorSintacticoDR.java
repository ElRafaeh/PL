import java.security.PublicKey;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.TooManyListenersException;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.stream.Collectors;

public class AnalizadorSintacticoDR
{
        private boolean flagNumerosReglas;
        private AnalizadorLexico al;
        private StringBuilder numerosRegla = new StringBuilder();
        private Token token;

        /**
         * Constructor de la clase
         * @param al analizador lexico para sacar tokens
         */
        AnalizadorSintacticoDR(AnalizadorLexico al)
        {
                this.al = al;
                this.flagNumerosReglas = true;
                token = al.siguienteToken();
        }

        /**
         * Debajo de este comentario se van a poner las reglas de la gramatica
         */
        public void S()
        {
                if(token.tipo == Token.ALGORITMO)
                {
                        añadirRegla(1);
                        emparejar(Token.ALGORITMO);
                        emparejar(Token.ID);
                        emparejar(Token.PYC);
                        Vsp();
                        Bloque();
                }
                else errorSintaxis(Token.ALGORITMO);
        }

        public void Vsp()
        {
                if(token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        añadirRegla(2);
                        Unsp();
                        Vspp();
                }
                else errorSintaxis(Token.VAR, Token.FUNCION);
        }

        public void Vspp()
        {
                if(token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        añadirRegla(3);
                        Unsp();
                        Vspp();
                }
                else if(token.tipo == Token.BLQ)
                {
                        añadirRegla(4);
                        // Regla epsilon
                }
                else errorSintaxis(Token.VAR, Token.FUNCION, Token.BLQ);
        }

        public void Unsp()
        {
                if(token.tipo == Token.FUNCION)
                {
                        añadirRegla(5);
                        emparejar(Token.FUNCION);
                        emparejar(Token.ID);
                        emparejar(Token.DOSP);
                        Tipo();
                        emparejar(Token.PYC);
                        Vsp();
                        Bloque();
                        emparejar(Token.PYC);
                }
                else if (token.tipo == Token.VAR)
                {
                        añadirRegla(6);
                        emparejar(Token.VAR);
                        LV();
                }
                else errorSintaxis(Token.FUNCION, Token.VAR);
        }

        public void LV()
        {
                if(token.tipo == Token.ID)
                {
                        añadirRegla(7);
                        V();
                        LVp();
                }
                else errorSintaxis(Token.ID);
        }

        public void LVp()
        {
                if(token.tipo == Token.ID)
                {
                        añadirRegla(8);
                        V();
                        LVp();
                }
                else if (token.tipo == Token.VAR || token.tipo == Token.FUNCION || token.tipo == Token.BLQ)
                {
                        añadirRegla(9);
                        // Regla epsilon
                }
                else errorSintaxis(Token.ID, Token.VAR, Token.FUNCION, Token.BLQ);
        }

        public void V()
        {
                if(token.tipo == Token.ID)
                {
                        añadirRegla(10);
                        emparejar(Token.ID);
                        Lid();
                        emparejar(Token.DOSP);
                        Tipo();
                        emparejar(Token.PYC);
                }
                else errorSintaxis(Token.ID);
        }

        public void Lid()
        {
                if(token.tipo == Token.COMA)
                {
                        añadirRegla(11);
                        emparejar(Token.COMA);
                        emparejar(Token.ID);
                        Lid();
                }
                else if(token.tipo == Token.DOSP)
                {
                        añadirRegla(12);
                        // Regla epsilon
                }
                else errorSintaxis(Token.COMA, Token.DOSP);
        }

        public void Tipo()
        {
                if(token.tipo == Token.ENTERO)
                {
                        añadirRegla(13);
                        emparejar(Token.ENTERO);
                }
                else if(token.tipo == Token.REAL)
                {
                        añadirRegla(14);
                        emparejar(Token.REAL);
                }
                else errorSintaxis(Token.ENTERO, Token.REAL);
        }

        public void Bloque()
        {
                if(token.tipo == Token.BLQ)
                {
                        añadirRegla(15);
                        emparejar(Token.BLQ);
                        SInstr();
                        emparejar(Token.FBLQ);
                }
                else errorSintaxis(Token.BLQ);
        }

        public void SInstr()
        {
                if (token.tipo == Token.BLQ || token.tipo == Token.ID || token.tipo == Token.SI || token.tipo == Token.MIENTRAS || token.tipo == Token.ESCRIBIR)
                {
                        añadirRegla(16);
                        Instr();
                        SInstrp();
                }
                else errorSintaxis(Token.BLQ, Token.ID, Token.SI, Token.MIENTRAS, Token.ESCRIBIR);
        }

        public void SInstrp()
        {
                if (token.tipo == Token.PYC)
                {
                        añadirRegla(17);
                        emparejar(Token.PYC);
                        Instr();
                        SInstrp();
                }
                else if (token.tipo == Token.FBLQ)
                {
                        añadirRegla(18);
                        // Regla epsilon
                }
                else errorSintaxis(Token.PYC, Token.FBLQ);
        }

        public void Instr()
        {
                if(token.tipo == Token.BLQ)
                {
                        añadirRegla(19);
                        Bloque();
                }
                else if (token.tipo == Token.ID)
                {
                        añadirRegla(20);
                        emparejar(Token.ID);
                        emparejar(Token.ASIG);
                        E();
                }
                else if (token.tipo == Token.SI)
                {
                        añadirRegla(21);
                        emparejar(Token.SI);
                        E();
                        emparejar(Token.ENTONCES);
                        Instr();
                        Instrp();
                }
                else if (token.tipo == Token.MIENTRAS)
                {
                        añadirRegla(24);
                        emparejar(Token.MIENTRAS);
                        E();
                        emparejar(Token.HACER);
                        Instr();
                }
                else if (token.tipo == Token.ESCRIBIR)
                {
                        añadirRegla(25);
                        emparejar(Token.ESCRIBIR);
                        emparejar(Token.PARI);
                        E();
                        emparejar(Token.PARD);
                }
                else errorSintaxis(Token.BLQ, Token.ID, Token.SI, Token.MIENTRAS, Token.ESCRIBIR);
        }

        public void Instrp()
        {
                if (token.tipo == Token.FSI)
                {
                        añadirRegla(22);
                        emparejar(Token.FSI);
                }
                else if (token.tipo == Token.SINO)
                {
                        añadirRegla(23);
                        emparejar(Token.SINO);
                        Instr();
                        emparejar(Token.FSI);
                }
                else errorSintaxis(Token.FSI, Token.SINO);
        }

        public void E()
        {
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        añadirRegla(26);
                        Expr();
                        Ep();
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);
        }

        public void Ep()
        {
                if (token.tipo == Token.OPREL)
                {
                        añadirRegla(27);
                        emparejar(Token.OPREL);
                        Expr();
                }
                else if (token.tipo == Token.HACER || token.tipo == Token.PARD || token.tipo == Token.ENTONCES || token.tipo == Token.FSI || token.tipo == Token.PYC || token.tipo == Token.FBLQ || token.tipo == Token.SINO)
                {
                        añadirRegla(28);
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPREL, Token.HACER, Token.PARD, Token.ENTONCES, Token.FSI, Token.PYC, Token.FBLQ ,Token.SINO);
        }

        public void Expr()
        {
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        añadirRegla(29);
                        Term();
                        Exprp();
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);
        }

        public void Exprp()
        {
                if (token.tipo == Token.OPAS)
                {
                        añadirRegla(30);
                        emparejar(Token.OPAS);
                        Term();
                        Exprp();
                }
                else if (token.tipo == Token.PARD || token.tipo == Token.OPREL || token.tipo == Token.ENTONCES || token.tipo == Token.HACER || token.tipo == Token.FSI || token.tipo == Token.SINO || token.tipo == Token.PYC || token.tipo == Token.FBLQ)
                {
                        añadirRegla(31);
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPAS, Token.PARD, Token.OPREL, Token.ENTONCES, Token.HACER, Token.FSI, Token.SINO, Token.PYC, Token.FBLQ);
        }

        public void Term()
        {
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        añadirRegla(32);
                        Factor();
                        Termp();
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);
        }

        public void Termp()
        {
                if (token.tipo == Token.OPMD)
                {
                        añadirRegla(33);
                        emparejar(Token.OPMD);
                        Factor();
                        Termp();
                }
                else if (token.tipo == Token.OPAS || token.tipo == Token.PARD || token.tipo == Token.OPREL || token.tipo == Token.ENTONCES || token.tipo == Token.HACER || token.tipo == Token.FSI || token.tipo == Token.SINO || token.tipo == Token.PYC || token.tipo == Token.FBLQ)
                {
                        añadirRegla(34);
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPMD, Token.OPAS, Token.PARD, Token.OPREL, Token.ENTONCES, Token.HACER, Token.FSI, Token.SINO, Token.PYC, Token.FBLQ);
        }

        public void Factor()
        {
                if(token.tipo == Token.ID)
                {
                        añadirRegla(35);
                        emparejar(Token.ID);
                }
                else if (token.tipo == Token.NENTERO)
                {
                        añadirRegla(36);
                        emparejar(Token.NENTERO);
                }
                else if (token.tipo == Token.NREAL)
                {
                        añadirRegla(37);
                        emparejar(Token.NREAL);
                }
                else if (token.tipo == Token.PARI)
                {
                        añadirRegla(38);
                        emparejar(Token.PARI);
                        Expr();
                        emparejar(Token.PARD);
                }
                else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
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
                if(flagNumerosReglas) System.out.println(numerosRegla);
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
                        System.err.print("Error sintactico: encontrado fin de fichero");

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
}
