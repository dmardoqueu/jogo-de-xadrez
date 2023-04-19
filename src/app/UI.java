package app;

import xadrez.PecaDeXadrez;
public interface UI {
    public static void imprimirTabuleiro(PecaDeXadrez[][] pecas) {
        System.out.println(" R = RAINHA\n" +
                " C = CAVALO\n" +
                " K = REI (KING)\n" +
                " P = PEÃO\n" +
                " T = TORRE\n" +
                " B = BISPO");
        System.out.println();

        for (int i = 0; i < pecas.length; i++) {
            System.out.print((8 - i) + "  ");
            for (int j = 0; j < pecas.length; j++) {
                imprimirPeca(pecas[i][j]);
            }
            System.out.println();
        }
        System.out.println("   a b c d e f g h");
    }

    private static void imprimirPeca(PecaDeXadrez peca) {
        if (peca == null) {
            System.out.print("-");
        }
        else {
            System.out.print(peca);
        }
        System.out.print(" ");
    }
}
