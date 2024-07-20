import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class LeitorArquivos {

    // Mapas para opcodes e registradores
    private static final Map<String, String> opcodes = new HashMap<>();
    private static final Map<String, String> functs = new HashMap<>();
    private static final Map<String, String> registers = new HashMap<>();

    static {
        // Inicialização dos opcodes
        opcodes.put("lw", "100011");
        opcodes.put("sw", "101011");
        opcodes.put("add", "000000");
        opcodes.put("mult", "000000");
        opcodes.put("srl", "000000");
        opcodes.put("sll", "000000");
        opcodes.put("addi", "001000");
        opcodes.put("ori", "001101");

        // Inicialização dos códigos de função
        functs.put("add", "100000");
        functs.put("mult", "011000");
        functs.put("srl", "000010");
        functs.put("sll", "000000");

        // Inicialização dos registradores
        registers.put("$zero", "00000");
        registers.put("$t0", "01000");
        registers.put("$t1", "01001");
        registers.put("$t2", "01010");
        registers.put("$t3", "01011");
        registers.put("$t4", "01100");
        registers.put("$t5", "01101");
        registers.put("$t6", "01110");
        registers.put("$t7", "01111");
        registers.put("$s0", "10000");
        registers.put("$s1", "10001");
        registers.put("$s2", "10010");
        registers.put("$s3", "10011");
        registers.put("$s4", "10100");
        registers.put("$s5", "10101");
        registers.put("$s6", "10110");
        registers.put("$s7", "10111");
    }

    public static void main(String[] args) {
        // Nomes dos arquivos para leitura
        String[] nomesArquivos = {"programa1.txt", "programa2.txt", "programa3.txt"};

        for (String nomeArquivo : nomesArquivos) {
            try (Scanner scanner = new Scanner(new File(nomeArquivo))) {
                System.out.println("\nConteúdo do arquivo " + nomeArquivo + ":");
                while (scanner.hasNextLine()) {
                    String linha = scanner.nextLine();
                    System.out.println(linha);
                    String binario = converterParaBinario(linha);
                    System.out.println(binario);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Erro: Arquivo não encontrado - " + nomeArquivo);
            }
        }
    }

    private static String converterParaBinario(String instrucao) {
        String[] partes = instrucao.split("[ ,()]+");
        String op = partes[0];
        String rs, rt, rd, shamt, imm;
        StringBuilder binario = new StringBuilder();

        switch (op) {
            case "lw":
            case "sw":
                rt = registers.get(partes[1]);
                imm = String.format("%16s", Integer.toBinaryString(Integer.parseInt(partes[2]))).replace(' ', '0');
                rs = registers.get(partes[3]);
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm);
                break;
            case "add":
            case "sll":
            case "srl":
                rd = registers.get(partes[1]);
                rs = registers.get(partes[2]);
                if (op.equals("sll") || op.equals("srl")) {
                    rt = registers.get(partes[1]);
                    shamt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(partes[3]))).replace(' ', '0');
                    binario.append(opcodes.get(op)).append("00000").append(rt).append(rs).append(shamt).append(functs.get(op));
                } else {
                    rt = registers.get(partes[3]);
                    binario.append(opcodes.get(op)).append(rs).append(rt).append(rd).append("00000").append(functs.get(op));
                }
                break;
            case "mult":
                rs = registers.get(partes[1]);
                rt = registers.get(partes[2]);
                binario.append(opcodes.get(op)).append(rs).append(rt).append("0000000000").append(functs.get(op));
                break;
            case "addi":
            case "ori":
                rt = registers.get(partes[1]);
                rs = registers.get(partes[2]);
                imm = String.format("%16s", Integer.toBinaryString(Integer.parseInt(partes[3]))).replace(' ', '0');
                if (imm.length() > 16) {
                    imm = imm.substring(imm.length() - 16);
                }
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm);
                break;
            default:
                System.err.println("Erro: Instrução não reconhecida - " + instrucao);
        }

        return binario.toString();
    }
}
