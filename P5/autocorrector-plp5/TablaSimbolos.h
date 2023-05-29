

#ifndef _TablaSimbolos_
#define _TablaSimbolos_

#include <string>
#include <vector>

using namespace std;

const unsigned ENTERO=0;
const unsigned REAL=1;
const unsigned LOGICO=2;

struct Simbolo {

  string nombre;
  unsigned tipo;
  unsigned dir;
  unsigned tam;

  Simbolo(string nombre, unsigned tipo, unsigned dir, unsigned tam) : nombre(nombre), tipo(tipo), dir(dir), tam(tam) {}
};


class TablaSimbolos {

   public:
      TablaSimbolos *padre;
      vector<Simbolo> simbolos;
   
      TablaSimbolos(TablaSimbolos *padre);
      TablaSimbolos *getAmbitoAnterior() { return padre; }

      bool newSymb(Simbolo s);
      Simbolo* searchSymb(string nombre);
      Simbolo* buscarAmbito(string nombre);
};
   
#endif
