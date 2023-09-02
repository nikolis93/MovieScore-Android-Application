package com.netdil.recapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Filter;
import android.widget.Toast;

public class myDialogFragment extends DialogFragment {

    String [] gernes =  ApplContext.getAppContext().getResources().getStringArray(R.array.movieGenres);
    String [] ageRatings =  ApplContext.getAppContext().getResources().getStringArray(R.array.agerating);
    String [] popularities =  ApplContext.getAppContext().getResources().getStringArray(R.array.popularity);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String calledFrom = getArguments().getString("calledFrom");
        int prevPosition = getArguments().getInt("prevPosition");
        if(calledFrom.equalsIgnoreCase("genres")) {

            builder.setTitle("Select Genre")
                    .setSingleChoiceItems(R.array.movieGenres,prevPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            FilterActivity.setGenre(gernes[which]);
                            FilterActivity.setGenrePosition(which);
                            Toast.makeText(ApplContext.getAppContext(), gernes[which], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    });
        }else if(calledFrom.equalsIgnoreCase("ageRating")){
            builder.setTitle("Select Age Rating")
                    .setSingleChoiceItems(R.array.agerating,prevPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            FilterActivity.setAgeRating(ageRatings[which]);
                            FilterActivity.setAgeRatingPosition(which);
                            Toast.makeText(ApplContext.getAppContext(), ageRatings[which], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }else if(calledFrom.equalsIgnoreCase("popularity")){
            builder.setTitle("Select Popularity")
                    .setSingleChoiceItems(R.array.popularity,prevPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            FilterActivity.setPopularity(popularities[which]);
                            FilterActivity.setPopularityPosition(which);
                            Toast.makeText(ApplContext.getAppContext(), popularities[which], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }


        return builder.create();

    }

}
