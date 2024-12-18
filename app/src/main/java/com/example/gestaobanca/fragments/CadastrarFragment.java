package com.example.gestaobanca.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gestaobanca.R;
import com.example.gestaobanca.adapter.ItemAdapter;
import com.example.gestaobanca.databinding.FragmentCadastrarBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CadastrarFragment extends Fragment implements ItemAdapter.OnItemLongClickListener {

    private FragmentCadastrarBinding binding;

    private EditText editTextCategory;
    private Button buttonAddCategory, buttonEditCategory, buttonDeleteCategory;
    private Spinner spinnerCategories;
    private FloatingActionButton fabAddItem;
    private RecyclerView recyclerViewItems;

    private ItemAdapter itemAdapter;

    private ArrayList<String> categories;
    private ArrayAdapter<String> spinnerAdapter;
    private HashMap<String, ArrayList<String>> categoryItemsMap;  // HashMap to store items for each category
    private ArrayList<String> currentItemsList;
    private String currentCategory;


    // Nome do SharedPreferences e chave para salvar a lista
    private static final String PREFS_NAME = "my_prefs";
    private static final String LIST_KEY = "produtos_key";


    private OnCategoryItemsMapListener listener;



    // Interface para enviar o HashMap
    public interface OnCategoryItemsMapListener {
        void onEnviarCategoryItemsMap(HashMap<String, ArrayList<String>> categoryItemsMap);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verifica se o contexto (Activity) implementa a interface
        if (context instanceof OnCategoryItemsMapListener) {
            listener = (OnCategoryItemsMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " deve implementar OnCategoryItemsMapListener");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cadastrar, container, false);


        // Inicializando os componentes
        editTextCategory = view.findViewById(R.id.editTextCategory);
        buttonAddCategory = view.findViewById(R.id.buttonAddCategory);
        spinnerCategories = view.findViewById(R.id.spinnerCategories);
        buttonEditCategory = view.findViewById(R.id.buttonEditCategory);
        buttonDeleteCategory = view.findViewById(R.id.buttonDeleteCategory);
        fabAddItem = view.findViewById(R.id.fabAddItem);
        recyclerViewItems = view.findViewById(R.id.recyclerViewItems);

        // Inicializando as listas
        categories = new ArrayList<>();
        categoryItemsMap = new HashMap<>();
        currentItemsList = new ArrayList<>();

        // Ordena a lista em ordem alfabética
        Collections.sort(currentItemsList);



        // Configurando o Spinner
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        // Carregar dados do SharedPreferences
        loadDataFromSharedPreferences();



        // Configurando o RecyclerView e o Adapter
        itemAdapter = new ItemAdapter(currentItemsList, this);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewItems.setAdapter(itemAdapter);


        // Método para chamar o listener e passar o HashMap
        if (listener != null) {
            listener.onEnviarCategoryItemsMap(categoryItemsMap);
        }


        // Botão para adicionar uma nova categoria
        buttonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = editTextCategory.getText().toString().trim();
                if (!newCategory.isEmpty() && !categories.contains(newCategory)) {
                    categories.add(newCategory);
                    Collections.sort(categories);  // Ordena as categorias
                    spinnerAdapter.notifyDataSetChanged();
                    categoryItemsMap.put(newCategory, new ArrayList<>());  // Cria uma lista de itens para a nova categoria

                    saveDataToSharedPreferences();
                    editTextCategory.setText("");  // Limpa o campo após adicionar


                }
            }
        });

        // Listener para mudar a lista do RecyclerView ao selecionar uma categoria no Spinner
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = categories.get(position);
                updateRecyclerViewForCategory(currentCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não faz nada se nada for selecionado
            }
        });

        // FloatingActionButton para adicionar um item relacionado à categoria selecionada
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNomesItens();
            }
        });


        // Botão para editar a categoria
        buttonEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCategory != null) {
                    editCategoryDialog(currentCategory);
                }
            }
        });

        buttonDeleteCategory.setOnClickListener(v -> {
            // Verificar se há categorias cadastradas
            if (categories.isEmpty()) {
                // Mostrar mensagem avisando que não há categorias para deletar
                Toast.makeText(getContext(), "No categories available to delete", Toast.LENGTH_SHORT).show();
            } else {
                // Pegar a categoria selecionada
                String selectedCategory = spinnerCategories.getSelectedItem().toString();

                if (selectedCategory != null && !selectedCategory.isEmpty()) {
                    // Mostrar confirmação ao usuário
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Category")
                            .setMessage("Are you sure you want to delete the category '" + selectedCategory + "' and all its items?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Chama o método para deletar a categoria
                                deleteCategory(selectedCategory);
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    // Exibir mensagem se nenhuma categoria estiver selecionada
                    Toast.makeText(getContext(), "No category selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }


    private void AddNomesItens() {

        // Verifica se há uma categoria atual definida
        if (currentCategory == null || currentCategory.isEmpty()) {
            Toast.makeText(getContext(), "Adicione uma Categoria para adicionar um item!", Toast.LENGTH_SHORT).show();
            return;  // Sai do método se não houver categoria
        }

        // Se houver uma categoria, segue com o fluxo para adicionar itens
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.adicionar_item_lista, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(dialogView);

        EditText nomeInput = dialogView.findViewById(R.id.edit_text_name);

        dialogBuilder.setTitle("Adicionar item");
        dialogBuilder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = nomeInput.getText().toString().trim();

                if (!itemName.isEmpty()) {  // Verifica se o nome do item não está vazio
                    ArrayList<String> items = categoryItemsMap.get(currentCategory); // Obtém a lista de itens da categoria

                    if (items != null) {
                        items.add(itemName);  // Adiciona o item à lista da categoria
                        Collections.sort(items);  // Ordena os itens da categoria
                        saveDataToSharedPreferences();
                        updateRecyclerViewForCategory(currentCategory);  // Atualiza o RecyclerView
                    }
                } else {
                    Toast.makeText(getContext(), "O nome do item não pode estar vazio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancelar", null);

        // Mostrar o dialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    // Atualiza o RecyclerView para exibir os itens relacionados à categoria atual
    private void updateRecyclerViewForCategory(String category) {
        currentItemsList.clear();  // Limpa a lista atual de itens
        if (categoryItemsMap.containsKey(category)) {
            currentItemsList.addAll(categoryItemsMap.get(category));  // Adiciona os itens da categoria selecionada
        }
        itemAdapter.notifyDataSetChanged();  // Notifica o Adapter para atualizar a visualização
    }

    // Dialog para editar o nome da categoria
    private void editCategoryDialog(final String oldCategoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Categoria");

        // Campo de input
        final EditText input = new EditText(getContext());
        input.setText(oldCategoryName);
        builder.setView(input);

        // Botões de confirmação/cancelamento
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategoryName = input.getText().toString().trim();
                if (!newCategoryName.isEmpty() && !categories.contains(newCategoryName)) {
                    // Renomeia a categoria
                    ArrayList<String> items = categoryItemsMap.remove(oldCategoryName);
                    categoryItemsMap.put(newCategoryName, items);

                    // Atualiza a lista de categorias
                    int index = categories.indexOf(oldCategoryName);
                    categories.set(index, newCategoryName);
                    saveDataToSharedPreferences();
                    spinnerAdapter.notifyDataSetChanged();

                    // Atualiza a categoria atual
                    currentCategory = newCategoryName;
                    updateRecyclerViewForCategory(currentCategory);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Método para excluir uma categoria
    private void deleteCategory(String categoryName) {
        // Remove a categoria e seus itens
        categoryItemsMap.remove(categoryName);
        categories.remove(categoryName);

        saveDataToSharedPreferences();

        // Atualiza o Spinner
        spinnerAdapter.notifyDataSetChanged();

        // Limpa a lista de itens no RecyclerView
        currentItemsList.clear();
        itemAdapter.notifyDataSetChanged();
    }

    private void saveDataToSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(categoryItemsMap);  // Converte o HashMap para JSON

        editor.putString("categoryItemsMap", json);   // Salva o JSON no SharedPreferences
        editor.apply();  // Aplica as mudanças
    }

    private void loadDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("categoryItemsMap", null);  // Recupera o JSON

        if (json != null) {
            Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            categoryItemsMap = gson.fromJson(json, type);  // Converte o JSON de volta para HashMap
        } else {
            categoryItemsMap = new HashMap<>();  // Se não houver dados salvos, inicializa um novo HashMap
        }

        // Atualiza a lista de categorias e o spinner
        categories.clear();
        categories.addAll(categoryItemsMap.keySet());
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongClick(int position) {
        String itemToDelete = currentItemsList.get(position);

        new AlertDialog.Builder(getContext())
                .setTitle("Excluir Item")
                .setMessage("Tem certeza de que deseja excluir o item '" + itemToDelete + "'?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Remove o item da lista atual e do HashMap
                    currentItemsList.remove(position);
                    categoryItemsMap.get(currentCategory).remove(itemToDelete);

                    // Atualiza o RecyclerView e salva as alterações no SharedPreferences
                    itemAdapter.notifyItemRemoved(position);
                    saveDataToSharedPreferences();

                    Toast.makeText(getContext(), "Item excluído", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }

}