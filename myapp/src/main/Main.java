package myapp.src.main;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Criação da janela principal
        JFrame frame = new JFrame("GUI de Ordenação");
        frame.setSize(600, 600); // Aumentado para acomodar a barra de progresso
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Adicionando o rótulo para seleção do tamanho do array
        JLabel labelTamanho = new JLabel("Escolha o tamanho do array:");
        labelTamanho.setBounds(160, 20, 360, 40);
        frame.add(labelTamanho);

        // Adicionando o ComboBox para seleção de tamanhos predefinidos
        String[] tamanhos = { "100", "1000", "10000", "100000", "1000000" };
        JComboBox<String> comboBoxTamanho = new JComboBox<>(tamanhos);
        comboBoxTamanho.setBounds(160, 60, 100, 30);
        frame.add(comboBoxTamanho);

        // Campo de texto para inserção de tamanho personalizado
        JTextField textFieldTamanhoPersonalizado = new JTextField();
        textFieldTamanhoPersonalizado.setBounds(265, 60, 100, 30);
        frame.add(textFieldTamanhoPersonalizado);

        // Botão para iniciar a ordenação
        JButton botaoOrdenar = new JButton("Ordenar");
        botaoOrdenar.setBounds(160, 100, 100, 30);
        frame.add(botaoOrdenar);

        // Área de texto para exibir os resultados
        JTextArea areaResultado = new JTextArea();
        areaResultado.setBounds(40, 180, 500, 300);
        areaResultado.setEditable(false);
        frame.add(areaResultado);

        // Botão para salvar os resultados em um arquivo CSV
        JButton botaoSalvar = new JButton("Salvar Resultado");
        botaoSalvar.setBounds(270, 100, 150, 30);
        frame.add(botaoSalvar);

        // Botão para abrir o arquivo CSV no Excel
        JButton botaoGrafico = new JButton("Abrir CSV no Excel");
        botaoGrafico.setBounds(200, 500, 150, 30);
        frame.add(botaoGrafico);

        // Adicionando a barra de progresso
        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(40, 140, 500, 30);
        progressBar.setStringPainted(true); // Exibe o percentual concluído na barra
        frame.add(progressBar);

        // Adicionando ação ao botão de ordenação
        botaoOrdenar.addActionListener(e -> {
            // Obtendo o tamanho do array a ser ordenado
            String tamanhoStr = textFieldTamanhoPersonalizado.getText();
            int tamanho;

            if (tamanhoStr.isEmpty()) {
                tamanho = Integer.parseInt((String) comboBoxTamanho.getSelectedItem());
            } else {
                try {
                    tamanho = Integer.parseInt(tamanhoStr);
                } catch (NumberFormatException ex) {
                    areaResultado.setText("Tamanho inválido. Por favor, insira um número válido.");
                    return;
                }
            }

            // Definindo os métodos e ordens de ordenação
            String[] metodos = { "InsertionSort", "BubbleSort", "SelectionSort" };
            String[] ordens = { "Crescente", "Decrescente", "Nenhum" };
            StringBuilder resultados = new StringBuilder();
            resultados.append("Tamanho ").append(tamanho).append(" : Execuções\n");

            // ExecutorService para executar tarefas de forma assíncrona
            ExecutorService executor = Executors.newSingleThreadExecutor();
            progressBar.setIndeterminate(true); // Ativa o modo indeterminado enquanto ordena

            // SwingWorker para executar a ordenação em segundo plano e atualizar a GUI
            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    int totalTasks = metodos.length * ordens.length; // Calcula o total de tarefas, que é o número de combinações de métodos de ordenação e ordens.
                    int completedTasks = 0; // Inicializa o contador de tarefas concluídas.

                    // Itera sobre cada ordem de ordenação.
                    for (String ordem : ordens) {
                        resultados.append("Ordem ").append(ordem).append("\n"); // Adiciona a ordem atual ao resultado.

                        // Itera sobre cada método de ordenação.
                        for (String metodo : metodos) {
                            long somaDuracao = 0; // Inicializa a soma das durações das execuções.
                            int repeticoes = 10; // Define o número de execuções para calcular a média.

                            // Executa a ordenação repeticoes vezes para calcular a média.
                            for (int i = 0; i < repeticoes; i++) {
                                int[] arr = Utils.gerarArrayAleatorio(tamanho, ordem); // Gera um array aleatório com base no tamanho e ordem selecionados.

                                // Define a tarefa de ordenação como um Callable que retorna a duração da ordenação.
                                Callable<Long> task = () -> {
                                    long tempoInicio = System.nanoTime(); // Marca o tempo de início.
                                    switch (metodo) { // Seleciona o método de ordenação baseado na string.
                                        case "InsertionSort" -> InsertionSort.ordenar(arr);
                                        case "BubbleSort" -> BubbleSort.ordenar(arr);
                                        case "SelectionSort" -> SelectionSort.ordenar(arr);
                                    }
                                    long tempoFim = System.nanoTime(); // Marca o tempo de fim.
                                    return (tempoFim - tempoInicio) / 1000000; // Calcula a duração em milissegundos e retorna.
                                };

                                try {
                                    Future<Long> future = executor.submit(task); // Submete a tarefa para execução no executor.
                                    long duracao = future.get(30, TimeUnit.SECONDS); // Espera pela conclusão da tarefa com timeout de 30 segundos.
                                    somaDuracao += duracao; // Adiciona a duração ao total.
                                } catch (ExecutionException | InterruptedException
                                        | java.util.concurrent.TimeoutException ex) {
                                    somaDuracao += 30000; // Se houver exceção, considera a duração como 30000 ms (timeout).
                                }
                            }
                            long duracaoMedia = somaDuracao / repeticoes; // Calcula a duração média das execuções.
                            resultados.append(metodo).append(": ").append(duracaoMedia).append(" ms\n"); // Adiciona o resultado ao StringBuilder.

                            completedTasks++; // Incrementa o número de tarefas concluídas.
                            int progress = (int) ((completedTasks / (float) totalTasks) * 100); // Calcula o progresso percentual.
                            publish(progress); // Publica o progresso para atualizar a barra de progresso.
                        }
                    }
                    executor.shutdown(); // Encerra o executor.
                    return null; // Retorna null porque o SwingWorker é do tipo Void.
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    int progress = chunks.get(chunks.size() - 1); // Obtém o último valor de progresso publicado.
                    progressBar.setValue(progress); // Define o valor da barra de progresso.
                }

                @Override
                protected void done() {
                    try {
                        get(); // Obtém o resultado da execução do doInBackground (lança exceções se houver).
                        areaResultado.setText(resultados.toString()); // Define o texto da área de resultado com os resultados.
                    } catch (InterruptedException | ExecutionException ex) {
                        areaResultado.setText("Erro ao executar a ordenação: " + ex.getMessage()); // Exibe mensagem de erro se houver exceção.
                    } finally {
                        progressBar.setIndeterminate(false); // Desativa o modo indeterminado da barra de progresso.
                        progressBar.setValue(100); // Marca a barra de progresso como concluída.
                    }
                }
            };

            worker.execute();
        });

        // Adicionando ação ao botão de salvar
        botaoSalvar.addActionListener(e -> {
            try {
                try (FileWriter writer = new FileWriter("resultados.csv")) {
                    writer.write(areaResultado.getText().replace(" ", ",").replace("\n", System.lineSeparator()));
                }
                areaResultado.append("\nSalvo em resultados.csv");
            } catch (IOException ioException) {
                areaResultado.append("\nFalha ao salvar o arquivo");
            }
        });

        // Adicionando ação ao botão de abrir o CSV no Excel
        botaoGrafico.addActionListener(e -> {
            try {
                File csvFile = new File("resultados.csv");
                if (csvFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(csvFile);
                } else {
                    areaResultado.append(
                            "\nO arquivo resultados.csv não foi encontrado ou a funcionalidade de Desktop não é suportada.");
                }
            } catch (IOException ioException) {
                areaResultado.append("\nFalha ao abrir o arquivo CSV no Excel: " + ioException.getMessage());
            }
        });

        // Tornando a janela visível
        frame.setVisible(true);
    }
}
