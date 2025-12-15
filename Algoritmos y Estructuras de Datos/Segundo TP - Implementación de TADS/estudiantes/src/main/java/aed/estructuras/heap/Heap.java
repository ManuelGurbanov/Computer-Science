package aed.estructuras.heap;

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
    private ArrayList<Nodo> heap;

    private class Nodo { 
        T valor;
        HandleHeap<T> handle;

        Nodo(T valor, HandleHeap<T> handle) { //O(1)
            this.valor = valor;
            this.handle = handle;
        }
    }

    // Definimos el handle para el heap
    public static class HandleHeap<T> { //O(1)
        private Integer refInterna;

        public HandleHeap(int refInterna) {
            this.refInterna = refInterna;
        }
    }

    private int indicePadre(int i) { //O(1)
        return (i - 1) / 2;
    }

    private int indiceHijoIzq(int i) { //O(1)
        return 2 * i + 1;
    }

    private int indiceHijoDer(int i) { //O(1)
        return 2 * i + 2;
    }

    public Heap(T[] secuencia) {
        this.heap = new ArrayList<>();
        for (int i = 0; i < secuencia.length; i++) {
            HandleHeap<T> handle = new HandleHeap<>(i);
            heap.add(new Nodo(secuencia[i], handle));
        }

        for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
            bajar(i);
        }
    }

    public Heap(T[] secuencia, HandleHeap<T>[] handlesExternos) { //Heap con algoritmo de Floyd, O(p) porque le mandamos los usuarios
        this.heap = new ArrayList<>(secuencia.length); //0(1)
        for (int i = 0; i < secuencia.length; i++) { 
            HandleHeap<T> handle = new HandleHeap<>(i); //O(1)
            heap.add(new Nodo(secuencia[i], handle)); //O(1)
            handlesExternos[i] = handle; //O(1)
        }

        for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
            bajar(i);
        } 
    }

    private void bajar(int i) { //O(log n)
        int hijoIzq = indiceHijoIzq(i); //O(1)
        int hijoDer = indiceHijoDer(i); //O(1)
        int mayor = i; //O(1)

        if (hijoIzq < heap.size() &&
                heap.get(hijoIzq).valor.compareTo(heap.get(mayor).valor) > 0) {
            mayor = hijoIzq;
        } //O(1)

        if (hijoDer < heap.size() &&
                heap.get(hijoDer).valor.compareTo(heap.get(mayor).valor) > 0) {
            mayor = hijoDer;
        } //O(1)

        if (mayor != i) { //O(log n)
            intercambiar(i, mayor); //O(1)
            bajar(mayor); //O(log n)
        }
    }

    private void intercambiar(int i, int j) { //O(1)
        Nodo ni = heap.get(i); //O(1)
        Nodo nj = heap.get(j); //O(1)

        heap.set(i, nj); //O(1)
        heap.set(j, ni); //O(1)

        ni.handle.refInterna = j; //O(1)
        nj.handle.refInterna = i; //O(1)
    }

    private void subir(int i) {
        while (i > 0 && heap.get(i).valor.compareTo(heap.get(indicePadre(i)).valor) > 0) { //O(log p)
            intercambiar(i, indicePadre(i));
            i = indicePadre(i);
        }
    }

    public HandleHeap<T> encolar(T elemento) { //O(log n), no lo usamos
        HandleHeap<T> handle = new HandleHeap<>(heap.size()); //O(1)
        Nodo nodo = new Nodo(elemento, handle); //O(1)
        heap.add(nodo); //O(1)
        subir(heap.size() - 1);
        return handle;
    }

    public T desencolar() { //O(log nb)
        if (estaVacio()) { //O(1)
            throw new IllegalStateException("El heap está vacío.");
        }
        T max = heap.get(0).valor; //O(1)
        int ultimo = heap.size() - 1; //O(1)
        intercambiar(0, ultimo); //O(1)
        //remove es O(1) porque sabemos que estamos eliminando el último elemento, que es la hoja mas a la derecha del heap.
        heap.remove(ultimo); //O(1)

        if (!estaVacio()) { //O(Log nb)
            bajar(0);
        }
        return max;
    }

    public T maximo() { //O(1)
        return this.heap.get(0).valor;
    }

    public boolean estaVacio() { //O(1)
        return heap.isEmpty();
    }

    public int tamaño() { //O(1)
        return heap.size();
    }

    public T obtenerValor(int i) { 
        return heap.get(i).valor;
    }

    public T obtenerValor(HandleHeap<T> handle) { // O(1)
        if (handle == null) {
            throw new NullPointerException();
        }
        return heap.get(handle.refInterna).valor; //O(1) porque es un arrayList y accedemos a una posicion.
    }

    public void actualizar(HandleHeap<T> handle) { //O(log p)
        int i = handle.refInterna; //O(1)
        bajar(i); //O(log p)
        subir(i); //O(log p)
    }

    public ArrayList<Nodo> heap(){
        return this.heap;
    }
}
