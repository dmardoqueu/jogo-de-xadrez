# ♟️ Jogo de Xadrez em Java

Este repositório contém uma implementação de um jogo de xadrez em Java, com suporte completo às regras, incluindo movimentos especiais e gerenciamento de partidas. O projeto está organizado em pacotes que contêm classes para representar o tabuleiro, as peças e a lógica do jogo.

## 📂 Estrutura do Projeto

```
src/
│
├── app/                         # Contém a classe principal e a interface do jogo.
│   ├── Programa.java            # Classe principal que inicia o jogo.
│   └── UI.java                  # Classe responsável pela interface do usuário.
│
├── tabuleiro/                   # Contém as classes genéricas do tabuleiro e peças.
│   ├── Peca.java                # Classe abstrata que representa uma peça genérica.
│   ├── Posicao.java             # Classe que representa uma posição no tabuleiro.
│   ├── Tabuleiro.java           # Classe que gerencia o tabuleiro e as peças.
│   └── TabuleiroException.java  # Exceção para erros específicos do tabuleiro.
│
└── xadrez/                      # Contém as classes específicas do jogo de xadrez.
    ├── pecas/                   # Subpacote com classes para cada peça do xadrez.
    │   ├── Cor.java             # Enumeração que define as cores das peças.
    │   └── (Demais classes das peças: Rei, Rainha, etc. - podem ser adicionadas aqui)
    │
    ├── Partida.java             # Controla o andamento da partida e implementa regras do xadrez.
    ├── PecaDeXadrez.java        # Classe abstrata para peças de xadrez, estende `Peca`.
    ├── PosicaoDeXadrez.java     # Classe que converte entre notação algébrica e posição no tabuleiro.
    └── XadrezException.java     # Exceção para erros específicos do jogo de xadrez.
```

### Descrição dos Pacotes

- **`app`**: Contém as classes principais para execução do jogo e interface do usuário.
  - **Programa**: Classe principal que executa o jogo.
  - **UI**: Interface com o usuário, responsável por exibir o tabuleiro e processar os movimentos.

- **`tabuleiro`**: Define classes genéricas para o tabuleiro e peças.
  - **Peca**: Classe abstrata que representa uma peça genérica.
  - **Posicao**: Representa uma posição no tabuleiro.
  - **Tabuleiro**: Gerencia o tabuleiro de 8x8 e a posição das peças.
  - **TabuleiroException**: Exceção para erros relacionados ao tabuleiro.

- **`xadrez`**: Contém classes específicas do jogo de xadrez.
  - **pecas**: Subpacote com classes para cada peça de xadrez (Rei, Rainha, Torre, Bispo, Cavalo, Peão) e a enumeração `Cor`.
  - **Partida**: Controla a partida, incluindo regras de turno, verificação de xeque e xeque-mate, movimentos especiais, etc.
  - **PecaDeXadrez**: Classe abstrata que estende `Peca`, com métodos específicos como contagem de movimentos e identificação de peças oponentes.
  - **PosicaoDeXadrez**: Converte entre posições no tabuleiro e notação algébrica (ex: a1, h8).
  - **XadrezException**: Exceção para erros específicos do jogo de xadrez.

## 🚀 Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/dmardoqueu/jogo-de-xadrez.git
   ```

2. Compile o código:
   ```bash
   javac -d bin src/app/*.java src/tabuleiro/*.java src/xadrez/*.java src/xadrez/pecas/*.java
   ```

3. Execute o jogo:
   ```bash
   java -cp bin app.Programa
   ```

## 📝 Regras Implementadas

- **Movimentos básicos** para cada tipo de peça.
- **Verificação de xeque e xeque-mate**.
- **Movimentos especiais**: roque e en passant.
- **Promoção de peões**.
- **Controle de turnos**, alternando entre os jogadores.
