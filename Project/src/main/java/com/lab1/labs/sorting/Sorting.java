package com.lab1.labs.sorting;

import com.lab1.labs.Entities.Book;

import java.util.Arrays;

public class Sorting {
    Book[] array;

    public Sorting(Book[] array) {
        this.array = array;
    }


    public void swap(Book[] array, int ind1, int ind2) {
        Book tmp = array[ind1];
        array[ind1] = array[ind2];
        array[ind2] = tmp;
    }

    static void invertUsingFor(Book[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            Book temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    public void insertionSort(Book[] array)
    {
        int n = array.length;
        for (int i = 1; i < n; ++i) {
            Book key = array[i];
            int j = i - 1;

            while (j >= 0 && array[j].getRating() > key.getRating()) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
        invertUsingFor(array);
    }

    public void quickSort(Book[] array, int leftBorder, int rightBorder) {
        int leftMarker = leftBorder;
        int rightMarker = rightBorder;
        Book pivot = array[(leftMarker + rightMarker) / 2];
        do {

            while (array[leftMarker].getRating() > pivot.getRating()) {
                leftMarker++;
            }

            while (array[rightMarker].getRating() < pivot.getRating()) {
                rightMarker--;
            }

            if (leftMarker <= rightMarker) {

                if (leftMarker < rightMarker) {
                    Book tmp = array[leftMarker];
                    array[leftMarker] = array[rightMarker];
                    array[rightMarker] = tmp;
                }

                leftMarker++;
                rightMarker--;
            }
        } while (leftMarker <= rightMarker);

        if (leftMarker < rightBorder) {
            quickSort(array, leftMarker, rightBorder);
        }
        if (leftBorder < rightMarker) {
            quickSort(array, leftBorder, rightMarker);
        }
    }

    public void mergeSort(Book[] array, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        Book[] l = new Book[mid];
        Book[] r = new Book[n - mid];

        for (int i = 0; i < mid; i++) {
            l[i] = array[i];
        }
        for (int i = mid; i < n; i++) {
            r[i - mid] = array[i];
        }
        mergeSort(l, mid);
        mergeSort(r, n - mid);

        merge(array, l, r, mid, n - mid);
    }

    public void merge(Book[] a, Book[] l, Book[] r, int left, int right) {

        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (l[i].getRating() <= r[j].getRating()) {
                a[k++] = l[i++];
            }
            else {
                a[k++] = r[j++];
            }
        }
        while (i < left) {
            a[k++] = l[i++];
        }
        while (j < right) {
            a[k++] = r[j++];
        }
        invertUsingFor(array);
    }

    public void selectionSort(Book[] array){
        System.out.println(Arrays.toString(array));
        for (int left = 0; left < array.length; left++) {
            int minInd = left;
            for (int i = left; i < array.length; i++) {
                if (array[i].getRating() < array[minInd].getRating()) {
                    minInd = i;
                }
            }
            swap(array, left, minInd);
        }
        invertUsingFor(array);
        System.out.println(Arrays.toString(array));
    }

    public void shuttleSort(Book[] array){
        System.out.println(Arrays.toString(array));
        for (int i = 1; i < array.length; i++) {
            if (array[i].getRating() < array[i - 1].getRating()) {
                swap(array, i, i - 1);
                for (int z = i - 1; (z - 1) >= 0; z--) {
                    if (array[z].getRating() < array[z - 1].getRating()) {
                        swap(array, z, z - 1);
                    } else {
                        break;
                    }
                }
            }
        }
        invertUsingFor(array);
        System.out.println(Arrays.toString(array));
    }

    public void shellSort(Book[] array){
        System.out.println(Arrays.toString(array));
        int gap = array.length / 2;

        while (gap >= 1) {
            for (int right = 0; right < array.length; right++) {

                for (int c = right - gap; c >= 0; c -= gap) {
                    if (array[c].getRating() > array[c + gap].getRating()) {
                        swap(array, c, c + gap);
                    }
                }
            }

            gap = gap / 2;
        }
        invertUsingFor(array);
        System.out.println(Arrays.toString(array));
    }



}
