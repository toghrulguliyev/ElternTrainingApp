package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;


import com.alamkanak.weekview.WeekViewEvent;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.ac.univie.entertain.elterntrainingapp.model.Event;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;



public class CalendarActivity extends BaseCalendarActivity implements Callback<List<Event>> {

    private SharedPreferences sharedPreferences;
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    private List<Event> fullEvents = new ArrayList<Event>();
    private EditText eName;
    private EditText eMessage;
    private EditText beginDate;
    //private EditText beginTime;
    private EditText endDate;
    //private EditText endTime;
    private Spinner colorSpin, userSpin;
    private Event editEvent;
    private Event addEvent;
    private Event deleteEvent;
    private static final String[] eventTypes = {"Arbeit", "Elternübung", "Kindübung", "Kind", "Familie", "Zeit zu Zweit"};
    private int day, month, year;
    boolean calledNetwork = false;
    ArrayAdapter<String> memberAdapter, kinderAdapter, parentsAdapter, allMembersAdapter;
    private String notifyUser = "---";
    private TableRow tr;
    private Space space;




    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        if (!calledNetwork) {
            Retrofit retrofit = RetrofitClient.getRetrofitClient();
            APIInterface api = retrofit.create(APIInterface.class);

            String username = getUsername();
            String token = getToken();
            String familyId = "";

            if (getFamilyId() != null && !getFamilyId().isEmpty()) {
                familyId = getFamilyId();
            }

            Call<List<Event>> call = api.getEvents(token, username, familyId);

            call.enqueue(this);
            calledNetwork = true;
        }

        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : events) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogEvent = this.getLayoutInflater().inflate(R.layout.dialog_add_edit_event, null);
        builder.setView(dialogEvent);
        builder.setTitle("Termin hinzufügen");

        eName = (EditText) dialogEvent.findViewById(R.id.event_name);
        eMessage = (EditText) dialogEvent.findViewById(R.id.event_message);
        beginDate = (EditText) dialogEvent.findViewById(R.id.event_beginn_date);
        endDate = (EditText) dialogEvent.findViewById(R.id.event_end_date);
        colorSpin = (Spinner) dialogEvent.findViewById(R.id.event_spinner);
        userSpin = (Spinner) dialogEvent.findViewById(R.id.event_spinner_users);
        tr = (TableRow) dialogEvent.findViewById(R.id.event_spinner_tablerow);
        space = (Space) dialogEvent.findViewById(R.id.event_dialog_after_tablerow_space);

        addEvent = new Event();

        String[] subs = {"---"};
        allMembersAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subs);
        allMembersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kinderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subs);
        kinderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subs);
        parentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (getMembersList() == null || getMembersList().isEmpty()) {
            Toast.makeText(this, "Keine Familienmitglieder gefunden", Toast.LENGTH_SHORT).show();
        } else if (getMembersList() != null && !getMembersList().isEmpty()) {
            kinderAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getKinder());
            kinderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            parentsAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getParents());
            parentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            allMembersAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getAllMembers());
            allMembersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, eventTypes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpin.setAdapter(adapter);
        colorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        addEvent.setEventType(eventTypes[0]);
                        colorSpin.setBackgroundColor(Color.RED);
                        addEvent.setColor("#" + Integer.toHexString(Color.RED));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 1:
                        addEvent.setEventType(eventTypes[1]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_03));
                        addEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_03)));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 2:
                        addEvent.setEventType(eventTypes[2]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_04));
                        addEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_04)));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 3:
                        addEvent.setEventType(eventTypes[3]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_02));
                        addEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_02)));
                        //userSpin.setVisibility(View.VISIBLE);
                        tr.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);
                        userSpin.setAdapter(kinderAdapter);
                        //userSpin.setSelection(0);
                        userSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                notifyUser = getKinder().get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case 4:
                        addEvent.setEventType(eventTypes[4]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_01));
                        addEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_01)));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 5:
                        addEvent.setEventType(eventTypes[5]);
                        colorSpin.setBackgroundColor(Color.MAGENTA);
                        addEvent.setColor("#" + Integer.toHexString(Color.MAGENTA));
                        //userSpin.setVisibility(View.VISIBLE);
                        tr.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                notifyUser = getAllMembers().get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if ((eMessage.getText().toString().isEmpty()) || (eName.getText().toString().isEmpty()) || (beginDate.getText().toString().isEmpty()) || (endDate.getText().toString().isEmpty())) {
//                           if (eMessage.getText().toString() == null || eMessage.getText().toString().isEmpty()) {
//                               eMessage.setError("Nachricht eingeben");
//                           }
//                           if (eName.getText().toString().isEmpty()) {
//                               eName.setError("Nachricht eingeben");
//                           }
//                           if (beginDate.getText().toString().isEmpty()) {
//                               beginDate.setError("Nachricht eingeben");
//                           }
//                           if (endDate.getText().toString().isEmpty()) {
//                               endDate.setError("Nachricht eingeben");
//                           }
//                           if (addEvent.getColor().isEmpty()) {
//                               Toast.makeText(CalendarActivity.this, "Terminart auswählen", Toast.LENGTH_LONG).show();
//                           }
//                           if (!isValidFormat(beginDate.getText().toString()) || !isValidFormat(endDate.getText().toString())) {
//                               beginDate.setText("");
//                               endDate.setText("");
//                               beginDate.setError("Falsche Format!");
//                               endDate.setError("Falsche Format!");
//                           }
                           isValidFormat(beginDate.getText().toString());
                           System.out.println("eName: " + eName.getText().toString());
                           System.out.println("eMessage: " + eMessage.getText().toString());
                           System.out.println("beginDate: " + beginDate.getText().toString());
                           System.out.println("endDate: " + endDate.getText().toString());
                           System.out.println("eMessage: " + eMessage.getText().toString());
                           Toast.makeText(CalendarActivity.this, "Daten vollständig und richtig ausfüllen!", Toast.LENGTH_SHORT).show();
                       } else {

                           addEvent.setMessage(eMessage.getText().toString());
                           addEvent.setName(eName.getText().toString());
                           addEvent.setStartTime(beginDate.getText().toString());
                           addEvent.setEndTime(endDate.getText().toString());
                           addEvent.setAutor(getUsername());
                           addEvent.generateId();
                           if (getFamilyId() != null && !getFamilyId().isEmpty()) {
                               addEvent.setFamilyId(getFamilyId());
                           }
                           if (notifyUser != null && !notifyUser.isEmpty()) {
                               addEvent.setNotifyUser(notifyUser);
                           }
                           saveEvent(addEvent);
                           calledNetwork = false;
                       }

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogEvent = this.getLayoutInflater().inflate(R.layout.dialog_add_edit_event, null);
        builder.setView(dialogEvent);
        builder.setTitle("Termin bearbeiten");

        eName = (EditText) dialogEvent.findViewById(R.id.event_name);
        eMessage = (EditText) dialogEvent.findViewById(R.id.event_message);
        beginDate = (EditText) dialogEvent.findViewById(R.id.event_beginn_date);
        //beginTime = (EditText) dialogEvent.findViewById(R.id.event_beginn_time);
        endDate = (EditText) dialogEvent.findViewById(R.id.event_end_date);
        //endTime = (EditText) dialogEvent.findViewById(R.id.event_end_time);
        colorSpin = (Spinner) dialogEvent.findViewById(R.id.event_spinner);
        userSpin = (Spinner) dialogEvent.findViewById(R.id.event_spinner_users);
        tr = (TableRow) dialogEvent.findViewById(R.id.event_spinner_tablerow);
        space = (Space) dialogEvent.findViewById(R.id.event_dialog_after_tablerow_space);
        final String evType;


        for (Event findEvent : fullEvents) {
            if (event.getId() == findEvent.getId()) {
                editEvent = findEvent;
            }
        }


        evType = editEvent.getEventType();
        eName.setText(editEvent.getName());
        eMessage.setText(editEvent.getMessage());
        beginDate.setText(editEvent.getStartTime());
        endDate.setText(editEvent.getEndTime());


        String[] subs = {"----"};
//        memberAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, subs);
//        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allMembersAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subs);
        allMembersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kinderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subs);
        kinderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subs);
        parentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (getMembersList() == null || getMembersList().isEmpty()) {
            Toast.makeText(this, "Keine Familienmitglieder gefunden", Toast.LENGTH_SHORT).show();
        } else if (getMembersList() != null && !getMembersList().isEmpty()) {
            kinderAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getKinder());
            kinderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            parentsAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getParents());
            parentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            allMembersAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getAllMembers());
            allMembersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpin.setAdapter(adapter);
        colorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        editEvent.setEventType(eventTypes[0]);
                        colorSpin.setBackgroundColor(Color.RED);
                        editEvent.setColor("#" + Integer.toHexString(Color.RED));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 1:
                        editEvent.setEventType(eventTypes[1]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_03));
                        editEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_03)));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 2:
                        editEvent.setEventType(eventTypes[2]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_04));
                        editEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_04)));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        //userSpin.setVisibility(View.GONE);
                        break;
                    case 3:
                        editEvent.setEventType(eventTypes[3]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_02));
                        editEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_02)));
                        tr.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);
                        userSpin.setAdapter(kinderAdapter);
                        for (String username : getKinder()) {
                            if (username.equals(editEvent.getNotifyUser())) {
                                userSpin.setSelection(getKinder().indexOf(username));
                            }
                        }
                        userSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                notifyUser = getKinder().get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case 4:
                        editEvent.setEventType(eventTypes[4]);
                        colorSpin.setBackgroundColor(getResources().getColor(R.color.event_color_01));
                        editEvent.setColor("#" + Integer.toHexString(ContextCompat.getColor(getBaseContext(), R.color.event_color_01)));
                        tr.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);
                        userSpin.setAdapter(allMembersAdapter);
                        userSpin.setSelection(0);
                        break;
                    case 5:
                        editEvent.setEventType(eventTypes[5]);
                        colorSpin.setBackgroundColor(Color.MAGENTA);
                        editEvent.setColor("#" + Integer.toHexString(Color.MAGENTA));
                        tr.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);
                        userSpin.setAdapter(allMembersAdapter);
                        for (String username : getAllMembers()) {
                            if (username.equals(editEvent.getNotifyUser())) {
                                userSpin.setSelection(getAllMembers().indexOf(username));
                            }
                        }
                        userSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                notifyUser = getAllMembers().get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i = 0; i < eventTypes.length; i++) {
            System.out.println("EditEvent: " + editEvent.getEventType() + " == " + eventTypes[i]);
            if (editEvent.getEventType().equals(eventTypes[i])) {
                System.out.println(eventTypes[i]);
                colorSpin.setSelection(i);
            }
        }

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    if (!eName.getText().toString().equals(editEvent.getName()) || !eMessage.getText().toString().equals(editEvent.getMessage()) || !beginDate.getText().toString().equals(editEvent.getStartTime()) || !endDate.getText().toString().equals(editEvent.getEndTime()) || editEvent.getEventType().equals(evType)) {
                        editEvent.setMessage(eMessage.getText().toString());
                        editEvent.setName(eName.getText().toString());
                        editEvent.setStartTime(beginDate.getText().toString());
                        editEvent.setEndTime(endDate.getText().toString());
                        editEvent.setNotifyUser(notifyUser);
                        saveEvent(editEvent);
                        calledNetwork = false;
                    } else {
                        Toast.makeText(CalendarActivity.this, "Ändern Sie Daten", Toast.LENGTH_SHORT).show();
                    }
                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeEvent(editEvent.getId());
                calledNetwork = false;
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);;
            }
        });
        builder.show();

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

        for (Event findEvent : fullEvents) {
            if (event.getId() == findEvent.getId()) {
                deleteEvent = findEvent;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wirklich löschen?");

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeEvent(deleteEvent.getId());
                        calledNetwork = false;
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
        if (response.isSuccessful()) {
            fullEvents = response.body();
            if (fullEvents == null || fullEvents.isEmpty()) {
                Toast.makeText(this, "Keine Termine gefunden", Toast.LENGTH_SHORT).show();
            } else {
                for (Event event : fullEvents) {
                    events.add(event.toWeekViewEvent());

                }
            }
        }
        getWeekView().notifyDatasetChanged();
    }

    @Override
    public void onFailure(Call<List<Event>> call, Throwable t) {
        Toast.makeText(this, "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidFormat(String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

}
