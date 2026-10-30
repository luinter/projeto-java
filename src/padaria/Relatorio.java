package padaria;

import java.util.ArrayList;
import java.util.Locale;

public class Relatorio {
    private static final int LARGURA = 72;
    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    public static void imprimirEstoque(Padaria padaria) {
        titulo("ESTOQUE - " + padaria.getNome());
        ArrayList<Produto> lista = padaria.getProdutos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            imprimirTabelaDeProdutos(lista);
        }
        linha();
        System.out.println("Valor total em estoque: " + moeda(padaria.valorTotalEmEstoque()));
    }

    public static void imprimirResultadoBusca(ArrayList<Produto> encontrados, String trecho) {
        titulo("BUSCA POR \"" + trecho + "\"");
        if (encontrados.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }
        imprimirTabelaDeProdutos(encontrados);
        linha();
        System.out.println(encontrados.size()
                + (encontrados.size() == 1 ? " produto encontrado" : " produtos encontrados"));
    }

    public static void imprimirVendasDaSemana(Padaria padaria) {
        titulo("VENDAS DA SEMANA (unidades)");
        ArrayList<Produto> lista = padaria.getProdutos();

        StringBuilder cabecalho = new StringBuilder(String.format("%-16s", "Produto"));
        for (String dia : Padaria.DIAS) {
            cabecalho.append(String.format("%6s", dia));
        }
        cabecalho.append(String.format("%8s", "Total"));
        System.out.println(cabecalho);
        linha();

        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linhaDoProduto = new StringBuilder(
                    String.format("%-16s", cortar(lista.get(i).getNome(), 15)));
            for (int dia = 1; dia <= 7; dia++) {
                linhaDoProduto.append(String.format("%6d", padaria.getVendas(i, dia)));
            }
            linhaDoProduto.append(String.format("%8d", padaria.totalVendidoDoProduto(i)));
            System.out.println(linhaDoProduto);
        }
        linha();

        StringBuilder totais = new StringBuilder(String.format("%-16s", "TOTAL"));
        int totalGeral = 0;
        for (int dia = 1; dia <= 7; dia++) {
            int totalDoDia = padaria.totalVendidoNoDia(dia);
            totalGeral += totalDoDia;
            totais.append(String.format("%6d", totalDoDia));
        }
        totais.append(String.format("%8d", totalGeral));
        System.out.println(totais);
    }

    public static void imprimirEstoqueBaixo(Padaria padaria) {
        titulo("ESTOQUE BAIXO (abaixo de " + Padaria.LIMITE_ESTOQUE_BAIXO + " unidades)");
        ArrayList<Produto> baixos = padaria.produtosComEstoqueBaixo();
        if (baixos.isEmpty()) {
            System.out.println("Nenhum produto com estoque baixo.");
        } else {
            imprimirTabelaDeProdutos(baixos);
        }
    }

    public static void imprimirFechamento(Padaria padaria) {
        titulo("FECHAMENTO DA SEMANA - " + padaria.getNome());
        System.out.println("Faturamento por dia:");
        for (int dia = 1; dia <= 7; dia++) {
            System.out.println(String.format(" %-4s %s", Padaria.DIAS[dia - 1],
                    moeda(padaria.getFaturamentoDoDia(dia))));
        }
        linha();
        System.out.println("Faturamento total da semana: " + moeda(padaria.getFaturamentoTotal()));

        Produto maisVendido = padaria.produtoMaisVendido();
        System.out.println("Produto mais vendido: "
                + (maisVendido == null ? "nenhum (sem vendas)" : maisVendido.getNome()));

        int melhorDia = padaria.diaDeMaiorFaturamento();
        System.out.println("Dia de maior faturamento: "
                + (melhorDia == 0 ? "nenhum (sem vendas)" : Padaria.DIAS[melhorDia - 1]));

        System.out.println("Valor total em estoque: " + moeda(padaria.valorTotalEmEstoque()));
        System.out.println("Produtos com estoque baixo: " + padaria.produtosComEstoqueBaixo().size());
        System.out.println("Operações registradas: " + padaria.getHistorico().size());
    }

    public static void imprimirHistorico(Padaria padaria) {
        titulo("HISTÓRICO DE OPERAÇÕES");
        ArrayList<String> historico = padaria.getHistorico();
        if (historico.isEmpty()) {
            System.out.println("Nenhuma operação registrada.");
            return;
        }
        for (String registro : historico) {
            System.out.println(registro);
        }
    }

    private static void imprimirTabelaDeProdutos(ArrayList<Produto> lista) {
        System.out.println(String.format("%-6s %-18s %-12s %10s %8s",
                "Cód.", "Nome", "Categoria", "Preço", "Qtd."));
        linha();
        for (Produto p : lista) {
            System.out.println(String.format("%-6d %-18s %-12s %10s %8d",
                    p.getCodigo(),
                    cortar(p.getNome(), 18),
                    cortar(p.getCategoria(), 12),
                    moeda(p.getPreco()),
                    p.getQuantidade()));
        }
    }

    private static void titulo(String texto) {
        System.out.println("=".repeat(LARGURA));
        System.out.println(texto);
        System.out.println("=".repeat(LARGURA));
    }

    private static void linha() {
        System.out.println("-".repeat(LARGURA));
    }
  
    private static String cortar(String texto, int maximo) {
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo - 1) + ".";
    }

    private static String moeda(double valor) {
        return String.format(BR, "R$ %,.2f", valor);
    }

}
