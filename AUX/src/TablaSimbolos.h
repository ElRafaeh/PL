
#ifndef _TablaSimbolos_
#define _TablaSimbolos_

#include <string>
#include <vector>

using namespace std;

const int ENTERO=1;
const int REAL=2;
const int FUNCION=3;

struct Simbolo {

  string nombre;
  int tipo;
  string nomtrad;
};


class TablaSimbolos {

   public:
   
      TablaSimbolos *padre;
      vector<Simbolo> simbolos;
   
      TablaSimbolos(TablaSimbolos *padre);
      TablaSimbolos *getAmbitoAnterior() { return padre; }
   
      Simbolo *buscarAmbito(string nombre); // ver si está en el ámbito actual
      bool nuevoSimbolo(Simbolo s);
      Simbolo* buscar(string nombre);
};


#endif
