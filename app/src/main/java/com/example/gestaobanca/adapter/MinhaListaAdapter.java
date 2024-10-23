package com.example.gestaobanca.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaobanca.R;
import com.example.gestaobanca.model.MinhaLista;

import java.util.ArrayList;
import java.util.List;

public class MinhaListaAdapter extends RecyclerView.Adapter<MinhaListaAdapter.ViewHolder> {
    private List<MinhaLista> listaApostas;
    private Context context;

    public MinhaListaAdapter(List<MinhaLista> listaApostas, Context context) {
        this.listaApostas = listaApostas;
        this.context = context;
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

    }

    // Método para encontrar a posição da situação selecionada
    private int getSituacaoPosition(String situacao) {
        // Implementação para retornar a posição correta do spinner com base na situação
        // Exemplo: supondo que as opções do spinner são "Aberto", "Green", "Red"
        switch (situacao) {
            case "Green":
                return 1;  // Posição do "Green"
            case "Red":
                return 2;  // Posição do "Red"
            default:
                return 0;  // Posição padrão, por exemplo "Aberto"
        }
    }

    @Override
    public int getItemCount() {
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

        }
    }
}
