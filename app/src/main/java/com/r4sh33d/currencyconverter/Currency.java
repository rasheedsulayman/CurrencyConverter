package com.r4sh33d.currencyconverter;

/**
 * Created by r4sh33d on 10/20/17.
 */

public class Currency  {
    String countryName;
    String countryShortCode;
    double btcEquivalent;
    double ethEquivalent;
    int isActivated;

    public Currency(String countryName, String countryShortCode, double btcEquivalent, double ethEquivalent, int isActivated) {
        this.countryName = countryName;
        this.countryShortCode = countryShortCode;
        this.btcEquivalent = btcEquivalent;
        this.ethEquivalent = ethEquivalent;
        this.isActivated = isActivated;
    }
}
