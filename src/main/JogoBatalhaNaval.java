import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class JogoBatalhaNaval {

    public static void main(String[] args) {
        int[][] tabuleiroJogador1 = new int[16][16];
        int[][] tabuleiroJogador2 = new int[16][16];
        boolean[][] tirosJogador1 = new boolean[16][16];
        boolean[][] tirosJogador2 = new boolean[16][16];

        try {
            recuperarTabuleiro(tabuleiroJogador1, "j1.txt");
            recuperarTabuleiro(tabuleiroJogador2, "j2.txt");
            System.out.println("TABULEIROS CARREGADOS COM SUCESSO");
            boolean vezJogador1 = true;
            boolean jogoEmAndamento = true;
            Scanner scanner = new Scanner(System.in);

            while (jogoEmAndamento) {
                String prompt = vezJogador1 ? "J1> " : "J2> ";
                System.out.print(prompt);
                String entrada = scanner.nextLine();
                String[] coordenadas = entrada.split(" ");

                if (coordenadas.length != 2) {
                    System.out.println("JOGADA INVALIDA");
                    continue;
                }

                try {
                    int x = Integer.parseInt(coordenadas[0]);
                    int y = Integer.parseInt(coordenadas[1]);
                    if (x < 0 || x >= 16 || y < 0 || y >= 16) {
                        System.out.println("JOGADA INVALIDA");
                        continue;
                    }

                    boolean[][] tiros = vezJogador1 ? tirosJogador1 : tirosJogador2;
                    int[][] tabuleiro = vezJogador1 ? tabuleiroJogador2 : tabuleiroJogador1;
                    if (tiros[y][x]) {
                        System.out.println("TIRO JA EXECUTADO");
                        continue;
                    }
                    boolean acertou = tabuleiro[y][x] == 1;
                    tiros[y][x] = true;
                    if (acertou) {
                        System.out.println("ACERTOU");
                        boolean afundouNavio = Navio.navioAfundado(tabuleiro, tiros, x, y);
                        if (afundouNavio) {
                            if (todosNaviosAfundados(tabuleiro, tiros)) {
                                jogoEmAndamento = false;
                                System.out.println("FIM DE JOGO");
                                System.out.println("VENCEDOR: Jogador " + (vezJogador1 ? "1" : "2"));
                            }
                        } else {
                            vezJogador1 = !vezJogador1;
                        }
                    } else {
                        System.out.println("ÁGUA");
                        vezJogador1 = !vezJogador1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("JOGADA INVALIDA");
                }
            }
            scanner.close();
        } catch (ErroLeituraArquivoException | NavioException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void recuperarTabuleiro(int[][] tabuleiro, String nomeArquivo) throws ErroLeituraArquivoException, NavioException {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            int linhaAtual = 0;
            Set<Integer> coordenadas = new HashSet<>();

            while ((linha = reader.readLine()) != null) {
                linhaAtual++;
                String[] partes = linha.split(" ");
                if (partes.length != 5) {
                    throw new ErroLeituraArquivoException("ERRO LINHA " + linhaAtual + ": " + nomeArquivo);
                }
                String tipoNavioString = partes[0];
                TipoNavio tipoNavio = TipoNavio.valueOf(tipoNavioString);

                int x1 = Integer.parseInt(partes[1]);
                int y1 = Integer.parseInt(partes[2]);
                int x2 = Integer.parseInt(partes[3]);
                int y2 = Integer.parseInt(partes[4]);

                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                        int coordenada = x * 100 + y;
                        if (coordenadas.contains(coordenada)) {
                            throw new ErroLeituraArquivoException("ERRO LINHA " + linhaAtual + ": " + nomeArquivo);
                        }
                        coordenadas.add(coordenada);
                    }
                }

                Navio navio = new Navio(tipoNavio);
                int tamanhoEsperado = navio.getTamanho();
                int tamanhoFornecido = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)) + 1;
                if (tamanhoFornecido != tamanhoEsperado) {
                    throw new NavioException("ERRO LINHA " + linhaAtual + ": " + nomeArquivo);
                }
                Navio.colocarNavio(tabuleiro, x1, y1, x2, y2);
            }
        } catch (IOException e) {
            throw new ErroLeituraArquivoException("ERRO ARQUIVO: " + nomeArquivo);
        }
    }

    public static boolean todosNaviosAfundados(int[][] tabuleiro, boolean[][] tiros) {
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[0].length; j++) {
                if (tabuleiro[i][j] == 1 && !tiros[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
