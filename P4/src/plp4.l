/*------------------------------ ejemplo.l -------------------------------*/
Digito    [0-9]
Letra    [a-zA-Z]
LetraDigito   [0-9a-zA-Z]

%{
#include <string.h> 
#include <string>
#include <iostream>

using namespace std;

#include "comun.h"
#include "plp4.tab.h"

int columna = 1,
    fila = 1;

int findefichero = 0;

int ret(int token);  
// función que actualiza 'fila' y 'columna' y devuelve el token

void msgError(int nerror, int fila,int columna,const char *s);
// función para producir mensajes de error

%}

%x COMENTARIO

%%
%{
/* codigo local */
%}

"(*"                    	{columna += strlen(yytext);BEGIN(COMENTARIO);}
<COMENTARIO>[^/*\n]+    	{columna += strlen(yytext);}
<COMENTARIO>\n          	{fila++;columna=1;}
<COMENTARIO><<EOF>>     	{msgError(ERRLEXEOF,-1,-1,"");}
<COMENTARIO>"*)"        	{columna+=strlen(yytext); BEGIN(INITIAL);}
<COMENTARIO>[*/]        	{columna += strlen(yytext);}

" "     					{columna++;}
[\t]    					{columna++;}
[\n]						{fila++;columna=1;}

"("							{return ret(pari);}
")"							{return ret(pard);}

"*"							{return ret(opmd);}
"/"							{return ret(opmd);}
"//"						{return ret(opmd);}

"+"							{return ret(opas);}
"-"							{return ret(opas);}

"<"							{return ret(oprel);}
">"							{return ret(oprel);}
"<="						{return ret(oprel);}
">="						{return ret(oprel);}
"="							{return ret(oprel);}
"<>"						{return ret(oprel);}

";"							{return ret(pyc);}
":"							{return ret(dosp);}
","							{return ret(coma);}
":="						{return ret(asig);}
"var"						{return ret(var);}
"real"						{return ret(real);}
"entero"					{return ret(entero);}
"algoritmo"					{return ret(algoritmo);}
"blq"						{return ret(blq);}
"fblq"						{return ret(fblq);}
"funcion"					{return ret(funcion);}
"si"						{return ret(si);}
"entonces"					{return ret(entonces);}
"sino"						{return ret(sino);}
"fsi"						{return ret(fsi);}
"mientras"					{return ret(mientras);}
"hacer"						{return ret(hacer);}
"escribir"					{return ret(escribir);}

{Letra}({LetraDigito})*		{return ret(id);}
{Digito}+                   {return ret(nentero);}
{Digito}+(\.){Digito}+      {return ret(nreal);}
.							{msgError(ERRLEXICO,fila,columna,yytext);} 

%%

int yywrap(void) {findefichero=1; return 1;} /* para no tener que linkar con la libreria del lex */

int ret(int token)
{
   yylval.lexema=strdup(yytext);
   yylval.fila=fila;
   yylval.columna=columna;
   columna+=(strlen(yytext));
   return(token);
}