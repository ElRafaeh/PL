const int ERR_YA_EXISTE=1,
          ERR_NO_VARIABLE=2,
          ERR_NO_DECL=3,
          ERR_NO_BOOL=4,
          ERR_ASIG_REAL=5,
          ERR_SIMIENTRAS=6,
          ERR_DIVENTERA=7;
                          
void errorSemantico(int nerr,int fila,int columna,char *lexema) 
{
        fprintf(stderr,"Error semantico (%d,%d): ",fila,columna);
        switch (nerr) {
	        	case ERR_YA_EXISTE:
	        		fprintf(stderr,"'%s' ya existe en este ambito\n",lexema);
	        		break;
			case ERR_NO_VARIABLE:
			        fprintf(stderr,"'%s' no es una variable\n",lexema);
				break;
			case ERR_NO_DECL:
				fprintf(stderr,"'%s' no ha sido declarado\n",lexema);
				break;
			case ERR_NO_BOOL:
			    	fprintf(stderr,"'%s' no admite expresiones booleanas\n",lexema);
				break;
			case ERR_ASIG_REAL:
			    	fprintf(stderr,"'%s' debe ser de tipo real\n",lexema);
				break;
			case ERR_SIMIENTRAS:
			    	fprintf(stderr,"en la instruccion '%s' la expresion debe ser relacional\n",lexema);
				break;
			case ERR_DIVENTERA:
			    	fprintf(stderr,"los dos operandos de '%s' deben ser enteros\n",lexema);
				break;
        }
	exit(-1);
}


