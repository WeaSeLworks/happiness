package com.github.weaselworks.happiness.twitter;

import org.apache.commons.lang3.StringUtils;
import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static com.github.weaselworks.happiness.twitter.HappinessConstants.SENTIMENT_ANALYSER_ADDRESS;
import static com.github.weaselworks.happiness.twitter.HappinessConstants.MACHINE_TRANSLATION_ADDRESS;

/**
 * Created by steve on 27/10/2014.
 */
public class MachineTranslation extends Verticle {

    Logger logger = LoggerFactory.getLogger(MachineTranslation.class);
    private static final String WIDGET_SEPERATOR = "#&#";

    public void start(){
        super.start();

        final EventBus eb = vertx.eventBus();

        Handler<Message<JsonObject>> machineTranslationHandler = new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> message) {

                logger.info("message received "+message.body());

                //perform some action in here to call the bluemix machine translation service
                //then either swap out the original text for the translation text or
                //append the translated text as an additional property


                String username = null;
                String passwd = null;
                String restServerURL = null;

                // Find my service from VCAP_SERVICES in BlueMix
                String VCAP_SERVICES = System.getenv("VCAP_SERVICES");

                if (VCAP_SERVICES != null) {
                    try {

                        JSONObject credentials = getCredentials(VCAP_SERVICES);

                        restServerURL = credentials.getString("uri");
                        username = credentials.getString("userid");
                        passwd = credentials.getString("password");
                        JSONArray sids = credentials.getJSONArray("sids");

                    } catch (NullPointerException | JSONException e) {

                        // add logging
                        e.printStackTrace();

                    }

                    try {
                        if(StringUtils.isNoneEmpty(message.body().toString())) {

                            String[] splitRequest = message.body().toString().split(WIDGET_SEPERATOR);
                            StringBuilder resultStringBuilder = new StringBuilder("[");
                            JSONArray resultJson = new JSONArray();

                            for (String singleText : splitRequest) {
                                //@TODO need to work out what the sid is
                                String postRequest = "seg=ppl&rt=text&sid=" + URLEncoder.encode("change this for a real sid", "UTF-8")
                                        + "&txt=" + URLEncoder.encode(singleText, "UTF-8");

                                // prepare the HTTP connection
                                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(restServerURL)
                                        .openConnection();

                                setPropertiesOnConnection(httpURLConnection, username, passwd);

                                DataOutputStream output = new DataOutputStream(
                                        httpURLConnection.getOutputStream());
                                // make the connection
                                httpURLConnection.connect();
                                // post request
                                output.writeBytes(postRequest);
                                output.flush();
                                output.close();

                                // read response
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                                        httpURLConnection.getInputStream(), "UTF-8"));
                                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                } else {
                                    System.err.println("Unsuccesful response: "
                                            + httpURLConnection.getResponseCode() + " from: "
                                            + httpURLConnection.getURL().toString());
                                }

                                String line = "";
                                StringBuffer stringBuffer = new StringBuffer();
                                while ((line = bufferedReader.readLine()) != null) {
                                    stringBuffer.append(line);
                                    stringBuffer.append("\n");
                                }
                                bufferedReader.close();

                                JSONObject jsonResultObject = new JSONObject();
                                jsonResultObject.put("result", stringBuffer.toString());
                                resultJson.put(jsonResultObject);

                            }
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        throw new RuntimeException("bad url " + restServerURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }




                } else {


                    logger.info("VCAP_SERVICES is null for the environment.  "
                            + "Services are probably not associated with this application.");
                }




                eb.publish(SENTIMENT_ANALYSER_ADDRESS, message);


            }
        };

        eb.registerHandler(MACHINE_TRANSLATION_ADDRESS, machineTranslationHandler);

    }

    private  JSONObject getCredentials (String VCAP_SERVICES ) {
        JSONObject credentials = null;
        try {
            JSONObject obj = (JSONObject) JSON.parse(VCAP_SERVICES);
            JSONArray service = obj.getJSONArray("smt");
            // retrieve the service information
            JSONObject catalog = service.getJSONObject(0);
            // retrieve the credentials
            credentials = catalog.getJSONObject("credentials");

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        return credentials;

    }

    private Response returnHTTPResponse( Response.Status status, String string) {
        return Response
                .status(status)
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")
                .entity(string)
                .build();
    }

    private void setPropertiesOnConnection(HttpURLConnection httpURLConnection, String username, String passwd) throws Exception {

        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "*/*");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        String auth = username + ":" + passwd;
        httpURLConnection.setRequestProperty("Authorization","Basic " + Base64.encodeBase64String(auth.getBytes()));
    }
}
