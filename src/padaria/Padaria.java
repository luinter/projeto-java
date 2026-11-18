package padaria;

import java.util.ArrayList;

public class Padaria {
   
    public static final int MAX_PRODUTOS = 20;
    public static final int LIMITE_ESTOQUE_BAIXO = 5;
    public static final String[] DIAS = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"};

    private String nome;
    private ArrayList<Produto> produtos;
    private ArrayList<String> historico;
    private int[][] vendas;
    private double[] faturamentoPorDia;

    public Padaria(String nome) {
        this.nome = nome;
        this.produtos = new ArrayList<>();
        this.historico = new ArrayList<>();
        this.vendas = new int[MAX_PRODUTOS][7];
        this.faturamentoPorDia = new double[7];
    }

    public String getNome() {
        return nome;
    }

    public String cadastrarProduto(String nomeProd, String categoria, double preco, int quantidade) {
        if (produtos.size() >= MAX_PRODUTOS) {
            return "ERRO: limite máximo de " + MAX_PRODUTOS + " produtos atingido.";
        }
        if (preco <= 0) {
            return "ERRO: o preço deve ser maior que zero.";
        }
        if (quantidade < 0) {
            return "ERRO: a quantidade inicial não pode ser negativa.";
        }
        if (nomeProd == null || nomeProd.trim().isEmpty()) {
            return "ERRO: o nome do produto não pode ficar vazio.";
        }
        if (existeProdutoComNome(nomeProd)) {
            return "ERRO: já existe um produto chamado \"" + nomeProd + "\".";
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            categoria = "Sem categoria";
        }

        Produto novo = new Produto(nomeProd.trim(), categoria.trim(), preco, quantidade);
        produtos.add(novo);

        registrarNoHistorico("Cadastro: " + novo.getNome() + " (Cód: " + novo.getCodigo() + ")");

        return "OK: produto cadastrado com o código " + novo.getCodigo() + ".";
    }

    public Produto buscarPorCodigo(int codigo) {
        int indice = indiceDoProduto(codigo);
        if (indice != -1) {
            return produtos.get(indice);
        }
        return null;
    }

    public ArrayList<Produto> buscarPorNome(String trecho) {
        ArrayList<Produto> encontrados = new ArrayList<>();
        if (trecho == null) return encontrados;
        String busca = trecho.trim().toLowerCase();
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(busca)) {
                encontrados.add(p);
            }
        }
        return encontrados;
    }

    public ArrayList<Produto> getProdutos() {
        return new ArrayList<>(produtos);
    }

    public int getQuantidadeDeProdutos() {
        return produtos.size();
    }

    public String registrarVenda(int codigo, int unidades, int dia) {
        if (!diaValido(dia)) {
            return "ERRO: dia inválido. Use valores de 1 a 7.";
        }
        if (unidades <= 0) {
            return "ERRO: a quantidade vendida deve ser maior que zero.";
        }

        Produto p = buscarPorCodigo(codigo);
        if (p == null) {
            return "ERRO: produto de código " + codigo + " não encontrado.";
        }

        int posicao = indiceDoProduto(codigo);

        if (p.getQuantidade() < unidades) {
            return "ERRO: estoque insuficiente. Disponível: " + p.getQuantidade() + " unidade(s).";
        }

        p.removerEstoque(unidades);

        vendas[posicao][dia - 1] += unidades;

        double totalVenda = unidades * p.getPreco();
        faturamentoPorDia[dia - 1] += totalVenda;

        registrarNoHistorico("Venda: " + unidades + " x " + p.getNome() + " (" + DIAS[dia - 1] + ") = R$ " + String.format("%.2f", totalVenda));

        return "OK: venda registrada. Total: R$ " + String.format("%.2f", totalVenda) + ". Estoque restante: " + p.getQuantidade() + ".";
    }

    public String reporEstoque(int codigo, int unidades) {
        if (unidades <= 0) {
            return "ERRO: a quantidade de reposição deve ser maior que zero.";
        }
        Produto p = buscarPorCodigo(codigo);
        if (p == null) {
            return "ERRO: produto de código " + codigo + " não encontrado.";
        }

        p.adicionarEstoque(unidades);
        registrarNoHistorico("Reposição: +" + unidades + " x " + p.getNome() + " (Estoque atual: " + p.getQuantidade() + ")");
        return "OK: estoque de " + p.getNome() + " agora é " + p.getQuantidade() + ".";
    }

    public String alterarPreco(int codigo, double novoPreco) {
        if (novoPreco <= 0) {
            return "ERRO: o preço deve ser maior que zero.";
        }
        Produto p = buscarPorCodigo(codigo);
        if (p == null) {
            return "ERRO: produto de código " + codigo + " não encontrado.";
        }

        double precoAntigo = p.getPreco();
        p.setPreco(novoPreco);
        registrarNoHistorico("Alteração de preço: " + p.getNome() + " de R$ " + String.format("%.2f", precoAntigo) + " para R$ " + String.format("%.2f", novoPreco));
        return "OK: preço de " + p.getNome() + " alterado para R$ " + String.format("%.2f", novoPreco) + ".";
    }

    public int getVendas(int posicaoProduto, int dia) {
        if (posicaoProduto >= 0 && posicaoProduto < produtos.size() && dia >= 1 && dia <= 7) {
            return vendas[posicaoProduto][dia - 1];
        }
        return 0;
    }

    public int totalVendidoDoProduto(int posicaoProduto) {
        int total = 0;
        if (posicaoProduto >= 0 && posicaoProduto < produtos.size()) {
            for (int d = 0; d < 7; d++) {
                total += vendas[posicaoProduto][d];
            }
        }
        return total;
    }

    public int totalVendidoNoDia(int dia) {
        int total = 0;
        if (dia >= 1 && dia <= 7) {
            for (int i = 0; i < produtos.size(); i++) {
                total += vendas[i][dia - 1];
            }
        }
        return total;
    }

    public double getFaturamentoDoDia(int dia) {
        if (dia >= 1 && dia <= 7) {
            return faturamentoPorDia[dia - 1];
        }
        return 0.0;
    }

    public double getFaturamentoTotal() {
        double total = 0;
        for (double f : faturamentoPorDia) {
            total += f;
        }
        return total;
    }

    public Produto produtoMaisVendido() {
        if (produtos.isEmpty()) return null;
        int maxVendas = -1;
        Produto maisVendido = null;
        for (int i = 0; i < produtos.size(); i++) {
            int totalProd = totalVendidoDoProduto(i);
            if (totalProd > maxVendas) {
                maxVendas = totalProd;
                maisVendido = produtos.get(i);
            }
        }
        return maxVendas > 0 ? maisVendido : null;
    }

    public int diaDeMaiorFaturamento() {
        double maiorFat = -1;
        int melhorDia = 0;
        for (int d = 1; d <= 7; d++) {
            double fat = getFaturamentoDoDia(d);
            if (fat > maiorFat) {
                maiorFat = fat;
                melhorDia = d;
            }
        }
        return maiorFat > 0 ? melhorDia : 0;
    }

    public ArrayList<Produto> produtosComEstoqueBaixo() {
        ArrayList<Produto> baixos = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.estoqueBaixo(LIMITE_ESTOQUE_BAIXO)) {
                baixos.add(p);
            }
        }
        return baixos;
    }

    public double valorTotalEmEstoque() {
        double total = 0;
        for (Produto p : produtos) {
            total += p.valorEmEstoque();
        }
        return total;
    }

    public ArrayList<String> getHistorico() {
        return new ArrayList<>(historico);
    }

    
    private int indiceDoProduto(int codigo) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getCodigo() == codigo) {
                return i;
            }
        }
        return -1;
    }

    private boolean existeProdutoComNome(String nome) {
        String nomeBusca = nome.trim().toLowerCase();
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().equals(nomeBusca)) {
                return true;
            }
        }
        return false;
    }

    private boolean diaValido(int dia) {
        return dia >= 1 && dia <= 7;
    }

    private void registrarNoHistorico(String descricao) {
        String numeroFormatado = String.format("%03d", historico.size() + 1);
        historico.add(numeroFormatado + " | " + descricao);
    }
}
