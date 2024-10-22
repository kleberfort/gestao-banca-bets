package com.example.gestaobanca.fragments;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaobanca.R;
import com.example.gestaobanca.adapter.MinhaListaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText etBancaInicialCarregar, etValorOdd, etValorUnd;
    private TextView tvUndBanca, tvOddxUnd, tvTotalBanca;
    private Spinner spnHome, spnAway, spnMercado;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdicionar;
    private RadioButton rb100Und, rb50Und, rb40Und;
    private RadioGroup rgOpcoes;
    private HashMap<String, List<String>> minhaListaMap;
    private ArrayList<String> listaItems; // Lista para o RecyclerView
    private MinhaListaAdapter minhaListaAdapter; // Adaptador do RecyclerView


    private double bancaInicial = 0.0;
    private double divisor = 0.0;  //

    private double valorOdd = 0.0;
    private double valorUnd = 0.0;



    // Variáveis para armazenar os itens selecionados
    private String itemSelecionadoHome;
    private String itemSelecionadoAway;
    private String itemSelecionadoMercado;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAdicionar = view.findViewById(R.id.floatingActionButton);
        rgOpcoes = view.findViewById(R.id.rgOpcoes);
        rb100Und = view.findViewById(R.id.rb100Und);
        rb50Und = view.findViewById(R.id.rb50Und);
        rb40Und = view.findViewById(R.id.rb40Und);

        // Recuperar os dados do HashMap passado como argumento
        if (getArguments() != null) {
            minhaListaMap = (HashMap<String, List<String>>) getArguments().getSerializable("categoryItemsMap");

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

        // Adiciona o TextWatcher ao EditText para capturar a entrada do usuário
        etBancaInicialCarregar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        bancaInicial = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        bancaInicial = 0.0; // Definir como 0 se houver erro na conversão
                    }
                    calcularValorUnidade();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
        });



        // Configurar os Spinners
        if (minhaListaMap != null) {
            setupSpinner(spnHome, "Selecione uma categoria");
            setupSpinner(spnAway, "Selecione uma categoria");
            setupSpinner(spnMercado, "Selecione uma categoria");
        }





            return view;
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
            tvUndBanca.setText(String.format("1 Und Sua é: %.2f", valorUnidade));
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

                    // Adicionar listener para selecionar produtos
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String produtoSelecionado = produtos.get(position);

                            // Armazenar o item selecionado para cada Spinner
                            if (spinner == spnHome) {
                                itemSelecionadoHome = produtoSelecionado;
                            } else if (spinner == spnAway) {
                                itemSelecionadoAway = produtoSelecionado;
                            } else if (spinner == spnMercado) {
                                itemSelecionadoMercado = produtoSelecionado;
                            }

                            Toast.makeText(getContext(), "Produto selecionado: " + produtoSelecionado, Toast.LENGTH_SHORT).show();

                            Log.d("itens", "selecao: " +itemSelecionadoHome +"-"+ itemSelecionadoAway+"-"+itemSelecionadoMercado);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Nenhuma ação necessária
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nenhuma ação necessária
            }
        });
    }

    // Métodos para recuperar os itens selecionados
    public String getItemSelecionadoHome() {
        return itemSelecionadoHome;
    }

    public String getItemSelecionadoAway() {
        return itemSelecionadoAway;
    }

    public String getItemSelecionadoMercado() {
        return itemSelecionadoMercado;
    }

    }


