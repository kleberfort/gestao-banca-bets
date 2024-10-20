package com.example.gestaobanca.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

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
import com.example.gestaobanca.model.NomesLista;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class CadastrarFragment extends Fragment {

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

        // Configurando o Spinner
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        // Configurando o RecyclerView e o Adapter
        itemAdapter = new ItemAdapter(currentItemsList);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewItems.setAdapter(itemAdapter);

        // Botão para adicionar uma nova categoria
        buttonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = editTextCategory.getText().toString().trim();
                if (!newCategory.isEmpty() && !categories.contains(newCategory)) {
                    categories.add(newCategory);
                    spinnerAdapter.notifyDataSetChanged();
                    categoryItemsMap.put(newCategory, new ArrayList<>());  // Cria uma lista de itens para a nova categoria
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


//                if (currentCategory != null && !currentCategory.isEmpty()) {
//                    NomesLista nomesLista = new NomesLista();
//                    String newItem = "Item related to: " + currentCategory;
//                    ArrayList<String> items = categoryItemsMap.get(currentCategory); // Obtém a lista de itens da categoria
//                    if (items != null) {
//                        items.add(newItem);  // Adiciona o item à lista da categoria
//                        updateRecyclerViewForCategory(currentCategory);  // Atualiza o RecyclerView
//                    }
//                }
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

        // Atualiza o Spinner
        spinnerAdapter.notifyDataSetChanged();

        // Limpa a lista de itens no RecyclerView
        currentItemsList.clear();
        itemAdapter.notifyDataSetChanged();
    }

}