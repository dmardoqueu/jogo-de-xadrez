package app;

import xadrez.Cor;
import xadrez.Partida;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDeXadrez;


import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public interface UI {

//    https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static PosicaoDeXadrez lerPosicao (Scanner scanner) {
        try {
            String s = scanner.nextLine();
            char coluna = s.charAt(0);
            int linha = Integer.parseInt(s.substring(1));
            return new PosicaoDeXadrez(coluna, linha);
        } catch (RuntimeException e) {
            throw new InputMismatchException("Erro ao ler a posição. Valores validos são de a1 à h8.");
        }
    }

    public static void imprimirPartida(Partida partida, List<PecaDeXadrez> capturadas) {
        imprimirTabuleiro(partida.getPecas());
        System.out.println();
        imprimirPecasCapturadas(capturadas);
        System.out.println();
        System.out.println("Turno: " + partida.getTurno());
        if (!partida.getXequemate()) {
            System.out.println("Aguardando jogador: " + partida.getJogadorAtual());
            if (partida.getXeque()) {
                System.out.print("XEQUE!");
            }
        } else {
            System.out.println("XEQUEMATE!");
            System.out.println("Vencedor: " + partida.getJogadorAtual());
        }
    }

    public static void imprimirTabuleiro(PecaDeXadrez[][] pecas) {
//        System.out.println(" R = RAINHA\n" +
//                " C = CAVALO\n" +
//                " K = REI (KING)\n" +
//                " P = PEÃO\n" +
//                " T = TORRE\n" +
//                " B = BISPO");
//        System.out.println();

        for (int i = 0; i < pecas.length; i++) {
            System.out.print((8 - i) + "  ");
            for (int j = 0; j < pecas.length; j++) {
                imprimirPeca(pecas[i][j], false);
            }
            System.out.println();
        }
        System.out.println("   a b c d e f g h");
    }

    public static void imprimirTabuleiro(PecaDeXadrez[][] pecas, boolean[][] movimentosPossiveis) {
//        System.out.println(" R = RAINHA\n" +
//                " C = CAVALO\n" +
//                " K = REI (KING)\n" +
//                " P = PEÃO\n" +
//                " T = TORRE\n" +
//                " B = BISPO");
//        System.out.println();

        for (int i = 0; i < pecas.length; i++) {
            System.out.print((8 - i) + "  ");
            for (int j = 0; j < pecas.length; j++) {
                imprimirPeca(pecas[i][j], movimentosPossiveis[i][j]);
            }
            System.out.println();
        }
        System.out.println("   a b c d e f g h");
    }

    private static void imprimirPeca(PecaDeXadrez peca, boolean fundo) {
        if(fundo) {
            System.out.print(ANSI_BLUE_BACKGROUND);
        }
        if (peca == null) {
            System.out.print("-" + ANSI_RESET);
        }
        else {
            if (peca.getCor() == Cor.BRANCO) {
                System.out.print(ANSI_WHITE + peca + ANSI_RESET);
            }
            else {
                System.out.print(ANSI_YELLOW + peca + ANSI_RESET);
            }
        }
        System.out.print(" ");
    }

    private static void imprimirPecasCapturadas(List<PecaDeXadrez> capturadas) {
        List<PecaDeXadrez> brancas = capturadas.stream().filter(x -> x.getCor() == Cor.BRANCO).collect(Collectors.toList());
        List<PecaDeXadrez> pretas = capturadas.stream().filter(x -> x.getCor() == Cor.PRETO).collect(Collectors.toList());
        System.out.println("Peças capturadas: ");
        System.out.print("Brancas: ");
        System.out.print(ANSI_WHITE);
        System.out.println(Arrays.toString(brancas.toArray()));
        System.out.print(ANSI_RESET);
        System.out.print("Pretas: ");
        System.out.print(ANSI_YELLOW);
        System.out.println(Arrays.toString(pretas.toArray()));
        System.out.print(ANSI_RESET);
    }
}
