import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LeitorArquivos_e_Conversor {

    // Mapas para opcodes e registradores
    private static final Map<String, String> opcodes = new HashMap<>();
    private static final Map<String, String> functs = new HashMap<>();
    private static final Map<String, String> registradores = new HashMap<>();

    static {
        // Inicialização dos opcodes
        opcodes.put("lw", "100011");
        opcodes.put("sw", "101011");
        opcodes.put("addi", "001000");
        opcodes.put("ori", "001101");
        opcodes.put("slti", "001010");
        opcodes.put("sltiu", "001011");
        opcodes.put("addiu", "001001");
        opcodes.put("andi", "001100");
        opcodes.put("xori", "001110");
        opcodes.put("lui", "001111");
        opcodes.put("lb", "100000");
        opcodes.put("lh", "100001");
        opcodes.put("lwl", "100010");
        opcodes.put("lbu", "100100");
        opcodes.put("lhu", "100101");
        opcodes.put("lwr", "100110");
        opcodes.put("sb", "101000");
        opcodes.put("sh", "101001");
        opcodes.put("swl", "101010");
        opcodes.put("swr", "101110");
        opcodes.put("beq", "000100");
        opcodes.put("bne", "000101");
        opcodes.put("blez", "000110");
        opcodes.put("bgtz", "000111");
        opcodes.put("bltz", "000001");
        opcodes.put("bgez", "000001");
        opcodes.put("j", "000010");
        opcodes.put("jal", "000011");

        // Inicialização dos códigos de função
        functs.put("add", "100000");
        functs.put("addu", "100001");
        functs.put("sub", "100010");
        functs.put("subu", "100011");
        functs.put("and", "100100");
        functs.put("or", "100101");
        functs.put("xor", "100110");
        functs.put("nor", "100111");
        functs.put("slt", "101010");
        functs.put("sltu", "101011");
        functs.put("sll", "000000");
        functs.put("srl", "000010");
        functs.put("sra", "000011");
        functs.put("sllv", "000100");
        functs.put("srlv", "000110");
        functs.put("srav", "000111");
        functs.put("mult", "011000");
        functs.put("div", "011010");
        functs.put("divu", "011011");
        functs.put("mfhi", "010000");
        functs.put("mflo", "010010");
        functs.put("mthi", "010001");
        functs.put("mtlo", "010011");
        functs.put("jr", "001000");
        functs.put("jalr", "001001");
        functs.put("multu", "011001");

        // Inicialização dos registradores
        registradores.put("$zero", "00000");
        registradores.put("$at", "00001");
        registradores.put("$v0", "00010");
        registradores.put("$v1", "00011");
        registradores.put("$a0", "00100");
        registradores.put("$a1", "00101");
        registradores.put("$a2", "00110");
        registradores.put("$a3", "00111");
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
        registradores.put("$t8", "11000");
        registradores.put("$t9", "11001");
        registradores.put("$k0", "11010");
        registradores.put("$k1", "11011");
        registradores.put("$gp", "11100");
        registradores.put("$sp", "11101");
        registradores.put("$fp", "11110");
        registradores.put("$ra", "11111");
    }

    public static void main(String[] args) {
        // Nomes dos arquivos para leitura
        String[] nomesArquivos = {"programa1.txt", "programa2.txt", "programa3.txt", "teste.txt"};

        // Nome do arquivo de saída
        String nomeArquivoSaida = "conversao_pronta.txt";

        try (FileWriter writer = new FileWriter(nomeArquivoSaida)) {
            for (String nomeArquivo : nomesArquivos) {
                try (Scanner scanner = new Scanner(new File(nomeArquivo))) {
                    writer.write("Conteúdo do arquivo " + nomeArquivo + ":\n");
                    // Leitura linha a linha do arquivo
                    while (scanner.hasNextLine()) {
                        String linha = scanner.nextLine().trim();
                        writer.write(linha + "\n"); // Escreve a linha original
                        String binario = converterParaBinario(linha); // Converte a linha para binário
                        writer.write(binario + "\n"); // Escreve a linha em binário
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("Arquivo não encontrado - " + nomeArquivo);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de saída - " + nomeArquivoSaida);
        }
    }

    private static String converterParaBinario(String instrucao) {
        // Divide a instrução em partes usando espaço, vírgula e parênteses como delimitadores
        String[] partes = instrucao.split("[ ,()]+");
        if (partes.length < 2) return "00000000000000000000000000000000"; // Instrução inválida

        String op = partes[0]; // Operação
        String rs, rt, rd, shamt, imm;
        StringBuilder binario = new StringBuilder();

        switch (op) {
            case "lw":
            case "sw":
            case "lb":
            case "lh":
            case "lwl":
            case "lbu":
            case "lhu":
            case "lwr":
            case "sb":
            case "sh":
            case "swl":
            case "swr":
                rt = registradores.get(partes[1]); // Registrador de destino
                imm = formatarImediato(partes[2], 16); // Imediato
                rs = registradores.get(partes[3]); // Registrador base
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm); // Monta a instrução binária
                break;
            case "add":
            case "addu":
            case "sub":
            case "subu":
            case "and":
            case "or":
            case "xor":
            case "nor":
            case "slt":
            case "sltu":
                rd = registradores.get(partes[1]); // Registrador de destino
                rs = registradores.get(partes[2]); // Primeiro operando
                rt = registradores.get(partes[3]); // Segundo operando
                shamt = "00000"; // Shift amount (para instruções de shift)
                binario.append("000000").append(rs).append(rt).append(rd).append(shamt).append(functs.get(op)); // Monta a instrução binária
                break;
            case "sll":
            case "srl":
            case "sra":
                rd = registradores.get(partes[1]); // Registrador de destino
                rt = registradores.get(partes[2]); // Primeiro operando
                shamt = formatarImediato(partes[3], 5); // Shift amount
                rs = "00000"; // Registrador base para instruções de shift
                binario.append("000000").append(rs).append(rt).append(rd).append(shamt).append(functs.get(op)); // Monta a instrução binária
                break;
            case "addi":
            case "slti":
            case "sltiu":
            case "addiu":
            case "andi":
            case "ori":
            case "xori":
            case "lui":
                rt = registradores.get(partes[1]); // Registrador de destino
                rs = registradores.get(partes[2]); // Primeiro operando
                imm = formatarImediato(partes[3], 16); // Imediato
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm); // Monta a instrução binária
                break;
            case "beq":
            case "bne":
            case "blez":
            case "bgtz":
            case "bgez":
                rs = registradores.get(partes[1]); // Primeiro operando
                rt = registradores.get(partes[2]); // Segundo operando
                imm = formatarImediato(partes[3], 16); // Imediato
                binario.append(opcodes.get(op)).append(rs).append(rt).append(imm); // Monta a instrução binária
                break;
            case "j":
            case "jal":
                imm = formatarImediato(partes[1], 26); // Imediato
                binario.append(opcodes.get(op)).append(imm); // Monta a instrução binária
                break;
            case "jr":
            case "jalr":
                rs = registradores.get(partes[1]); // Primeiro operando
                binario.append(opcodes.get(op)).append(rs).append("000000000000000").append(functs.get(op)); // Monta a instrução binária
                break;
            case "mfhi":
            case "mflo":
            case "mthi":
            case "mtlo":
                rd = registradores.get(partes[1]); // Registrador de destino
                binario.append("000000000000").append(rd).append("00000").append(functs.get(op)); // Monta a instrução binária
                break;
            case "mult":
            case "div":
            case "divu":
            case "multu":
                rs = registradores.get(partes[1]); // Primeiro operando
                rt = registradores.get(partes[2]); // Segundo operando
                binario.append("000000").append(rs).append(rt).append("0000000000").append(functs.get(op)); // Monta a instrução binária
                break;
            case "sllv":
            case "srlv":
            case "srav":
                rd = registradores.get(partes[1]); // Registrador de destino
                rt = registradores.get(partes[2]); // Registrador com o valor a ser shiftado
                rs = registradores.get(partes[3]); // Registrador com o shift amount
                binario.append("000000").append(rs).append(rt).append(rd).append("00000").append(functs.get(op)); // Monta a instrução binária
                break;
            case "bltz":
                rs = registradores.get(partes[1]); // Registrador base
                imm = formatarImediato(partes[2], 16); // Imediato
                binario.append(opcodes.get(op)).append(rs).append("00000").append(imm); // Monta a instrução binária
                break;
            default:
                System.err.println("Instrução desconhecida: " + instrucao);
                binario.append("00000000000000000000000000000000"); // Para instrução desconhecida
                break;
        }

        return binario.toString();
    }

    // Formatar os valores negativos para um equivalente em complemento de dois
    private static String formatarImediato(String imediato, int tamanho) {
        int valor;
        try {
            valor = Integer.parseInt(imediato);
        } catch (NumberFormatException e) {
            return "0000000000000000"; // Valor inválido, retorna zero
        }
        if (valor < 0) {
            valor = (1 << tamanho) + valor; // Converte para complemento de dois
        }
        return String.format("%" + tamanho + "s", Integer.toBinaryString(valor)).replace(' ', '0');
    }
}
