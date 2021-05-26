package tech.abralica.fbdbexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.UUID;

public class AddMovieFragment extends Fragment {
    private DatabaseReference databaseReference;
    private EditText movieName;
    private EditText urlPoster;
    private RatingBar ratingBar;
    private Button submit;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_movie_fragment, container, false);

        movieName = view.findViewById(R.id.edt_movieName);
        urlPoster = view.findViewById(R.id.edtImg);
        ratingBar = view.findViewById(R.id.ratingBar);
        submit = view.findViewById(R.id.btn_add);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        submit.setOnClickListener(view -> {
            if(movieName.getText().toString() != null && urlPoster.getText().toString() != null) {
                newMovie("53", movieName.getText().toString(), urlPoster.getText().toString(),
                        ratingBar.getRating());
            } else if (movieName.getText().toString() == null) {
                Toast.makeText(getContext(), "Please, enter a movie name!", Toast.LENGTH_SHORT).show();
            } else if (urlPoster.getText().toString() == null) {
                Toast.makeText(getContext(), "Please, enter a movie url!", Toast.LENGTH_SHORT).show();
            }
            requireActivity().onBackPressed();
        }
        );

        return view;
    }

    private void newMovie(String userId, String movieName, String moviePoster, float rating) {
        Movie movie = new Movie(UUID.randomUUID().toString(), movieName, moviePoster, rating);
        databaseReference.child("users").child(userId).child("movies").child(movie.getId()).setValue(movie);
    }
}
