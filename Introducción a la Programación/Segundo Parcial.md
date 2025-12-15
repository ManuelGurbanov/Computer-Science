Nota: 8.07 / 10.0 (APROBADO)

puntaje ej1: 1.87
puntaje ej2: 0.2
puntaje ej3: 2
puntaje ej4: 2
puntaje ej5: 0.75
puntaje ej6: 0.75
puntaje ej7: 0.5



Parcial Python - Tema 2
Importante
El parcial se aprueba con 6 puntos
Utilizar este archivo fuente de base para la programación. Ya cuenta con los def y las signaturas correctas.
Lista de funciones permitidas aca
Para testear el código pueden usar este archivo que ya cuenta con todo lo necesario para desarrollar sus propios tests (este archivo no se entrega)
Para aprobar el parcial es requisito indispensable que todos los programas pasen los tests del archivo del punto anterior
1) Alerta Enfermedades Infecciosas (3 puntos)

Necesitamos detectar la aparición de posibles epidemias. Para esto contamos con un lista de enfermedades infecciosas y los registros de atención por guardia dados por una lista expedientes. Cada expediente es una tupla con ID paciente y enfermedad que motivó la atención. Debemos devolver un diccionario cuya clave son las enfermedades infecciosas y su valor es la proporción de pacientes que se atendieron por esa enfermedad. En este diccionario deben aparecer solo aquellas enfermedades infecciosas cuya proporción supere cierto umbral.
problema alarma_epidemiologica (registros: seq⟨ZxString⟩, infecciosas: seq⟨String⟩, umbral: R) : dict⟨String,R⟩ {
  requiere: {0 < umbral < 1}
  asegura: {claves de res pertenecen a infecciosas}
  asegura: {Para cada enfermedad perteneciente a infecciosas, si el porcentaje de pacientes que se atendieron por esa enfermedad sobre el total de registros es mayor o igual al umbral, entonces res[enfermedad] = porcentaje}
  asegura: {Para cada enfermedad perteneciente a infecciosas, si el porcentaje de pacientes que se atendieron por esa enfermedad sobre el total de registros es menor que el umbral, entonces enfermedad no aparece en res}
}

2) Orden de atención (1 punto)

Desde el Hospital Fernandez nos pidieron solucionar una serie de problemas relacionados con la información que maneja sobre los pacientes y el personal de salud. En primer lugar debemos resolver en qué orden se deben atender los pacientes que llegan a la guardia. En enfermería, hay una primera instancia que clasifica en dos colas a los pacientes: una urgente y otra postergable (esto se llama hacer triage). A partir de dichas colas que contienen la identificación del paciente, se pide devolver una nueva cola según la siguiente especificación.
problema orden_de_atencion ( in urgentes: cola⟨Z⟩, in postergables: cola⟨Z⟩) : cola⟨Z⟩ {
  requiere: {no hay elementos repetidos en urgentes}
  requiere: {no hay elementos repetidos en postergables}
  requiere: {la intersección entre postergables y urgentes es vacía}
  requiere: {|postergables| = |urgentes|}
  asegura: {no hay repetidos en res }
  asegura: {res es permutación de la concatenación de urgentes y postergables}
  asegura: {Si urgentes no es vacía, en tope de res hay un elemento de urgentes}
  asegura: {En res no hay dos seguidos de urgentes}
  asegura: {En res no hay dos seguidos de postergables}
  asegura: {Para todo c1 y c2 de tipo "urgente" pertenecientes a urgentes si c1 aparece antes que c2 en urgentes entonces c1 aparece antes que c2 en res}
  asegura: {Para todo c1 y c2 de tipo "postergable" pertenecientes a postergables si c1 aparece antes que c2 en postergables entonces c1 aparece antes que c2 en res}

3) Camas ocupadas en el hospital (2 puntos)
Queremos saber qué porcentaje de ocupación de camas hay en el hospital. El hospital se representa por una matriz en donde las filas son los pisos, y las columnas son las camas. Los valores de la matriz son booleanos que indican si la cama está ocupada o no. Si el valor es verdadero (True) indica que la cama está ocupada. Se nos pide programar en Python una función que devuelve una secuencia de reales, indicando la proporción de camas ocupadas en cada piso.
problema nivel_de_ocupacion(camas_por_piso:seq⟨seq⟨Bool⟩⟩) : seq⟨R⟩ {
  requiere: {Todos los pisos tienen la misma cantidad de camas.}
  requiere: {Hay por lo menos 1 piso en el hospital.}
  requiere: {Hay por lo menos una cama por piso.}
  asegura: {|res| = |camas_por_piso|}
  asegura: {Para todo 0<= i < |res| se cumple que res[i] es igual a la cantidad de camas ocupadas del piso i dividido el total de camas del piso i)}
}

4) Quiénes trabajaron más? (2 puntos)
Dado un diccionario con la cantidad de horas trabajadas por empleado, en donde la clave es el ID del empleado y el valor es una lista de las horas trabajadas por día, queremos saber quienes trabajaron más para darles un premio. Se deberá buscar la o las claves para la cual se tiene el máximo valor de cantidad total de horas, y devolverlas en una lista.
problema empleados_del_mes(horas:dicc⟨Z,seq⟨Z⟩⟩) : seq⟨Z⟩ {
  requiere: {No hay valores en horas que sean listas vacías}
  asegura: {Si ID pertence a res entonces ID pertence a las claves de horas}
  asegura: {Si ID pertenece a res, la suma de sus valores de horas es el máximo de la suma de elementos de horas de todos los otros IDs}
  asegura: {Para todo ID de claves de horas, si la suma de sus valores es el máximo de la suma de elementos de horas de los otros IDs, entonces ID pertences a res}
}

5) Preguntas teóricas (2 puntos)
Conteste marcando la opción correcta.

A) ¿Qué es una estructura de control en Python? (0.75 punto)
 Una herramienta que permite la ejecución condicional y repetitiva de bloques de código.
 Un tipo especial de variable que almacena datos complejos.
 Una librería utilizada para manipular archivos en el sistema operativo.

B) ¿Qué es una variable con 'scope local' en Python? (0.75 punto)
 Una variable definida fuera de cualquier función y accesible en todo el programa.
 Una variable definida dentro de una función, que solo puede ser utilizada dentro de esa función.
 Una variable que puede ser utilizada en cualquier módulo importado.

C) ¿Qué representa un nodo en un Control Flow Graph? (0.5 punto)
 Una variable utilizada en el programa.
 Una condición lógica o una instrucción en el código.
 Un archivo de datos externo.





Solucion entregada por el alumno
from queue import Queue as Cola

def alarma_epidemiologica (registros: list[tuple[int, str]], infecciosas: list[str], umbral: float) -> dict[str, float]:
  res:dict[str,float] = {}

  cantidad_enfermos_enfermedad:list[int] = [];

  while len(cantidad_enfermos_enfermedad) < len(infecciosas):
    cantidad_enfermos_enfermedad.append(0);

  for r in registros:
    enfermedad_actual:str = r[1];
    i_enf_actual = indice_enfermedad(enfermedad_actual, infecciosas)
    if i_enf_actual != -1:
      cantidad_enfermos_enfermedad[i_enf_actual] += 1;

  enfermos_totales:int = len(registros);

  for inf in infecciosas:
    su_pctg = (cantidad_enfermos_enfermedad[indice_enfermedad(inf, infecciosas)]  / enfermos_totales) * 100;

    if (su_pctg > umbral):
      res[inf] = su_pctg;
  return res;

#auxiliar
def indice_enfermedad(enf:str, infecciosas:list[str]):
  res:int = -1;

  for i in range(len(infecciosas)):
    if (enf == infecciosas[i]):
      res = i;
  return res;

#auxiliar
def copy_cola(entry:Cola) -> Cola:
  copy:Cola = Cola();
  temp:Cola = Cola();

  while not entry.empty():
    element = entry.get();
    copy.put(element);
    temp.put(element)

  while not temp.empty():
    entry.put(temp.get());
  return copy;


def orden_de_atencion (urgentes: Cola[int], postrgables: Cola[int]) -> Cola[int]:
  res:Cola = Cola();
  copy_urgentes:Cola = Cola();
  copy_postergables:Cola = Cola();
  copy_urgentes = copy_cola(urgentes);
  copy_postergables = copy_cola(postrgables);

  while not copy_urgentes.empty():
    res.put(copy_urgentes.get());
  while not copy_postergables.empty():
    res.put(copy_postergables.get());
  return res;



def nivel_de_ocupacion(camas_por_piso:list[list[bool]]) -> list[float]:
  res:list[float] = [];

  for i_fila in range(len(camas_por_piso)):

    ocupadasEnEstaFila:int = 0;
    porcentajeEnFila:float = 0;


    for valorCama in camas_por_piso[i_fila]:
      if (valorCama):
        ocupadasEnEstaFila += 1;

    porcentajeEnFila = ocupadasEnEstaFila / len(camas_por_piso[i_fila]);
    res.append(porcentajeEnFila);
  return res;


def empleados_del_mes(horas:dict[int, list[int]]) -> list[int]:

  res:list[int] = []
  maximas_horas:int = 0;

  for id_empleado in horas:
    suma:int = sumatoria(horas[id_empleado])
    if suma > maximas_horas:
      maximas_horas = suma
      res.clear();
      res.append(id_empleado);
    elif suma == maximas_horas:
      res.append(id_empleado);

  return res;

#auxiliar
def sumatoria(lista:list[int]) -> int:
  res:int=0
  for n in lista:
    res = res + n;
  return res;


Resultado de la compilacion

Ejecucion de los tests
tema2-test-ej1.py.compilacion.out
Puntaje del ej: 1.87 / 3

test_alarma_epidemiologica_4 (__main__.Ej1Test.test_alarma_epidemiologica_4) ... FAIL
test_alarma_epidemiologica_infecciosas_vacio (__main__.Ej1Test.test_alarma_epidemiologica_infecciosas_vacio) ... ok
test_alarma_epidemiologica_long_infeccionas_mas_larga (__main__.Ej1Test.test_alarma_epidemiologica_long_infeccionas_mas_larga) ... ok
test_caso_base (__main__.Ej1Test.test_caso_base) ... ok
test_nadie_supera_umbral (__main__.Ej1Test.test_nadie_supera_umbral) ... FAIL
test_ninguna_infecciosa_en_registros (__main__.Ej1Test.test_ninguna_infecciosa_en_registros) ... ok
test_sin_registros (__main__.Ej1Test.test_sin_registros) ... ERROR
test_todas_infecciosas_superan_umbral (__main__.Ej1Test.test_todas_infecciosas_superan_umbral) ... ok

======================================================================
ERROR: test_sin_registros (__main__.Ej1Test.test_sin_registros)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/tema2-test-ej1.py", line 76, in test_sin_registros
    self.assertEqual(alarma_epidemiologica(registros, infecciosas, umbral), esperado)
                     ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/solucion.py", line 20, in alarma_epidemiologica
    su_pctg = (cantidad_enfermos_enfermedad[indice_enfermedad(inf, infecciosas)]  / enfermos_totales) * 100;
               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~^~~~~~~~~~~~~~~~~~
ZeroDivisionError: division by zero

======================================================================
FAIL: test_alarma_epidemiologica_4 (__main__.Ej1Test.test_alarma_epidemiologica_4)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/tema2-test-ej1.py", line 96, in test_alarma_epidemiologica_4
    self.assertFloatDict(res, esperado)
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/tema2-test-ej1.py", line 33, in assertFloatDict
    self.assertEqual(a_dict.keys(), b_dict.keys())
AssertionError: dict_keys(['Viruela', 'Cólera', 'Fiebre amarilla']) != dict_keys(['Cólera', 'Viruela'])

======================================================================
FAIL: test_nadie_supera_umbral (__main__.Ej1Test.test_nadie_supera_umbral)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/tema2-test-ej1.py", line 62, in test_nadie_supera_umbral
    self.assertEqual(alarma_epidemiologica(registros, infecciosas, umbral), esperado)
AssertionError: {'gripe': 40.0, 'covid': 40.0, 'ebola': 20.0} != {}
- {'covid': 40.0, 'ebola': 20.0, 'gripe': 40.0}
+ {}

----------------------------------------------------------------------
Ran 8 tests in 0.001s

FAILED (failures=2, errors=1)

tema2-test-ej2.py.compilacion.out
Puntaje del ej: 0.2 / 1

test_caso_base_1 (__main__.Ej2Test.test_caso_base_1) ... ok
test_caso_base_2 (__main__.Ej2Test.test_caso_base_2) ... FAIL
test_elementos_3 (__main__.Ej2Test.test_elementos_3) ... FAIL
test_vacio (__main__.Ej2Test.test_vacio) ... ok

======================================================================
FAIL: test_caso_base_2 (__main__.Ej2Test.test_caso_base_2)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/tema2-test-ej2.py", line 51, in test_caso_base_2
    self.assertEqual(Ej2Test.cola_to_list(orden_de_atencion(urgentes, postergables)), Ej2Test.cola_to_list(esperado))
AssertionError: Lists differ: [1, -3, 2, -4] != [1, 2, -3, -4]

First differing element 1:
-3
2

- [1, -3, 2, -4]
?         ---

+ [1, 2, -3, -4]
?     +++


======================================================================
FAIL: test_elementos_3 (__main__.Ej2Test.test_elementos_3)
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/home/pablo/git/intro-programacion/1c2024/examen_online/correcciones/python-tm-comA/gurbanov_manuel_45689966/tema2-test-ej2.py", line 57, in test_elementos_3
    self.assertEqual(Ej2Test.cola_to_list(orden_de_atencion(urgentes, postergables)), Ej2Test.cola_to_list(esperado))
AssertionError: Lists differ: [7, -9, 11, 8, -10, 12] != [7, 8, -9, -10, 11, 12]

First differing element 1:
-9
8

- [7, -9, 11, 8, -10, 12]
+ [7, 8, -9, -10, 11, 12]

----------------------------------------------------------------------
Ran 4 tests in 0.001s

FAILED (failures=2)

tema2-test-ej3.py.compilacion.out
Puntaje del ej: 2 / 2

test_camas_mixtas (__main__.Ej3Test.test_camas_mixtas) ... ok
test_camas_mixtas_x6 (__main__.Ej3Test.test_camas_mixtas_x6) ... ok
test_caso_base (__main__.Ej3Test.test_caso_base) ... ok
test_niguna_cama_ocupada (__main__.Ej3Test.test_niguna_cama_ocupada) ... ok
test_nivel_de_ocupacion_mixtas_4 (__main__.Ej3Test.test_nivel_de_ocupacion_mixtas_4) ... ok
test_todas_camas_ocupadas (__main__.Ej3Test.test_todas_camas_ocupadas) ... ok

----------------------------------------------------------------------
Ran 6 tests in 0.000s

OK

tema2-test-ej4.py.compilacion.out
Puntaje del ej: 2 / 2

test_caso_base (__main__.Ej4Test.test_caso_base) ... ok
test_empleado_sin_horas_max (__main__.Ej4Test.test_empleado_sin_horas_max) ... ok
test_empleados_vacios (__main__.Ej4Test.test_empleados_vacios) ... ok
test_todos_iguales (__main__.Ej4Test.test_todos_iguales) ... ok
test_un_empleado (__main__.Ej4Test.test_un_empleado) ... ok
test_varios_empleados_max (__main__.Ej4Test.test_varios_empleados_max) ... ok

----------------------------------------------------------------------
Ran 6 tests in 0.000s

OK

mchoice.json-ej5.compilacion.out
Puntaje del ej: 0.75 / 0.75

mchoice ej5: respuesta del alumno=1, respuesta correcta=1

Ran 1 test in 0 seconds

OK

mchoice.json-ej6.compilacion.out
Puntaje del ej: 0.75 / 0.75

mchoice ej6: respuesta del alumno=2, respuesta correcta=2

Ran 1 test in 0 seconds

OK

mchoice.json-ej7.compilacion.out
Puntaje del ej: 0.5 / 0.5

mchoice ej7: respuesta del alumno=2, respuesta correcta=2

Ran 1 test in 0 seconds

OK

FIN



