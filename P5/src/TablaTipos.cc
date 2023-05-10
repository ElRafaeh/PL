
#include "TablaTipos.h"

TablaTipos::TablaTipos()
{
  // inicializar con los tipos básicos
  
  unTipo b;
  
  b.clase = TIPOBASICO;
  b.tipoBase = ENTERO;  
  tipos.push_back(b);
  
  b.tipoBase = REAL;    
  tipos.push_back(b);

  b.tipoBase = LOGICO;  
  tipos.push_back(b);
}

unsigned TablaTipos::nuevoTipoArray(unsigned tam,unsigned tbase)
{
  unTipo a;
  
  a.clase = ARRAY;
  a.tamanyo = tam;
  a.tipoBase = tbase;

  tipos.push_back(a);
  return tipos.size()-1;
}

