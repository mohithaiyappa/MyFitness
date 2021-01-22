package com.example.myfitness.tab_screen.week_tab;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfitness.R;
import com.example.myfitness.model.NewWeekEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;

public class WeekRecyclerAdapter extends RecyclerView.Adapter<WeekRecyclerAdapter.WeekViewHolder> {

    private List<NewWeekEvent> weekEvents = new ArrayList<>();
    private int background = Color.parseColor("#3F7388");

    public WeekRecyclerAdapter(List<NewWeekEvent> events) {
        weekEvents = events;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.hour_view_layout, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    public void updateData(List<NewWeekEvent> newEvents) {
        weekEvents = newEvents;
        notifyDataSetChanged();
    }

    class WeekViewHolder extends RecyclerView.ViewHolder {
        TextView hourText;
        TextView mondayQ1, mondayQ2, mondayQ3, mondayQ4;
        TextView tuesdayQ1, tuesdayQ2, tuesdayQ3, tuesdayQ4;
        TextView wednesdayQ1, wednesdayQ2, wednesdayQ3, wednesdayQ4;
        TextView thursdayQ1, thursdayQ2, thursdayQ3, thursdayQ4;
        TextView fridayQ1, fridayQ2, fridayQ3, fridayQ4;
        TextView saturdayQ1, saturdayQ2, saturdayQ3, saturdayQ4;
        TextView sundayQ1, sundayQ2, sundayQ3, sundayQ4;

        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            hourText = itemView.findViewById(R.id.hourText);

            //monday
            mondayQ1 = itemView.findViewById(R.id.mondayQ1);
            mondayQ2 = itemView.findViewById(R.id.mondayQ2);
            mondayQ3 = itemView.findViewById(R.id.mondayQ3);
            mondayQ4 = itemView.findViewById(R.id.mondayQ4);
            //tuesday
            tuesdayQ1 = itemView.findViewById(R.id.tuesdayQ1);
            tuesdayQ2 = itemView.findViewById(R.id.tuesdayQ2);
            tuesdayQ3 = itemView.findViewById(R.id.tuesdayQ3);
            tuesdayQ4 = itemView.findViewById(R.id.tuesdayQ4);
            //wednesday
            wednesdayQ1 = itemView.findViewById(R.id.wednesdayQ1);
            wednesdayQ2 = itemView.findViewById(R.id.wednesdayQ2);
            wednesdayQ3 = itemView.findViewById(R.id.wednesdayQ3);
            wednesdayQ4 = itemView.findViewById(R.id.wednesdayQ4);
            //thursday
            thursdayQ1 = itemView.findViewById(R.id.thursdayQ1);
            thursdayQ2 = itemView.findViewById(R.id.thursdayQ2);
            thursdayQ3 = itemView.findViewById(R.id.thursdayQ3);
            thursdayQ4 = itemView.findViewById(R.id.thursdayQ4);
            //friday
            fridayQ1 = itemView.findViewById(R.id.fridayQ1);
            fridayQ2 = itemView.findViewById(R.id.fridayQ2);
            fridayQ3 = itemView.findViewById(R.id.fridayQ3);
            fridayQ4 = itemView.findViewById(R.id.fridayQ4);
            //saturday
            saturdayQ1 = itemView.findViewById(R.id.saturdayQ1);
            saturdayQ2 = itemView.findViewById(R.id.saturdayQ2);
            saturdayQ3 = itemView.findViewById(R.id.saturdayQ3);
            saturdayQ4 = itemView.findViewById(R.id.saturdayQ4);
            //sunday
            sundayQ1 = itemView.findViewById(R.id.sundayQ1);
            sundayQ2 = itemView.findViewById(R.id.sundayQ2);
            sundayQ3 = itemView.findViewById(R.id.sundayQ3);
            sundayQ4 = itemView.findViewById(R.id.sundayQ4);
        }

        public void bindData(int position) {
            String hrText = String.format("%02d:00", position);
            hourText.setText(hrText);
            clearViews();

            try {
                for (NewWeekEvent event : weekEvents) {

                    switch (event.getDayOfWeek()) {
                        case Calendar.SUNDAY:
                            sundayEvent(event, position);
                            break;
                        case Calendar.MONDAY:
                            mondayEvent(event, position);
                            break;
                        case Calendar.TUESDAY:
                            tuesdayEvent(event, position);
                            break;
                        case Calendar.WEDNESDAY:
                            wednesdayEvent(event, position);
                            break;
                        case Calendar.THURSDAY:
                            thursdayEvent(event, position);
                            break;
                        case Calendar.FRIDAY:
                            fridayEvent(event, position);
                            break;
                        case Calendar.SATURDAY:
                            saturdayEvent(event, position);
                            break;
                    }
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }

            /*
            mondayQ1.setBackgroundColor(background);
            tuesdayQ2.setBackgroundColor(background);
            wednesdayQ3.setBackgroundColor(background);
            thursdayQ4.setBackgroundColor(background);
            fridayQ3.setBackgroundColor(background);
            saturdayQ2.setBackgroundColor(background);
            sundayQ1.setBackgroundColor(background);*/
        }

        public void clearViews() {
            mondayQ1.setBackgroundColor(Color.WHITE);
            mondayQ1.setText("");
            mondayQ2.setBackgroundColor(Color.WHITE);
            mondayQ2.setText("");
            mondayQ3.setBackgroundColor(Color.WHITE);
            mondayQ3.setText("");
            mondayQ4.setBackgroundColor(Color.WHITE);
            mondayQ4.setText("");

            tuesdayQ1.setBackgroundColor(Color.WHITE);
            tuesdayQ1.setText("");
            tuesdayQ2.setBackgroundColor(Color.WHITE);
            tuesdayQ2.setText("");
            tuesdayQ3.setBackgroundColor(Color.WHITE);
            tuesdayQ3.setText("");
            tuesdayQ4.setBackgroundColor(Color.WHITE);
            tuesdayQ4.setText("");

            wednesdayQ1.setBackgroundColor(Color.WHITE);
            wednesdayQ1.setText("");
            wednesdayQ2.setBackgroundColor(Color.WHITE);
            wednesdayQ2.setText("");
            wednesdayQ3.setBackgroundColor(Color.WHITE);
            wednesdayQ3.setText("");
            wednesdayQ4.setBackgroundColor(Color.WHITE);
            wednesdayQ4.setText("");

            thursdayQ1.setBackgroundColor(Color.WHITE);
            thursdayQ1.setText("");
            thursdayQ2.setBackgroundColor(Color.WHITE);
            thursdayQ2.setText("");
            thursdayQ3.setBackgroundColor(Color.WHITE);
            thursdayQ3.setText("");
            thursdayQ4.setBackgroundColor(Color.WHITE);
            thursdayQ4.setText("");

            fridayQ1.setBackgroundColor(Color.WHITE);
            fridayQ1.setText("");
            fridayQ2.setBackgroundColor(Color.WHITE);
            fridayQ2.setText("");
            fridayQ3.setBackgroundColor(Color.WHITE);
            fridayQ3.setText("");
            fridayQ4.setBackgroundColor(Color.WHITE);
            fridayQ4.setText("");

            saturdayQ1.setBackgroundColor(Color.WHITE);
            saturdayQ1.setText("");
            saturdayQ2.setBackgroundColor(Color.WHITE);
            saturdayQ2.setText("");
            saturdayQ3.setBackgroundColor(Color.WHITE);
            saturdayQ3.setText("");
            saturdayQ4.setBackgroundColor(Color.WHITE);
            saturdayQ4.setText("");

            sundayQ1.setBackgroundColor(Color.WHITE);
            sundayQ1.setText("");
            sundayQ2.setBackgroundColor(Color.WHITE);
            sundayQ2.setText("");
            sundayQ3.setBackgroundColor(Color.WHITE);
            sundayQ3.setText("");
            sundayQ4.setBackgroundColor(Color.WHITE);
            sundayQ4.setText("");

        }

        public void sundayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) sundayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) sundayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) sundayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) sundayQ4.setBackgroundColor(background);

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) sundayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) sundayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) sundayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) sundayQ4.setText(event.eventDisplayName);
            }
        }

        public void mondayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) mondayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) mondayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) mondayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) mondayQ4.setBackgroundColor(background);

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) mondayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) mondayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) mondayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) mondayQ4.setText(event.eventDisplayName);
            }
        }

        public void tuesdayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) tuesdayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) tuesdayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) tuesdayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) tuesdayQ4.setBackgroundColor(background);

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) tuesdayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) tuesdayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) tuesdayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) tuesdayQ4.setText(event.eventDisplayName);
            }
        }

        public void wednesdayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) wednesdayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) wednesdayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) wednesdayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) wednesdayQ4.setBackgroundColor(background);

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) wednesdayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) wednesdayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) wednesdayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) wednesdayQ4.setText(event.eventDisplayName);
            }
        }

        public void thursdayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) thursdayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) thursdayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) thursdayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) thursdayQ4.setBackgroundColor(background);

            Log.d("NewTestingAdapter", "thursdayEvent: " + event.eventString.toString());
            Log.d("NewTestingAdapter", "thursdayEvent: size" + event.eventString.size());

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) thursdayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) thursdayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) thursdayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) thursdayQ4.setText(event.eventDisplayName);
            }
        }

        public void fridayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) fridayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) fridayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) fridayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) fridayQ4.setBackgroundColor(background);

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) fridayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) fridayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) fridayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) fridayQ4.setText(event.eventDisplayName);
            }
        }

        public void saturdayEvent(NewWeekEvent event, int position) {
            String q1 = String.format("%02d:00", position);
            String q2 = String.format("%02d:15", position);
            String q3 = String.format("%02d:30", position);
            String q4 = String.format("%02d:45", position);
            if (event.eventString.contains(q1)) saturdayQ1.setBackgroundColor(background);
            if (event.eventString.contains(q2)) saturdayQ2.setBackgroundColor(background);
            if (event.eventString.contains(q3)) saturdayQ3.setBackgroundColor(background);
            if (event.eventString.contains(q4)) saturdayQ4.setBackgroundColor(background);

            String eventStartQuad = event.eventString.get(0);
            if (eventStartQuad != null) {
                if (eventStartQuad.equals(q1)) saturdayQ1.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q2)) saturdayQ2.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q3)) saturdayQ3.setText(event.eventDisplayName);
                else if (eventStartQuad.equals(q4)) saturdayQ4.setText(event.eventDisplayName);
            }
        }
    }
}
