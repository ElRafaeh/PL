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
#include "plp5.tab.h"

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

"//"                    	{columna += strlen(yytext);BEGIN(COMENTARIO);}
<COMENTARIO>[^/*\n]+    	{columna += strlen(yytext);}
<COMENTARIO>\n          	{fila++;columna=1;BEGIN(INITIAL);}
<COMENTARIO>[*/]        	{columna += strlen(yytext);}

" "     					{columna++;}
[\t]    					{columna++;}
[\n]						{fila++;columna=1;}

"algoritmo"                 {return ret(algoritmo);}
"falgoritmo"                {return ret(falgoritmo);}
"var"						{return ret(var);}
"fvar"						{return ret(fvar);}
"entero"					{return ret(entero);}
"real"						{return ret(real);}
"logico"                    {return ret(logico);}
"tabla"                     {return ret(tabla);}
"de"                        {return ret(de);}
"escribe"                   {return ret(escribe);}
"lee"                       {return ret(lee);}
"si"                        {return ret(si);}
"entonces"					{return ret(entonces);}
"sino"						{return ret(sino);}
"mientras"					{return ret(mientras);}
"hacer"						{return ret(hacer);}
"repetir"				    {return ret(repetir);}
"hasta" 				    {return ret(hasta);}
"blq"						{return ret(blq);}
"fblq"						{return ret(fblq);}
"cierto"					{return ret(cierto);}
"falso"						{return ret(falso);}

{Letra}({LetraDigito})*		{return ret(id);}
{Digito}+                   {return ret(nentero);}
{Digito}+(\.){Digito}+      {return ret(nreal);}

","                         {return ret(coma);}
";"							{return ret(pyc);}
":"					        {return ret(dospto);}

"("							{return ret(pari);}
")"							{return ret(pard);}

"="							{return ret(oprel);}
"<>"						{return ret(oprel);}
"<"							{return ret(oprel);}
"<="						{return ret(oprel);}
">"							{return ret(oprel);}
">="						{return ret(oprel);}

"+"							{return ret(opas);}
"-"							{return ret(opas);}

"*"							{return ret(opmd);}
"/"							{return ret(opmd);}
":="						{return ret(opasig);}

"["						    {return ret(cori);}
"]"						    {return ret(cord);}

"&&"                        {return ret(ybool);}
"||"                        {return ret(obool);}
"!"                         {return ret(nobool);}

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