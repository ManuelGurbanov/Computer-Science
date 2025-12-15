package aed;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aed.estructuras.heap.Heap;
import aed.estructuras.heap.Heap.HandleHeap;

public class TestsPropios {

    private Berretacoin berretacoin;

    @Test
    public void testBloqueYTransacciones() {
        Transaccion[] txs = new Transaccion[] {
            new Transaccion(0, 0, 1, 1), // ID 0
            new Transaccion(1, 1, 2, 2), // ID 1
            new Transaccion(2, 2, 3, 3), // ID 2
            new Transaccion(3, 3, 1, 2), // ID 3
            new Transaccion(4, 1, 2, 1), // ID 4
            new Transaccion(5, 2, 3, 1)  // ID 5
        };

        Bloque bloque = new Bloque(txs);

        // Verificar que las transacciones se mantengan en orden de inserción
        Transaccion[] actualEnOrden = bloque.getTransaccionesArray();
        Transaccion[] esperadoEnOrden = Arrays.copyOf(txs, txs.length);
        assertArrayEquals(esperadoEnOrden, actualEnOrden,
            "Las transacciones deberían mantenerse en orden de inserción");

        // Eliminar una transacción por ID (ID 2 en este caso)
        bloque.eliminarTransaccionPorId(2); // elimina Transaccion con ID = 2 (monto 3)

        // Verificar que ya no esté en la lista, pero las demás sí
        Transaccion[] actualDespuesDeEliminar = bloque.getTransaccionesArray();

        assertEquals(5, actualDespuesDeEliminar.length,
            "Debe haber 5 transacciones después de eliminar una");

        // Verificar presencia de otras transacciones
        assertTrue(Arrays.asList(actualDespuesDeEliminar).contains(txs[0]),
            "Debe contener la transacción con ID 0");
        assertTrue(Arrays.asList(actualDespuesDeEliminar).contains(txs[1]),
            "Debe contener la transacción con ID 1");
        assertTrue(Arrays.asList(actualDespuesDeEliminar).contains(txs[3]),
            "Debe contener la transacción con ID 3");
        assertTrue(Arrays.asList(actualDespuesDeEliminar).contains(txs[4]),
            "Debe contener la transacción con ID 4");
        assertTrue(Arrays.asList(actualDespuesDeEliminar).contains(txs[5]),
            "Debe contener la transacción con ID 5");

        // Verificar que la transacción con ID 2 fue eliminada
        assertFalse(Arrays.asList(actualDespuesDeEliminar).contains(txs[2]),
            "No debe contener la transacción con ID 2");

        // Verificar que el heap sigue conteniendo las 6 transacciones (no afectado)
        assertEquals(6, bloque.heap().tamaño(),
            "El heap debería seguir teniendo las 6 transacciones originales");

        // Verificamos que la transacción con ID 2 todavía existe en el heap. Es correcto probar esto, puesto que sólo eliminamos en el heap cuando desencolamos (i.e. nos deshacemos de la raiz). Entonces cualquier uso que se le de a bloque que no sea desencolar, no elimina nada del heap.
        boolean estaEnHeap = false;
        for (int i = 0; i <= bloque.heap().tamaño() - 1; i++) {
            if (bloque.heap().obtenerValor(i).id() == 2) {
                estaEnHeap = true;
                break;
            }
        }

        assertTrue(estaEnHeap, "La transacción con ID 2 todavía debe estar en el heap");

        //Eliminamos la transaccion con ID dos del heap (porque es la raiz):
        bloque.heap().desencolar();
        assertEquals(5, bloque.heap().tamaño(),
            "El heap tiene una tx menos");
            
        estaEnHeap = false;
        for (int i = 0; i <= bloque.heap().tamaño() - 1; i++) {
            if (bloque.heap().obtenerValor(i).id() == 2) {
                estaEnHeap = true;
                break;
            }
        }
        assertFalse(estaEnHeap, "La transacción con ID 2 no está en el heap porque era la raiz y se desencoló");
    }


    @BeforeEach
    public void setUp() {
        berretacoin = new Berretacoin(20);
    }

    @Test
    public void maximoTenedorConMuchosEmpates() {
        for (int i = 1; i <= 5; i++) {
            Transaccion[] bloque = {
                new Transaccion(i, 0, i, 10)
            };
            berretacoin.agregarBloque(bloque);
        }

        assertEquals(1, berretacoin.maximoTenedor());
    }

    @Test
    public void hackeoHastaVaciarBloque() {
        Transaccion[] bloque = {
            new Transaccion(0, 0, 1, 5),
            new Transaccion(1, 1, 2, 3),
            new Transaccion(2, 2, 3, 2)
        };

        berretacoin.agregarBloque(bloque);

        berretacoin.hackearTx();
        assertEquals(2, berretacoin.txUltimoBloque().length);

        berretacoin.hackearTx();
        assertEquals(1, berretacoin.txUltimoBloque().length);

        berretacoin.hackearTx();
        assertEquals(0, berretacoin.txUltimoBloque().length);
    }

    @Test
    public void agregarBloqueConUsuariosAltos() {
        Transaccion[] bloque = {
            new Transaccion(0, 0, 10, 2),
            new Transaccion(1, 10, 9, 1),
            new Transaccion(2, 9, 8, 1),
        };

        berretacoin.agregarBloque(bloque);

        assertEquals(8, berretacoin.maximoTenedor());
        assertEquals(new Transaccion(0, 0, 10, 2), berretacoin.txMayorValorUltimoBloque());
    }

    @Test
    public void hackearConUnaSolaTransaccion() {
        Transaccion[] bloque = {
            new Transaccion(0, 0, 1, 5)
        };

        berretacoin.agregarBloque(bloque);
        berretacoin.hackearTx();

        assertEquals(0, berretacoin.txUltimoBloque().length);
    }

    @Test
    public void transaccionQueDejaSaldoEnCeroDebeSerValida() {
        Transaccion[] bloque = {
            new Transaccion(0, 0, 1, 1) // El creador tiene 1
        };

        berretacoin.agregarBloque(bloque);

        assertEquals(1, berretacoin.txUltimoBloque().length);
        assertEquals(1, berretacoin.maximoTenedor());
    }


}
