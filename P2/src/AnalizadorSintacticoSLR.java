import java.util.Stack;

public class AnalizadorSintacticoSLR {
    private AnalizadorLexico al;
    private Stack<Integer> estados;
    private Stack<Integer> reglas;
    public Token token;

    private String[][] actionTable =
    {
        // PARI   PARD   OPAS   PYC     DOSP   ASIG   VAR     REAL   ENTERO   ALGORITMO BLQ    FBLQ   SI     ENTONCES   FSI    MIENTRAS   HACER   ESCRIBIR   ID     NENTERO   NREAL   EOF      -ESTADOS-
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   "d1" ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 0
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d3" , ""   ,    ""   ,  ""        }, // 1
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  "aceptar" }, // 2
        {  ""   , ""   , ""   , "d4" ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 3
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "d6" ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 4
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "d6" ,  ""   , ""   ,   ""   ,    "d19", ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 5
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d10", ""   ,    ""   ,  ""        }, // 6
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "r3" ,  ""   , ""   ,   ""   ,    "r3" , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 7
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "r4" ,  ""   , ""   ,   ""   ,    "r4" , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d10", ""   ,    ""   ,  ""        }, // 8
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "r6" ,  ""   , ""   ,   ""   ,    "r6" , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "r6" , ""   ,    ""   ,  ""        }, // 9
        {  ""   , ""   , ""   , ""   ,  "d11", ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 10
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  "d14", "d13",   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 11
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "r5" ,  ""   , ""   ,   ""   ,    "r5" , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "r5" , ""   ,    ""   ,  ""        }, // 12
        {  ""   , ""   , ""   , "r8" ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 13
        {  ""   , ""   , ""   , "r9" ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 14
        {  ""   , ""   , ""   , "d16",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 15
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "r7" ,  ""   , ""   ,   ""   ,    "r7" , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "r7" , ""   ,    ""   ,  ""        }, // 16
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  "r1"      }, // 17
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , "r2" ,  ""   , ""   ,   ""   ,    "r2" , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 18
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    "d19", ""   , "d26", ""   ,     ""   , "d27",     ""   ,  "d28",     "d25", ""   ,    ""   ,  ""        }, // 19
        {  ""   , ""   , ""   , "d24",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "d21", ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 20
        {  ""   , ""   , ""   , "r10",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r10", ""   , ""   ,     "r10", ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  "r10"     }, // 21
        {  ""   , ""   , ""   , "r12",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r12", ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 22
        {  ""   , ""   , ""   , "r13",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r13", ""   , ""   ,     "r13", ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 23
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    "d19", ""   , "d26", ""   ,     ""   , "d27",     ""   ,  "d28",     "d25", ""   ,    ""   ,  ""        }, // 24
        {  ""   , ""   , ""   , ""   ,  ""   , "d31", ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 25
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d34", "d35",    "d36",  ""        }, // 26
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d34", "d35",    "d36",  ""        }, // 27
        {  "d29", ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 28
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d34", "d35",    "d36",  ""        }, // 29
        {  ""   , ""   , ""   , "r11",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r11", ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 30
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d34", "d35",    "d36",  ""        }, // 31
        {  ""   , ""   , "d40", ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , "d41",     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 32
        {  ""   , "r19", "r19", "r19",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r19", ""   , "r19",     "r19", ""   ,     "r19",  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 33
        {  ""   , "r20", "r20", "r20",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r20", ""   , "r20",     "r20", ""   ,     "r20",  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 34
        {  ""   , "r21", "r21", "r21",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r21", ""   , "r21",     "r21", ""   ,     "r21",  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 35
        {  ""   , "r22", "r22", "r22",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r22", ""   , "r22",     "r22", ""   ,     "r22",  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 36
        {  ""   , "d42", "d40", ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 37
        {  ""   , ""   , "d40", ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     "d43",  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 38
        {  ""   , ""   , "d40", "r14",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r14", ""   , ""   ,     "r14", ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 39
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     ""   , ""   ,     ""   ,  ""   ,     "d34", "d35",    "d36",  ""        }, // 40
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    "d19", ""   , "d26", ""   ,     ""   , "d27",     ""   ,  "d28",     "d25", ""   ,    ""   ,  ""        }, // 41
        {  ""   , ""   , ""   , "r17",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r17", ""   , ""   ,     ""   , "r17",     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 42
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    "d19", ""   , "d26", ""   ,     ""   , "d27",     ""   ,  "d28",     "d25", ""   ,    ""   ,  ""        }, // 43
        {  ""   , "r18", "r18", "r18",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r18", ""   , "r18",     "r18", ""   ,     "r18",  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 44
        {  ""   , ""   , ""   , "r16",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r16", ""   , ""   ,     "r16", ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 45
        {  ""   , ""   , ""   , ""   ,  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , ""   , ""   , ""   ,     "d47", ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }, // 46
        {  ""   , ""   , ""   , "r15",  ""   , ""   , ""   ,  ""   , ""   ,   ""   ,    ""   , "r15", ""   , ""   ,     "r15", ""   ,     ""   ,  ""   ,     ""   , ""   ,    ""   ,  ""        }  // 47
    };

    private Integer[][] gotoTable =
    {       // 0    1    2    3   4    5       6       7       8      9     10
            // S   VSP  UNSP  LV  V   TIPO   BLOQUE  SINSTR   INSTR   E   FACTOR      -ESTADOS-
            {  2 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 0
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 1
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 2
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 3
            {  0 , 5 ,  7 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 4
            {  0 , 0 ,  18,   0 , 0 , 0 ,    17,     0 ,      0 ,     0 , 0            }, // 5
            {  0 , 0 ,  0 ,   8 , 9 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 6
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 7
            {  0 , 0 ,  0 ,   0 , 12, 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 8
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 9
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 10
            {  0 , 0 ,  0 ,   0 , 0 , 15,    0 ,     0 ,      0 ,     0 , 0            }, // 11
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 12
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 13
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 14
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 15
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 16
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 17
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 18
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    23,     20,      22,     0 , 0            }, // 19
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 20
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 21
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 22
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 23
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    23,     0 ,      30,     0 , 0            }, // 24
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 25
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     32, 33           }, // 26
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     38, 33           }, // 27
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 28
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     37, 33           }, // 29
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 30
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     39, 33           }, // 31
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 32
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 33
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 34
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 35
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 36
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 37
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 38
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 39
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 44           }, // 40
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    23,     0,      46,     0 , 0            }, // 41
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 42
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    23,     0,      45,     0 , 0            }, // 43
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 44
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 45
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }, // 46
            {  0 , 0 ,  0 ,   0 , 0 , 0 ,    0 ,     0 ,      0 ,     0 , 0            }  // 47
    };

    private Integer[] parteIzquierda =
    { // 0  1  2  3  4  5  6  7  8  9  10  11  12  13  14  15  16  17  18  19  20  21  22 : REGLAS
         0, 0, 1, 1, 2, 3, 3, 4, 5, 5, 6,  7,  7,  8,  8,  8,  8,  8,  9,  9,  10, 10, 10
    };

    private Integer[] logitudParteDerecha =
    { // 0  1  2  3  4  5  6  7  8  9  10  11  12  13  14  15  16  17  18  19  20  21  22 : REGLAS
         1, 5, 2, 1, 2, 2, 1, 4, 1, 1, 3,  3,  1,  1,  3,  5,  4,  4,  3,  1,  1,  1,  1
    };

    public AnalizadorSintacticoSLR(AnalizadorLexico al)
    {
        this.al = al;
        this.estados = new Stack<>();
        this.reglas = new Stack<>();
    }

    public void analizar()
    {
        estados.push(0);
        token = al.siguienteToken();
        boolean finAnalisis = false;

        while(!finAnalisis)
        {
            int s = estados.peek();

            if (actionTable[s][token.tipo] == "")
            {
                errorSintaxis();
            }
            else if ((actionTable[s][token.tipo]).charAt(0) == 'd')
            {
                int j = Integer.parseInt(actionTable[s][token.tipo].substring(1, actionTable[s][token.tipo].length()));
                estados.push(j);
                token = al.siguienteToken();
            }
            else if ((actionTable[s][token.tipo]).charAt(0) == 'r')
            {
                Integer k = Integer.parseInt(actionTable[s][token.tipo].substring(1, actionTable[s][token.tipo].length()));
                reglas.push(k);
                for (int i = 1; i <= logitudParteDerecha[k]; i++) estados.pop();
                int p = estados.peek();
                int A = parteIzquierda[k];
                estados.push(gotoTable[p][A]);
            }
            else if (actionTable[s][token.tipo] == "aceptar")
            {
                finAnalisis = true;
            }
        }

        System.out.print(reglas.pop().toString());
        while (!reglas.isEmpty())
        {
            System.out.print(" " + reglas.pop().toString());
        }
        System.out.println();
    }

    public void errorSintaxis()
    {
        if(token.tipo != Token.EOF)
            System.err.print("Error sintactico ("+token.fila+","+token.columna+"): encontrado '"+token.lexema+"'");
        else
            System.err.print("Error sintactico: encontrado " + Token.nombreToken.get(token.tipo));

        System.err.print(", esperaba");
        String[] pitoDeLeche = actionTable[estados.peek()];

        for(int i=0; i < pitoDeLeche.length; i++)
        {
            if (pitoDeLeche[i] != "")
            {
                System.err.print(" " + Token.nombreToken.get(i));
            }
        }
        System.err.println();
        System.exit(-1);
    }
}



