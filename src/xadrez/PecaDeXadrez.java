package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class PecaDeXadrez extends Peca {
    private Cor cor;
    private int contagemDeMovimentos;

    public PecaDeXadrez(Tabuleiro tabuleiro, Cor cor) {
        super(tabuleiro);
        this.cor = cor;
    }

    public Cor getCor() {
        return cor;
    }

    public int getContagemDeMovimentos() {
        return contagemDeMovimentos;
    }

    public void incrementarContagem() {
        contagemDeMovimentos++;
    }

    public void decrementarContagem() {
        contagemDeMovimentos--;
    }

    public PosicaoDeXadrez getPosicaoDeXadrez() {
        return PosicaoDeXadrez.reposicionar(posicao);
    }

    protected boolean isPecaOponente(Posicao posicao) {
        PecaDeXadrez p = (PecaDeXadrez)getTabuleiro().peca(posicao);
        return p != null && p.getCor() != cor;
    }
}
