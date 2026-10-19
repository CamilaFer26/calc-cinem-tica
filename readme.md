```text
Camila Fernanda e Lara Pereira
Física Geral e Experimental 2026.2
Prof. Dr. Jonathan da Silva Ramos
```
---
# Calculadora de Cinemática

Aplicação desktop em Java (Swing) que resolve problemas de cinemática em 1D e 2D. O usuário preenche os dados que conhece, escolhe a grandeza que quer calcular e o programa encontra o caminho de fórmulas necessário, mostrando cada fórmula usada e o resultado na unidade escolhida.

## Funcionalidades

- Duas abas: **Cálculos 1D** (MRU/MRUV) e **Cálculos 2D** (eixos X e Y, lançamentos).
- Conversão automática de unidades (comprimento, tempo, velocidade, aceleração) para o SI.
- Aceita vírgula ou ponto como separador decimal.
- Motor de inferência: aplica as fórmulas repetidamente até descobrir a variável pedida.
- Exibe apenas as fórmulas que contribuíram para o resultado.
- Eixo Y com aceleração `-g` (positivo para cima) e cálculo do ângulo inicial `θ0 = atan2(v0y, v0x)`.
- Aviso quando a equação de Torricelli tem duas soluções (±).

## Como usar

1. Preencha os campos conhecidos e selecione a unidade de cada um.
2. Em **"Quero calcular"**, escolha a grandeza desejada (o campo dela deve ficar vazio).
3. Clique em **Calcular**. Use **Limpar** para recomeçar.

## Requisitos

- JDK 8 ou superior.

## Como executar

```bash
# na raiz do projeto (pasta com os pacotes math, util e view)
javac -encoding UTF-8 -d out math/*.java util/*.java view/*.java
java -cp out view.MainView
```

Se estiver usando uma pasta `src/`, ajuste os caminhos (`src/math/*.java`, etc.).

## Estrutura do projeto

```
view/
  MainView.java    Janela principal com as abas
  View1D.java      Painel de cálculos 1D
  View2D.java      Painel de cálculos 2D
math/
  Calculo1D.java   Motor de cálculo 1D
  Calculo2D.java   Motor de cálculo 2D
util/
  Units.java       Unidades e fatores de conversão para o SI
  Parser.java      Leitura/validação dos campos e conversão para SI
  Colors.java      Paleta de cores da interface
```

## Diagrama de classes

```mermaid
classDiagram
    direction TB

    class JFrame {
        <<Swing>>
    }
    class JPanel {
        <<Swing>>
    }
    class ActionListener {
        <<interface>>
        +actionPerformed(ActionEvent e) void
    }

    class MainView {
        +MainView()
        +main(String[] args)$ void
    }

    class View1D {
        -Units u
        ~String[] rotulos
        -String[][] nomes
        -double[][] fat
        -int[] padrao
        ~JTextField[] campos
        ~JComboBox~String~[] combos
        ~JComboBox~String~ alvo
        ~JTextArea resultado
        ~JButton botaoCalc
        ~JButton botaoLimpar
        +View1D()
        +actionPerformed(ActionEvent e) void
    }

    class View2D {
        -Units u
        ~String[] rotulos
        -String[][] nomes
        -double[][] fat
        -int[] padrao
        ~JTextField[] campos
        ~JComboBox~String~[] combos
        ~JComboBox~String~ alvo
        ~JTextArea resultado
        ~JButton botaoCalc
        ~JButton botaoLimpar
        +View2D()
        +actionPerformed(ActionEvent e) void
    }

    class Calculo1D {
        ~String[] simb$
        ~String obs$
        ~List~PassoCalculo~ historicoPassos$
        +calcular1D(Double[] x, int alvo, String target, double fator, String unidade, String si)$ String
        ~reconstruirPassosUteis(int alvo)$ List~String~
        ~aprender(...)$ boolean
        ~diferenca(...)$ boolean
        ~razao(...)$ boolean
        ~regrasSimples(...)$ boolean
        ~raizTempo(A, B, C)$ double
        ~sinalFinal(v0, dx)$ double
        ~equacaoCinematica(...)$ boolean
        ~avisar()$ void
    }

    class Calculo2D {
        ~String[] simb$
        ~String obs$
        ~List~PassoCalculo~ historicoPassos$
        +calcular2D(Double[] x, int alvo, String target, double fator, String unidade)$ String
        ~reconstruirPassosUteis(int alvo)$ List~String~
        ~aprender(...)$ boolean
        ~diferenca(...)$ boolean
        ~razao(...)$ boolean
        ~regrasSimples(...)$ boolean
        ~raizTempo(A, B, C)$ double
        ~equacaoEixo(...)$ boolean
        ~avisar()$ void
    }

    class PassoCalculo {
        ~int varCalculada
        ~List~Integer~ deps
        ~String formula
        ~PassoCalculo(int, List~Integer~, String)
    }

    class Units {
        +String[] Comprimento$
        +double[] fComprimento$
        +String[] Tempo$
        +double[] fTempo$
        +String[] Velocidade$
        +double[] fVelocidade$
        +String[] Aceleracao$
        +double[] fAceleracao$
        +String[] Angulo$
        +double[] fAngulo$
    }

    class Parser {
        +ler(JTextField, JComboBox~String~, double[], String)$ Double
    }

    class Colors {
        +get1()$ Color
        +get2()$ Color
        +get3()$ Color
        +get4()$ Color
        +get5()$ Color
    }

    JFrame <|-- MainView : extends
    JPanel <|-- View1D : extends
    JPanel <|-- View2D : extends
    ActionListener <|.. View1D : implements
    ActionListener <|.. View2D : implements

    MainView *-- View1D : aba
    MainView *-- View2D : aba

    View1D --> Calculo1D : usa
    View2D --> Calculo2D : usa
    View1D --> Parser : usa
    View2D --> Parser : usa
    View1D *-- Units
    View2D *-- Units
    MainView ..> Colors
    View1D ..> Colors
    View2D ..> Colors

    Calculo1D +-- PassoCalculo : classe aninhada
    Calculo2D +-- PassoCalculo : classe aninhada
```

### Observações de projeto

- **Interfaces:** o projeto não define interfaces próprias; implementa `ActionListener` (Swing) em `View1D` e `View2D` para tratar os botões.
- **Classes abstratas:** não há. A herança vem apenas de `JFrame` (`MainView`) e `JPanel` (`View1D`, `View2D`).
- **Classes utilitárias:** `Units`, `Parser`, `Colors`, `Calculo1D` e `Calculo2D` usam membros estáticos e não são instanciadas (exceto `Units`, que é instanciada nas views).
- **`PassoCalculo`:** classe estática aninhada, declarada separadamente em `Calculo1D` e `Calculo2D`. Registra qual variável foi calculada, de quais depende e a fórmula usada.

## Como o cálculo funciona

1. A view lê os campos via `Parser.ler`, que converte cada valor para o SI e devolve `null` para campos vazios.
2. `Calculo1D`/`Calculo2D` marcam as variáveis conhecidas e repetem as regras (diferenças, razões, médias e as equações cinemáticas) até o alvo ser conhecido ou não haver mais progresso.
3. Cada valor descoberto é registrado em `historicoPassos` com suas dependências.
4. `reconstruirPassosUteis` percorre o histórico de trás para frente e mantém só os passos necessários ao alvo.
5. O resultado é convertido para a unidade escolhida e exibido junto das fórmulas.

# By Camila e Lara
