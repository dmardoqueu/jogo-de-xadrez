package app;

import xadrez.Partida;


public class Programa {
    public static void main(String[] args) {
        Partida partida = new Partida();
        UI.imprimirTabuleiro(partida.getPecas());
    }
}
