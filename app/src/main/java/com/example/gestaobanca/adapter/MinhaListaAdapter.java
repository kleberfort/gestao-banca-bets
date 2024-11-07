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

    private final List<MinhaLista> listaApostas;
    private final Context context;
    private double bancaInicial;
    private double bancaAtual;
    private final OnBancaChangeListener onBancaChangeListener;
    private final OnItemLongClickListener onItemLongClickListener;

    public interface OnBancaChangeListener {
        void onBancaChange(double novoValorBanca);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public MinhaListaAdapter(Context context, List<MinhaLista> itens, double bancaInicial,
                             OnBancaChangeListener onBancaChangeListener, OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        this.listaApostas = itens;
        this.bancaInicial = bancaInicial;
        this.bancaAtual = bancaInicial;
        this.onBancaChangeListener = onBancaChangeListener;
        this.onItemLongClickListener = onItemLongClickListener;
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

        // Configurar os dados do item
        holder.tvHomeTeam.setText(aposta.getHomeTeam() + " X");
        holder.tvAwayTeam.setText(aposta.getAwayTeam());
        holder.tvMercado.setText(aposta.getMercado());
        holder.tvDataInsercao.setText(aposta.getDataInsercao());
        holder.tvOdd.setText(String.format("Odd: %.2f", aposta.getOdd()));
        holder.tvValor.setText(String.format("Valor: %.2f", aposta.getValor()));
        holder.tvOddxValor.setText(String.format("Ganhos: %.2f", aposta.getOddxValor()));

        // Configurar o spinner com valores do array de strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.situacoes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerSituacao.setAdapter(adapter);

        // Define o estado inicial do Spinner
        if ("Red".equals(aposta.getSituacao())) {
            holder.spinnerSituacao.setSelection(adapter.getPosition("Red"));
            holder.linearLayoutAdicaoItem.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        } else if ("Green".equals(aposta.getSituacao())) {
            holder.spinnerSituacao.setSelection(adapter.getPosition("Green"));
            holder.linearLayoutAdicaoItem.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.spinnerSituacao.setSelection(0); // Posição inicial
        }

        // Listener do Spinner para detectar alterações
        holder.spinnerSituacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                String situacaoAtual = (String) parentView.getItemAtPosition(pos);

                // Atualiza o retorno de acordo com a situação selecionada
                double valorRetorno = aposta.getRetornoStatus(situacaoAtual);
                aposta.setValorRetornoStatusAposta(valorRetorno);
                aposta.setSituacao(situacaoAtual);

                // Atualiza a cor do layout com base na situação
                if ("Red".equals(situacaoAtual)) {
                    holder.linearLayoutAdicaoItem.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                } else if ("Green".equals(situacaoAtual)) {
                    holder.linearLayoutAdicaoItem.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                }

                // Calcula a nova banca atualizada
                double novaBanca = bancaInicial;
                for (MinhaLista item : listaApostas) {
                    novaBanca += item.getValorRetornoStatusAposta();
                }
                bancaAtual = novaBanca;

                // Notifica o Fragment sobre a mudança na banca
                notificarMudancaBanca();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Não faz nada
            }
        });

        // Listener para clique longo
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    public void atualizarBanca(double novaBanca) {
        this.bancaAtual = novaBanca;
        notificarMudancaBanca();
    }

    @Override
    public int getItemCount() {
        return listaApostas.size();
    }

    private void notificarMudancaBanca() {
        if (onBancaChangeListener != null) {
            onBancaChangeListener.onBancaChange(bancaAtual);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeTeam, tvAwayTeam, tvMercado, tvDataInsercao, tvOdd, tvValor, tvOddxValor;
        Spinner spinnerSituacao;
        LinearLayout linearLayoutAdicaoItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHomeTeam = itemView.findViewById(R.id.tvHomeTeam);
            tvAwayTeam = itemView.findViewById(R.id.tvAwayTeam);
            tvMercado = itemView.findViewById(R.id.textViewMercado);
            tvDataInsercao = itemView.findViewById(R.id.tvDataInsercao);
            tvOdd = itemView.findViewById(R.id.tvOdd);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvOddxValor = itemView.findViewById(R.id.tvOddxUnd);
            spinnerSituacao = itemView.findViewById(R.id.spinnerSituacao);
            linearLayoutAdicaoItem = itemView.findViewById(R.id.linearLayoutAdicaoItem);
        }
    }
}
