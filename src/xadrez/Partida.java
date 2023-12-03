package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class Partida {

// R = RAINHA
// C = CAVALO
// K = REI (KING)
// P = PEÃO
// T = TORRE
// B = BISPO

    private Tabuleiro tabuleiro;

    public Partida() {
        tabuleiro = new Tabuleiro(8, 8);
        iniciarPartida();
    }

    public PecaDeXadrez[][] getPecas() {
        PecaDeXadrez[][] matriz = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                matriz[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
            }
        }
        return matriz;
    }

    public PecaDeXadrez executarMovimento(PosicaoDeXadrez posicaoInicial, PosicaoDeXadrez posicaoAlvo) {
        Posicao inicial = posicaoInicial.posicionar();
        Posicao alvo = posicaoAlvo.posicionar();
        validarPosicaoInicial(inicial);
        Peca pecaCapturada = fazerMovimento(inicial, alvo);
        return (PecaDeXadrez)pecaCapturada;
    }

    private Peca fazerMovimento(Posicao inicial, Posicao alvo) {
        Peca p = tabuleiro.removerPeca(inicial);
        Peca pecaCapturada = tabuleiro.removerPeca(alvo);
        tabuleiro.lugarDaPeca(p, alvo);
        return pecaCapturada;
    }

    public void validarPosicaoInicial(Posicao posicao) {
        if (!tabuleiro.temPeca(posicao)) {
            throw new XadrezException("Não existe peça na posição de origem.");
        }
    }

    private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
        tabuleiro.lugarDaPeca(peca, new PosicaoDeXadrez(coluna, linha).posicionar());
    }

    private void iniciarPartida() {
        colocarNovaPeca('c', 1, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('c', 2, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('d', 2, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('e', 2, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('e', 1, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('d', 1, new Rei(tabuleiro, Cor.BRANCO));

        colocarNovaPeca('c', 7, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('c', 8, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('d', 7, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('e', 7, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('e', 8, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('d', 8, new Rei(tabuleiro, Cor.PRETO));
    }
}
