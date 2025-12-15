package aed;

import java.util.ArrayList;
import aed.estructuras.heap.Heap;
import aed.estructuras.heap.Heap.HandleHeap;

public class Berretacoin {
    private Bloque ultimoBloque;
    private Heap<Usuario> usuarios;
    private ArrayList<Heap.HandleHeap<Usuario>> handlesUsuarios;
    private int montosTotalesUltimoBloque;
    private int cantidadTransaccionesUltimoBloque;

    public Berretacoin(int n_usuarios) { //O(p)
        this.handlesUsuarios = new ArrayList<>(n_usuarios + 1);
        for (int i = 0; i <= n_usuarios; i++) { //O(p)
           this.handlesUsuarios.add(null); // llenar con nulls, fix del error que teniamos
        }

        Usuario[] arregloUsuarios = new Usuario[n_usuarios + 1]; //O(1)
        HandleHeap<Usuario>[] handles = new HandleHeap[n_usuarios + 1]; //O(1)

        for (int i = 0; i <= n_usuarios; i++) { //O(p)
            arregloUsuarios[i] = new Usuario(i); //O(1)
            if (i == 0) arregloUsuarios[i].actualizarSaldo(-1); //O(1)
        }

        this.usuarios = new Heap<>(arregloUsuarios, handles); //O(p)

        // Guardar los handles en la lista
        for (int i = 0; i <= n_usuarios; i++) { //O(p)
            handlesUsuarios.set(arregloUsuarios[i].id(), handles[i]); //O(1), llenamos todas las posiciones.
        }
    }

    public void agregarBloque(Transaccion[] transacciones){ //O(nb * log p)
        Bloque nuevoBloque = new Bloque(transacciones); //O(nb)
        ultimoBloque = nuevoBloque;
        int sumaMontos = 0;
        int cantidadValidas = 0;

        for (Transaccion transaccion : transacciones) { //O(nb)
            actualizarMonto(transaccion); //O(log p)
            // Contar solo si comprador != 0
            if (transaccion.id_comprador() != 0) {
                sumaMontos += transaccion.monto();
                cantidadValidas++;
            } //O(1)
        } //O(1)

        this.montosTotalesUltimoBloque = sumaMontos; //O(1)
        this.cantidadTransaccionesUltimoBloque = cantidadValidas; // O(1)
        //O(nb + nb*log p) = O(nb*log p)
    }


    public void actualizarMonto(Transaccion t) { //O(log p)
        int idComprador = t.id_comprador(); //O(1)
        int idVendedor = t.id_vendedor(); //O(1)
        int montoTransaccion = t.monto(); //O(1)

        // Acceso O(1) usando el ID como índice
        HandleHeap<Usuario> handleComprador = handlesUsuarios.get(idComprador); //O(1)
        HandleHeap<Usuario> handleVendedor = handlesUsuarios.get(idVendedor); //O(1)

        if (handleComprador == null || handleVendedor == null) { //O(1)
            System.err.println("Error: Transacción con ID de usuario inválido o inactivo: Comprador " + idComprador + ", Vendedor " + idVendedor); //O(1)
            return;
        }

        Usuario comprador = usuarios.obtenerValor(handleComprador); // O(1) con el método obtenerValor
        Usuario vendedor = usuarios.obtenerValor(handleVendedor); // O(1) con el método obtenerValor

        comprador.actualizarSaldo(-montoTransaccion); //O(1)
        vendedor.actualizarSaldo(montoTransaccion); //O(1)

        usuarios.actualizar(handleComprador); //O(log p)
        usuarios.actualizar(handleVendedor); //O(log p)
    }


    public Transaccion txMayorValorUltimoBloque(){ //O(1)
        Transaccion mayorTransaccion = ultimoBloque.heap().maximo(); // O(1) porque es un heap
        return mayorTransaccion;
    }

    public Transaccion[] txUltimoBloque() { //O(nb)
        return ultimoBloque.getTransaccionesArray(); //O(nb)
    }

    public int maximoTenedor(){
        return usuarios.maximo().id(); //O(1) ya que tengo usuarios de 0 a n, pero el usuario 0 no existe
    }

    public int montoMedioUltimoBloque() {
        if (cantidadTransaccionesUltimoBloque == 0) {
            return 0;
        }
        return montosTotalesUltimoBloque / cantidadTransaccionesUltimoBloque;
    }


    public void hackearTx() { // O(log nb + log P)
        Transaccion t = ultimoBloque.heap().desencolar(); // O(log nb)
        int idComprador = t.id_comprador(); // O(1)
        int idVendedor = t.id_vendedor(); // O(1)
        int idTx = t.id(); // O(1)
        int montoTransaccion = t.monto(); // O(1)

        HandleHeap<Usuario> handleComprador = handlesUsuarios.get(idComprador); // O(1)
        HandleHeap<Usuario> handleVendedor = handlesUsuarios.get(idVendedor); // O(1)
        Usuario comprador = usuarios.obtenerValor(handleComprador); // O(1) con el método obtenerValor
        Usuario vendedor = usuarios.obtenerValor(handleVendedor); // O(1) con el método obtenerValor

        ultimoBloque.eliminarTransaccionPorId(idTx); // O(1)

        comprador.actualizarSaldo(montoTransaccion); // O(1)
        // Actualizamos el arbol antes de actualizar el saldo vendedor para no romper el invariante del arbol.
        usuarios.actualizar(handleComprador); // O(log P)

        vendedor.actualizarSaldo(-montoTransaccion); // O(1)
        // Actualizamos el arbol antes de actualizar el saldo vendedor para no romper el invariante del arbol.
        usuarios.actualizar(handleVendedor); // O(log P)

        // Si el id es distinto de 0, descontar su aporte en el promedio.
        if (idComprador != 0) { //O(1)
            this.montosTotalesUltimoBloque -= montoTransaccion;
            this.cantidadTransaccionesUltimoBloque -= 1;
        }
    }

}
