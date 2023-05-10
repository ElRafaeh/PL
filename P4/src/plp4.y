/*------------------------------ ejemplo.y -------------------------------*/
%token pari pard
%token opmd opas oprel
%token pyc dosp coma
%token asig
%token var
%token real entero
%token algoritmo
%token blq
%token fblq
%token funcion
%token si
%token entonces
%token sino
%token fsi
%token mientras
%token hacer
%token escribir
%token id nentero nreal
%token eof

%{

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>

using namespace std;

#include "comun.h"
#include "TablaSimbolos.h"

// Variables para manejo de errores semanticos
const int ERR_YA_EXISTE=1,
          ERR_NO_VARIABLE=2,
          ERR_NO_DECL=3,
          ERR_NO_BOOL=4,
          ERR_ASIG_REAL=5,
          ERR_SIMIENTRAS=6,
          ERR_DIVENTERA=7;

// variables y funciones del A. Léxico
extern int columna,fila,findefichero;

extern int yylex();
extern char *yytext;
extern FILE *yyin;

int yyerror(char *s);

string operador, s1, s2;  // string auxiliares

void errorSemantico(int nerr,int fila,int columna,char *lexema); // funcion para producir mensajes de errores semanticos

TablaSimbolos *tsActual = new TablaSimbolos(NULL);  // Para el manejo de ambitos

// Para la funcion opera
struct Pair {
  string trad;
  int tipo;

  Pair(string nom, int tip) : trad(nom), tipo(tip){}
};

Pair opera(string op, Pair Izq, Pair Der);

%}

%%
	S       :   algoritmo id pyc 							{   $$.prefijo = "main";    } 
				Vsp Bloque 									{   
                                                                int token = yylex();
                                                                if (token == 0) // si es fin de fichero, yylex() devuelve 0
                                                                {
                                                                    cout << "//algoritmo " << $2.lexema << endl << $5.trad << "int main() " << $6.trad; 
                                                                }
                                                                else
                                                                    yyerror(""); 
                                                            }
    ;
    Vsp     :   Vsp Unsp 									{	
                                                                $$.trad = $1.trad + $2.trad; 
                                                                $$.prefijo = $0.prefijo;	
                                                            }
            |   Unsp                                        { 	
                                                                $$.trad = $1.trad;	
                                                                $$.prefijo = $0.prefijo;    
                                                            }
    ;
    Unsp    :   funcion id dosp Tipo pyc 					{
                                                                string nombre_ambito = (($0.prefijo == "main") ? "" : $0.prefijo + "_") + $2.lexema;

																if (tsActual->buscarAmbito($2.lexema) != NULL)
                                                                    errorSemantico(ERR_YA_EXISTE, $2.fila, $2.columna, $2.lexema);

																tsActual->nuevoSimbolo(Simbolo($2.lexema, FUNCION, $0.prefijo+$2.lexema));
																tsActual = new TablaSimbolos(tsActual);
                                                                $$.prefijo = nombre_ambito;
                                                            }
				Vsp Bloque pyc             					{
                                                                string nombre_ambito = (($0.prefijo == "main") ? "" : $0.prefijo + "_") + $2.lexema;

                                                                $$.trad = $7.trad + $4.trad + " " + nombre_ambito + "()" + $8.trad + "\n";
                                                                tsActual = tsActual->getAmbitoAnterior();
                                                            }
            |   var {$$.prefijo = $0.prefijo;}  LV          {   $$.trad = $3.trad;	}
    ;
    LV      :   LV V										{	$$.trad = $1.trad + $2.trad;    }
            |   V										    {	$$.trad = $1.trad;	}
    ;
    V       :   Lid                                         {   $$.prefijo = $0.prefijo;    } 
                dosp Tipo pyc							    {	
                                                                string id_lid, pos_lid;
                                                                stringstream ss($1.lid_trad), ss2($1.pos_lid_trad);
                                                                string delimiter = " ";                                                              

                                                                while ((ss >> id_lid) && (ss2 >> pos_lid))
                                                                {
                                                                    if(tsActual->buscarAmbito(id_lid) == NULL)
                                                                        tsActual->nuevoSimbolo(Simbolo(id_lid, $4.tipo, $0.prefijo+(string)"_"+id_lid));
                                                                    else
                                                                    {
                                                                        int pos1 = stoi(pos_lid.substr(0, pos_lid.find(","))), pos2 = stoi(pos_lid.substr(pos_lid.find(",")+1, pos_lid.length()));
                                                                        errorSemantico(ERR_YA_EXISTE, pos1, pos2, const_cast<char*>(id_lid.c_str()));
                                                                    }
                                                                }

																$$.trad = $4.trad + " " + $1.trad + ";\n";
                                                                $$.prefijo = $0.prefijo;
															}
    ;
    Lid     :   Lid coma id                                 {
																$$.trad = $1.trad + "," + $0.prefijo+(string)"_"+$3.lexema;
                                                                $$.lid_trad = $1.lid_trad + (string)" " + $3.lexema;
                                                                $$.prefijo = $0.prefijo;
                                                                $$.pos_lid_trad = $1.pos_lid_trad + (string)" " + to_string($3.fila) + (string)"," + to_string($3.columna);
															}
            |   id											{	
																$$.trad = $0.prefijo+(string)"_"+$1.lexema;	
                                                                $$.lid_trad = (string)" " + $1.lexema;
                                                                $$.prefijo = $0.prefijo;   
                                                                $$.pos_lid_trad = (string)" " + to_string($1.fila) + (string)"," + to_string($1.columna);
															}
    ;
    Tipo    :   entero                                      { 	$$.trad = "int"; $$.tipo = ENTERO;	}
            |   real                                        { 	$$.trad = "double"; $$.tipo = REAL;	}
    ;
    Bloque  :   blq SInstr fblq                             { 	
                                                                $$.trad = "{\n" + $2.trad + "}\n";	
                                                            }
    ;
    SInstr  :   SInstr pyc Instr							{ 	$$.trad = $1.trad + $3.trad;	}
            |   Instr                                       { 	$$.trad = $1.trad;	}
    ;
    Instr   :   Bloque                                      { 	$$.trad = $1.trad;	}
            |   id                                          { 
                                                                if(tsActual->buscar($1.lexema) == NULL)
																	errorSemantico(ERR_NO_DECL, $1.fila, $1.columna, $1.lexema);
																else if(tsActual->buscar($1.lexema)->tipo == FUNCION)
																	errorSemantico(ERR_NO_VARIABLE, $1.fila, $1.columna, $1.lexema);
															}
                asig E									    {																
                                                                if (tsActual->buscar($1.lexema)->tipo == ENTERO && $4.tipo == REAL)
																	errorSemantico(ERR_ASIG_REAL, $1.fila, $1.columna, $1.lexema);
																else if ($4.tipo == 0)
																	errorSemantico(ERR_NO_BOOL, $3.fila, $3.columna, $3.lexema);

																$$.trad = opera("=", Pair(tsActual->buscar($1.lexema)->nomtrad, tsActual->buscar($1.lexema)->tipo), Pair($4.trad, $4.tipo)).trad + " ;\n";
															}		
            |   si E entonces                               {
                                                                if ($2.tipo != 0)
                                                                    errorSemantico(ERR_SIMIENTRAS, $1.fila, $1.columna, $1.lexema);
                                                                
                                                                
                                                            } 
                Instr ColaIf                                {   $$.trad += "if ( " + $2.trad + " )\n" + $5.trad + $6.trad; }
            |   mientras E hacer                            {
                                                                if ($2.tipo != 0)
                                                                    errorSemantico(ERR_SIMIENTRAS, $1.fila, $1.columna, $1.lexema);
                                                            }
                Instr                                       { 
                                                                $$.trad += "while ( " + $2.trad + " )\n" + $5.trad;
                                                            }
            |   escribir pari E pard                        {

                                                                if($3.tipo == 0)
                                                                    errorSemantico(ERR_NO_BOOL, $1.fila, $1.columna, $1.lexema);

                                                                $$.trad += "printf(\"";
                                                                if ($3.tipo == ENTERO)
                                                                        $$.trad += "%d\\n\",";
                                                                else if ($3.tipo == REAL)
                                                                        $$.trad += "%g\\n\",";

                                                                $$.trad += $3.trad + ");\n";
                                                            }
    ;
    ColaIf  :   fsi											{ 	$$.trad = "";	}
            |   sino Instr fsi								{ 	$$.trad = "else\n" + $2.trad;	}
    ;
    E       :   Expr oprel Expr								{	
																if ($2.lexema == string("=")) 
                                                                { 
																	$$.trad = opera("==", Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).trad;
                                                                    $$.tipo = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).tipo;
                                                                }
																else if ($2.lexema == string("<>")) 
                                                                {
																	$$.trad = opera("!=", Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).trad;
                                                                    $$.tipo = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).tipo;
                                                                }
																else 
                                                                {
																	$$.trad = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).trad;
                                                                    $$.tipo = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).tipo;	
                                                                }
																
																$$.tipo = 0;
															}
            |   Expr										{ 	$$.trad = $1.trad;	$$.tipo = $1.tipo; }
    ;
    Expr    :   Expr opas Term								{	
																$$.trad = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).trad;	
																$$.tipo = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).tipo;
															}
            |   Term										{ 	$$.trad = $1.trad;	$$.tipo = $1.tipo;  }
    ;
    Term    :   Term opmd Factor							{	
                                                                if((strcmp($2.lexema, "//") == 0) && ($1.tipo != ENTERO || $3.tipo != ENTERO)) {
                                                                    errorSemantico(ERR_DIVENTERA, $2.fila, $2.columna, $2.lexema);
                                                                }           

																$$.trad = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).trad;	
																$$.tipo = opera($2.lexema, Pair($1.trad, $1.tipo), Pair($3.trad, $3.tipo)).tipo;
															}
            |   Factor										{ 	$$.trad = $1.trad;	$$.tipo = $1.tipo; }
    ;
    Factor  :   id                                          {
                                                        		if(tsActual->buscar($1.lexema) == NULL)
                                                                    errorSemantico(ERR_NO_DECL, $1.fila, $1.columna, $1.lexema);
                                                                else if(tsActual->buscar($1.lexema)->tipo == FUNCION)
                                                                    errorSemantico(ERR_NO_VARIABLE, $1.fila, $1.columna, $1.lexema);
                                                                else
                                                                    $$.tipo = tsActual->buscar($1.lexema)->tipo;
                                                                    $$.trad = tsActual->buscar($1.lexema)->nomtrad;  
                                                            }
            |   nentero                                     {
                                                                $$.tipo = ENTERO;
                                                                $$.trad = $1.lexema;
                                                            }
            |   nreal                                       {
                                                                $$.tipo = REAL;
                                                                $$.trad = $1.lexema;
                                                            }
            |   pari Expr pard                              {
                                                                $$.tipo = $2.tipo;
                                                                $$.trad = "(" + $2.trad + ")";
                                                            }
    ;
%%

void msgError(int nerror,int fila,int columna,const char *s)
{
    switch (nerror) {
        case ERRLEXICO: fprintf(stderr,"Error lexico (%d,%d): caracter '%s' incorrecto\n",fila,columna,s);
        break;
        case ERRSINT: fprintf(stderr,"Error sintactico (%d,%d): en '%s'\n",fila,columna,s);
        break;
        case ERREOF: fprintf(stderr,"Error sintactico: fin de fichero inesperado\n");
        break;
        case ERRLEXEOF: fprintf(stderr,"Error lexico: fin de fichero inesperado\n");
        break;
    }
        
    exit(1);
}

Pair opera(string op, Pair Izq, Pair Der)
{
	Pair devolver("",-1);
	if (!(op == "/") && !(op == "//")) {
			if (Izq.tipo == ENTERO && Der.tipo == ENTERO)
			{
					devolver.tipo = ENTERO;
					devolver.trad = Izq.trad + " " + op + "i " + Der.trad;
			}
			else if (Izq.tipo == REAL && Der.tipo == ENTERO)
			{
					devolver.tipo = REAL;
					devolver.trad = Izq.trad + " " + op + "r " + "itor(" + Der.trad + ")";
			}
			else if (Izq.tipo == ENTERO && Der.tipo == REAL)
			{
					devolver.tipo = REAL;
					devolver.trad = "itor(" + Izq.trad + ") " + op + "r " + Der.trad;
			}
			else
			{ // REAL && REAL
					devolver.tipo = REAL;
					devolver.trad = Izq.trad + " " + op + "r " + Der.trad;
			}
	}
	else if(op == "/")
	{
			if (Izq.tipo == ENTERO && Der.tipo == ENTERO)
			{
					devolver.tipo = REAL;
					devolver.trad = "itor(" + Izq.trad + ") " + op + "r " + "itor(" + Der.trad + ")";
			}
			else if (Izq.tipo == REAL && Der.tipo == ENTERO)
			{
					devolver.tipo = REAL;
					devolver.trad = Izq.trad + " " + op + "r " + "itor(" + Der.trad + ")";
			}
			else if (Izq.tipo == ENTERO && Der.tipo == REAL)
			{
					devolver.tipo = REAL;
					devolver.trad = "itor(" + Izq.trad + ") " + op + "r " + Der.trad;
			}
			else
			{ // REAL && REAL
					devolver.tipo = REAL;
					devolver.trad = Izq.trad + " " + op + "r " + Der.trad;
			}
	}
	else if(op =="//")
	{
		devolver.tipo = ENTERO;
		devolver.trad = Izq.trad + " /i " + Der.trad;
	}

	return devolver;
}

                          
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

int yyerror(char *s)
{
    if (findefichero) 
    {
      msgError(ERREOF,0,0,"");
    }
    else
    {  
      msgError(ERRSINT,fila,columna-strlen(yytext),yytext);
    }
    return 0;
}

int main(int argc,char *argv[])
{
    FILE *fent;

    if (argc==2)
    {
        fent = fopen(argv[1],"rt");
        if (fent)
        {
            yyin = fent;
            yyparse();
            fclose(fent);
        }
        else
            fprintf(stderr,"No puedo abrir el fichero\n");
    }
    else
        fprintf(stderr,"Uso: ejemplo <nombre de fichero>\n");
}