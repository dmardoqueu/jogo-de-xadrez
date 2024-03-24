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
    private PecaDeXadrez dePassagemVulneravel;
    private PecaDeXadrez promovida;

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

    public PecaDeXadrez getDePassagemVulneravel() {
        return dePassagemVulneravel;
    }



    public PecaDeXadrez getPromovida() {
        return promovida;
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

        PecaDeXadrez pecaMovida = (PecaDeXadrez) tabuleiro.peca(alvo);

        // MOVIMENTO ESPECIAL PROMOÇÃO
        promovida = null;
        if (pecaMovida instanceof Peao) {
            if (pecaMovida.getCor() == Cor.BRANCO && alvo.getLinha() == 0 || pecaMovida.getCor() == Cor.PRETO && alvo.getLinha() == 7) {
                promovida = (PecaDeXadrez) tabuleiro.peca(alvo);
                promovida = substituirPromovida("Q");
            }

        }

        xeque = (testeXeque(oponente(jogadorAtual))) ? true : false;

        if (testeXequemate(oponente(jogadorAtual))) {
            xequemate = true;
        } else {
            proximoTurno();
        }

        // movimento especial DE PASSAGEM
        if (pecaMovida instanceof Peao && (alvo.getLinha() == inicial.getLinha() - 2 || alvo.getLinha() == inicial.getLinha() + 2)) {
            dePassagemVulneravel = pecaMovida;
        } else {
            dePassagemVulneravel = null;
        }

        return (PecaDeXadrez) pecaCapturada;
    }


    public PecaDeXadrez substituirPromovida(String tipo) {
        if (promovida == null) {
            throw new IllegalStateException("Não há peça para ser promovida");
        }
        if (!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
            return promovida;
        }
        Posicao posicao = promovida.getPosicaoDeXadrez().posicionar();
        Peca peca = tabuleiro.removerPeca(posicao);
        pecasNoTabuleiro.remove(peca);

        PecaDeXadrez novaPeca = novaPeca(tipo, promovida.getCor());
        tabuleiro.lugarDaPeca(novaPeca, posicao);
        pecasNoTabuleiro.add(novaPeca);

        return novaPeca;
    }

    private PecaDeXadrez novaPeca(String tipo, Cor cor) {
        if (tipo.equals("B")) return new Bispo(tabuleiro, cor);
        if (tipo.equals("C")) return new Cavalo(tabuleiro, cor);
        if (tipo.equals("Q")) return new Rainha(tabuleiro, cor);
        return new Torre(tabuleiro, cor);
    }

    private Peca fazerMovimento(Posicao inicial, Posicao alvo) {
        PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removerPeca(inicial);
        p.incrementarContagem();
        Peca pecaCapturada = tabuleiro.removerPeca(alvo);
        tabuleiro.lugarDaPeca(p, alvo);

        if (pecaCapturada != null) {
            pecasNoTabuleiro.remove(pecaCapturada);
            pecasCapturadas.add(pecaCapturada);
        }

        // movimento especial roque (lado do rei) TORRE
        if (p instanceof Rei && alvo.getColuna() == inicial.getColuna() + 2) {
            Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() + 3);
            Posicao alvoTorre = new Posicao(inicial.getLinha(), inicialTorre.getColuna() + 1);
            PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removerPeca(inicialTorre);
            tabuleiro.lugarDaPeca(torre, alvoTorre);
            torre.incrementarContagem();
        }

        // movimento especial roque (lado da rainha) TORRE
        if (p instanceof Rei && alvo.getColuna() == inicial.getColuna() - 2) {
            Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() - 4);
            Posicao alvoTorre = new Posicao(inicial.getLinha(), inicialTorre.getColuna() - 1);
            PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removerPeca(inicialTorre);
            tabuleiro.lugarDaPeca(torre, alvoTorre);
            torre.incrementarContagem();
        }

        // movimento especial de passagem
        if (p instanceof Peao) {
            if (inicial.getColuna() != alvo.getColuna() && pecaCapturada == null) {
                Posicao posicaoDoPeao;
                if (p.getCor() == Cor.BRANCO) {
                    posicaoDoPeao = new Posicao(alvo.getLinha() + 1, alvo.getColuna());
                } else {
                    posicaoDoPeao = new Posicao(alvo.getLinha() - 1, alvo.getColuna());
                }
                pecaCapturada = tabuleiro.removerPeca(posicaoDoPeao);
                pecasCapturadas.add(pecaCapturada);
                pecasNoTabuleiro.remove(pecaCapturada);
            }
        }

        return pecaCapturada;
    }

    private void desfazerMovimento(Posicao inicial, Posicao alvo, Peca pecaCapturada) {
        PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removerPeca(alvo);
        p.decrementarContagem();
        tabuleiro.lugarDaPeca(p, inicial);

        if (pecaCapturada != null) {
            tabuleiro.lugarDaPeca(pecaCapturada, alvo);
            pecasCapturadas.remove(pecaCapturada);
            pecasNoTabuleiro.add(pecaCapturada);
        }

        // movimento especial roque (lado do rei) TORRE
        if (p instanceof Rei && alvo.getColuna() == inicial.getColuna() + 2) {
            Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() + 3);
            Posicao alvoTorre = new Posicao(inicial.getLinha(), inicialTorre.getColuna() + 1);
            PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removerPeca(alvoTorre);
            tabuleiro.lugarDaPeca(torre, inicialTorre);
            torre.decrementarContagem();
        }

        // movimento especial roque (lado da rainha) TORRE
        if (p instanceof Rei && alvo.getColuna() == inicial.getColuna() - 2) {
            Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() - 4);
            Posicao alvoTorre = new Posicao(inicial.getLinha(), inicialTorre.getColuna() - 1);
            PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removerPeca(alvoTorre);
            tabuleiro.lugarDaPeca(torre, inicialTorre);
            torre.decrementarContagem();
        }

        // movimento especial de passagem
        if (p instanceof Peao) {
            if (inicial.getColuna() != alvo.getColuna() && pecaCapturada == dePassagemVulneravel) {
                PecaDeXadrez peao = (PecaDeXadrez)tabuleiro.removerPeca(alvo);
                Posicao posicaoDoPeao;
                if (p.getCor() == Cor.BRANCO) {
                    posicaoDoPeao = new Posicao(3, alvo.getColuna());
                } else {
                    posicaoDoPeao = new Posicao(4, alvo.getColuna());
                }
                tabuleiro.lugarDaPeca(peao, posicaoDoPeao);
            }
        }
    }

    public void validarPosicaoInicial(Posicao posicao) {
        if (!tabuleiro.temPeca(posicao)) {
            throw new XadrezException("Não existe peça na posição de origem.");
        }
        if (jogadorAtual != ((PecaDeXadrez) tabuleiro.peca(posicao)).getCor()) {
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
        List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor).collect(Collectors.toList());
        for (Peca p : lista) {
            if (p instanceof Rei) {
                return (PecaDeXadrez) p;
            }
        }
        throw new IllegalStateException("Não existe um rei da cor " + cor + " no tabuleiro.");
    }

    private boolean testeXeque(Cor cor) {
        Posicao posicaoDoRei = rei(cor).getPosicaoDeXadrez().posicionar();
        List<Peca> pecasDoOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == oponente(cor)).collect(Collectors.toList());
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
        List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor).collect(Collectors.toList());
        for (Peca p : lista) {
            boolean[][] mat = p.movimentosPossiveis();
            for (int i = 0; i < tabuleiro.getLinhas(); i++) {
                for (int j = 0; j < tabuleiro.getColunas(); j++) {
                    if (mat[i][j]) {
                        Posicao inicial = ((PecaDeXadrez) p).getPosicaoDeXadrez().posicionar();
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
        colocarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
        colocarNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
        colocarNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));

        colocarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
        colocarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
        colocarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
        colocarNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
        colocarNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));
    }
}
