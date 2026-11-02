void check(boolean cond, String msg) {
    IO.println((cond ? "OK   " : "FAIL ") + msg);
}

// Oba sposoby budowy muszą dać poprawny kopiec (delMax malejąco) z tymi samymi danymi;
void testCreate() {
    java.util.Random rnd = new java.util.Random(1);
    boolean okMethod1 = true, okMethod2 = true;

    for (int n = 0; n <= 200; n++) {
        int[] data = new int[n];
        //losowy zestaw danych:
        for (int i = 0; i < n; i++) {
            data[i] = rnd.nextInt(50);
        }

        int[] sorted = data.clone();
        java.util.Arrays.sort(sorted);

        Heap heap1 = Heap.createMethod1(data);
        Heap heap2 = Heap.createMethod2(data);
        okMethod1 &= heap1.size() == n;
        okMethod2 &= heap2.size() == n;
        for (int i = n - 1; i >= 0; i--) {
            okMethod1 &= heap1.delMax() == sorted[i];
            okMethod2 &= heap2.delMax() == sorted[i];
        }
        okMethod1 &= heap1.isEmpty();
        okMethod2 &= heap2.isEmpty();
    }
    check(okMethod1, "createMethod1");
    check(okMethod2, "createMethod2");

}

// Wykres liczby przebiegów pętli przy budowie kopca dla różnych n.
void testCreateComplexity() throws Exception
{
    java.util.Random rnd = new java.util.Random(1);

    java.util.List<Integer> ns = new java.util.ArrayList<>();
    java.util.List<Long> y1 = new java.util.ArrayList<>();
    java.util.List<Long> y2 = new java.util.ArrayList<>();

    for (int n = 50; n <= 2000; n += 50)
    {
        int[] data = new int[n];

        //pesymistyczny zestaw n danych:
        //
        data[0] = 123;
        data[1] = 321;
        //...

        Heap heap1 = Heap.createMethod1(data);
        Heap heap2 = Heap.createMethod2(data);

        ns.add(n);
        y1.add((long)rnd.nextInt(1000));   //liczba przebiegów while dla metody 1
        y2.add((long)rnd.nextInt(1000));   //liczba przebiegów while dla metody 1
    }
    //Rysowanie wykresu:
    Chart.draw(ns, y1, "Metoda 1", y2, "Metoda 2","Heap: złożoność pesymistyczna budowy kopca", "heap_create_complexity.png", false);
    IO.println("Zapisano heap_create_complexity.png");
}

void main() throws Exception {
    testCreate();
    testCreateComplexity();
}


