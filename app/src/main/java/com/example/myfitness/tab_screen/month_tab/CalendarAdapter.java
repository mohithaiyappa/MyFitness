package com.example.myfitness.tab_screen.month_tab;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myfitness.R;
import com.example.myfitness.model.Event;
import com.example.myfitness.repository.EventRepo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarAdapter extends BaseAdapter {
    private final Context mContext;
    private final DateManager mDateManager;
    private final LayoutInflater mLayoutInflater;
    private List<Date> dateArray = new ArrayList();
    private List<Event> monthEvents = new ArrayList<>();
    private View currentSelectedView;
    private Date currentSelectedDate = EventRepo.getInstance().getSelectedDate().getValue();

    public CalendarAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);
            holder.eventText = convertView.findViewById(R.id.eventsText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth() / 7 /*- (int)dp+((int)(3*dp))*/,
                (parent.getHeight() - (int) dp * mDateManager.getWeeks()) / mDateManager.getWeeks());
        convertView.setLayoutParams(params);

        if (mDateManager.isCurrentMonth(dateArray.get(position))) {


            //日付のみ表示させる
            final SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
            holder.dateText.setText(dateFormat.format(dateArray.get(position)));
            //run only if monthEvents is not empty
            holder.eventText.setText(getThisDaysEvents(dateArray.get(position)));

            //当月以外のセルをグレーアウト
            if (mDateManager.isCurrentMonth(dateArray.get(position))) {
                convertView.setBackgroundColor(Color.WHITE);
                holder.eventText.setBackgroundColor(Color.WHITE);
            }

            //日曜日を赤、土曜日を青に color used for weekends
        /*
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))) {
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;
            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);*/
            holder.dateText.setTextColor(Color.BLACK);
            if (mDateManager.isToday(dateArray.get(position))) {
                holder.dateText.setBackgroundResource(R.drawable.color_background_selected);
                holder.dateText.setTextColor(Color.WHITE);
            } else if (mDateManager.isCurrentMonth(dateArray.get(position))) {
                holder.dateText.setBackgroundResource(R.drawable.circle_background);
            }
            if (currentSelectedDate != null & (sameDay(dateArray.get(position), currentSelectedDate))) {
                selectView(convertView);
            }

        } else {
            convertView.setBackgroundColor(Color.WHITE);
            holder.dateText.setText("");
            holder.dateText.setBackgroundColor(Color.WHITE);
            holder.eventText.setText("");
            holder.eventText.setBackgroundColor(Color.WHITE);
            holder.dateText.setBackgroundResource(R.drawable.circle_background);

        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return dateArray.get(position);
    }

    //表示月を取得 display the current month
    public String getTitle() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月", Locale.US);
        return format.format(mDateManager.mCalendar.getTime());
    }

    //翌月表示 display the next month
    public void nextMonth() {
        mDateManager.nextMonth();
        EventRepo.getInstance().loadEvents(mDateManager.getYear(), mDateManager.getMonth());
        dateArray = mDateManager.getDays();
        monthEvents.clear();
        deselectView();
        currentSelectedView = null;
        this.notifyDataSetChanged();
    }

    //前月表示 display the previous month
    public void prevMonth() {
        mDateManager.prevMonth();
        EventRepo.getInstance().loadEvents(mDateManager.getYear(), mDateManager.getMonth());
        dateArray = mDateManager.getDays();
        monthEvents.clear();
        deselectView();
        currentSelectedView = null;
        this.notifyDataSetChanged();
    }

    public void goToToday() {
        Date todayDate = new Date();
        mDateManager.goToToday(todayDate);
        EventRepo.getInstance().loadEvents(mDateManager.getYear(), mDateManager.getMonth());
        EventRepo.getInstance().setSelectedDate(todayDate);
        dateArray = mDateManager.getDays();
        monthEvents.clear();
        deselectView();
        currentSelectedView = null;
        currentSelectedDate = todayDate;
        this.notifyDataSetChanged();
    }

    public DateManager getDateManager() {
        return mDateManager;
    }

    //checks for day events to add to calendar grid view
    private String getThisDaysEvents(Date date) {
        //todo handle event object here
        //todo ask if all video titles should appear in the calendar view or only the first one
        String eventText = "";
        if (monthEvents.isEmpty()) return eventText;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString = dateFormat.format(date);
        int counter = 0;
        for (Event event : monthEvents) {
            if (event.getEventDate().equals(dateString)) {
                counter++;
                if (counter < 3) {
                    int lastIndex = event.getStartTime().lastIndexOf(":");
                    eventText = eventText
                            + event.getStartTime().substring(0, lastIndex)//todo here
                            + " "
                            + event.getVideoArray().get(0).getVideoTitle().substring(0, 4)//todo here
                            + "...\n";
                }
            }
        }
        if (counter > 2) return eventText + (counter - 2) + " more event";
        return eventText;
    }

    public void updateList(List<Event> events) {
        //remove later if there is no bugs in loading events in cal view
        //if(monthEvents.isEmpty()&&events.isEmpty()) return;

        monthEvents = events;
        dateArray = mDateManager.getDays();
        notifyDataSetChanged();
    }

    private boolean sameDay(Date d1, Date d2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return fmt.format(d1).equals(fmt.format(d2));
    }

    public void clickedDate(View view, Date selectedDate) {
        currentSelectedDate = selectedDate;
        deselectView();
        selectView(view);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private void deselectView() {
        if (currentSelectedView != null) {
            int dp = (int) mContext.getResources().getDisplayMetrics().density;
            int cdPadding = 3 * dp;
            LinearLayout oldLinearLayout = currentSelectedView.findViewById(R.id.calendarCellLinearLayout);
            TextView cellDescTv = oldLinearLayout.findViewById(R.id.eventsText);
            oldLinearLayout.setPadding(0, 0, 0, 0);
            cellDescTv.setPadding(cdPadding, 0, cdPadding, 0);
            currentSelectedView.setBackgroundColor(Color.WHITE);
            cellDescTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        }
    }

    private void selectView(View view) {
        int dp = (int) mContext.getResources().getDisplayMetrics().density;
        int llPadding = 3 * dp;
        int cdPadding = dp;
        LinearLayout linearLayout = view.findViewById(R.id.calendarCellLinearLayout);
        TextView cellDescTv = view.findViewById(R.id.eventsText);
        currentSelectedView = view;
        currentSelectedView.setBackgroundColor(mContext.getResources().getColor(R.color.cerise));
        linearLayout.setPadding(llPadding, llPadding, llPadding, llPadding);
        cellDescTv.setPadding(cdPadding, 0, cdPadding, 0);
        cellDescTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    }

    public void reloadEvents() {
        EventRepo.getInstance().loadEvents(mDateManager.getYear(), mDateManager.getMonth());
    }

    //カスタムセルを拡張したらここでWigetを定義
    private static class ViewHolder {
        public TextView dateText;
        public TextView eventText;
    }
}