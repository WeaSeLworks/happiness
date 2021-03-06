package com.github.weaselworks.happiness.twitter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 07/11/2014.
 */
public class Iso3166CountryCodeConverter {

    Logger log = LoggerFactory.getLogger(Iso3166CountryCodeConverter.class);

    private Map<String, String> isoAlpha2ToAlpha3 = new HashMap<String, String>();

    public Iso3166CountryCodeConverter() {

        loadData();
    }

    public String getIsoAlpha3Code(String alpha2Code) {
        return isoAlpha2ToAlpha3.get(alpha2Code);
    }


    private void loadData() {

        BufferedReader br = null;

        try {

            InputStream is = this.getClass().getResourceAsStream("/web/iso_3166_2_countries.csv");

            br = new BufferedReader(new InputStreamReader(is));

            String line = null;

            // Skip headers
            br.readLine();

            while (null != (line = br.readLine())) {

                String[] vals = line.split(",", -1);

                String country = vals[1];
                String alpha2 = vals[10];
                String alpha3 = vals[11];

                if (StringUtils.isNotBlank(alpha2) && StringUtils.isNotBlank(alpha3)) {
                    isoAlpha2ToAlpha3.put(alpha2, alpha3);
                    log.debug("Added mapping for country: " + country + ", a2: " + alpha2 + ", a3: " + alpha3);
                }
                else System.out.println("Pause");
            }

        }
        catch (IOException ex) {
            log.error("Unexpected exception in BufferedReader", ex);
        }

    }
}
