import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Una classe che rappresenta una lista concatenata con il calcolo degli hash
 * MD5 per ciascun elemento. Ogni nodo della lista contiene il dato originale di
 * tipo generico T e il relativo hash calcolato utilizzando l'algoritmo MD5.
 *
 * <p>
 * La classe supporta le seguenti operazioni principali:
 * <ul>
 * <li>Aggiungere un elemento in testa alla lista</li>
 * <li>Aggiungere un elemento in coda alla lista</li>
 * <li>Rimuovere un elemento dalla lista in base al dato</li>
 * <li>Recuperare una lista ordinata di tutti gli hash contenuti nella
 * lista</li>
 * <li>Costruire una rappresentazione testuale della lista</li>
 * </ul>
 *
 * <p>
 * Questa implementazione include ottimizzazioni come il mantenimento di un
 * riferimento all'ultimo nodo della lista (tail), che rende l'inserimento in
 * coda un'operazione O(1).
 *
 * <p>
 * La classe utilizza la classe HashUtil per calcolare l'hash MD5 dei dati.
 *
 * @param <T>
 *                il tipo generico dei dati contenuti nei nodi della lista.
 * 
 * @author Luca Tesei, Marco Caputo (template), Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 * 
 */
public class HashLinkedList<T> implements Iterable<T> {
    private Node head; // Primo nodo della lista

    private Node tail; // Ultimo nodo della lista

    private int size; // Numero di nodi della lista

    private int numeroModifiche; // Numero di modifiche effettuate sulla lista
                                 // per l'implementazione dell'iteratore
                                 // fail-fast

    public HashLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.numeroModifiche = 0;
    }

    /**
     * Restituisce il numero attuale di nodi nella lista.
     *
     * @return il numero di nodi nella lista.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     * 
     * @return {@code true} if this list contains no elements.
     *
     */
    private boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Rappresenta un nodo nella lista concatenata.
     */
    private class Node {
        String hash; // Hash del dato

        T data; // Dato originale

        Node next;

        Node(T data) {
            this.data = data;
            this.hash = HashUtil.dataToHash(data);
            this.next = null;
        }
    }

    /**
     * Aggiunge un nuovo elemento in testa alla lista.
     *
     * @param data
     *                 il dato da aggiungere.
     */
    public void addAtHead(T data) {
        // Questo metodo costa O(1).

        // Non voglio aggiungere oggetti nulli nella mia lista
        if (data == null) throw new IllegalArgumentException("un dato non può essere nullo");
        // Creo il nuovo nodo
        Node node = new Node(data);
        // Controllo se la lista era vuota, in quel caso il nuovo nodo è sia testa sia coda
        if (this.isEmpty()) this.tail = node; 
        // Se la lista non era vuota faccio puntare il nuovo nodo alla vecchia testa
        else node.next = this.head;
        // Aggiorno la testa della lista e setto che è il nuovo nodo
        this.head = node;
        // Aggiorno le variabili di istanza numeroModifiche e size
        this.numeroModifiche++;
        this.size++;
    }

    /**
     * Aggiunge un nuovo elemento in coda alla lista.
     *
     * @param data
     *                 il dato da aggiungere.
     */
    public void addAtTail(T data) {
        // Questo metodo costa O(1).

        // Non voglio aggiungere oggetti nulli nella mia lista
        if (data == null) throw new IllegalArgumentException("un dato non può essere nullo");
        // Creo il nuovo nodo
        Node node = new Node(data);
        // Controllo se la lista era vuota, in quel caso il nuovo nodo è sia testa sia coda
        if (this.isEmpty()) this.head = node; 
        // Se la lista non era vuota aggiungo il nuovo nodo alla fine della lista
        else this.tail.next = node;
        // Aggiorno la coda della lista e setto che è il nuovo nodo
        this.tail = node;
        // Aggiorno le variabili di istanza numeroModifiche e size
        this.numeroModifiche++;
        this.size++;
    }

    /**
     * Restituisce un'ArrayList contenente tutti gli hash nella lista in ordine.
     *
     * @return una lista con tutti gli hash della lista.
     */
    public ArrayList<String> getAllHashes() {
        // Questo Metodo costa O(n), infatti scorre tutti i Nodi della Lista.

        // Controllo se la lista è vuota
        if (this.isEmpty()) return null;
        // Creo l'ArrayList che per ora è vuoto
        ArrayList<String> ris = new ArrayList<String>();
        // Itero su tutti i Nodi della lista e inserisco il loro Hash in ordine nell'ArrayList
        // Uso un ciclo do-while dato che sono sicuro che ci sia almeno un elemento
        Node node = this.head;
        do {
            ris.add(node.hash);
            node = node.next;
        } while (node != null);
        // Ritorno l'ArrayList di Hash
        return ris;
    }

    /**
     * Costruisce una stringa contenente tutti i nodi della lista, includendo
     * dati e hash. La stringa dovrebbe essere formattata come nel seguente
     * esempio:
     * 
     * <pre>
     *     Dato: StringaDato1, Hash: 5d41402abc4b2a76b9719d911017c592
     *     Dato: StringaDato2, Hash: 7b8b965ad4bca0e41ab51de7b31363a1
     *     ...
     *     Dato: StringaDatoN, Hash: 2c6ee3d301aaf375b8f026980e7c7e1c
     * </pre>
     *
     * @return una rappresentazione testuale di tutti i nodi nella lista.
     */
    public String buildNodesString() {
        // Questo Metodo costa O(n), infatti scorre tutti i Nodi della Lista.

        // controllo se la lista è vuota
        if (this.isEmpty()) return null;
        // creo la Stringa da Restituire che per ora è vuota
        String ris = new String();
        // itero su tutti i Nodi della lista e inserisco i loro valori nella Stringa
        // uso un ciclo do-while dato che sono sicuro che ci sia almeno un elemento
        Node node = this.head;
        do {
            ris = ris + "Dato: " + node.data + ", Hash: " + node.hash + "\n";
            node = node.next;
        } while (node != null);
        // ritorno l'ArrayList di Hash
        return ris;
    }

    /**
     * Rimuove il primo elemento nella lista che contiene il dato specificato.
     *
     * @param data
     *                 il dato da rimuovere.
     * @return true se l'elemento è stato trovato e rimosso, false altrimenti.
     */
    public boolean remove(T data) {
        // Questo Metodo costa O(n), infatti nel Caso Pessimo scorre tutti i Nodi della Lista.

        // non sono presenti elementi nulli nella mia Lista
        if (data == null) throw new IllegalArgumentException("un dato non può essere nullo");

        // controllo se la lista è vuota
        if (this.isEmpty()) return false;

        /**
         * Bisogna controllare e gestire i casi limite:
            • Caso 1: Il Nodo da rimuovere è la Testa della Lista;
            • Caso 2: Il Nodo da rimuovere è l'Unico Nodo della Lista (e quindi è sia Testa sia Coda);
            • Caso 3: Il Nodo da rimuovere non è la Testa della Lista;
            • Caso 4: Il Nodo da rimuovere è la Coda della Lista.
        */

        // Se il Nodo da eliminare è la Testa non c'è bisogno che scorro la Lista, il metodo termina subito
        if (this.head.data.equals(data)) { // Caso 1
            this.head = this.head.next;
            if (this.head == null) { // Caso 2
                this.tail = null;
            }
            // la prima occorrenza del nodo da rimuovere è stata cancellata,
            // quindi aggiorno la size, il numeroModifiche e ritorno true.
            this.size--;
            this.numeroModifiche++;
            return true;
        }

        // Se il Nodo da eliminare non è la Testa devo scorrere la Lista
        Node node = this.head;
        // Nota Bene: Il nodo da rimuovere è rappresentato da node.next
        while (node.next != null) {
            if (node.next.data.equals(data)) { // Caso 3
                node.next = node.next.next;
                if (node.next == null) { // Caso 4
                    this.tail = node;
                }
                // La prima occorrenza del nodo da rimuovere è stata cancellata,
                // quindi aggiorno la size, il numeroModifiche e ritorno true.
                this.size--;
                this.numeroModifiche++;
                return true;
            }
            else { // Se node.next non è il nodo da rimuovere continuo a scorrere la Lista
                node = node.next;
            }
        }
        
        // Caso Pessimo: Nodo da Eliminare non Trovato dopo aver scorso tutta la Lista
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    /**
     * Classe che realizza un iteratore fail-fast per HashLinkedList.
     */
    private class Itr implements Iterator<T> {

        // Tiene traccia dell'ultimo nodo.data restituito dalla funzione next().
        private Node lastNodeReturned;
    
        // Variabile necessaria per rendere l'Iteratore Fail-Fast.
        private int numeroModificheAtteso;
    
        private Itr() {
            // Inizializzo `lastNodeReturned` a null perchè ancora non è stata fatta alcuna iterazione
            // e copio il valore attuale del contatore di modifiche della Lista.
            this.lastNodeReturned = null;
            this.numeroModificheAtteso = HashLinkedList.this.numeroModifiche;
        }
    
        @Override
        public boolean hasNext() {
            if (this.lastNodeReturned == null) 
                // Se siamo alla prima iterazione verifico che la lista non sia vuota.
                return HashLinkedList.this.head != null;
            else 
                // Se l'iterazione è cominciata controllo se c'è un nodo successivo.
                return lastNodeReturned.next != null;
        }

        @Override
        public T next() {
            // Controllo fail-fast: verifico che durante l'Iterazione 
            // non sia stata apportata alcuna modifica alla Lista HashLinkedList.this.
            if (this.numeroModificheAtteso != HashLinkedList.this.numeroModifiche) 
                throw new ConcurrentModificationException("La Lista è stata modificata durante l'Iterazione");
            
            if (!hasNext()) // Se non ci sono altri elementi da scorrere nella lista lancio un'eccezione
                throw new NoSuchElementException("Richiesta di next quando hasNext è falso");
    
            // Se sono all'inizio dell'Iterazione (so che la lista non è vuota) 
            // ritorno il .data della testa
            if (this.lastNodeReturned == null) {
                this.lastNodeReturned = HashLinkedList.this.head;
                return HashLinkedList.this.head.data;
            }
            // Se l'Iterazione è già iniziata (e so che c'è un next)
            // ritorno il .data del next
            else { 
                lastNodeReturned = lastNodeReturned.next;
                return lastNodeReturned.data;
            }
        }
    }
}