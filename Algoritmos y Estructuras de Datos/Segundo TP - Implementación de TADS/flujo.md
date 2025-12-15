
## LEER DESDE GITHUB

 Vamos a ver **paso a paso** cómo se construye un `Heap<Integer>` a partir de un array de 5 enteros, con atención especial a cómo se usan los `Handle`.

---

## 🎯 Escenario

Queremos construir un `Heap<Integer>` con el siguiente arreglo:

```java
Integer[] arr = {4, 1, 3, 2, 5};
Heap<Integer> heap = new Heap<>(arr);
```

---

## 🧱 Paso 1: Crear nodos y handles

En el constructor `Heap(T[] secuencia)`, el heap comienza como una lista vacía:

```java
heap = new ArrayList<>();
```

Luego se recorre el array y se agrega cada número como un `Nodo`:

| Índice | Valor `T` | Handle `refInterna` | Nodo almacenado en heap |
| ------ | --------- | ------------------- | ----------------------- |
| 0      | 4         | 0                   | `Nodo(4, Handle(0))`    |
| 1      | 1         | 1                   | `Nodo(1, Handle(1))`    |
| 2      | 3         | 2                   | `Nodo(3, Handle(2))`    |
| 3      | 2         | 3                   | `Nodo(2, Handle(3))`    |
| 4      | 5         | 4                   | `Nodo(5, Handle(4))`    |

---

## 📉 Paso 2: Aplicar heapify (algoritmo de Floyd)

Comenzamos desde el primer nodo que **tiene hijos**:

```java
for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
    bajar(i);
}
```

El índice inicial es `(5 / 2) - 1 = 1`. Así que bajamos desde el índice 1 hacia atrás.

---

### 🔽 Iteración 1: `bajar(1)`

* Nodo en posición 1: **1**
* Hijo izquierdo: índice 3 → **2**
* Hijo derecho: índice 4 → **5**

El mayor hijo es **5**, así que intercambiamos 1 ↔ 5

✔️ `intercambiar(1, 4)`
✔️ Se actualizan los `Handle`:

* `Handle(1)` pasa a `refInterna = 4`
* `Handle(4)` pasa a `refInterna = 1`

**Heap (valores) después de intercambiar:**

```
[4, 5, 3, 2, 1]
```

**Handles actualizados:**

| Valor | Índice en heap | refInterna |
| ----- | -------------- | ---------- |
| 4     | 0              | 0          |
| 5     | 1              | 1          |
| 3     | 2              | 2          |
| 2     | 3              | 3          |
| 1     | 4              | 4          |

---

### 🔽 Iteración 2: `bajar(0)`

* Nodo en posición 0: **4**
* Hijos: índice 1 → **5**, índice 2 → **3**

El mayor es **5**, así que intercambiamos 4 ↔ 5

✔️ `intercambiar(0, 1)`
✔️ `Handle(0)` pasa a `refInterna = 1`
✔️ `Handle(1)` pasa a `refInterna = 0`

**Heap final (valores):**

```
[5, 4, 3, 2, 1]
```

**Handles finales:**

| Valor | Índice en heap | refInterna |
| ----- | -------------- | ---------- |
| 5     | 0              | 0          |
| 4     | 1              | 1          |
| 3     | 2              | 2          |
| 2     | 3              | 3          |
| 1     | 4              | 4          |

---

## 🧠 ¿Qué hizo el handle?

Cada `Handle<T, Integer>` guarda una referencia **actualizada** a la posición del elemento en el `ArrayList`. Esto permite:

* Acceder rápidamente al nodo correspondiente.
* Llamar a `actualizar(handle)` para subir o bajar el nodo en el heap si cambia internamente (por ejemplo, en un `Usuario` cuyo saldo cambió).
* Evitar recorrer todo el heap para encontrarlo.

---

## 🧪 Extra: Visualización como árbol

```
          5 (índice 0)
        /   \
      4(1)  3(2)
     /   \
   2(3)  1(4)
```

---

¿Querés que ahora simulemos lo que pasa si `encolar()` un nuevo número, o cómo sería `actualizar()` si cambiara el valor de algún nodo?
