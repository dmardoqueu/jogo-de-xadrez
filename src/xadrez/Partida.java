package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Partida {

// R = RAINHA
// C = CAVALO
// K = REI (KING)
// P = PEÃO
// T = TORRE
// B = BISPO

    private Tabuleiro tabuleiro;
    private int turno;
    private Cor jogadorAtual;
    private boolean xeque;
    private boolean xequemate;

    private List<Peca> pecasNoTabuleiro = new ArrayList<>();
    private List<Peca> pecasCapturadas = new ArrayList<>();

    public Partida() {
        tabuleiro = new Tabuleiro(8, 8);
        turno = 1;
        jogadorAtual = Cor.BRANCO;
        iniciarPartida();
    }

    public int getTurno() {
        return turno;
    }

    public Cor getJogadorAtual() {
        return jogadorAtual;
    }

    public boolean getXeque() {
        return xeque;
    }

    public boolean getXequemate() {
        return xequemate;
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

    public boolean[][] movimentosPossiveis(PosicaoDeXadrez posicaoInicial) {
        Posicao posicao = posicaoInicial.posicionar();
        validarPosicaoInicial(posicao);
        return tabuleiro.peca(posicao).movimentosPossiveis();
    }

    public PecaDeXadrez executarMovimento(PosicaoDeXadrez posicaoInicial, PosicaoDeXadrez posicaoAlvo) {
        Posicao inicial = posicaoInicial.posicionar();
        Posicao alvo = posicaoAlvo.posicionar();
        validarPosicaoInicial(inicial);
        validarPosicaoAlvo(inicial, alvo);
        Peca pecaCapturada = fazerMovimento(inicial, alvo);

        if (testeXeque(jogadorAtual)) {
            desfazerMovimento(inicial, alvo, pecaCapturada);
            throw new XadrezException("Você não pode se colocar em xeque");
        }

        xeque = (testeXeque(oponente(jogadorAtual))) ? true : false;

        if (testeXequemate(oponente(jogadorAtual))) {
            xequemate = true;
        } else {
            proximoTurno();
        }

        return (PecaDeXadrez)pecaCapturada;
    }

    private Peca fazerMovimento(Posicao inicial, Posicao alvo) {
        PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removerPeca(inicial);
        p.incrementarContagem();
        Peca pecaCapturada = tabuleiro.removerPeca(alvo);
        tabuleiro.lugarDaPeca(p, alvo);

        if (pecaCapturada != null) {
            pecasNoTabuleiro.remove(pecaCapturada);
            pecasCapturadas.add(pecaCapturada);
        }
        return pecaCapturada;
    }

    private void desfazerMovimento(Posicao inicial, Posicao alvo, Peca pecaCapturada) {
        PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removerPeca(alvo);
        p.decrementarContagem();
        tabuleiro.lugarDaPeca(p, inicial);

        if (pecaCapturada != null) {
            tabuleiro.lugarDaPeca(pecaCapturada, alvo);
            pecasCapturadas.remove(pecaCapturada);
            pecasNoTabuleiro.add(pecaCapturada);
        }
    }

    public void validarPosicaoInicial(Posicao posicao) {
        if (!tabuleiro.temPeca(posicao)) {
            throw new XadrezException("Não existe peça na posição de origem.");
        }
        if(jogadorAtual != ((PecaDeXadrez)tabuleiro.peca(posicao)).getCor()) {
            throw new XadrezException("A peça escolhida não é sua.");
        }
        if (!tabuleiro.peca(posicao).existeMovimentoPossivel()) {
            throw new XadrezException("Não existem movimentos possíveis para a peça escolhida.");
        }
    }

    private void validarPosicaoAlvo(Posicao inicial, Posicao alvo) {
        if (!tabuleiro.peca(inicial).movimentoPossivel(alvo)) {
            throw new XadrezException("A peça escolhida não pode se mover para a posição de destino.");
        }
    }

    public void proximoTurno() {
        turno++;
        jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }

    private Cor oponente(Cor cor) {
        return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }

    private PecaDeXadrez rei(Cor cor) {
        List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
        for (Peca p : lista) {
            if (p instanceof Rei) {
                return (PecaDeXadrez)p;
            }
        }
        throw new IllegalStateException("Não existe um rei da cor " + cor + " no tabuleiro.");
    }

    private boolean testeXeque(Cor cor) {
        Posicao posicaoDoRei = rei(cor).getPosicaoDeXadrez().posicionar();
        List<Peca> pecasDoOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
        for (Peca p : pecasDoOponente) {
            boolean[][] mat = p.movimentosPossiveis();
            if (mat[posicaoDoRei.getLinha()][posicaoDoRei.getColuna()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testeXequemate(Cor cor) {
        if (!testeXeque(cor)) {
            return false;
        }
        List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
        for (Peca p : lista) {
            boolean[][] mat = p.movimentosPossiveis();
            for (int i = 0; i < tabuleiro.getLinhas(); i++) {
                for (int j = 0; j < tabuleiro.getColunas(); j++) {
                    if (mat[i][j]) {
                        Posicao inicial = ((PecaDeXadrez)p).getPosicaoDeXadrez().posicionar();
                        Posicao alvo = new Posicao(i, j);
                        Peca pecaCapturada = fazerMovimento(inicial, alvo);
                        boolean testeXeque = testeXeque(cor);
                        desfazerMovimento(inicial, alvo, pecaCapturada);
                        if (!testeXeque) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
        tabuleiro.lugarDaPeca(peca, new PosicaoDeXadrez(coluna, linha).posicionar());
        pecasNoTabuleiro.add(peca);
    }

    private void iniciarPartida() {
        colocarNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO));

        colocarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
        colocarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO));
        colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO));
        colocarNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO));
    }
}
