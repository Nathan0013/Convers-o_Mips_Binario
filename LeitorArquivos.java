import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class LeitorArquivos {

    // Mapas para opcodes e registradores
    private static final Map<String, String> opcodes = new HashMap<>();
    private static final Map<String, String> functs = new HashMap<>();
    private static final Map<String, String> registradores = new HashMap<>();

    static {
        // Inicialização dos opcodes
        opcodes.put("lw", "100011"); // Carregar palavra
        opcodes.put("sw", "101011"); // Salvar palavra
        opcodes.put("add", "000000"); // Adição
        opcodes.put("mult", "000000"); // Multiplicação
        opcodes.put("srl", "000000"); // Deslocamento lógico à direita
        opcodes.put("sll", "000000"); // Deslocamento lógico à esquerda
        opcodes.put("addi", "001000"); // Adição imediata
        opcodes.put("ori", "001101"); // OU imediato

        // Inicialização dos códigos de função
        functs.put("add", "100000"); // Função para adição
        functs.put("mult", "011000"); // Função para multiplicação
        functs.put("srl", "000010"); // Função para deslocamento lógico à direita
        functs.put("sll", "000000"); // Função para deslocamento lógico à esquerda

        // Inicialização dos registradores
        registradores.put("$zero", "00000");
        registradores.put("$t0", "01000");
        registradores.put("$t1", "01001");
        registradores.put("$t2", "01010");
        registradores.put("$t3", "01011");
        registradores.put("$t4", "01100");
        registradores.put("$t5", "01101");
        registradores.put("$t6", "01110");
        registradores.put("$t7", "01111");
        registradores.put("$s0", "10000");
        registradores.put("$s1", "10001");
        registradores.put("$s2", "10010");
        registradores.put("$s3", "10011");
        registradores.put("$s4", "10100");
        registradores.put("$s5", "10101");
        registradores.put("$s6", "10110");
        registradores.put("$s7", "10111");
    }

    public static void main(String[] args) {
        // Nomes dos arquivos para leitura
        String[] nomesArquivos = {"programa1.txt", "programa2.txt", "programa3.txt"};

        for (String nomeArquivo : nomesArquivos) {
            try (Scanner scanner = new Scanner(new File(nomeArquivo))) {
                System.out.println("\nConteúdo do arquivo " + nomeArquivo + ":");
                // Leitura linha a linha do arquivo
                while (scanner.hasNextLine()) {
                    String linha = scanner.nextLine();
                    System.out.println(linha); // Imprime a linha original
                    String binario = converterParaBinario(linha); // Converte a linha para binário
                    System.out.println(binario); // Imprime a linha em binário
                }
            } catch (FileNotFoundException e) {
                System.err.println("Arquivo não encontrado - " + nomeArquivo);
            }
        }
    }

    private static String converterParaBinario(String instrucao) {
        // Divide a instrução em partes usando espaço, vírgula e parênteses como delimitadores
        String[] partes = instrucao.split("[ ,()]+");
        String op = partes[0]; // Operação
        String rs, rt, rd, shamt, imm;
        StringBuilder binario = new StringBuilder();

        switch (op) {
            case "lw":
            case "sw":
                rt = registradores.get(partes[1]); // Registrador de destino
                imm = String.format("%16s", Integer.toBinaryString(Integer.parseInt(partes[2]))).replace(' ', '0'); // Imediato
                rs = registradores.get(partes[3]); // Registrador base
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm); // Monta a instrução binária
                break;
            case "add":
            case "sll":
            case "srl":
                rd = registradores.get(partes[1]); // Registrador de destino
                rs = registradores.get(partes[2]); // Primeiro operando
                if (op.equals("sll") || op.equals("srl")) {
                    rt = registradores.get(partes[3]); // Segundo operando
                    shamt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(partes[3]))).replace(' ', '0'); // Shift amount
                    binario.append(opcodes.get(op)).append("00000").append(rt).append(rd).append(shamt).append(functs.get(op)); // Monta a instrução binária
                } else {
                    rt = registradores.get(partes[3]); // Segundo operando
                    binario.append(opcodes.get(op)).append(rs).append(rt).append(rd).append("00000").append(functs.get(op)); // Monta a instrução binária
                }
                break;
            case "mult":
                rs = registradores.get(partes[1]); // Primeiro operando
                rt = registradores.get(partes[2]); // Segundo operando
                binario.append(opcodes.get(op)).append(rs).append(rt).append("0000000000").append(functs.get(op)); // Monta a instrução binária
                break;
            case "addi":
            case "ori":
                rt = registradores.get(partes[1]); // Registrador de destino
                rs = registradores.get(partes[2]); // Registrador base
                int valorImediato = Integer.parseInt(partes[3]);
                if (valorImediato < 0) {
                    imm = Integer.toBinaryString(valorImediato);
                    imm = imm.substring(imm.length() - 16); // Pega os últimos 16 bits do valor imediato
                } else {
                    imm = String.format("%16s", Integer.toBinaryString(valorImediato)).replace(' ', '0'); // Imediato
                }
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm); // Monta a instrução binária
                break;
            default:
                System.err.println("Instrução não reconhecida - " + instrucao);
        }

        return binario.toString(); // Retorna a instrução binária
    }
}
