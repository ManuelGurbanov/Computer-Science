package aed.estructuras.listaEnlazada;

public class ListaEnlazada<T> {
    private Nodo primero;
    private Nodo ultimo;
    private int tamano;

    // Usamos protected para que HandleLE pueda acceder
    protected class Nodo {
        protected Nodo anterior;
        protected Nodo siguiente;
        protected T valor;

        Nodo(T v) {
            this.anterior = null;
            this.siguiente = null;
            this.valor = v;
        }
    }

    // Definimos el HANDLE apra la lista enlazada
    // Clase HandleLE: permite acceso O(1) a un nodo específico
    public static class HandleLE<T> {
        // Necesita ser de tipo ListaEnlazada<T>.Nodo porque Nodo es una clase interna no estática de ListaEnlazada
        private ListaEnlazada<T>.Nodo nodoInterno;
         // Indica si el handle sigue siendo válido o lo invalidamos

        // Constructor: Solo el método de la lista enlazada debería crear Handles
        protected HandleLE(ListaEnlazada<T>.Nodo nodo) {
            this.nodoInterno = nodo;
        }
    }

    public ListaEnlazada() {
        this.primero = null;
        this.ultimo = null;
        this.tamano = 0;
    }

    public int longitud() {
        return this.tamano;
    }

    public void agregarAdelante(T elem) { //O(1) son todas asignaciones
        Nodo nuevo = new Nodo(elem);
        if (this.primero == null) {
            this.primero = nuevo;
            this.ultimo = nuevo;    
        } else{
            nuevo.siguiente = this.primero;
            nuevo.anterior = null;
            this.primero.anterior = nuevo;
            this.primero = nuevo;
        }      
        this.tamano++;
    }

    public void agregarAtras(T elem) { 
        Nodo nuevo = new Nodo(elem);
        if (this.primero == null) {
            this.primero = nuevo;
            this.ultimo = nuevo;
        } else{
            this.ultimo.siguiente = nuevo;
            nuevo.anterior = this.ultimo;
            this.ultimo = nuevo;
            
        }
        this.tamano++;
    }

    public HandleLE<T> agregarAtrasConHandle(T elem) { //O(1)
        Nodo nuevo = new Nodo(elem);
        if (this.primero == null) {
            this.primero = nuevo;
            this.ultimo = nuevo;
        } else {
            this.ultimo.siguiente = nuevo;
            nuevo.anterior = this.ultimo;
            this.ultimo = nuevo;
        }
        this.tamano++;
        return new HandleLE<>(nuevo); // Devolvemos el handle
    }

    public T obtener(int i) { //O(n)
        Nodo actual = this.primero;
        int j = 0;
        if (i >= this.tamano || i < 0) {
            return null;
        }
        while( i != j ){
            actual = actual.siguiente;
            j++;
        }
        return actual.valor;
        
    }

    public void eliminar(int i) { //O(n)
        if (i >= this.tamano || this.tamano <= 0) {
            return;
        } else if (this.tamano == 1) {
            this.primero = null;
            this.ultimo = null;
            this.tamano--;    
            return;
        } 
        else if(i == (this.tamano - 1)){
            this.ultimo = this.ultimo.anterior;
            this.ultimo.siguiente = null;
            this.tamano--;    
            return;
        } else if(i == 0){
            this.primero = this.primero.siguiente;
            this.primero.anterior = null;
            this.tamano--;
            return;
        } 

        Nodo actual = this.primero;
        for( int j = 0; j < i; j++ ){
            actual = actual.siguiente;
        }

       Nodo anterior  = actual.anterior;
       Nodo siguiente = actual.siguiente;

       anterior.siguiente = siguiente;
       siguiente.anterior = anterior;

       this.tamano--;

    }

    public void eliminar(HandleLE<T> handle) { //O(1)
        if (handle == null) { //O(1)
            return; // Handle inválido
        }

        Nodo nodoAEliminar = handle.nodoInterno; //O(1)

        // Si el nodo es el primero
        if (nodoAEliminar.anterior == null) {
            this.primero = nodoAEliminar.siguiente;
        } else {
            nodoAEliminar.anterior.siguiente = nodoAEliminar.siguiente;
        } //O(1)

        // Si el nodo es el ultimo
        if (nodoAEliminar.siguiente == null) {
            this.ultimo = nodoAEliminar.anterior;
        } else {
            nodoAEliminar.siguiente.anterior = nodoAEliminar.anterior;
        } //O(1)

        // Si la lista queda vacía
        if (this.tamano == 1) { // Caso especial: era el único elemento
            this.primero = null;
            this.ultimo = null;
        } //O(1)

        this.tamano--;
    }

    public Iterador<T> iterador() { //O(1)
        return new ListaIterador();
    }

    private class ListaIterador implements Iterador<T> {
        private Nodo anterior;
        private Nodo siguiente;
        
        public ListaIterador() { //O(1)
            this.anterior = null;
            this.siguiente = ListaEnlazada.this.primero;
        }

        public boolean haySiguiente() { //O(1)
	        return siguiente != null;
        }
        
        public boolean hayAnterior() { //O(1)
	        return anterior != null;
        }

        public T siguiente() { //O(1)
            T valor = siguiente.valor;

            this.anterior = this.siguiente;
            this.siguiente = this.siguiente.siguiente;

            return valor;
        }
        

        public T anterior() { //O(1)
            T valor = anterior.valor;
            
            this.siguiente = this.anterior;
            this.anterior = this.anterior.anterior;

            return valor;
        }
    }

}

