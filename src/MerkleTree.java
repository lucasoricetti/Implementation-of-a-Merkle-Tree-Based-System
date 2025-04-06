import java.util.*;

/**
 * Un Merkle Tree, noto anche come hash tree binario, è una struttura dati per
 * verificare in modo efficiente l'integrità e l'autenticità dei dati
 * all'interno di un set di dati più ampio. Viene costruito eseguendo l'hashing
 * ricorsivo di coppie di dati (valori hash crittografici) fino a ottenere un
 * singolo hash root. In questa implementazione la verifica di dati avviene
 * utilizzando hash MD5.
 * 
 * @author Luca Tesei, Marco Caputo (template) Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 *
 * @param <T>
 *                il tipo di dati su cui l'albero è costruito.
 */
public class MerkleTree<T> {
    /**
     * Nodo radice dell'albero.
     */
    private final MerkleNode root;

    /**
     * Larghezza dell'albero, ovvero il numero di nodi nell'ultimo livello.
     */
    private final int width;

    /**
     * Costruisce un albero di Merkle a partire da un oggetto HashLinkedList,
     * utilizzando direttamente gli hash presenti nella lista per costruire le
     * foglie. Si noti che gli hash dei nodi intermedi dovrebbero essere
     * ottenuti da quelli inferiori concatenando hash adiacenti due a due e
     * applicando direttmamente la funzione di hash MD5 al risultato della
     * concatenazione in bytes.
     *
     * @param hashList
     *                     un oggetto HashLinkedList contenente i dati e i
     *                     relativi hash.
     * @throws IllegalArgumentException
     *                                      se la lista è null o vuota.
     */
    public MerkleTree(HashLinkedList<T> hashList) {
        if (hashList == null || hashList.getSize() == 0) 
            throw new IllegalArgumentException("la lista è null o vuota");

        // definisco la width del mio albero, ovvero il numero di nodi nell'ultimo livello
        this.width = hashList.getSize();
        
        // creo un ArrayList che inizialmente contiene i MerkleNodes foglia dell'albero
        // questo ArrayList rappresenterà nell'iterazione il livello di cui bisogna costruire i parents.
        ArrayList<MerkleNode> merkleNodes = new ArrayList<>(this.width);
        for (T data : hashList) {
            merkleNodes.add(new MerkleNode(HashUtil.dataToHash(data)));
        }

        // inizio l'iterazione per costruire l'Albero

        // se il livello di cui devo costruire i parents 
        // è composto da un singolo Nodo significa che ho finito perchè rappresenta la root.
        while (merkleNodes.size() > 1) { 
            // creo un ArrayList, per il momento vuoto, che deve andare a contenere i parents di 'merkleNodes'.
            // la Capacity dell'array 'parents' dipende da quanti nodi figli ci sono.
            ArrayList<MerkleNode> parents = new ArrayList<>((int) Math.ceil(merkleNodes.size()/2));

            // itero per tutti i nodi di merkleNodes
            for (int i = 0; i < merkleNodes.size(); i += 2) { 
                MerkleNode left = merkleNodes.get(i);
                // grazie al Figlio Destro gestisco i casi di livelli con numeri dispari di nodi
                MerkleNode right = (i + 1 < merkleNodes.size()) ? merkleNodes.get(i+1) : null;
                // se il figlio destro esiste allora l'hash del genitore è l'hash combinato dei due figli
                String parentHash;
                if (right != null) parentHash = left.getHash() + right.getHash();
                else parentHash = left.getHash();
                // creo il nodo padre di right e left
                MerkleNode parent = new MerkleNode(
                    HashUtil.computeMD5((parentHash).getBytes()), // hash del nodo parent
                    left, right // figlio sinistro e destro del nodo parent
                ); 
                // Aggiungo il nuovo nodo alla lista dei parents di merkleNodes
                parents.add(parent);
            }
            merkleNodes = parents; // aggiorno il livello di cui devo costruire i parents
        }
        // se arrivo qui la size di merkleNodes è = 1, quindi l'unico nodo in merkleNodes è la Root
        this.root = merkleNodes.get(0);
    }

    /**
     * Restituisce il nodo radice dell'albero.
     *
     * @return il nodo radice.
     */
    public MerkleNode getRoot() {
        return root;
    }

    /**
     * Restituisce la larghezza dell'albero.
     *
     * @return la larghezza dell'albero.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Restituisce l'altezza dell'albero.
     *
     * @return l'altezza dell'albero.
     */
    public int getHeight() {
        // L'altezza dell'Albero, conoscendo il numero di foglie (width),
        // si calcola facendo height = log_base_2(width), e se necessario si
        // approssima il risultato per eccesso.

        // Calcolo il logaritmo in base 2
        double height = Math.log(width) / Math.log(2);
        // Arrotondo per eccesso il risultato
        return (int) Math.ceil(height);
    }

    /**
     * Restituisce l'indice di un elemento secondo questo albero di Merkle. Gli
     * indici forniti partono da 0 e corrispondono all'ordine degli hash
     * corrispondenti agli elementi nell'ultimo livello dell'albero da sinistra
     * a destra (e quindi l'ordine degli elementi forniti alla costruzione). Se
     * l'hash dell'elemento non è presente come dato dell'albero, viene
     * restituito -1.
     *
     * @param data
     *                 l'elemento da cercare.
     * @return l'indice del dato nell'albero; -1 se il dato non è presente.
     * @throws IllegalArgumentException
     *                                      se il dato è null.
    */
    public int getIndexOfData(T data) {
        if (data == null)
            throw new IllegalArgumentException("il parametro data non può essere null.");
        
        // Chiamo il metodo getIndexOfData(MerkleNode branch, T data) passando this.root come branch da cui partire
        return getIndexOfData(this.root, data);
    }

    /**
     * Restituisce l'indice di un dato elemento secondo l'albero di Merkle
     * descritto da un dato branch. Gli indici forniti partono da 0 e
     * corrispondono all'ordine degli hash corrispondenti agli elementi
     * nell'ultimo livello dell'albero da sinistra a destra. Nel caso in cui il
     * branch fornito corrisponda alla radice di un sottoalbero, l'indice
     * fornito rappresenta un indice relativo a quel sottoalbero, ovvero un
     * offset rispetto all'indice del primo elemento del blocco di dati che
     * rappresenta. Se l'hash dell'elemento non è presente come dato
     * dell'albero, viene restituito -1.
     *
     * @param branch
     *                   la radice dell'albero di Merkle.
     * @param data
     *                   l'elemento da cercare.
     * @return l'indice del dato nell'albero; -1 se l'hash del dato non è
     *         presente.
     * @throws IllegalArgumentException
     *                                      se il branch o il dato sono null o
     *                                      se il branch non è parte
     *                                      dell'albero.
    */
    public int getIndexOfData(MerkleNode branch, T data) {
        if (branch == null || data == null)
            throw new IllegalArgumentException("I parametri branch e data non possono essere null.");
        
        // Calcolo l'hash del dato da cercare
        String hash = HashUtil.dataToHash(data);
        
        // Inizializzo un array di dimensione 1.
        // Utilizzo l'array per tenere traccia dell'indice dell'ultima foglia visitata
        // attraverso le chiamate ricorsive, poiché gli array sono oggetti mutabili in Java 
        // (e sono quindi memorizzati nell'heap e raggiungibili tramite un puntatore).
        int[] index = {0};
    
        // Avvio la Ricerca del Dato.
        // Nelle chiamate ricorsive mi passo sempre il parametro index in quanto è il puntatore
        // all'array index memorizzato nell'heap;
        // i valori dell'array sono raggiungibili a patto che si conosca il puntatore.
        return getIndexOfDataRec(branch, hash, index);
    }

    /**
     * Metodo ricorsivo che effettua la visita in pre-ordine dell'albero con radice 'branch' 
     * che l'ha invocato al fine di restituire l'indice di un dato elemento.
     * 
     * @param node il nodo corrente dell'albero
     * @param hash l'hash del dato da cercare
     * @param index puntatore ad un array che tiene traccia 
     *              dell'indice dell'ultima foglia visitata durante la ricerca
     * @return l'indice del dato se trovato; -1 se non è presente
     * @author Luca Soricetti
    */
    private int getIndexOfDataRec(MerkleNode node, String hash, int[] index) {
        // Se il nodo è nullo, il dato di sicuro non è presente in questo ramo
        if (node == null) return -1;
    
        // Se il nodo corrente è una foglia
        if (node.isLeaf()) {
            // Confronto gli hash e se sono uguali restituisco l'indice della foglia corrente
            if (node.getHash().equals(hash)) return index[0];
            // Altrimenti incremento l'indice e continuo la visita, notificando che l'elemento
            // che cercavo non si trova qui
            index[0]++;
            return -1;
        }
    
        // Visito il sottoalbero sinistro
        int leftResult = getIndexOfDataRec(node.getLeft(), hash, index);
        if (leftResult != -1) { // Se è stato trovato in questo sottoalbero devo restituire l'indice
            return leftResult;
        }
    
        // Visito il sottoalbero destro
        return getIndexOfDataRec(node.getRight(), hash, index);
    }

    /**
     * Sottopone a validazione un elemento fornito per verificare se appartiene
     * all'albero di Merkle, controllando se il suo hash è parte dell'albero
     * come hash di un nodo foglia.
     *
     * @param data
     *                 l'elemento da validare
     * @return true se l'hash dell'elemento è parte dell'albero; false
     *         altrimenti.
    */
    public boolean validateData(T data) {
        if (data == null) throw new IllegalArgumentException("un dato null non si trova nell'albero");
        
        // Faccio partire la ricerca dalla root
        return validateHashRec(this.root, HashUtil.dataToHash(data));
    }

    /**
     * Sottopone a validazione un dato sottoalbero di Merkle, corrispondente
     * quindi a un blocco di dati, per verificare se è valido rispetto a questo
     * albero e ai suoi hash. Un sottoalbero è valido se l'hash della sua radice
     * è uguale all'hash di un qualsiasi nodo intermedio di questo albero. Si
     * noti che il sottoalbero fornito può corrispondere a una foglia.
     *
     * @param branch
     *                   la radice del sottoalbero di Merkle da validare.
     * @return true se il sottoalbero di Merkle è valido; false altrimenti.
    */
    public boolean validateBranch(MerkleNode branch) {
        if (branch == null) throw new IllegalArgumentException("un branch null non si trova nell'albero");
        
        // Faccio partire la ricerca dalla root
        return validateHashRec(this.root, branch.getHash());
    }

    /**
     * Metodo che effettua ricorsivamente la visita in pre-ordine dell'albero per cercare l'hash interessato tra i nodi.
     * @param node il nodo corrente
     * @param hash l'hash del dato da trovare
     * @return true se l'hash è trovato; false altrimenti
     * @author Luca Soricetti
    */
    private boolean validateHashRec(MerkleNode node, String hash) {
        // Se il nodo è nullo di sicuro il dato non è presente in questo ramo
        if (node == null) return false;

        // Controllo il nodo corrente
        if (node.getHash().equals(hash)) return true;

        // Se il nodo corrente è una foglia e l'hash non è quello che cerco torno indietro
        if (node.isLeaf()) return false;

        // Visito il sottoalbero sinistro, se il valore è stato trovato ritorno true,
        // altrimenti cerco nel sottoalbero destro
        if (validateHashRec(node.getLeft(), hash)) return true;

        // Visito il sottoalbero destro, se il valore è stato trovato ritorno true,
        // altrimenti ritorno che la ricerca su questo sotto-albero non è andata a buon fine
        return validateHashRec(node.getRight(), hash);
    }

    /**
     * Sottopone a validazione un dato albero di Merkle per verificare se è
     * valido rispetto a questo albero e ai suoi hash. Grazie alle proprietà
     * degli alberi di Merkle, ciò può essere fatto in tempo costante.
     *
     * @param otherTree
     *                      il nodo radice dell'altro albero di Merkle da
     *                      validare.
     * @return true se l'altro albero di Merkle è valido; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se l'albero fornito è null.
    */
    public boolean validateTree(MerkleTree<T> otherTree) {
        if (otherTree == null)
            throw new IllegalArgumentException("L'albero fornito non può essere null.");
    
        // Se il numero di Foglie è lo stesso allora mi basta 
        // confrontare gli Hash delle Radici per determinare se gli Alberi sono Uguali
        if (this.width == otherTree.getWidth()) return this.root.getHash().equals(otherTree.getRoot().getHash());

        // Se il numero di foglie è diverso so di per certo che non sono Uguali
        return false;
    }

    /**
     * Trova gli indici degli elementi di dati non validi (cioè con un hash
     * diverso) in un dato Merkle Tree, secondo questo Merkle Tree. Grazie alle
     * proprietà degli alberi di Merkle, ciò può essere fatto confrontando gli
     * hash dei nodi interni corrispondenti nei due alberi. Ad esempio, nel caso
     * di un singolo dato non valido, verrebbe percorso un unico cammino di
     * lunghezza pari all'altezza dell'albero. Gli indici forniti partono da 0 e
     * corrispondono all'ordine degli elementi nell'ultimo livello dell'albero
     * da sinistra a destra (e quindi l'ordine degli elementi forniti alla
     * costruzione). Se l'albero fornito ha una struttura diversa, possibilmente
     * a causa di una quantità diversa di elementi con cui è stato costruito e,
     * quindi, non rappresenta gli stessi dati, viene lanciata un'eccezione.
     *
     * @param otherTree
     *                      l'altro Merkle Tree.
     * @throws IllegalArgumentException
     *                                      se l'altro albero è null o ha una
     *                                      struttura diversa.
     * @return l'insieme di indici degli elementi di dati non validi.
    */    
    public Set<Integer> findInvalidDataIndices(MerkleTree<T> otherTree) {
        // se i due alberi hanno strutture diverse lancio un'eccezione
        if (otherTree == null || this.width != otherTree.getWidth()) 
            throw new IllegalArgumentException("l'albero passato è null o ha una struttura diversa");

        // se i due alberi hanno la stessa root significa che non hanno dati con hash diversi
        if (this.root.getHash().equals(otherTree.getRoot().getHash())) {
            return Collections.emptySet();
        }
    
        // Inizializzo un array (oggetto immutabile) per tenere traccia dell'indice delle foglie visitate 
        // (o di cui la visita non era necessaria)
        int[] index = {0};

        // Inizializzo il Set degli invalidIndices 
        Set<Integer> invalidIndices = new HashSet<>();
    
        // Avvio la ricorsione
        findInvalidDataIndicesRec(this.root, otherTree.getRoot(), invalidIndices, index, this.width, this.getHeight());
    
        return invalidIndices;
    }

    /**
     * Metodo ricorsivo per individuare gli indici dei dati non validi tra due Merkle Tree.
     * Confronta i nodi corrispondenti di due alberi ed
     * identifica e restituisce i nodi foglia in cui gli hash non corrispondono.
     * 
     * @param thisNode il nodo corrente dell'albero this.
     * @param otherNode il nodo corrente dell'albero da confrontare.
     * @param invalidIndices il set che raccoglie gli indici degli elementi non validi.
     * @param index puntatore ad un array contenente l'indice corrente, utilizzato per tenere traccia delle foglie visitate.
     * @param width il numero di foglie dell'albero che ha come radice il nodo corrente.
     * @param currentHeight l'altezza dell'albero che ha come radice il nodo corrente.
     * @author Luca Soricetti
    */
    private void findInvalidDataIndicesRec
    (MerkleNode thisNode, MerkleNode otherNode, Set<Integer> invalidIndices, int[] index, int width, int currentHeight) {
        // Se i nodi sono uguali allora di sicuro tutti i figli sono uguali
        if (thisNode.getHash().equals(otherNode.getHash())) {
            // Salto il sottoalbero del nodo corrente incrementando l'indice 
            // di un valore pari al numero di foglie sotto il nodo
            index[0] += width;
            return;
        }

        // Se arrivati qui entrambi i nodi sono foglie, (se thisNode è una foglia anche l'altro lo è),
        // sono sicuro che gli Hash non corrispondono
        if (thisNode.isLeaf()) {
            // Aggiungo l'indice corrente al set degli indici non validi
            invalidIndices.add(index[0]);
            // Incremento di uno l'indice delle foglie visitate
            index[0]++;
            return;
        }

        // Calcolo il numero di foglie nel sottoalbero sinistro del nodo corrente.
        // Per le proprietà degli Alberi di Merkle, il numero di foglie presenti nel sottoalbero sinistro
        // di un nodo è normalmente 2^(h-1), dove h è l'altezza del nodo corrente, 
        // ma se il nodo corrente si trova nel sottoalbero destro della radice this, e quindi ha magari height > 1
        // ma un solo figlio, allora so che il numero di foglie nel suo sottoalbero sinistro sarà uguale al suo.
        int leftSubtreeLeaves = (int) Math.min(width, Math.pow(2, currentHeight - 1));
        // Visito il figlio sinistro
        findInvalidDataIndicesRec(thisNode.getLeft(), otherNode.getLeft(), invalidIndices, index, leftSubtreeLeaves, currentHeight - 1);

        // Calcolo il numero di foglie nel sottoalbero destro del nodo corrente.
        // Il sottoalbero destro contiene tutte le foglie rimanenti, ovvero il totale delle foglie 
        // meno quelle già assegnate al sottoalbero sinistro.
        int rightSubtreeLeaves = width - leftSubtreeLeaves;
        // Visito il figlio destro (se la sua width è > 0, altrimenti capisco che questo nodo non ha un figlio destro)
        if (rightSubtreeLeaves > 0)
        findInvalidDataIndicesRec(thisNode.getRight(), otherNode.getRight(), invalidIndices, index, rightSubtreeLeaves, currentHeight - 1);
        else return;
    }

    /**
     * Restituisce la prova di Merkle per un dato elemento, ovvero la lista di
     * hash dei nodi fratelli di ciascun nodo nel cammino dalla radice a una
     * foglia contenente il dato. La prova di Merkle dovrebbe fornire una lista
     * di oggetti MerkleProofHash tale per cui, combinando l'hash del dato con
     * l'hash del primo oggetto MerkleProofHash in un nuovo hash, il risultato
     * con il successivo e così via fino all'ultimo oggetto, si possa ottenere
     * l'hash del nodo padre dell'albero. Nel caso in cui, in determinati
     * step della prova, non ci siano due hash distinti da combinare, l'hash deve
     * comunque ricalcolato sulla base dell'unico hash disponibile.
     *
     * @param data
     *                 l'elemento per cui generare la prova di Merkle.
     * @return la prova di Merkle per il dato.
     * @throws IllegalArgumentException
     *                                      se il dato è null o non è parte
     *                                      dell'albero.
     */
    public MerkleProof getMerkleProof(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Il dato non può essere null");
        }
    
        // Calcolo l'hash del dato
        String hash = HashUtil.dataToHash(data);
    
        // Inizializzo la prova di Merkle
        // sapendo che la lunghezza massima della proof sarà pari all'altezza dell'Albero
        MerkleProof proof = new MerkleProof(this.root.getHash(), this.getHeight());
    
        // Avvio la costruzione della MerkleProof
        if (makeProofForData(this.root, hash, proof)) {
            return proof;
        }
    
        // Se il nodo non è trovato, lancio eccezione
        throw new IllegalArgumentException("Il dato passato non fa parte dell'albero");
    }
    
    /**
     * Metodo ricorsivo che cerca un dato nell'albero e costruisce la sua MerkleProof.
     *
     * @param node il nodo corrente
     * @param hash l'hash del dato da trovare e di cui costruire la prova
     * @param proof la MerkleProof da costruire
     * @return true se il nodo è trovato, false altrimenti
     * @author Luca Soricetti
     */
    private boolean makeProofForData(MerkleNode node, String hash, MerkleProof proof) {
        // Se il nodo è nullo torno indietro nella ricorsione
        if (node == null) return false;

        // Se il nodo corrente contiene l'hash cercato, 
        // la ricerca è completata e posso tornare indietro per creare la proof
        if (node.getHash().equals(hash)) {
            return true;
        }

        // Se il nodo corrente è una foglia non uguale all'hash che sto cercando torno indietro
        if (node.isLeaf()) {
            return false;
        }
    
        // Visito il figlio sinistro
        if (makeProofForData(node.getLeft(), hash, proof)) { // Se l'hash è stato trovato nel figlio sinistro del nodo
                                                             // corrente aggiungo alla proof il figlio destro
            // Se il nodo corrente ha un figlio destro lo aggiungo alla prova
            if (node.getRight() != null) {
                proof.addHash(node.getRight().getHash(), false);
            } 
            // Se il nodo corrente non ha un figlio destro significa che mi trovo in un ramo incompleto
            // e quindi aggiungo alla prova una stringa vuota, la quale permetterà durante la costruzione
            // della prova di ricalcolare l'hash corrente senza dovergli appendere un fratello.
            else {
                proof.addHash("", false);
            }
            return true;
        }
    
        // Visito il figlio destro
        if (makeProofForData(node.getRight(), hash, proof)) { // Se l'hash è stato trovato nel figlio destro del nodo
                                                              // corrente aggiungo alla proof il figlio sinistro
            // sono sicuro che abbia un fratello sinistro per le proprietà di MerkleTree
            proof.addHash(node.getLeft().getHash(), true);
            return true;
        }
        
        // Se arrivo qui significa che non ho trovato nessun nodo con hash uguale a quello interessato
        return false;
    }
    
    /**
     * Restituisce la prova di Merkle per un dato branch, ovvero la lista di
     * hash dei nodi fratelli di ciascun nodo nel cammino dalla radice al dato
     * nodo branch, rappresentativo di un blocco di dati. La prova di Merkle
     * dovrebbe fornire una lista di oggetti MerkleProofHash tale per cui,
     * combinando l'hash del branch con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, si possa ottenere l'hash del nodo padre dell'albero.
     * Nel caso in cui non ci, in determinati step della prova non ci siano due
     * hash distinti da combinare, l'hash deve comunque ricalcolato sulla base
     * dell'unico hash disponibile.
     *
     * @param branch
     *                   il branch per cui generare la prova di Merkle.
     * @return la prova di Merkle per il branch.
     * @throws IllegalArgumentException
     *                                      se il branch è null o non è parte
     *                                      dell'albero.
     */
    public MerkleProof getMerkleProof(MerkleNode branch) {
        if (branch == null) {
            throw new IllegalArgumentException("Il branch non può essere null");
        }

        // Prendo l'hash del branch interessato
        String hash = branch.getHash();

        // Creo un puntatore ad una sola MerkleProof che verrà istanziata successivamente dalla funzione ricorsiva
        // Ho bisogno di usare questo stratagemma per poter inizializzare la proof durante la visita all'albero,
        // e lo faccio quando conosco la sua lunghezza massima, 
        // data da (Height dell'Albero 'this' - Height dell'Albero con radice 'branch')
        MerkleProof[] proofPointer = new MerkleProof[1];

        // Avvio la costruzione della MerkleProof
        makeProofForBranch(this.root, hash, proofPointer, this.getHeight());

        // Verifico che la MerkleProof sia stata creata
        if (proofPointer[0] == null) {
            throw new IllegalArgumentException("Il branch specificato non è parte dell'albero");
        }

        // Se è stata creata la ritorno
        return proofPointer[0];
    }

    /**
     * Metodo ricorsivo che cerca un branch nell'albero e costruisce la sua MerkleProof.
     *
     * @param node il nodo corrente
     * @param hash l'hash del nodo branch da trovare e di cui costruire la prova
     * @param proofPointer puntatore all'array con la MerkleProof, stratagemma
     *                       utilizzato per inizializzare la MerkleProof all'interno della ricorsione
     * @param currentHeight l'altezza dell'albero che ha come radice il nodo corrente
     * @return true se il branch è trovato, false altrimenti
     * @author Luca Soricetti
     */
    private boolean makeProofForBranch(MerkleNode node, String hash, MerkleProof[] proofPointer, int currentHeight) {
        // Se il nodo è nullo torno indietro nella ricorsione
        if (node == null) return false;
        
        // Se il nodo corrente contiene l'hash cercato, la ricerca è completata
        // e posso finalmente inizializzare la proof dato che conosco la sua lunghezza massima
        if (node.getHash().equals(hash)) {
            proofPointer[0] = new MerkleProof(this.root.getHash(), this.getHeight() - currentHeight);
            return true;
        }

        // Se il nodo corrente è una foglia non uguale all'hash che sto cercando torno indietro
        if (node.isLeaf()) {
            return false;
        }

        // Visito il figlio sinistro
        // Se l'hash è stato trovato nel figlio sinistro del nodo
        // corrente aggiungo alla proof il figlio destro
        if (makeProofForBranch(node.getLeft(), hash, proofPointer, currentHeight - 1)) {                                                                
            // Se il nodo corrente ha un figlio destro lo aggiungo alla prova
            if (node.getRight() != null) {
                proofPointer[0].addHash(node.getRight().getHash(), false);
            } 
            // Se il nodo corrente non ha un figlio destro significa che mi trovo in un ramo incompleto
            // e quindi aggiungo alla prova una stringa vuota, la quale permetterà durante la costruzione
            // della prova di ricalcolare l'hash corrente senza dovergli appendere un fratello.
            else {
                proofPointer[0].addHash("", false);
            }
            return true;
        }

        // Visito il figlio destro
        // Se l'hash è stato trovato nel figlio destro del nodo
        // corrente aggiungo alla proof il figlio sinistro
        if (makeProofForBranch(node.getRight(), hash, proofPointer, currentHeight - 1)) {
            // sono sicuro che abbia un fratello sinistro per le proprietà di MerkleTree
            proofPointer[0].addHash(node.getLeft().getHash(), true);
            return true;
        }

        // Se arrivo qui significa che non ho trovato nessun nodo con hash uguale a quello interessato
        return false;
    }
}
