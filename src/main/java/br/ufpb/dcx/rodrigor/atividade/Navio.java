package br.ufpb.dcx.rodrigor.atividade;

import java.util.HashSet;
import java.util.Set;

public class Navio {

    private TipoNavio tipo;
    private int tamanho;

    public Navio(TipoNavio tipo) {
        this.tipo = tipo;
        definirTamanho();
    }

    private void definirTamanho() {
        switch (tipo) {
            case PORTA_AVIOES:
                tamanho = 5;
                break;
            case DESTROYER:
            case CRUZADOR:
                tamanho = 4;
                break;
            case SUBMARINO:
                tamanho = 3;
                break;
            case PATRULHA:
                tamanho = 2;
                break;
        }
    }

    public int getTamanho() {
        return tamanho;
    }

    public TipoNavio getTipo() {
        return tipo;
    }

    public static void colocarNavio(int[][] tabuleiro, int x1, int y1, int x2, int y2) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                tabuleiro[y][x] = 1;
            }
        }
    }

    public static TipoNavio identificarTipoNavio(Set<Integer> coordenadas) {
        switch (coordenadas.size()) {
            case 2:
                return TipoNavio.PATRULHA;
            case 3:
                return TipoNavio.SUBMARINO;
            case 4:
                return TipoNavio.CRUZADOR;
            case 5:
                return TipoNavio.PORTA_AVIOES;
            default:
                return null;
        }
    }

    public static boolean navioAfundado(int[][] tabuleiro, boolean[][] tiros, int x, int y) {
        Set<Integer> coordenadas = new HashSet<>();

        for (int i = x; i < tabuleiro[0].length && tabuleiro[y][i] == 1; i++) {
            coordenadas.add(i * 100 + y);
            if (!tiros[y][i]) {
                return false;
            }
        }
        for (int i = x - 1; i >= 0 && tabuleiro[y][i] == 1; i--) {
            coordenadas.add(i * 100 + y);
            if (!tiros[y][i]) {
                return false;
            }
        }

        for (int j = y; j < tabuleiro.length && tabuleiro[j][x] == 1; j++) {
            coordenadas.add(x * 100 + j);
            if (!tiros[j][x]) {
                return false;
            }
        }
        for (int j = y - 1; j >= 0 && tabuleiro[j][x] == 1; j--) {
            coordenadas.add(x * 100 + j);
            if (!tiros[j][x]) {
                return false;
            }
        }

        TipoNavio tipoNavio = identificarTipoNavio(coordenadas);
        if (tipoNavio != null) {
            System.out.println("AFUNDOU " + tipoNavio);
        }
        return true;
    }
}