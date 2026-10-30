package padaria;

public class Produto {private static int proximoCodigo = 1;

    private final int codigo;
    private final String nome;
    private final String categoria;

    private double preco;
    private int quantidade;

    public Produto(String nome, String categoria, double preco, int quantidade) {
        this.codigo = proximoCodigo; 
        proximoCodigo++;             
        this.nome = nome;
        this.categoria = categoria;
        this.preco = (preco > 0) ? preco : 0;
        this.quantidade = (quantidade >= 0) ? quantidade : 0;
    }
    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }
    public boolean setPreco(double novoPreco) {
        if (novoPreco > 0) { 
            this.preco = novoPreco;
            return true;
        }
        return false;
    }
    public boolean adicionarEstoque(int unidades) {
        if (unidades <= 0) {
            return false;
        }
        this.quantidade += unidades;
        return true;
    }
    public boolean removerEstoque(int unidades) {
        if (unidades <= 0 || unidades > this.quantidade) {
            return false;
        }
        this.quantidade -= unidades;
        return true;
    }
    public double valorEmEstoque() {
        return preco * quantidade;
    }
    public boolean estoqueBaixo(int limite) {
        return quantidade < limite;
    }

}
