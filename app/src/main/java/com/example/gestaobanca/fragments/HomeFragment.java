package com.example.gestaobanca.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaobanca.R;
import com.example.gestaobanca.adapter.MinhaListaAdapter;
import com.example.gestaobanca.model.MinhaLista;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements MinhaListaAdapter.OnBancaChangeListener, MinhaListaAdapter.OnItemLongClickListener {

    private EditText etBancaInicialCarregar, etValorOdd, etValorUnd;
    private TextView tvUndBanca, tvOddxUnd, tvTotalBanca;
    private Spinner spnHome, spnAway, spnMercado;
    private RecyclerView rvMinhaListaItens;
    private FloatingActionButton fabAdicionar;
    private RadioButton rb100Und, rb50Und, rb40Und;
    private RadioGroup rgOpcoes;
    private HashMap<String, List<String>> minhaListaMap;
    private ArrayList<MinhaLista> listaItems; // Lista para o RecyclerView
    private MinhaListaAdapter minhaListaAdapter; // Adaptador do RecyclerView

    private double bancaInicial = 0.0;
    private double divisor = 0.0;
    private double valorOdd = 0.0;
    private double valorUnd = 0.0;
    private double totalBanca = 0.0;

    // Variáveis para armazenar os itens selecionados
    private String itemSelecionadoHome;
    private String itemSelecionadoAway;
    private String itemSelecionadoMercado;

    private static final String PREFS_NAME = "my_preferences";

    private static final String KEY_PRODUTOS = "produtos";
    private static final String KEY_BANCA_INICIAL = "banca_inicial";  // Chave para o valor da banca inicial
    private static final String KEY_SELECTED_OPTION = "selected_option";  // Chave para a opção selecionada do RadioGroup
    private static final String KEY_VALOR_UNIDADE = "valorUnidadeStr ";

    private static final String KEY_TOTAL_BANCA = "total_banca";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializando os componentes da tela
        etBancaInicialCarregar = view.findViewById(R.id.etBancaInicialCarregar);
        etValorOdd = view.findViewById(R.id.etValorOdd);
        etValorUnd = view.findViewById(R.id.etValorUnd);
        tvUndBanca = view.findViewById(R.id.tvUndBanca);
        tvOddxUnd = view.findViewById(R.id.tvOddxUnd);
        tvTotalBanca = view.findViewById(R.id.tvTotalBanca);
        spnHome = view.findViewById(R.id.spnHome);
        spnAway = view.findViewById(R.id.spnAway);
        spnMercado = view.findViewById(R.id.spnMercado);
        fabAdicionar = view.findViewById(R.id.floatingActionButton);
        rgOpcoes = view.findViewById(R.id.rgOpcoes);
        rb100Und = view.findViewById(R.id.rb100Und);
        rb50Und = view.findViewById(R.id.rb50Und);
        rb40Und = view.findViewById(R.id.rb40Und);
        rvMinhaListaItens = view.findViewById(R.id.rvMinhaListaItens);

        // Acessar o SharedPreferences para limpar
  //      limparListaSharedPrerefereces();
//
        // Inicializar lista e adaptador
        listaItems = new ArrayList<>();


        // Carregar dados do SharedPreferences
        carregarDados();

        minhaListaAdapter = new MinhaListaAdapter(getContext(), listaItems, bancaInicial, this, this);
        rvMinhaListaItens.setLayoutManager(new LinearLayoutManager(getContext())); // Define o LayoutManager
        rvMinhaListaItens.setAdapter(minhaListaAdapter);

        totalBanca += bancaInicial;

        // totalBanca = bancaInicial;
        tvTotalBanca.setText(String.format("Total Banca: R$ %.2f", totalBanca));

        // Ao clicar no botão "Adicionar"
        fabAdicionar.setOnClickListener(v -> adicionarAposta());



        // Recuperar os dados do HashMap passado como argumento
        if (getArguments() != null) {
            minhaListaMap = (HashMap<String, List<String>>) getArguments().getSerializable("categoryItemsMap");
        }

        // Configurar os Spinners
        if (minhaListaMap != null) {
            setupSpinner(spnHome, "Selecione uma categoria");
            setupSpinner(spnAway, "Selecione uma categoria");
            setupSpinner(spnMercado, "Selecione uma categoria");
        }


        // Adiciona o TextWatcher ao EditText etValorOdd
        etValorOdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        valorOdd = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        valorOdd = 0.0; // Define como 0 se a conversão falhar
                    }
                    calcularOddxUnd();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Adiciona o TextWatcher ao EditText etValorUnd
        etValorUnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        valorUnd = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        valorUnd = 0.0; // Define como 0 se a conversão falhar
                    }
                    calcularOddxUnd();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Adiciona o OnCheckedChangeListener ao RadioGroup para capturar a opção selecionada
        rgOpcoes.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb100Und) {
                divisor = 100.0;
            } else if (checkedId == R.id.rb50Und) {
                divisor = 50.0;
            } else if (checkedId == R.id.rb40Und) {
                divisor = 40.0;
            }
            calcularValorUnidade();  // Somente calcula após a escolha de uma opção
            salvarProdutos();

        });

        // Adiciona o TextWatcher ao EditText para capturar a entrada do usuário
        etBancaInicialCarregar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (!s.toString().isEmpty()) {
                    try {
                        String valorBanca = etBancaInicialCarregar.getText().toString();
                        bancaInicial = parseDoubleBrasileiro(valorBanca);
                        salvarProdutos();
                        calcularValorUnidade(); // Calcular e atualizar o valor da unidade

                    } catch (NumberFormatException e) {
                        bancaInicial = 0.0; // Definir como 0 se houver erro na conversão
                    }
                    // calcularValorUnidade();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Verifique se a lista de itens está vazia
        if (listaItems.isEmpty()) {
            totalBanca = bancaInicial; // Restaura a banca ao valor inicial
        }
        // Atualiza a interface do usuário
        tvTotalBanca.setText(String.format("Total Banca: R$ %.2f", totalBanca));
    }


    private void salvarProdutos() {

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listaItems);
        editor.putString(KEY_PRODUTOS, json);

        // Salvar o valor de totalBanca
        editor.putFloat(KEY_TOTAL_BANCA, (float) totalBanca); // Converter double para float ao salvar

        // Salvar o valor da banca inicial (valor do EditText etBancaInicialCarregar)
        String bancaInicialStr = etBancaInicialCarregar.getText().toString();
        if (!bancaInicialStr.isEmpty()) {
            try {
                double bancaInicial = Double.parseDouble(bancaInicialStr); // Converte String para double
                editor.putString(KEY_BANCA_INICIAL, String.valueOf(bancaInicial)); // Salva como String
            } catch (NumberFormatException e) {
                // Tratar caso o valor não seja válido
                Toast.makeText(getContext(), "Valor de banca inválido", Toast.LENGTH_SHORT).show();
            }
        }

        // Salvar a opção selecionada do RadioGroup (ID do botão selecionado)
        int selectedRadioButtonId = rgOpcoes.getCheckedRadioButtonId();
        editor.putInt(KEY_SELECTED_OPTION, selectedRadioButtonId);

        // Salvar o valor da unidade (que é calculado e exibido no tvUndBanca)
        String valorUnidadeStr = tvUndBanca.getText().toString();
        if (!valorUnidadeStr.isEmpty()) {
            editor.putString(KEY_VALOR_UNIDADE, valorUnidadeStr);
        }



        editor.apply();

    }

    private void carregarDados() {
        // Recuperar o valor da lista de produtos (listaItems)
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString(KEY_PRODUTOS, null);
        Type type = new TypeToken<ArrayList<MinhaLista>>() {}.getType();
        listaItems = gson.fromJson(json, type);


        // Verificar se a lista está nula
        if (listaItems == null) {
            listaItems = new ArrayList<>();
        }

        // Recuperar o valor da banca inicial
        String bancaInicialStr = sharedPreferences.getString(KEY_BANCA_INICIAL, "");
        if (!bancaInicialStr.isEmpty()) {
            try {
                // Converter o valor da banca inicial para double e exibir no EditText
                double bancaInicial = Double.parseDouble(bancaInicialStr);
                etBancaInicialCarregar.setText(String.valueOf(bancaInicial));
                this.bancaInicial = bancaInicial; // Atualiza a variável para o cálculo da unidade
            } catch (NumberFormatException e) {
                // Tratar caso o valor não seja válido
                Toast.makeText(getContext(), "Erro ao carregar valor da banca", Toast.LENGTH_SHORT).show();
            }
        }

        // Recuperar o valor de totalBanca
        float totalBancaFloat = sharedPreferences.getFloat(KEY_TOTAL_BANCA, 0.0f);
        double totalBanca = (double) totalBancaFloat;
        this.totalBanca = totalBanca; // Atualizar a variável totalBanca com o valor carregado

        // Recuperar a opção selecionada do RadioGroup
        int selectedRadioButtonId = sharedPreferences.getInt(KEY_SELECTED_OPTION, R.id.rb100Und);
        rgOpcoes.check(selectedRadioButtonId);

        // Atualizar o divisor de acordo com a opção selecionada
        if (selectedRadioButtonId == R.id.rb100Und) {
            divisor = 100.0;
        } else if (selectedRadioButtonId == R.id.rb50Und) {
            divisor = 50.0;
        } else if (selectedRadioButtonId == R.id.rb40Und) {
            divisor = 40.0;
        }

        // Chamar o método para calcular e exibir o valor da unidade com base na banca inicial e divisor
        calcularValorUnidade();



    }

    private void atualizarSpinner() {
        // Exemplo de como você pode atualizar o Spinner
        // Altere de acordo com a sua lógica
        spnHome.setSelection(0);
        spnAway.setSelection(0);
        spnMercado.setSelection(0); // Reseta para a primeira opção após adicionar

    }
    private void adicionarAposta() {
        // Verifica se todos os campos estão preenchidos antes de adicionar
        if (itemSelecionadoHome == null || itemSelecionadoAway == null || itemSelecionadoMercado == null) {
            Toast.makeText(getContext(), "Selecione todos os itens.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Capturar valores dos campos e spinners
        String homeTeam = itemSelecionadoHome;
        String awayTeam = itemSelecionadoAway;
        String mercado = itemSelecionadoMercado;
        String situacao = "Red";  // Pode ser alterado conforme sua lógica
        String dataInsercao = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // Capturar os valores numéricos
        double odd = valorOdd;
        double valor = valorUnd;
        double oddxValor = valorOdd * valorUnd;

        // Criar uma nova instância de MinhaLista
        MinhaLista novaAposta = new MinhaLista(homeTeam, awayTeam, mercado, situacao, dataInsercao, odd, valor, oddxValor);

        // Adiciona o item na primeira posição da lista
        listaItems.add(0, novaAposta); // Adiciona na posição 0

        // Salva os produtos (se necessário)
        salvarProdutos();

        // Notifica o adaptador da mudança na lista
        minhaListaAdapter.notifyItemInserted(0); // Notifica que um item foi inserido na posição 0
        minhaListaAdapter.notifyDataSetChanged(); // Pode ser mantido para garantir que a lista seja atualizada

        // Limpar os campos após a adição
        limparCampos();

        // Atualizar o Spinner
        atualizarSpinner();

        // Exibir uma mensagem de confirmação
        Toast.makeText(getContext(), "Aposta adicionada: " + homeTeam + " vs " + awayTeam, Toast.LENGTH_SHORT).show();
    }

    private void limparCampos() {
        etValorOdd.setText("");
        etValorUnd.setText("");
        spnHome.setSelection(0);
        spnAway.setSelection(0);
        spnMercado.setSelection(0);
        itemSelecionadoHome = spnHome.getItemAtPosition(0).toString();
        itemSelecionadoAway = spnAway.getItemAtPosition(0).toString();
        itemSelecionadoMercado = spnMercado.getItemAtPosition(0).toString();
    }

    // Método para calcular o resultado (Odd x Und) e exibir no TextView
    private void calcularOddxUnd() {
        if (valorOdd > 0 && valorUnd > 0) {
            double resultado = valorOdd * valorUnd;
            tvOddxUnd.setText(String.format("Valor: %.2f", resultado));
        } else {
            tvOddxUnd.setText("");  // Não exibe nada até que ambos os valores sejam válidos
        }
    }

    // Método para calcular e exibir o valor da unidade
    private void calcularValorUnidade() {
        if (bancaInicial > 0 && divisor > 0) {
            double valorUnidade = bancaInicial / divisor;
            tvUndBanca.setText(String.format("Unidade Sua é: %.2f", valorUnidade));
        } else {
            tvUndBanca.setText("");  // Deixa o TextView vazio até que uma opção seja escolhida
        }
    }

    // Método para configurar o Spinner
    private void setupSpinner(Spinner spinner, String prompt) {
        List<String> categoriasPrincipais = new ArrayList<>(minhaListaMap.keySet());
        categoriasPrincipais.add(0, prompt);

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoriasPrincipais);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCategorias);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSelecionada = categoriasPrincipais.get(position);

                if (categoriaSelecionada.equals(prompt)) {
                    return;
                }

                List<String> produtos = minhaListaMap.get(categoriaSelecionada);

                if (produtos != null && !produtos.isEmpty()) {
                    ArrayAdapter<String> adapterProdutos = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, produtos);
                    adapterProdutos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapterProdutos);
                }

                // Armazenar o item selecionado para cada Spinner
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String produtoSelecionado = produtos.get(position);

                        // Armazenar o item selecionado
                        if (spinner == spnHome) {
                            itemSelecionadoHome = produtoSelecionado;

                        } else if (spinner == spnAway) {
                            itemSelecionadoAway = produtoSelecionado;

                        } else if (spinner == spnMercado) {
                            itemSelecionadoMercado = produtoSelecionado;

                        }

                        Toast.makeText(getContext(), "Produto selecionado: " + produtoSelecionado, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Nenhuma ação necessária
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nenhuma ação necessária
            }
        });
    }

    private double parseDoubleBrasileiro(String valor) {
        try {
            // Configura o formato de números para o Brasil
            NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
            return nf.parse(valor).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0.0;  // Retorna 0.0 se houver erro na conversão
        }
    }



    @Override
    public void onBancaChange(double novaBanca) {
        totalBanca = novaBanca;
        tvTotalBanca.setText(String.format("Total Banca: R$ %.2f", totalBanca));
        salvarProdutos();  // Atualiza o valor de banca no SharedPreferences, se necessário
    }


    public void limparListaSharedPrerefereces(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Limpar a lista de produtos armazenada em SharedPreferences
        editor.remove(KEY_PRODUTOS);
        editor.apply(); // Aplica imediatamente a remoção
    }

    private void confirmarExclusaoItem(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Item")
                .setMessage("Tem certeza de que deseja excluir este item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Verifica se o índice é válido
                    if (position < 0 || position >= listaItems.size()) {
                        Toast.makeText(getContext(), "Item inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Remove o item da lista
                    MinhaLista itemRemovido = listaItems.get(position);
                    listaItems.remove(position);

                    // Atualiza o valor total da banca com base na situação do item removido
                    if ("Red".equals(itemRemovido.getSituacao())) {
                        totalBanca += itemRemovido.getValor(); // Adiciona o valor
                    } else {
                        totalBanca -= itemRemovido.getValor(); // Subtrai o valor
                    }

                    // Verifica se a lista de itens está vazia
                    if (listaItems.isEmpty()) {
                        // Se a lista estiver vazia, restaura a banca ao valor inicial
                        totalBanca = bancaInicial;
                    }

                    // Atualiza a interface do usuário
                    tvTotalBanca.setText(String.format("Total Banca: R$ %.2f", totalBanca));

                    // Salva a lista e totalBanca atualizados
                    salvarProdutos();

                    // Atualiza o adaptador e notifica a mudança da banca
                    minhaListaAdapter.notifyItemRemoved(position); // Notifica a remoção do item
                    minhaListaAdapter.atualizarBanca(totalBanca); // Atualiza a banca no adaptador

                    // Caso precise forçar a atualização da lista
                    minhaListaAdapter.notifyDataSetChanged();

                    Toast.makeText(getContext(), "Item excluído", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onItemLongClick(int position) {
        // Chama o método de confirmação de exclusão quando o item é pressionado longamente
        confirmarExclusaoItem(position);
    }

    private void atualizarTotalBanca() {
        totalBanca = bancaInicial;
        for (MinhaLista item : listaItems) {
            totalBanca += item.getValor();  // Supondo que `getValor()` retorne o valor do item
        }
        tvTotalBanca.setText(String.format("Total Banca: R$ %.2f", totalBanca));
    }
}