package com.sgandham.mycommuter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sb (View view) {
        new CallAPI().execute("50586");
    }

    public void nb (View view) { new CallAPI().execute("58100"); }

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

         /*   Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
            String[] items = new String[]{"1", "2", "three"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, items);
            dropdown.setAdapter(adapter); */


        }

    }

}