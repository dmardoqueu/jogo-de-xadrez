package app;

import xadrez.Partida;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDeXadrez;

import java.util.Scanner;


public class Programa {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Partida partida = new Partida();


        while (true) {
            UI.imprimirTabuleiro(partida.getPecas());
            System.out.println();
            System.out.print("Inicial: ");
            PosicaoDeXadrez inicial = UI.lerPosicao(scanner);

            System.out.println();
            System.out.print("Alvo: ");
            PosicaoDeXadrez alvo = UI.lerPosicao(scanner);

            PecaDeXadrez pecaCapturada = partida.executarMovimento(inicial, alvo);
        }
    }
}
