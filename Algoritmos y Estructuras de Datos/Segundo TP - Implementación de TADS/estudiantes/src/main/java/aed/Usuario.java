package aed;

public class Usuario implements Comparable<Usuario> {
    private int id;
    private int saldo;

    public Usuario(int id){ //O(1)
        this.id = id;
        this.saldo = 0;
    }

    public Usuario(int id, int saldo){ //O(1)
        this.id = id;
        this.saldo = saldo >= 0 ? saldo : 0; 
    }

    public void actualizarSaldo(int s){ //O(1)
        this.saldo += s;
    }

    public int id(){ //O(1)
        return this.id;
    }

    public int saldo(){ //O(1)
        return this.saldo;
    }


    @Override
    public boolean equals(Object otro){ //O(1)
        if (this == otro) return true; // Misma referencia, son iguales
        if (otro == null || getClass() != otro.getClass()) return false;

        Usuario that = (Usuario) otro;
        return this.id == that.id;
    }

    @Override
    public int compareTo(Usuario otro) { //O(1)
        if (this.saldo != otro.saldo) {
            return Integer.compare(this.saldo, otro.saldo); // mayor saldo primero
        } else {
            return Integer.compare(otro.id, this.id);
        }
    }

}
