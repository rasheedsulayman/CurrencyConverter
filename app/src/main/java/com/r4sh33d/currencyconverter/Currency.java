package com.r4sh33d.currencyconverter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by r4sh33d on 10/20/17.
 */

public class Currency implements Parcelable {
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

    protected Currency(Parcel in) {
        countryName = in.readString();
        countryShortCode = in.readString();
        btcEquivalent = in.readDouble();
        ethEquivalent = in.readDouble();
        isActivated = in.readInt();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public String toString() {
        return "Currency{" +
                "countryName='" + countryName + '\'' +
                ", countryShortCode='" + countryShortCode + '\'' +
                ", btcEquivalent=" + btcEquivalent +
                ", ethEquivalent=" + ethEquivalent +
                ", isActivated=" + isActivated +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryName);
        dest.writeString(countryShortCode);
        dest.writeDouble(btcEquivalent);
        dest.writeDouble(ethEquivalent);
        dest.writeInt(isActivated);
    }
}
