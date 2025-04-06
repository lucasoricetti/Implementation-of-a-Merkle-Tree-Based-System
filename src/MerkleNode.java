/**
 * Rappresenta un nodo di un albero di Merkle.
 * 
 * @author Luca Tesei, Marco Caputo (template) Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 */
public class MerkleNode {
    private final String hash; // Hash associato al nodo.

    private final MerkleNode left; // Figlio sinistro del nodo.

    private final MerkleNode right; // Figlio destro del nodo.

    /**
     * Costruisce un nodo Merkle foglia con un valore di hash, quindi,
     * corrispondente all'hash di un dato.
     *
     * @param hash
     *                 l'hash associato al nodo.
     */
    public MerkleNode(String hash) {
        this(hash, null, null);
    }

    /**
     * Costruisce un nodo Merkle con un valore di hash e due figli, quindi,
     * corrispondente all'hash di un branch.
     *
     * @param hash
     *                  l'hash associato al nodo.
     * @param left
     *                  il figlio sinistro.
     * @param right
     *                  il figlio destro.
     */
    public MerkleNode(String hash, MerkleNode left, MerkleNode right) {
        this.hash = hash;
        this.left = left;
        this.right = right;
    }

    /**
     * Restituisce l'hash associato al nodo.
     *
     * @return l'hash associato al nodo.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Restituisce il figlio sinistro del nodo.
     *
     * @return il figlio sinistro del nodo.
     */
    public MerkleNode getLeft() {
        return left;
    }

    /**
     * Restituisce il figlio destro del nodo.
     *
     * @return il figlio destro del nodo.
     */
    public MerkleNode getRight() {
        return right;
    }

    /**
     * Restituisce true se il nodo è una foglia, false altrimenti.
     *
     * @return true se il nodo è una foglia, false altrimenti.
     */
    public boolean isLeaf() {
        // Se il Nodo this non ha figli di sinistra è una foglia
        // (il figlio destro può essere null nel caso in cui this ha un solo figlio)
        if (this.left == null) return true;
        return false;
    }

    @Override
    public String toString() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // Due nodi sono uguali se hanno lo stesso hash

        // Controllo se puntano allo stesso oggetto in memoria
        if (this == obj) return true;
        // Controllo se obj è un oggetto null
        if (obj == null) return false;
        // Controllo se obj non è Istanza di MerkleNode
        if (!(obj instanceof MerkleNode)) return false;
        // obj è Istanza di MerkleNode, quindi faccio il cast
        MerkleNode other = (MerkleNode) obj;
        // Il metodo equals che uso è quello definito nella classe String
        if (this.hash.equals(other.getHash())) return true; 
        return false;
    }

    @Override
    public int hashCode() {
        /* implementare in accordo a equals */

        // Il metodo hashCode che uso è quello definito nella classe String
        return this.hash.hashCode();
    }
}
