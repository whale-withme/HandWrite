package tech.insight;

public class MyHashMap<K, V> {

    private int size = 0;

    private Node<K, V>[] table = new Node[16];

    public V put(K key, V value) {
        int index = hashIndex(key);
        Node<K, V> head = table[index];

        if (table[index] == null) {
            table[index] = new Node<>(key, value);
            size++;
            resizeNecessary();
            return value;
        } else {
            Node<K, V> newNode = new Node<>(key, value);
            while (true) {
                if (head.key.equals(newNode.key)) {
                    V oldV = head.value;
                    head.value = newNode.value;
                    return oldV;
                }

                if (head.next == null) {
                    head.next = newNode;
                    size++;
                    resizeNecessary();
                    return newNode.value;
                }
                head = head.next;
            }

        }
    }

    public V get(K key) {
        int index = hashIndex(key);

        if (table[index] == null) {
            return null;
        }

        Node<K, V> head = table[index];
        while (true) {
            if (head.key.equals(key)) {
                return head.value;
            }

            if (head.next == null) {
                return null;
            }

            head = head.next;
        }
    }

    public V remove(K key) {
        int index = hashIndex(key);

        if (table[index] == null) {
            return null;
        }

        Node<K, V> head = table[index];
        if (head.key.equals(key)) {
            table[index] = head.next;
            size--;
            return head.value;
        }

        Node<K, V> pre = head;
        Node<K, V> current = head.next;

        while (true) {
            if (current.key.equals(key)) {
                pre.next = current.next;
                current.next = null;
                size--;
                return current.value;
            }

            if (current.next == null) {
                return null;
            }
            current = current.next;
            pre = pre.next;
        }
    }

    class Node<K, V> {
        K key;
        V value;
        Node<K, V> pre;
        Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.pre = this.next = null;
        }
    }

    public int size() {
        return size;
    }

    public int hashIndex(Object key) {
        return key.hashCode() & (table.length - 1);
    }

    public void resizeNecessary() {
        if (this.size < this.table.length * 0.75) {
            return;
        }

        Node<K, V>[] newTable = new Node[this.table.length * 2];

        for (Node<K, V> head : table) {
            if (head == null) {
                continue;
            }

            Node<K, V> current = head;
            while (current != null) {
                // 取代模运算，提升性能
                int newIndex = current.key.hashCode() & (newTable.length - 1);
                if (newTable[newIndex] == null) {
                    newTable[newIndex] = current;
                    Node<K, V> nextNode = current.next;
                    current.next = null; // 新链表只放一个元素
                    current = nextNode;
                } else {
                    // 头插法
                    Node<K, V> nextNode = current.next;
                    current.next = newTable[newIndex];
                    newTable[newIndex] = current;
                    current = nextNode;
                }
            }
        }
        this.table = newTable;
        System.err.println("扩容到" + table.length);
    }
}
