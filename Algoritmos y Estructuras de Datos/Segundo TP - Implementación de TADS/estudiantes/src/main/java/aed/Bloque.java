package aed;

import aed.estructuras.heap.Heap;
import aed.estructuras.listaEnlazada.Iterador;
import aed.estructuras.listaEnlazada.ListaEnlazada;
import java.util.ArrayList;

public class Bloque {
    private Heap<Transaccion> transaccionesPorMonto; // Heap de transacciones por monto
    private ListaEnlazada<Transaccion> transaccionesEnOrden; // ListaEnlazada en lugar de array para mantener el orden de inserción/otros órdenes
    private ArrayList<ListaEnlazada.HandleLE<Transaccion>> transaccionHandles;

    public Bloque(Transaccion[] t) { //O(nb)
        this.transaccionesPorMonto = new Heap<>(t);  //O(nb)
        this.transaccionesEnOrden = new ListaEnlazada<>(); // O(1)
        
        // Esto es un fix para el caso de test que el ID de transacción no empieza en 0.
        // Por alguna razón solo tomando el ID máximo de las transacciones no funciona,
        // aunque se supone que los IDs son seguidos, siempre el último debería ser el mayor.

        //Calcular máximo ID de transacción
        int maxIdTx = 0; //O(1)
        for (Transaccion tx : t) { //O(nb)
            if (tx.id() > maxIdTx) {
                maxIdTx = tx.id();
            }
        }
        
        //Crear la lista
        this.transaccionHandles = new ArrayList<>(maxIdTx + 1);
        
        // Rellenar con nulls
        for (int i = 0; i <= maxIdTx; i++) {
            this.transaccionHandles.add(null);
        }
        
        //Insertar handles en el índice del ID de la transacción
        for (Transaccion tx : t) {
            ListaEnlazada.HandleLE<Transaccion> listaHandle = transaccionesEnOrden.agregarAtrasConHandle(tx);
            tx.setHandleEnLista(listaHandle);
            this.transaccionHandles.set(tx.id(), listaHandle);
        }
    }


    public Heap<Transaccion> heap() {  //O(1)
        return transaccionesPorMonto;
    }

    public Transaccion[] getTransaccionesArray() { // O(nb)
        Iterador<Transaccion> iterador = transaccionesEnOrden.iterador(); // O(1)
        
        Transaccion[] res = new Transaccion[transaccionesEnOrden.longitud()]; // O(1)
        int index = 0; // O(1)

        while (iterador.haySiguiente()) { // O (nb)
            Transaccion actual = iterador.siguiente(); // O(1)
            res[index] = actual; // O(1)
            index++; // O(1)
        }

        return res;
    }

    public void eliminarTransaccionPorId(int id) { // O(1)
        ListaEnlazada.HandleLE<Transaccion> handle = transaccionHandles.get(id); // O (1)
        transaccionesEnOrden.eliminar(handle); // O(1) por que se elimina con handle.
    }
}