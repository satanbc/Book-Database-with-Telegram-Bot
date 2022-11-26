package com.lab1.labs.Lists;

class LinkedList {
    LinkedList next;
    int data;
}

class List {
    private LinkedList head;
    private LinkedList tail;

    void addFront(int data)
    {
        LinkedList a = new LinkedList();
        a.data = data;

        if(head == null)
        {
            head = a;
            tail = a;
        }
        else {
            a.next = head;
            head = a;
        }
    }

    void addBack(int data) {
        LinkedList a = new LinkedList();
        a.data = data;
        if (tail == null)
        {
            head = a;
            tail = a;
        } else {
            tail.next = a;
            tail = a;
        }
    }

    void printList()
    {
        LinkedList t = this.head;
        while (t != null)
        {
            System.out.print(t.data + " ");
            t = t.next;
        }
        System.out.println();
    }


    public void addAfter(int prevEl, int nextEl){
        LinkedList t = this.head;
        while (t!=null){
            if(prevEl==t.data){
                LinkedList e = new LinkedList();
                e.data = nextEl;
                e.next = t.next;
                t.next = e;
                t =e;
            }
            t = t.next;
        }

    }

    void delEl(int data)
    {
        if(head == null)
            return;

        if (head == tail) {
            head = null;
            tail = null;
            return;
        }

        if (head.data == data) {
            head = head.next;
            return;
        }

        LinkedList t = head;
        while (t.next != null) {
            if (t.next.data == data) {
                if(tail == t.next)
                {
                    tail = t;
                }
                t.next = t.next.next;
                return;
            }
            t = t.next;
        }
    }
}

