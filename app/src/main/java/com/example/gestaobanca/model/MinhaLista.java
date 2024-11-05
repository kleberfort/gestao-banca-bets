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

        private double valorRetornoStatusAposta;

        public double getValorRetornoStatusAposta() {
            return valorRetornoStatusAposta;
        }

        public void setValorRetornoStatusAposta(double valorRetornoStatusAposta) {
            this.valorRetornoStatusAposta = valorRetornoStatusAposta;
        }

        public MinhaLista() {
        }

        // Construtor da classe ApostaItem
        public MinhaLista(String homeTeam, String awayTeam, String mercado, String situacao, String dataInsercao,
                          double odd, double valor, double oddxValor) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.mercado = mercado;
            this.situacao = situacao;
            this.dataInsercao = dataInsercao;
            this.odd = odd;
            this.valor = valor;
            this.oddxValor = oddxValor;  // Calcula odd x valor diretamente
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

        }

        public double getValor() { return valor; }
        public void setValor(double valor) {
            this.valor = valor;
            this.oddxValor = this.odd * valor;  // Atualiza oddxValor ao alterar valor

        }

        public double getOddxValor() { return oddxValor ; }






        public void setOddxValor(double oddxValor) {
            this.oddxValor = oddxValor;
        }

        public double getRetornoStatus(String status) {
             double retornoStatus = 0.0;

            if(status.equals("Red")){
                retornoStatus = -getValor();
            }else if(status.equals("Green")){
                retornoStatus = getOddxValor() - getValor();
            }

            return retornoStatus;
        }


    }
