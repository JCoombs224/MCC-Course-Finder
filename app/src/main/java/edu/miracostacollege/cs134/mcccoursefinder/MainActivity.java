package edu.miracostacollege.cs134.mcccoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.miracostacollege.cs134.mcccoursefinder.model.Course;
import edu.miracostacollege.cs134.mcccoursefinder.model.DBHelper;
import edu.miracostacollege.cs134.mcccoursefinder.model.Instructor;
import edu.miracostacollege.cs134.mcccoursefinder.model.Offering;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private static final String TAG = "MCC Course Finder";

    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;
    private OfferingListAdapter offeringsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allCoursesList = db.getAllCourses();
        allInstructorsList = db.getAllInstructors();
        allOfferingsList = db.getAllOfferings();
        filteredOfferingsList = new ArrayList<>(allOfferingsList);

        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(courseTitleTextWatcher);

        // Wire up the offerings listview
        offeringsListView = findViewById(R.id.offeringsListView);
        // Instantiate list adapter
        offeringsListAdapter = new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);
        // Associate list view with the list adapter
        offeringsListView.setAdapter(offeringsListAdapter);
        //TODO (1): Construct instructorSpinnerAdapter using the method getInstructorNames()
        //TODO: to populate the spinner.
        // Wire up the spinner
        instructorSpinner = findViewById(R.id.instructorSpinner);
        ArrayAdapter<String> instructorSpinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getInstructorNames());
        instructorSpinner.setAdapter(instructorSpinnerAdapter);

        instructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If the position is 0, call reset and return
                if (position==0)
                {
                    reset(view);
                    return;
                }
                // Get the selected name
                String selectedName = String.valueOf(parent.getItemAtPosition(position));
                // Clear out the list adapter for Offerings
                offeringsListAdapter.clear();
                // Rebuild it with courses offered by the selected instructor
                for (int i = 0; i < allOfferingsList.size(); i++) {
                    if (allOfferingsList.get(i).getInstructor().getFullName().equalsIgnoreCase(selectedName))
                        offeringsListAdapter.add(allOfferingsList.get(i));
                }

                offeringsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    //DONE (2): Create a method getInstructorNames that returns a String[] containing the entry
    //DONE: "[SELECT INSTRUCTOR]" at position 0, followed by all the full instructor names in the
    //DONE: allInstructorsList
    public String[] getInstructorNames()
    {
        String[] names = new String[allInstructorsList.size() + 1];
        names[0] = "[SELECT INSTRUCTOR]";
        for (int i = 1; i < names.length; i++) {
            names[i] = allInstructorsList.get(i-1).getFullName();
        }
        return names;
    }


    //TODO (3): Create a void method named reset that sets the test of the edit text back to an
    //TODO: empty string, sets the selection of the Spinner to 0 and clears out the offeringListAdapter,
    //TODO: then rebuild it with the allOfferingsList

    public void reset(View v)
    {
        courseTitleEditText.setText("");
        instructorSpinner.setSelection(0);
        offeringsListAdapter.clear();
        // rebuild list adapter
        for (Offering o : allOfferingsList)
            offeringsListAdapter.add(o);
    }


    //TODO (4): Create a TextWatcher named courseTitleTextWatcher that will implement the onTextChanged method.
    //TODO: In this method, set the selection of the instructorSpinner to 0, then
    //TODO: Clear the offeringListAdapter
    //TODO: If the entry is an empty String "", the offeringListAdapter should addAll from the allOfferingsList
    //TODO: Else, the offeringListAdapter should add each Offering whose course title starts with the entry.
    public TextWatcher courseTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            String cleanText = text.toString().toLowerCase();
            if (!cleanText.isEmpty())
            {
                offeringsListAdapter.clear();
                for (Offering o : allOfferingsList)
                {
                    Course c = o.getCourse();
                    if (c.getFullName().toLowerCase().contains(cleanText))
                        offeringsListAdapter.add(o);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    //TODO (5): Create an AdapterView.OnItemSelectedListener named instructorSpinnerListener and implement
    //TODO: the onItemSelected method to do the following:
    //TODO: If the selectedInstructorName != "[Select Instructor]", clear the offeringListAdapter,
    //TODO: then rebuild it with every Offering that has an instructor whose full name equals the one selected.
}
