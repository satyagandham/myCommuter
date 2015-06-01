package com.sgandham.mycommuter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.sgandham.mycommuter.entity.Route;
import com.sgandham.mycommuter.entity.RouteDirection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends Activity implements OnItemSelectedListener {

    private String selectedAgency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection() {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(this);
    }

    public void sb (View view) {
        new CallAPI().execute("50586");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
//        TextView tv = (TextView)view.findViewById(R.id.textView2);
//        tv.setText((String) parent.getItemAtPosition(pos));
        switch (parent.getId()) {
            case R.id.spinner1 :
                selectedAgency = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_SHORT).show();
                new CallListAgenciesStopsAPI().execute("50586");
                break;
            //case R.id.spinner2 :


        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void nb (View view) {

        new CallAPI().execute("58100");
    }

    public void agencies (View view) {
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        dropdown.setVisibility(View.VISIBLE);
        new CallListStopsAPI().execute("hi"); }

    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String stopCode= params[0];
            String urlString = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopCode.aspx?token=bd8c9a47-adf2-4fbb-8c5f-b4905136cdbc&stopcode="+stopCode;
            String resultToDisplay = "";
            InputStream in = null;
            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e ) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            List<String> results = null;
            try {
                String result =  convertISIntoString(in);
                System.out.println("result = " + result);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));

                results = parseXML(xpp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (results != null) {

                StringBuffer sb = new StringBuffer();
                for (String str : results) {
                    sb.append(str+"       ");
                }
                return sb.toString();
            }
            return "";
        }

        private List<String> parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {
            List<String> timesArr = new LinkedList<String>();
            int eventType = parser.getEventType();
            StringBuffer sb = new StringBuffer();
            boolean tagFlagged = false;
            while( eventType!= XmlPullParser.END_DOCUMENT) {
                String name = null;
                name = parser.getName();
                switch(eventType)
                {
                    case XmlPullParser.START_TAG:
                        if( name.equals("Route")) {
                            String nameAttr = parser.getAttributeValue(0);
                            if ("SB".equals(nameAttr)) {
                                tagFlagged = true;
                            }
                        }
                        if (tagFlagged) {
                            if (name.equals ("DepartureTime")) {
                                timesArr.add(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("DepartureTimeList") && tagFlagged) {
                            tagFlagged = false;
                        }
                        break;
                } // end switch

                eventType = parser.next();
            } // end while

            return timesArr;
        }

        private String convertISIntoString (InputStream in) throws IOException {
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while(read != null) {
                //System.out.println(read);
                sb.append(read);
                read =br.readLine();

            }

            return sb.toString();
        }

        protected void onPostExecute(String result) {
            TextView tv = (TextView)findViewById(R.id.textView2);
            tv.setText(result);

            Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
            String[] items = new String[]{"1", "2", "three"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, items);
            dropdown.setAdapter(adapter);
        }

    }

    private class CallListStopsAPI extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String stopCode= params[0];
            String urlString = "http://services.my511.org/Transit2.0/GetAgencies.aspx?token=bd8c9a47-adf2-4fbb-8c5f-b4905136cdbc";
            String resultToDisplay = "";
            InputStream in = null;
            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e ) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return null;
            }

            List<String> results = null;
            try {
                String result =  convertISIntoString(in);
                System.out.println("result = " + result);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));

                results = parseXML(xpp);
                System.out.println("result = " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            String[] newArray = new String[2];
//            newArray[0] = "hello";
//            newArray[1] = "hai";
//            return newArray;
            String[] retArray = results.toArray(new String[results.size()]);
            return results.toArray(retArray);
        }

        private List<String> parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {
            List<String> timesArr = new LinkedList<String>();
            timesArr.add("");
            int eventType = parser.getEventType();
            StringBuffer sb = new StringBuffer();
            boolean tagFlagged = false;
            while( eventType!= XmlPullParser.END_DOCUMENT) {
                String name = null;
                name = parser.getName();
                switch(eventType)
                {
                    case XmlPullParser.START_TAG:
                        if( name.equals("Agency")) {
                            String nameAttrName = parser.getAttributeName(0);
                            String nameAttrVal = parser.getAttributeValue(0);
                            if ("Name".equals(nameAttrName)) {
                                timesArr.add(nameAttrVal);
                            }
                        }
                } // end switch

                eventType = parser.next();
            } // end while

            return timesArr;
        }

        private String convertISIntoString (InputStream in) throws IOException {
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while(read != null) {
                //System.out.println(read);
                sb.append(read);
                read =br.readLine();
            }
            return sb.toString();
        }

        protected void onPostExecute(String[] result) {
//            TextView tv = (TextView)findViewById(R.id.textView2);
//            tv.setText("Hai");

            Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
            String[] items = new String[result.length];
            int listSize = 0;
            while (listSize != result.length) {
                items [listSize] = result[listSize];
                listSize++;
            }
            //items = new String[]{"1", "2", "three"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, items);
            dropdown.setAdapter(adapter);
        }
    }

    private class CallListAgenciesStopsAPI extends AsyncTask<String, String, Route[]> {

        @Override
        protected Route[] doInBackground(String... params) {
            if ("".equals(selectedAgency)) {
                return null;
            }
            String stopCode= params[0];
            String urlString = "http://services.my511.org/Transit2.0/GetRoutesForAgency.aspx?token=bd8c9a47-adf2-4fbb-8c5f-b4905136cdbc&agencyName=";
            urlString = urlString + selectedAgency;
            String resultToDisplay = "";
            InputStream in = null;
            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e ) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return null;
            }

            List<Route> results = null;
            try {
                String result =  convertISIntoString(in);
                System.out.println("result = " + result);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));

                results = parseXML(xpp);
                System.out.println("result = " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            String[] newArray = new String[2];
//            newArray[0] = "hello";
//            newArray[1] = "hai";
//            return newArray;
            Route[] retArray = results.toArray(new Route[results.size()]);
            return results.toArray(retArray);
        }

        private List<Route> parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {
            List<String> timesArr = new LinkedList<String>();
            timesArr.add("");
            int eventType = parser.getEventType();
            StringBuffer sb = new StringBuffer();
            boolean tagFlagged = false;
            List<Route> routesList = new ArrayList<Route>();
            Route newRoute= null;
            while( eventType!= XmlPullParser.END_DOCUMENT) {
                String name = null;
                name = parser.getName();
                switch(eventType)
                {
                    case XmlPullParser.START_TAG:
                        if( name.equals("Route")) {
                            newRoute = new Route();
                            newRoute.setName(parser.getAttributeValue(0));
                            newRoute.setCode(parser.getAttributeValue(1));
                            newRoute.setRouteDirectionList(new ArrayList<RouteDirection>());
                            routesList.add(newRoute);
                        } else if (name.equals("RouteDirection")) {
                            RouteDirection routeDirection = new RouteDirection();
                            routeDirection.setCode(parser.getAttributeValue(0));
                            routeDirection.setName(parser.getAttributeValue(1));
                            newRoute.getRouteDirectionList().add(routeDirection);
                        }
                } // end switch

                eventType = parser.next();
            } // end while

            return routesList;
        }

        private String convertISIntoString (InputStream in) throws IOException {
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while(read != null) {
                //System.out.println(read);
                sb.append(read);
                read =br.readLine();
            }
            return sb.toString();
        }

        protected void onPostExecute(Route[] result) {
            if (result == null) {
                return;
            }
            Spinner dropdown = (Spinner)findViewById(R.id.spinner2);
            dropdown.setVisibility(View.VISIBLE);
            List<String> itemsList = new ArrayList<String>();
            for (Route route : result) {
                for (RouteDirection routeDirection : route.getRouteDirectionList()) {
                    StringBuffer strbuf = new StringBuffer();
                    strbuf.append(route.getName());
                    strbuf.append("~");
                    strbuf.append(route.getCode());
                    strbuf.append(" | ");
                    strbuf.append(routeDirection.getCode());
                    strbuf.append("~");
                    strbuf.append(routeDirection.getName());
                    itemsList.add(strbuf.toString());
                }
            }
            String[] items = itemsList.toArray(new String[itemsList.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, items);
            dropdown.setAdapter(adapter);
        }
    }


}