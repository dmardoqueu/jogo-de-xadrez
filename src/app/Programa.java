package app;

import xadrez.Partida;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDeXadrez;
import xadrez.XadrezException;

import java.util.InputMismatchException;
import java.util.Scanner;


public class Programa {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Partida partida = new Partida();


        while (true) {
            try{
                UI.limparTela();
                UI.imprimirTabuleiro(partida.getPecas());
                System.out.println();
                System.out.print("Inicial: ");
                PosicaoDeXadrez inicial = UI.lerPosicao(scanner);

                boolean[][] movimentosPossiveis = partida.movimentosPossiveis(inicial);
                UI.limparTela();
                UI.imprimirTabuleiro(partida.getPecas(), movimentosPossiveis);

                System.out.println();
                System.out.print("Alvo: ");
                PosicaoDeXadrez alvo = UI.lerPosicao(scanner);

                PecaDeXadrez pecaCapturada = partida.executarMovimento(inicial, alvo);
            } catch (XadrezException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }
    }
}
