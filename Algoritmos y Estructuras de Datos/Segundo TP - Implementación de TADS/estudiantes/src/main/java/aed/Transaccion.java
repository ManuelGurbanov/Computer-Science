package aed;

import aed.estructuras.listaEnlazada.ListaEnlazada;
import aed.estructuras.heap.Heap.HandleHeap;

public class Transaccion implements Comparable<Transaccion> {
    private int id;
    private int id_comprador;
    private int id_vendedor;
    private int monto;
    
    private ListaEnlazada.HandleLE<Transaccion> handleEnLista;
    private HandleHeap<Transaccion> handleEnHeap;

    public Transaccion(int id, int id_comprador, int id_vendedor, int monto) { //O(1)
        this.id = id;
        this.id_comprador = id_comprador;
        this.id_vendedor = id_vendedor;
        this.monto = monto;
        this.handleEnLista = null; // inicializamos heap a null
        this.handleEnHeap = null;
    }

    public int id() { //O(1)
        return id;
    }

    public int monto() { //O(1)
        return monto;
    }

    public int id_comprador() { //O(1)
        return id_comprador;
    }

    public int id_vendedor() { //O(1)
        return id_vendedor;
    }

    public ListaEnlazada.HandleLE<Transaccion> getHandleEnLista() { //O(1)
        return handleEnLista;
    }

    public void setHandleEnLista(ListaEnlazada.HandleLE<Transaccion> handle) { //O(1)
        this.handleEnLista = handle;
    }

    public HandleHeap<Transaccion> getHandleEnHeap() { //O(1)
        return handleEnHeap;
    }

    public void setHandleEnHeap(HandleHeap<Transaccion> handle) { //O(1)
        this.handleEnHeap = handle;
    }

    @Override
    public int compareTo(Transaccion otro) { //O(1)
        if (this.monto != otro.monto) {
            return Integer.compare(this.monto, otro.monto);
        } else {
            // Se usa el ID como desempate para que transacciones con el mismo monto
            return Integer.compare(this.id, otro.id);
        }
    }


    @Override
    public boolean equals(Object otro){ //O(1)
        if (this == otro) return true; // Si es la misma referencia, es el mismo
        if (otro == null || getClass() != otro.getClass()) return false;
        
        Transaccion that = (Transaccion) otro;
        return this.id == that.id;
    }
}