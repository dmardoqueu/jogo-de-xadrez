package app;

import xadrez.Partida;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDeXadrez;
import xadrez.XadrezException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Programa {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Partida partida = new Partida();
        List<PecaDeXadrez> capturadas = new ArrayList<>();


        while (!partida.getXequemate()) {
            try{
                UI.limparTela();
                UI.imprimirPartida(partida, capturadas);
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

                if (pecaCapturada != null) {
                    capturadas.add(pecaCapturada);
                }

                if (partida.getPromovida() != null) {
                    System.out.print("Informe a peça para promoção (B/C/T/Q): ");
                    String tipo = scanner.nextLine().toUpperCase();
                    while (!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
                        System.out.print("Valor inválido! Informe a peça para promoção(B/C/T/Q): ");
                        tipo = scanner.nextLine().toUpperCase();
                    }
                    partida.substituirPromovida(tipo);
                }
            } catch (XadrezException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }
        UI.limparTela();
        UI.imprimirPartida(partida, capturadas);
    }
}
