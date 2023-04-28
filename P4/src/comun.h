/*----------------------- comun.h -----------------------------*/
#include <string>
/* fichero con definciones comunes para los ficheros .l y .y */


typedef struct {
   char *lexema;
   int fila,columna;
   int tipo;
   string trad;
   string lid_trad;
   string pos_lid_trad;
   string prefijo;
} Token;

#define YYSTYPE Token


#define ERRLEXICO    1
#define ERRSINT      2
#define ERREOF       3
#define ERRLEXEOF    4

void msgError(int nerror,int fila,int columna,const char *s);