package tech.abralica.fbdbexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String userId = "53";
    public static String idMovies = "idMovies";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ScaleAnimation shrinkAnimation;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Movie, MovieViewHolder> adapter;
    private DatabaseReference movieReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvMovies);
        shrinkAnimation = new ScaleAnimation(1.15f, 0, 1.15f, 0
                , Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }

        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        movieReference =
                FirebaseDatabase.getInstance().getReference().child("users").child(userId).child(
                        "movies");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        FirebaseRecyclerOptions<Movie> options =
                new FirebaseRecyclerOptions.Builder<Movie>().setQuery(movieReference,
                        Movie.class).build();

        FirebaseRecyclerAdapter<Movie, MovieViewHolder> adapter =
                new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull MainActivity.MovieViewHolder holder, int position, @NonNull Movie model) {
                        holder.tvMovieName.setText(model.getMovieName());
                        holder.ratingBar.setRating(model.getMovieRating());
                        Picasso.get()
                                .load(model.getMoviePoster())
                                .fit().centerCrop()
                                .into(holder.ivMoviePoster);

                        holder.eliminar.setOnClickListener(view -> {
                            AlertDialog.Builder confirm = new AlertDialog.Builder(getApplicationContext());
                            confirm.setMessage("Are you sure?").setCancelable(false)
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        databaseReference.child("users").child(userId).child("movies").child(model.getId()).removeValue();
                                        Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                    }).setNegativeButton("No", ((dialog, which) -> dialog.cancel()));

                            AlertDialog alertDialog = confirm.create();
                            alertDialog.setTitle("Delete Item");
                            alertDialog.show();
                        });

                        holder.modificar.setOnClickListener(view -> {
                            idMovies = model.getId();
                            // TODO: Pasar el id a un nuevo Activity
                        });
                    }

                    @NonNull
                    @Override
                    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =
                                LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.movie_board_item, parent, false);
                        return new MovieViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(view -> {

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    new AddMovieFragment()).addToBackStack(null)
                    .commit();

            shrinkAnimation.setDuration(400);
            fab.setAnimation(shrinkAnimation);
            shrinkAnimation.start();
            shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fab.getVisibility() == View.GONE) {
            fab.setVisibility(View.VISIBLE);
        }
    }

    private static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName;
        RatingBar ratingBar;
        ImageView ivMoviePoster;
        ImageButton modificar;
        ImageButton eliminar;

        public MovieViewHolder(View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tvName);
            ratingBar = itemView.findViewById(R.id.rbMovie);
            ivMoviePoster = itemView.findViewById(R.id.mvPoster);
            modificar = itemView.findViewById(R.id.btnUpdate);
            eliminar = itemView.findViewById(R.id.btnDelete);
        }
    }
}