package com.example.gestaobanca.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaobanca.R;
import com.example.gestaobanca.model.MinhaLista;

import java.util.ArrayList;
import java.util.List;

public class MinhaListaAdapter extends RecyclerView.Adapter<MinhaListaAdapter.ViewHolder> {
    private List<MinhaLista> listaApostas;
    private Context context;
    private double bancaInicial;
    private double bancaAtual;
    private OnBancaChangeListener onBancaChangeListener;

    private double auxBancaFinal = 0.0;

    private double teste = 0.0;

    private double recebeBind = 0.0;


    // Defina a interface
    public interface OnBancaChangeListener {
        void onBancaChange(double novoValorBanca);
    }

    // Construtor para passar a interface
    public MinhaListaAdapter(Context context, List<MinhaLista> itens, double bancaInicial,OnBancaChangeListener onBancaChangeListener) {
        this.context = context;
        this.listaApostas = itens;
        this.bancaInicial = bancaInicial;  // Valor inicial
        this.bancaAtual = bancaInicial;  // Começa com o mesmo valor da banca inicial
        this.onBancaChangeListener = onBancaChangeListener;

        //
        //Log.d("bancaAtual", "Contrutor: " + bancaAtual);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adicao_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MinhaLista aposta = listaApostas.get(position);

        holder.tvHomeTeam.setText(aposta.getHomeTeam() +"  X");
        holder.tvAwayTeam.setText(aposta.getAwayTeam());
        holder.tvMercado.setText(aposta.getMercado());
        holder.tvDataInsercao.setText(aposta.getDataInsercao());
        holder.tvOdd.setText(String.format("Odd: %.2f", aposta.getOdd()));
        holder.tvValor.setText(String.format("Valor: %.2f", aposta.getValor()));
        holder.tvOddxValor.setText(String.format("Ganhos: %.2f", aposta.getOddxValor()));





        // Configurar o Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.situacoes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerSituacao.setAdapter(adapter);


        holder.spinnerSituacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                String situacaoAtual = (String) parentView.getItemAtPosition(pos);

                // Chama o método para calcular e atualizar a banca atual
                //recalcularBanca(situacaoAtual, holder);



                 teste =  aposta.getRetornoStatus(situacaoAtual);
                Log.d("teste", "teste " + teste +" situacaoAtual " + situacaoAtual);
                 auxBancaFinal += teste;

                Log.d("teste", "auxBancaFinal " + teste +" situacaoAtual " + auxBancaFinal);

                // Alterar cor do layout de acordo com a situação
                if ("Red".equals(situacaoAtual)) {
                    holder.linearLayoutAdicaoItem.setBackgroundColor(ContextCompat.getColor(context, R.color.red));

                } else if ("Green".equals(situacaoAtual)) {
                    holder.linearLayoutAdicaoItem.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                }


                //Log.d("bancaAtual", "spinnerSituacao: " + bancaAtual + " situacaoAtual: " +situacaoAtual);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Não faz nada
            }
        });



    }

    // Método separado para realizar o cálculo e atualizar a banca
    private void recalcularBanca(String situacaoAtual, ViewHolder holder) {
        double aberto = 0.0;
        double green = 0.0;
        double red = 0.0;

        for (int i = 0; i < listaApostas.size(); i++) {
            if ("Aberto".equals(situacaoAtual)) {
                aberto += -listaApostas.get(i).getValor();
                Log.d("aberto", "aberto: " + aberto);
            } else if ("Green".equals(situacaoAtual)) {
                green += (listaApostas.get(i).getOdd() * listaApostas.get(i).getValor()) - listaApostas.get(i).getValor();
                Log.d("green", "green: " + green);
            } else {
                red += -(listaApostas.get(i).getOdd() * listaApostas.get(i).getValor());
                Log.d("red", "red: " + red);
            }
        }

//        bancaAtual = aberto + green + red;
//        Log.d("bancaAtual", "bancaAtual: agora " + bancaAtual);

        notificarMudancaBanca(); // Notifica a mudança na banca
    }

    private void notificarMudancaBanca() {
        if (onBancaChangeListener != null) {
            onBancaChangeListener.onBancaChange(bancaAtual);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("auxBancaFinal", "getItemCount " + auxBancaFinal);
        return listaApostas.size();



    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeTeam;
        TextView tvAwayTeam;
        TextView tvMercado;  // Corrigido de textViewMercado para tvMercado
        TextView tvDataInsercao;
        TextView tvOdd;
        TextView tvValor;
        TextView tvOddxValor;  // Corrigido de tvOddxUnd para tvOddxValor
        Spinner spinnerSituacao;  // Adicionando o Spinner
        LinearLayout linearLayoutAdicaoItem; // Adicione o layout aqui

        public ViewHolder(View itemView) {
            super(itemView);
            tvHomeTeam = itemView.findViewById(R.id.tvHomeTeam);
            tvAwayTeam = itemView.findViewById(R.id.tvAwayTeam);
            tvMercado = itemView.findViewById(R.id.textViewMercado);  // Usando o ID correto
            tvDataInsercao = itemView.findViewById(R.id.tvDataInsercao);
            tvOdd = itemView.findViewById(R.id.tvOdd);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvOddxValor = itemView.findViewById(R.id.tvOddxUnd);  // Usando o ID correto
            spinnerSituacao = itemView.findViewById(R.id.spinnerSituacao);
            linearLayoutAdicaoItem = itemView.findViewById(R.id.linearLayoutAdicaoItem); // Referência ao layout

        }
    }
}
