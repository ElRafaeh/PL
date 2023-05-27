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

extern int yylex();
extern char *yytext;
extern FILE *yyin;
int yyerror(char *s);

// variables y funciones del A. Léxico
extern int columna,fila,findefichero;

// Cosntantes de valores maximos de memoria
constexpr int MAX_VAR_SPACE = 15999;
constexpr int MAX_TEMP_SPACE = 16383;

// Para llevar el contador de las direcciones de memoria
int C_VAR = -1;       // Contador de variable
int C_TEMP = 15999;  // Contador de temporales
int registros = 0;   // Contador de registros

// Para el manejo de ambitos y tipos
TablaSimbolos *tsActual = new TablaSimbolos(NULL);   
TablaTipos *ttActual = new TablaTipos();  

// Función para operaciones
Token opera(string op, Token Izq, Token Der);  

// Funcion para producir mensajes de errores semanticos
void errorSemantico(int nerr,int fila,int columna,const char *lexema); 

// Función que comprueba si hay memoria suficiente e incrementa el contador de memoria de variables
void comprobarMemoriaVariables(Token token, int tam);
// Función que comprueba si hay memoria suficiente e incrementa el contador de memoria de temporales
int NuevaTemporal(); 

bool esArray(int tipo);
int tamT(int tipo);
int buscarT(int tipo);

%}

%%
   S       : algoritmo dospto id SDec SInstr falgoritmo   { 
                                                            int token = yylex();
                                                            if (token == 0) // si es fin de fichero, yylex() devuelve 0
                                                            {
                                                               cout << $5.cod << "halt\n";
                                                            }
                                                            else
                                                               yyerror("");
                                                         }
   ;
   SDec     : Dec                                        
            |                                            
   ;
   Dec      : var DVar MDVar fvar                                                
   ;
   DVar     : Tipo dospto id                             { 
                                                            if(tsActual->buscarAmbito($3.lexema) != NULL){
                                                               errorSemantico(ERR_YADECL, $3.fila, $3.columna, $3.lexema);
                                                            }
                                                            comprobarMemoriaVariables($3, $1.tam);
                                                            tsActual->newSymb(Simbolo($3.lexema,$1.tipo, C_VAR, $1.tam));
                                                            $$.tipo = $1.tipo;
                                                            $$.tam = $1.tam;
                                                         } 
            LId pyc
   ;
   MDVar    : DVar MDVar                              
            |                                            
   ;
   LId      : coma id                                    {
                                                            if(tsActual->buscarAmbito($2.lexema) != NULL){
                                                               errorSemantico(ERR_YADECL, $2.fila, $2.columna, $2.lexema);
                                                               
                                                            }
                                                            comprobarMemoriaVariables($2, $0.tam);
                                                            tsActual->newSymb(Simbolo($2.lexema,$0.tipo, C_VAR, $0.tam));
                                                            $$.tipo = $0.tipo;
                                                            $$.tam = $0.tam;
                                                         } 
            LId                                
            |                                           
   ;
   Tipo     : entero                                     {  
                                                            $$.tipo = ENTERO; 
                                                            $$.tam = 1;
                                                         }
            | real                                       {  
                                                            $$.tipo = REAL; 
                                                            $$.tam = 1;
                                                         }
            | logico                                     {  
                                                            $$.tipo = LOGICO; 
                                                            $$.tam = 1;
                                                         }
            | tabla nentero de Tipo                      {  
                                                            if(atoi($2.lexema) <= 0){
                                                               errorSemantico(ERR_DIM, $2.fila, $2.columna, $2.lexema);
                                                            }
                                                            $$.tipo = ttActual->nuevoTipoArray(atoi($2.lexema), $4.tipo);
                                                            $$.tam = atoi($2.lexema)*$4.tam;
                                                         }
   ;
   SInstr   : SInstr pyc                                 {  $$.guardaTemporal = C_TEMP; } 
              Instr                                      { 
                                                            $$.cod = $1.cod + "\n" + $4.cod;
                                                            C_TEMP = $3.guardaTemporal;  
                                                         }
            | Instr                                      {  $$.cod = $1.cod; }
   ;
   Instr    : escribe Expr                               {	
                                                            
                                                         }
            | lee Ref                                    {	
                                                            
                                                         }
            | si Expr entonces Instr ColaIf              {
                                                            
                                                         }
            | mientras Expr hacer Instr                  {
                                                            
                                                         }
            | repetir Instr hasta Expr                   {

                                                         }
            | Ref opasig Expr                            {
                                                            if ($1.tipo == ENTERO && $3.tipo == REAL) 
                                                               errorSemantico(ERR_EXDER_ENT, $2.fila, $2.columna, $2.lexema);
                                                            if ($1.tipo == LOGICO && ($3.tipo == ENTERO || $3.tipo == REAL)) 
                                                               errorSemantico(ERR_EXDER_LOG, $2.fila, $2.columna, $2.lexema);
                                                            if (($1.tipo == ENTERO || $1.tipo == REAL) && $3.tipo == LOGICO) 
                                                               ($1.tipo == ENTERO)? errorSemantico(ERR_EXDER_ENT, $2.fila, $2.columna, $2.lexema) : errorSemantico(ERR_EXDER_RE, $2.fila, $2.columna, $2.lexema);
                           
                                                            stringstream ss;

                                                            if ($1.tipo == REAL && $3.tipo == ENTERO)
                                                            {
                                                               ss << $1.cod + $3.cod;
                                                               ss << "mov " << $3.dir << " A\n";
                                                               ss << "itor\n";
                                                               ss << "mov " << "A " << $1.dir << "\n";
                                                               $$.cod = ss.str();
                                                            }
                                                            else 
                                                            {
                                                               ss << $1.cod + $3.cod;
                                                               ss << "mov " << $3.dir << " " << $1.dir << "\n";
                                                               $$.cod = ss.str();
                                                            }
							                                    }
            | blq                                        {  tsActual = new TablaSimbolos(tsActual); } 
              SDec SInstr fblq                           {
                                                            tsActual = tsActual->getAmbitoAnterior();
                                                            $$.cod = $4.cod;
                                                         }
   ;
   ColaIf   : sino Instr                                 {
                                                            $$.cod = $2.cod;
                                                         }
            | 
   ;
   Expr     : Expr obool Econj 	                        { 
                                                            if ($1.tipo != LOGICO)
                                                               errorSemantico(ERR_EXIZQ_LOG, $2.fila, $2.columna, $2.lexema);
                                                            if ($3.tipo != LOGICO)
                                                               errorSemantico(ERR_EXDER_LOG, $2.fila, $2.columna, $2.lexema);
                                                         }
            | Econj                                      {
                                                            $$.tipo = $1.tipo;
                                                            $$.cod = $1.cod;
                                                            $$.dir = $1.dir;
                                                         }
   ;
   Econj    : Econj ybool Ecomp                          {
                                                            if ($1.tipo != LOGICO)
                                                               errorSemantico(ERR_EXIZQ_LOG, $2.fila, $2.columna, $2.lexema);
                                                            if ($3.tipo != LOGICO)
                                                               errorSemantico(ERR_EXDER_LOG, $2.fila, $2.columna, $2.lexema);       
                                                         }
            | Ecomp                                      {
                                                            $$.tipo = $1.tipo;
                                                            $$.cod = $1.cod;
                                                            $$.dir = $1.dir;
                                                         }
   ;
   Ecomp    : Esimple oprel Esimple                      {
                                                            if ($1.tipo == LOGICO)
                                                               errorSemantico(ERR_EXIZQ_RE, $2.fila, $2.columna, $2.lexema);
                                                            if ($3.tipo == LOGICO)
                                                               errorSemantico(ERR_EXDER_RE, $2.fila, $2.columna, $2.lexema);

                                                            string op;
                                                            
                                                            if       (strcmp($2.lexema, "=") == 0)     op = "eql";
                                                            else if  (strcmp($2.lexema, "<>") == 0)    op = "neq";
                                                            else if  (strcmp($2.lexema, ">") == 0)     op = "gtr";
                                                            else if  (strcmp($2.lexema, ">=") == 0)    op = "geq";
                                                            else if  (strcmp($2.lexema, "<") == 0)     op = "lss";
                                                            else if  (strcmp($2.lexema, "<=") == 0)    op = "leq"; 
                                                            
                                                            Token operacion = opera(op, $1, $3);

                                                            $$.cod = $1.cod + $3.cod + operacion.cod;
                                                            $$.dir = operacion.dir;
                                                            $$.tipo = LOGICO;
                                                         }
            | Esimple                                    {
                                                            $$.tipo = $1.tipo;
                                                            $$.cod = $1.cod;
                                                            $$.dir = $1.dir;
                                                         }
   ;
   Esimple  : Esimple opas Term                          {	
                                                            if ($1.tipo == LOGICO)
                                                               errorSemantico(ERR_EXIZQ_RE, $2.fila, $2.columna, $2.lexema);
                                                            if ($3.tipo == LOGICO)
                                                               errorSemantico(ERR_EXDER_RE, $2.fila, $2.columna, $2.lexema);

                                                            string op;
                                                            (strcmp($2.lexema, "+"))? op = "sub" : op = "add";

                                                            Token operacion = opera(op, $1, $3);

                                                            $$.cod = $1.cod + $3.cod + operacion.cod;
                                                            $$.dir = operacion.dir;
                                                            $$.tipo = operacion.tipo;
                                                         }
            | Term                                       {
                                                            $$.tipo = $1.tipo;
                                                            $$.cod = $1.cod;
                                                            $$.dir = $1.dir; 
                                                         }
            | opas Term                                  {
                                                            $$.tipo = $2.tipo;
                                                            $$.cod = $2.cod;
                                                            $$.dir = $2.dir;
                                                         }
   ;
   Term     : Term opmd Factor                           {
                                                            if ($1.tipo == LOGICO)
                                                               errorSemantico(ERR_EXIZQ_RE, $2.fila, $2.columna, $2.lexema);
                                                            if ($3.tipo == LOGICO)
                                                               errorSemantico(ERR_EXDER_RE, $2.fila, $2.columna, $2.lexema);

                                                            string op;
                                                            (strcmp($2.lexema, "/"))? op = "mul" : op = "div";

                                                            Token operacion = opera(op, $1, $3);

                                                            $$.cod = $1.cod + $3.cod + operacion.cod;
                                                            $$.dir = operacion.dir;
                                                            $$.tipo = operacion.tipo;
                                                         }
            | Factor                                     {
                                                            $$.tipo = $1.tipo;
                                                            $$.cod = $1.cod;
                                                            $$.dir = $1.dir;
                                                         }
   ;
   Factor   : Ref                                        {	
                                                            $$.tipo = $1.tipo;
                                                            $$.cod = $1.cod;
                                                            $$.dir = $1.dir;
                                                         }
            | nentero                                    { 	
                                                            $$.tipo = ENTERO;
                                                            int tmp = NuevaTemporal();
                                                            stringstream ss;
                                                            ss << "mov #" << $1.lexema << " " << tmp << "\n"; 
                                                            $$.cod = ss.str();
                                                            $$.dir = tmp;
                                                         }
            | nreal                                      { 	
                                                            $$.tipo = REAL;
                                                            int tmp = NuevaTemporal();
                                                            stringstream ss;
                                                            
                                                            ss << "mov $" << $1.lexema << " " << tmp << "\n"; 
                                                            $$.cod = ss.str();
                                                            $$.dir = tmp;
                                                         }
            | pari Expr pard                             {
                                                            $$.tipo = $2.tipo;
                                                            $$.cod = $2.cod;
                                                            $$.dir = $2.dir;
                                                         }
            | nobool Factor                              {
                                                            if($2.tipo != LOGICO)
                                                               errorSemantico(ERR_EXP_LOG, $2.fila, $2.columna, $2.lexema);
                                                            else
                                                            {
                                                               $$.tipo = LOGICO;
                                                               int tmp = NuevaTemporal();
                                                               stringstream ss;
                                                               
                                                               ss << "mov " << $2.dir << " A\n"; 
                                                               ss << "noti\n";
                                                               ss << "mov A " << tmp << "\n";
                                                               $$.cod = ss.str();
                                                               $$.dir = tmp;
                                                            }
                                                         }
            | cierto                                     { 	
                                                            $$.tipo = LOGICO;
                                                            int tmp = NuevaTemporal();
                                                            stringstream ss;
                                                            
                                                            ss << "mov #1 " << tmp << "\n"; 
                                                            $$.cod = ss.str();
                                                            $$.dir = tmp;
                                                         }
            | falso                                      { 	
                                                            $$.tipo = LOGICO;
                                                            int tmp = NuevaTemporal();
                                                            stringstream ss;
                                                            
                                                            ss << "mov #0 " << tmp << "\n"; 
                                                            $$.cod = ss.str();
                                                            $$.dir = tmp;
                                                         }
   ;
   Ref      : id                                         { 	
                                                            if (tsActual->searchSymb($1.lexema) == NULL)
                                                               errorSemantico(ERR_NODECL, $1.fila, $1.columna, $1.lexema);
                                                            else
                                                            {
                                                               Simbolo simbolo = *(tsActual->searchSymb($1.lexema));
                                                               int tmp = NuevaTemporal();
                                                               stringstream ss;
                                                               ss << "mov " << simbolo.dir << " " << tmp << "\n";

                                                               //$$.cod = ss.str();
                                                               //$$.dir = tmp;
                                                               $$.dir = simbolo.dir;
                                                               $$.tipo = simbolo.tipo;
                                                            }
                                                         }
            | Ref cori                                   {
                                                            if(!esArray($1.tipo)) errorSemantico(0,0,0,$1.lexema);
                                                         }
              Esimple cord                               {
                                                            if($4.tipo != ENTERO)
                                                               errorSemantico(ERR_INDICE_ENTERO, 0, 0, NULL);
                                                            else
                                                            {
                                                               $$.dbase = $1.dbase;
                                                            }
                                                         }
   ;
%%

Token opera(string op, Token izq, Token der)
{
	Token devolver;
   int tmp = NuevaTemporal();
   devolver.dir = tmp;
   stringstream ss;

   if (izq.tipo == ENTERO && der.tipo == ENTERO)
   {
      devolver.tipo = ENTERO;

      ss << "mov " << izq.dir << " A\n";
      ss << op << "i " << der.dir << "\n";
      ss << "mov A " << tmp << "\n";
   }
   else if (izq.tipo == REAL && der.tipo == ENTERO)
   {
      int tmpcnv = NuevaTemporal();
      devolver.tipo = REAL;
      
      ss << "mov " << der.dir << " A\n";
      ss << "itor\n";
      ss << "mov A " << tmpcnv << "\n";
      ss << "mov " << izq.dir << " A\n";
      ss << op << "r " << tmpcnv << "\n";
      ss << "mov A " << tmp << "\n";
   }
   else if (izq.tipo == ENTERO && der.tipo == REAL)
   {
      devolver.tipo = REAL;
      
      ss << "mov " << izq.dir << " A\n";
      ss << "itor\n";
      ss << op << "r " << der.dir << "\n";
      ss << "mov A " << tmp << "\n";
   }
   else
   { // REAL && REAL
      devolver.tipo = REAL;

      ss << "mov " << izq.dir << " A\n";
      ss << op << "r " << der.dir << "\n";
      ss << "mov A " << tmp << "\n";
   }

   devolver.cod = ss.str();
	
   return devolver;
}

void comprobarMemoriaVariables(Token token, int tam)
{
   if(C_VAR+tam > MAX_VAR_SPACE){
		errorSemantico(ERR_NOCABE, token.fila, token.columna, token.lexema);
	}

   C_VAR += tam;
}

bool esArray(int tipo)
{
   return tipo > LOGICO;
}

int NuevaTemporal()
{
   C_TEMP += 1;

   if(C_TEMP > MAX_TEMP_SPACE){
		char* e = 0;
		errorSemantico(ERR_MAXTMP, 0, 0, e);
	}
	return C_TEMP;
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