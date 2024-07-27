package myapp.src.main;

public class InsertionSort {
    
    public static void ordenar(int[] arr) {
        // Itera sobre os elementos do array começando do segundo elemento.
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i]; // Armazena o valor atual em key.
            int j = i - 1; // Inicializa j como o índice anterior ao índice atual.
            
            // Move os elementos do array que são maiores que key para uma posição à frente.
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j]; // Desloca o elemento uma posição à frente.
                j = j - 1; // Decrementa j.
            }
            arr[j + 1] = key; // Coloca key na posição correta.
        }
    }
}
