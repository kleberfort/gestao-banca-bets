package com.example.gestaobanca.model;

    public class MinhaLista {
        private String homeTeam;
        private String awayTeam;
        private String mercado;
        private String situacao;
        private String dataInsercao;
        private double odd;
        private double valor;
        private double oddxValor;
        private double bancaInicial;
        private double bancaFinal;

        public MinhaLista() {
        }

        // Construtor da classe ApostaItem
        public MinhaLista(String homeTeam, String awayTeam, String mercado, String situacao, String dataInsercao,
                          double odd, double valor, double bancaInicial) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.mercado = mercado;
            this.situacao = situacao;
            this.dataInsercao = dataInsercao;
            this.odd = odd;
            this.valor = valor;
            this.oddxValor = odd * valor;  // Calcula odd x valor diretamente
            this.bancaInicial = bancaInicial;
            this.bancaFinal = calcularBancaFinal();
        }

        // Getters e Setters para os atributos
        public String getHomeTeam() { return homeTeam; }
        public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

        public String getAwayTeam() { return awayTeam; }
        public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

        public String getMercado() { return mercado; }
        public void setMercado(String mercado) { this.mercado = mercado; }

        public String getSituacao() { return situacao; }
        public void setSituacao(String situacao) { this.situacao = situacao; }

        public String getDataInsercao() { return dataInsercao; }
        public void setDataInsercao(String dataInsercao) { this.dataInsercao = dataInsercao; }

        public double getOdd() { return odd; }
        public void setOdd(double odd) {
            this.odd = odd;
            this.oddxValor = odd * this.valor;  // Atualiza oddxValor ao alterar odd
            this.bancaFinal = calcularBancaFinal();
        }

        public double getValor() { return valor; }
        public void setValor(double valor) {
            this.valor = valor;
            this.oddxValor = this.odd * valor;  // Atualiza oddxValor ao alterar valor
            this.bancaFinal = calcularBancaFinal();
        }

        public double getOddxValor() { return oddxValor; }

        public double getBancaInicial() { return bancaInicial; }
        public void setBancaInicial(double bancaInicial) {
            this.bancaInicial = bancaInicial;
            this.bancaFinal = calcularBancaFinal();
        }

        public double getBancaFinal() { return bancaFinal; }

        // Método para calcular a banca final com base na situação
        private double calcularBancaFinal() {
            if (situacao.equals("Green")) {
                return bancaInicial + oddxValor;  // Se for green, adiciona o valor de odd x valor
            } else if (situacao.equals("Red")) {
                return bancaInicial - valor;  // Se for red, subtrai o valor da aposta
            } else {
                return bancaInicial;  // Se for "Aberto" ou outra situação, banca final é igual à inicial
            }
        }
    }
