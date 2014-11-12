package com.github.weaselworks.happiness.twitter;

import org.junit.Assert;
import org.junit.Test;

public class Iso3166CountryCodeConverterTest {

    private Iso3166CountryCodeConverter cc = new Iso3166CountryCodeConverter();

    @Test
    public void testGetIsoAlpha3Code() {

        Assert.assertEquals("GBR", cc.getIsoAlpha3Code("GB"));

    }

    @Test
    public void testNonExistentCode() {

        Assert.assertNull(cc.getIsoAlpha3Code("NONEXISTENTCODE"));

    }

}