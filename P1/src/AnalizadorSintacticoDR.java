import java.security.PublicKey;
import java.util.EmptyStackException;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingDeque;

public class AnalizadorSintacticoDR
{
        private boolean flag = false;
        private AnalizadorLexico al;
        private Token token;

        /**
         * Constructor de la clase
         * @param al analizador lexico para sacar tokens
         */
        AnalizadorSintacticoDR(AnalizadorLexico al)
        {
                this.al = al;
                token = al.siguienteToken();
        }

        /**
         * Debajo de este comentario se van a poner los estados posibles de la gramatica
         */
        public void S()
        {
                if(token.tipo == Token.ALGORITMO)
                {
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
                        Unsp();
                        Vspp();
                }
                else errorSintaxis(Token.VAR, Token.FUNCION);
        }

        public void Vspp()
        {
                if(token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        Unsp();
                        Vspp();
                }
                else if(token.tipo == Token.BLQ)
                {
                        // Regla epsilon
                }
                else errorSintaxis(Token.VAR, Token.FUNCION, Token.BLQ);
        }

        public void Unsp()
        {
                if(token.tipo == Token.FUNCION)
                {
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
                        emparejar(Token.VAR);
                        LV();
                }
                else errorSintaxis(Token.FUNCION, Token.VAR);
        }

        public void LV()
        {
                if(token.tipo == Token.ID || token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        V();
                        LVp();
                }
                else errorSintaxis(Token.ID, Token.VAR, Token.FUNCION);
        }

        public void LVp()
        {
                if(token.tipo == Token.ID || token.tipo == Token.VAR || token.tipo == Token.FUNCION)
                {
                        V();
                        LVp();
                }
                else errorSintaxis(Token.ID, Token.VAR, Token.FUNCION);
        }

        public void V()
        {
                if(token.tipo == Token.ID)
                {
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
                        emparejar(Token.COMA);
                        emparejar(Token.ID);
                        Lid();
                }
                else if(token.tipo == Token.DOSP)
                {
                        // Regla epsilon
                }
                else errorSintaxis(Token.COMA, Token.DOSP);
        }

        public void Tipo()
        {
                if(token.tipo == Token.ENTERO)
                {
                        emparejar(Token.ENTERO);;
                }
                else if(token.tipo == Token.REAL)
                {
                        emparejar(Token.REAL);
                }
                else errorSintaxis(Token.ENTERO, Token.REAL);
        }

        public void Bloque()
        {
                if(token.tipo == Token.BLQ)
                {
                        emparejar(Token.BLQ);
                        SInstr();
                        emparejar(Token.FBLQ);
                }
                else errorSintaxis(Token.BLQ);
        }

        public void SInstr()
        {
                if (token.tipo == Token.MIENTRAS || token.tipo == Token.ESCRIBIR || token.tipo == Token.ID || token.tipo == Token.SI || token.tipo == Token.BLQ)
                {
                        Instr();
                        SInstrp();
                }
                else errorSintaxis();
        }

        public void SInstrp()
        {
                if (token.tipo == Token.PYC)
                {
                        emparejar(Token.PYC);
                        Instr();
                        SInstrp();
                }
                else if (token.tipo == Token.FBLQ)
                {
                        // Regla epsilon
                }
                else errorSintaxis(Token.PYC, Token.FBLQ);
        }

        public void Instr()
        {
                if(token.tipo == Token.BLQ)
                {
                        Bloque();
                }
                else if (token.tipo == Token.ID)
                {
                        emparejar(Token.ID);
                        emparejar(Token.ASIG);
                        E();
                }
                else if (token.tipo == Token.SI)
                {
                        emparejar(Token.SI);
                        E();
                        emparejar(Token.ENTONCES);
                        Instr();
                        Instrp();
                }
                else if (token.tipo == Token.MIENTRAS)
                {
                        emparejar(Token.MIENTRAS);
                        E();
                        emparejar(Token.HACER);
                        Instr();
                }
                else if (token.tipo == Token.ESCRIBIR)
                {
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
                        emparejar(Token.FSI);
                }
                else if (token.tipo == Token.SINO)
                {
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
                        Expr();
                        Ep();
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);
        }

        public void Ep()
        {
                if (token.tipo == Token.OPREL)
                {
                        emparejar(Token.OPREL);
                        Expr();
                }
                else if (token.tipo == Token.ENTONCES || token.tipo == Token.HACER || token.tipo == Token.PARD || token.tipo == Token.FSI || token.tipo == Token.SINO)
                {
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPREL, Token.ENTONCES, Token.HACER, Token.PARD, Token.FSI, Token.SINO);
        }

        public void Expr()
        {
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        Term();
                        Exprp();
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);
        }

        public void Exprp()
        {
                if (token.tipo == Token.OPAS)
                {
                        emparejar(Token.OPAS);
                        Term();
                        Exprp();
                }
                else if (token.tipo == Token.PARD || token.tipo == Token.OPREL || token.tipo == Token.ENTONCES || token.tipo == Token.HACER || token.tipo == Token.FSI || token.tipo == Token.SINO)
                {
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPAS, Token.PARD, Token.OPREL, Token.ENTONCES, Token.HACER, Token.FSI, Token.SINO);
        }

        public void Term()
        {
                if (token.tipo == Token.PARI || token.tipo == Token.NREAL || token.tipo == Token.NENTERO || token.tipo == Token.ID)
                {
                        Factor();
                        Termp();
                }
                else errorSintaxis(Token.PARI, Token.NREAL, Token.NENTERO, Token.ID);
        }

        public void Termp()
        {
                if (token.tipo == Token.OPAS)
                {
                        emparejar(Token.OPMD);
                        Factor();
                        Termp();
                }
                else if (token.tipo == Token.ID)
                {
                        // Regla epsilon
                }
                else errorSintaxis(Token.OPAS, Token.ID);
        }

        public void Factor()
        {
                if(token.tipo == Token.ID)
                {
                        emparejar(Token.ID);
                }
                else if (token.tipo == Token.NENTERO)
                {
                        emparejar(Token.NENTERO);
                }
                else if (token.tipo == Token.NREAL)
                {
                        emparejar(Token.NREAL);
                }
                else if (token.tipo == Token.PARI)
                {
                        emparejar(Token.PARI);
                        Expr();
                        emparejar(Token.PARD);
                }
                else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        }

        public void emparejar(int tokEsperado)
        {
                if (token.tipo == tokEsperado)
                        token = al.siguienteToken();
                else
                        errorSintaxis(tokEsperado);
        }

        public void errorSintaxis(int ... tokensError)
        {
                System.out.println("ERROR");
        }
}
