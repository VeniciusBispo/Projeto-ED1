package myapp.src.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Utils {

    public static int[] gerarArrayAleatorio(int tamanho, String ordem) {
        int[] array = new int[tamanho]; // Cria um array com o tamanho especificado.
        Random random = new Random();
        
        // Preenche o array com números aleatórios entre 0 e 999.
        for (int i = 0; i < tamanho; i++) {
            array[i] = random.nextInt(1000);
        }
        
        // Ordena o array em ordem crescente se especificado.
        if ("Crescente".equals(ordem)) {
            Arrays.sort(array);
        } 
        // Ordena o array em ordem decrescente se especificado.
        else if ("Decrescente".equals(ordem)) {
            Arrays.sort(array);
            // Inverte o array para obter a ordem decrescente.
            for (int i = 0; i < tamanho / 2; i++) {
                int temp = array[i];
                array[i] = array[tamanho - i - 1];
                array[tamanho - i - 1] = temp;
            }
        }
        
        return array; // Retorna o array gerado.
    }

    public static void salvarArrayParaArquivo(int[] array, String nomeArquivo, String metodo, int tamanho, long duracao, String ordem) throws IOException {
        // Usa FileWriter para escrever os dados em um arquivo.
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            writer.write("Método: " + metodo + "\n"); // Escreve o método de ordenação.
            writer.write("Tamanho: " + tamanho + "\n"); // Escreve o tamanho do array.
            writer.write("Ordem: " + ordem + "\n"); // Escreve a ordem do array.
            writer.write("Tempo: " + duracao + " ms\n"); // Escreve a duração da ordenação.
            writer.write("Array: " + Arrays.toString(array) + "\n"); // Escreve o array em formato de string.
        }
    }
}
