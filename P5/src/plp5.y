/*------------------------------ ejemplo.y -------------------------------*/
%token algoritmo falgoritmo
%token var fvar
%token entero real logico
%token tabla de escribe lee si entonces sino mientras hacer repetir hasta
%token blq fblq cierto falso
%token id nentero nreal
%token coma pyc dospto pari pard
%token oprel opas opmd opasig
%token cori cord
%token ybool obool nobool

%{

using namespace std;

#include "comun.h"
#include "TablaSimbolos.h"
#include "TablaTipos.h"

// variables y funciones del A. Léxico
extern int columna,fila,findefichero;

extern int yylex();
extern char *yytext;
extern FILE *yyin;

int yyerror(char *s);

string operador, s1, s2;  // string auxiliares

void errorSemantico(int nerr,int fila,int columna,char *lexema); // funcion para producir mensajes de errores semanticos

TablaSimbolos *tsActual = new TablaSimbolos(NULL);  // Para el manejo de ambitos

Pair opera(string op, Pair Izq, Pair Der);

%}

%%
   S       : algoritmo dospto id SDec SInstr falgoritmo   { 
                                                            int token = yylex();
                                                            if (token == 0) // si es fin de fichero, yylex() devuelve 0
                                                            {
                                                               cout << "//algoritmo " << $2.lexema << endl << $5.trad << "int main() " << $6.trad; 
                                                            }
                                                            else
                                                               yyerror("");
                                                         }
   ;
   SDec    : Dec
            | 
   ;
   Dec     : var DVar MDVar fvar
   ;
   DVar    : Tipo dospto id LId pyc
   ;
   MDVar   : DVar MDVar
            |
   ;
   LId     : coma id LId
            | 
   ;
   Tipo    : entero
            | real
            | logico
            | tabla nentero de Tipo
   ;
   SInstr  : SInstr pyc Instr
            | Instr
   ;
   Instr   : escribe Expr
            | lee Ref
            | si Expr entonces Instr
            | si Expr entonces Instr sino Instr
            | mientras Expr hacer Instr
            | repetir Instr hasta Expr
            | Ref opasig Expr
            | blq SDec SInstr fblq
   ;
   Expr    : Expr obool Econj
            | Econj
   ;
   Econj   : Econj ybool Ecomp
            | Ecomp
   ;
   Ecomp   : Esimple oprel Esimple
            | Esimple
   ;
   Esimple : Esimple opas Term
            | Term
            | opas Term
   ;
   Term    : Term opmd Factor
            | Factor
   ;
   Factor  : Ref
            | nentero
            | nreal
            | pari Expr pard
            | nobool Factor
            | cierto
            | falso
   ;
   Ref     : id
            | Ref cori Esimple cord
   ;
%%

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

void errorSemantico(int nerror,int nlin,int ncol,const char *s)
{
   fprintf(stderr,"Error semantico (%d,%d): ", nlin,ncol);
   switch(nerror) {
      case ERR_YADECL: fprintf(stderr,"variable '%s' ya declarada\n",s);
         break;
      case ERR_NODECL: fprintf(stderr,"variable '%s' no declarada\n",s);
         break;
      case ERR_DIM: fprintf(stderr,"la dimension debe ser mayor que cero\n");
         break;
      case ERR_FALTAN: fprintf(stderr,"faltan indices\n");
         break;
      case ERR_SOBRAN: fprintf(stderr,"sobran indices\n");
         break;
      case ERR_INDICE_ENTERO: fprintf(stderr,"la expresion entre corchetes debe ser de tipo entero\n");
         break;
      case ERR_EXP_LOG: fprintf(stderr,"la expresion debe ser de tipo logico\n");
         break;
      case ERR_EXDER_LOG: fprintf(stderr,"la expresion a la derecha de '%s' debe ser de tipo logico\n",s);
         break;
      case ERR_EXDER_ENT: fprintf(stderr,"la expresion a la derecha de '%s' debe ser de tipo entero\n",s);
         break;
      case ERR_EXDER_RE:fprintf(stderr,"la expresion a la derecha de '%s' debe ser de tipo real o entero\n",s);
         break;        
      case ERR_EXIZQ_LOG:fprintf(stderr,"la expresion a la izquierda de '%s' debe ser de tipo logico\n",s);
         break;       
      case ERR_EXIZQ_RE:fprintf(stderr,"la expresion a la izquierda de '%s' debe ser de tipo real o entero\n",s);
         break;       
      case ERR_NOCABE:fprintf(stderr,"la variable '%s' ya no cabe en memoria\n",s);
         break;
      case ERR_MAXTMP:fprintf(stderr,"no hay espacio para variables temporales\n");
         break;
   }
   exit(-1);
}

void msgError(int nerror,int nlin,int ncol,const char *s)
{
   switch (nerror) {
      case ERRLEXICO: fprintf(stderr,"Error lexico (%d,%d): caracter '%s' incorrecto\n",nlin,ncol,s);
         break;
      case ERRSINT: fprintf(stderr,"Error sintactico (%d,%d): en '%s'\n",nlin,ncol,s);
         break;
      case ERREOF: fprintf(stderr,"Error sintactico: fin de fichero inesperado\n");
         break;
   }

   exit(1);
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