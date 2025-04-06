/**
 * Una classe che rappresenta una prova di Merkle per un determinato albero di
 * Merkle ed un suo elemento o branch. Oggetti di questa classe rappresentano un
 * proccesso di verifica auto-contenuto, dato da una sequenza di oggetti
 * MerkleProofHash che rappresentano i passaggi necessari per validare un dato
 * elemento o branch in un albero di Merkle decisi al momento di costruzione
 * della prova.
 * 
 * @author Luca Tesei, Marco Caputo (template) Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 */
public class MerkleProof {

    /**
     * La prova di Merkle, rappresentata come una lista concatenata di oggetti
     * MerkleProofHash.
     */
    private final HashLinkedList<MerkleProofHash> proof;

    /**
     * L'hash della radice dell'albero di Merkle per il quale la prova è stata
     * costruita.
     */
    private final String rootHash;

    /**
     * Lunghezza massima della prova, dato dal numero di hash che la compongono
     * quando completa. Serve ad evitare che la prova venga modificata una volta
     * che essa sia stata completamente costruita.
     */
    private final int length;

    /**
     * Costruisce una nuova prova di Merkle per un dato albero di Merkle,
     * specificando la radice dell'albero e la lunghezza massima della prova. La
     * lunghezza massima della prova è il numero di hash che la compongono
     * quando completa, oltre il quale non è possibile aggiungere altri hash.
     *
     * @param rootHash
     *                     l'hash della radice dell'albero di Merkle.
     * @param length
     *                     la lunghezza massima della prova.
     */
    public MerkleProof(String rootHash, int length) {
        if (rootHash == null)
            throw new IllegalArgumentException("The root hash is null");
        this.proof = new HashLinkedList<>();
        this.rootHash = rootHash;
        this.length = length;
    }

    /**
     * Restituisce la massima lunghezza della prova, dato dal numero di hash che
     * la compongono quando completa.
     *
     * @return la massima lunghezza della prova.
     */
    public int getLength() {
        return length;
    }

    /**
     * Aggiunge un hash alla prova di Merkle, specificando se esso dovrebbe
     * essere concatenato a sinistra o a destra durante la verifica della prova.
     * Se la prova è già completa, ovvero ha già raggiunto il massimo numero di
     * hash deciso alla sua costruzione, l'hash non viene aggiunto e la funzione
     * restituisce false.
     *
     * @param hash
     *                   l'hash da aggiungere alla prova.
     * @param isLeft
     *                   true se l'hash dovrebbe essere concatenato a sinistra,
     *                   false altrimenti.
     * @return true se l'hash è stato aggiunto con successo, false altrimenti.
     */
    public boolean addHash(String hash, boolean isLeft) {
        // Gli hash non possono essere nulli
        if (hash == null) throw new IllegalArgumentException("un hash non può essere nullo");
        // Controllo se la prova ha già raggiunto il numero massimo di hash
        if (this.proof.getSize() == this.getLength()) return false;
        // Sono sicuro che posso aggiungere un Nodo MerkleProofHash alla Lista 'proof'
        this.proof.addAtTail(new MerkleProofHash(hash, isLeft));
        // L'hash è stato aggiunto con successo alla proof
        return true; 
    }

    /**
     * Rappresenta un singolo step di una prova di Merkle per la validazione di
     * un dato elemento.
     */
    public static class MerkleProofHash {
        /**
         * L'hash dell'oggetto.
         */
        private final String hash;

        /**
         * Indica se l'hash dell'oggetto dovrebbe essere concatenato a sinistra
         * durante la verifica della prova.
         */
        private final boolean isLeft;

        public MerkleProofHash(String hash, boolean isLeft) {
            if (hash == null)
                throw new IllegalArgumentException("The hash cannot be null");

            this.hash = hash;
            this.isLeft = isLeft;
        }

        /**
         * Restituisce l'hash dell'oggetto MerkleProofHash.
         *
         * @return l'hash dell'oggetto MerkleProofHash.
         */
        public String getHash() {
            return hash;
        }

        /**
         * Restituisce true se, durante la verifica della prova, l'hash
         * dell'oggetto dovrebbe essere concatenato a sinistra, false
         * altrimenti.
         *
         * @return true se l'hash dell'oggetto dovrebbe essere concatenato a
         *         sinistra, false altrimenti.
         */
        public boolean isLeft() {
            return isLeft;
        }

        @Override
        public boolean equals(Object obj) {
            /*
             * Due MerkleProofHash sono uguali se hanno lo stesso hash e lo
             * stesso flag isLeft
            */

            // Controllo se puntano allo stesso oggetto in memoria
            if (this == obj) return true;
            // Controllo se obj è un oggetto null
            if (obj == null) return false;
            // Controllo se obj non è Istanza di MerkleProofHash
            if (!(obj instanceof MerkleProofHash)) return false;
            // obj è Istanza di MerkleProofHash, quindi faccio il cast
            MerkleProofHash other = (MerkleProofHash) obj;
            // Il metodo equals che uso è quello definito nella classe String,
            // inoltre confronto che sia uguale anche il valore booleano isLeft
            if (this.hash.equals(other.getHash()) && this.isLeft == other.isLeft()) return true; 
            return false;
        }

        @Override
        public String toString() {
            return hash + (isLeft ? "L" : "R");
        }

        @Override
        public int hashCode() {
            // Inizializzo un numero primo adatto per ridurre le collisioni degli hashCode
            final int prime = 31;
            // Inizializzo il Risultato
            int result = 1;
            // Calcolo il risultato
            result = prime * result + hash.hashCode();
            result = prime * result + (isLeft ? 1 : 0);
            return result;
        }
    }

    /**
     * Valida un dato elemento per questa prova di Merkle. La verifica avviene
     * combinando l'hash del dato con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, e controllando che l'hash finale coincida con quello
     * del nodo radice dell'albero di Merkle orginale.
     *
     * @param data
     *                 l'elemento da validare.
     * @return true se il dato è valido secondo la prova; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se il dato è null.
     */
    public boolean proveValidityOfData(Object data) {
        // i data non possono essere nulli
        if (data == null) throw new IllegalArgumentException("un dato non può essere null");
        // calcolo l'hash del dato fornito e ne faccio la validazione
        return validateHash(HashUtil.dataToHash(data));
    }

    /**
     * Valida un dato branch per questa prova di Merkle. La verifica avviene
     * combinando l'hash del branch con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, e controllando che l'hash finale coincida con quello
     * del nodo radice dell'albero di Merkle orginale.
     *
     * @param branch
     *                   il branch da validare.
     * @return true se il branch è valido secondo la prova; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se il branch è null.
     */
    public boolean proveValidityOfBranch(MerkleNode branch) {
        // i branch non possono essere nulli
        if (branch == null) throw new IllegalArgumentException("un branch non può essere null");
        // prendo l'hash del branch fornito e ne faccio la validazione
        return validateHash(branch.getHash());
    }

    /**
     * Metodo privato che itera sui nodi (di tipo MerkleProofHash) della lista concatenata 'proof'
     * al fine di combinare l'hash iniziale con gli hash dei nodi della prova 
     * seguendo l'ordine e la posizione (sinistra o destra) di ciascun nodo, 
     * fino a ottenere un hash finale da confrontare con la radice dell'albero di Merkle.
     * @param hashToBeConfirmed l'hash iniziale da validare tramite la prova di Merkle.
     * @return true se l'hash finale calcolato corrisponde alla radice 
     * dell'albero di Merkle per cui la prova è stata costruita; false altrimenti.
     * @author Luca Soricetti
     */
    private boolean validateHash(String hashToBeConfirmed) {
        // Itero sui nodi della prova per calcolare l'hash combinato
        for (MerkleProofHash nodeHash : this.proof) {
            if (nodeHash.isLeft()) // se il nodo è marcato come "sinistro" aggiungo l'hash del nodo corrente a sinistra
                hashToBeConfirmed = HashUtil.computeMD5((nodeHash.getHash() + hashToBeConfirmed).getBytes());
            else // se il nodo non è marcato come isLeft aggiungo l'hash del nodo corrente a destra
                hashToBeConfirmed = HashUtil.computeMD5((hashToBeConfirmed + nodeHash.getHash()).getBytes());
        }
        // confronto hashToBeConfirmed con la radice della prova
        return hashToBeConfirmed.equals(this.rootHash);
    }
}
