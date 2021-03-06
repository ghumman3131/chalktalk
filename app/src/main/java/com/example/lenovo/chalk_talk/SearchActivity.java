package com.example.lenovo.chalk_talk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.lenovo.chalk_talk.dataModel.course_details;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {
    SearchView sv;

    String course;
    ArrayList<course_details> course_list;

    ArrayList<course_details> search_course_list;

    RecyclerView course_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        course_list = new ArrayList<>();

        search_course_list = new ArrayList<>();

        course_recycler = findViewById(R.id.recycler);

        course_recycler.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.VERTICAL, false));


        sv = (SearchView) findViewById(R.id.search_bar);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                filter_course(s);
                return false;
            }
        });

        get_course_list();

    }

    private void get_course_list() {
        FirebaseAuth firebase = FirebaseAuth.getInstance();
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        System.out.println("rrrr");

        String category = getIntent().getStringExtra("course");

        data.getReference().child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                course_list.clear();

                for(DataSnapshot all_course : dataSnapshot.getChildren()) {

                        for (DataSnapshot data2 : all_course.getChildren()) {


                            course_details details = data2.getValue(course_details.class);

                            course_details detail_with_key = new course_details(details.course_name, details.course_id, details.courseduration, details.tutorname, details.course_category, details.time, all_course.getKey());
                            System.out.println("rrrrrr");
                            course_list.add(detail_with_key);
                        }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class view_holder extends RecyclerView.ViewHolder{

        TextView course_id,course_name , date;
        LinearLayout course_lay;
        ImageView course_img;
        public view_holder(View itemView) {
            super(itemView);

            course_name = itemView.findViewById(R.id.course_name);
            course_lay = itemView.findViewById(R.id.course_lay);
            course_id = itemView.findViewById(R.id.course_id);
            course_img = itemView.findViewById(R.id.course_img);

            date = itemView.findViewById(R.id.date);
        }
    }

    public class Adapter extends RecyclerView.Adapter<view_holder>
    {

        @Override
        public view_holder onCreateViewHolder(ViewGroup parent, int viewType) {

            view_holder v = new view_holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.course_cell,parent , false ));

            return v ;
        }

        @Override
        public void onBindViewHolder(view_holder holder, int position) {


            final course_details data= search_course_list.get(position);
            holder.course_name.setText(data.course_name);
            if (data.course_category.equals("Computer Science")) {
                holder.course_img.setImageDrawable(getResources().getDrawable(R.drawable.cs_icon));

            } if(data.course_category.equals("Dance")) {
                holder.course_img.setImageDrawable(getResources().getDrawable(R.drawable.dance_icon));

            }
            if(data.course_category.equals("Music")) {
                holder.course_img.setImageDrawable(getResources().getDrawable(R.drawable.music_icon));

            }
            if(data.course_category.equals("Technology")) {
                holder.course_img.setImageDrawable(getResources().getDrawable(R.drawable.it_icon));

            }

            holder.date.setText(getDate(Long.parseLong(data.time)));


            holder.course_id.setText(data.course_id);
            holder.course_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String courseid=data.course_id;
                    String coursename=data.course_name;
                    String courseduration=data.courseduration;
                    String tutorname=data.tutorname;

                    Intent i=new Intent(SearchActivity.this,course_detail.class);
                    i.putExtra("coursename",coursename);
                    i.putExtra("courseid",courseid);
                    i.putExtra("courseduration",courseduration);
                    i.putExtra("coursetutor",tutorname);
                    i.putExtra("tutor_contact" , data.tutor_email);
                    i.putExtra("course_category" , data.course_category);
                    i.putExtra("course_time" , data.time);
                    startActivity(i);


                }
            });
        }

        @Override
        public int getItemCount() {
            return search_course_list.size();
        }
    }


    private void filter_course(String s)
    {
        search_course_list.clear();
        
        for (course_details details : course_list)
        {
            if(details.course_name.toLowerCase().contains(s.toLowerCase()))
            {
                search_course_list.add(details);
            }
        }

        Adapter adapter = new Adapter();

        course_recycler.setAdapter(adapter);
    }

    public static String getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy hh:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}

