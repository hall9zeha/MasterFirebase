package com.barryzea.masterdetailcloud;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.barryzea.masterdetailcloud.databinding.FragmentItemListBinding;
import com.barryzea.masterdetailcloud.databinding.ItemListContentBinding;

import com.barryzea.masterdetailcloud.placeholder.Foods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a list of Items. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String PATH_CODE = "code";
    private static final String PATH_PROFILE = "profile";
    private Spinner spinner;
    private Button btnRefresh;
    private ArrayAdapter<String> adapterFood ;
    List<Foods.Food> foodList= new ArrayList<>();
    Foods.Food foodUpdate;
    EditText edtName;
    EditText edtPrice;




    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentItemListBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        edtName=binding.editTextFoodName;
        edtPrice= binding.editTextPrice;
        spinner= binding.spFood;
        btnRefresh= binding.refreshSpinner;

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Foods.Food food= new Foods.Food(binding.editTextFoodName.getText().toString(), binding.editTextPrice.getText().toString()) ;
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef=db.getReference("Foods");

                //Foods.Food updateFood = Foods.getItem(food.getName());
                if(foodUpdate != null){
                    dbRef.child(foodUpdate.getId()).setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                foodUpdate=null;
                            }
                        }
                    });
                }
                else {
                    dbRef.push().setValue(food);
                }

                binding.editTextPrice.setText("");
                binding.editTextFoodName.setText("");
            }
        });
        btnRefresh.setOnClickListener(view -> {
            adapterFood.clear();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference dbRef= db.getReference("Foods");

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot snap : snapshot.getChildren()){
                        Foods.Food food =snap.getValue(Foods.Food.class);
                        food.setId(snap.getKey());
                        foodList.add(food);
                        adapterFood.add(food.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Error al cargar el adapter spinner", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return binding.getRoot();

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            Foods.Food item =
                    (Foods.Food) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, arguments);
            } else {
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
            }
        };

        /*
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        View.OnContextClickListener onContextClickListener = itemView -> {
            Foods.Food item =
                    (Foods.Food) itemView.getTag();
            Toast.makeText(
                    itemView.getContext(),
                    "Context click of item " + item.id,
                    Toast.LENGTH_LONG
            ).show();
            return true;
        };

        setupRecyclerView(recyclerView, onClickListener, onContextClickListener);
        configSpinner();


    }

    private void configSpinner() {
            spinner.setOnItemSelectedListener(this);

        adapterFood=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        adapterFood.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterFood);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        foodUpdate=foodList.get(position);
        edtName.setText(foodUpdate.getName());
        edtPrice.setText(foodUpdate.getPrice());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener,
            View.OnContextClickListener onContextClickListener
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                Foods.ITEMS,
                onClickListener,
                onContextClickListener
        ));

        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference dbRef= db.getReference("Foods");

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Foods.Food food= snapshot.getValue(Foods.Food.class);
                food.setId(snapshot.getKey());

                if (!Foods.ITEMS.contains(food)) {
                    Foods.addItem(food);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Foods.Food food= snapshot.getValue(Foods.Food.class);
                food.setId(snapshot.getKey());

                if (Foods.ITEMS.contains(food)) {
                    Foods.updateItem(food);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                Foods.Food food= snapshot.getValue(Foods.Food.class);
                food.setId(snapshot.getKey());

                if (Foods.ITEMS.contains(food)) {
                    Foods.deleteItem(food);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_info,menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
                final TextView textViewCode= new TextView(getActivity());
                textViewCode.setGravity(View.TEXT_ALIGNMENT_CENTER);
                LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textViewCode.setLayoutParams(params);
                textViewCode.setGravity(Gravity.CENTER_HORIZONTAL);
                textViewCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference dbRef= db.getReference(PATH_PROFILE).child(PATH_CODE);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        textViewCode.setText(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.title_dialog)
                        .setPositiveButton(R.string.ok, null)
                        .setView(textViewCode)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Foods.Food> mValues;
        private final View.OnClickListener mOnClickListener;
        private final View.OnContextClickListener mOnContextClickListener;

        SimpleItemRecyclerViewAdapter(List<Foods.Food> items,
                                      View.OnClickListener onClickListener,
                                      View.OnContextClickListener onContextClickListener) {
            mValues = items;
            mOnClickListener = onClickListener;
            mOnContextClickListener = onContextClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding =
                    ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText("S/." + mValues.get(position).getPrice());
            holder.mContentView.setText(mValues.get(position).name);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setOnContextClickListener(mOnContextClickListener);
            }
            holder.btnDelete.setOnClickListener(v->{
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference dbRef=db.getReference("Foods");
                dbRef.child(mValues.get(position).getId()).removeValue();
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final Button btnDelete;

            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.idText;
                mContentView = binding.content;
                btnDelete=binding.btnDelete;
            }

        }
    }
}