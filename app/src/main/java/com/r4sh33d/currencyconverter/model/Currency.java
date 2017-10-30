package com.r4sh33d.currencyconverter.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by r4sh33d on 10/20/17.
 */

public class Currency implements Parcelable {
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
    public String countryName;
    public String countryShortCode;
    public double oneBtcEquivalent;
    public double oneEthEquivalent;
    public int countryFlagResource;
    public int currencySymbolResource;
    int isActivated;


    public Currency(String countryName, String countryShortCode, double oneBtcEquivalent, double oneEthEquivalent,
                    int isActivated, int countryFlagResource, int currencySymbolResource) {
        this.countryName = countryName;
        this.countryShortCode = countryShortCode;
        this.oneBtcEquivalent = oneBtcEquivalent;
        this.oneEthEquivalent = oneEthEquivalent;
        this.isActivated = isActivated;
        this.countryFlagResource = countryFlagResource;
        this.currencySymbolResource = currencySymbolResource;
    }

    protected Currency(Parcel in) {
        countryName = in.readString();
        countryShortCode = in.readString();
        oneBtcEquivalent = in.readDouble();
        oneEthEquivalent = in.readDouble();
        isActivated = in.readInt();
        countryFlagResource = in.readInt();
        currencySymbolResource = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryName);
        dest.writeString(countryShortCode);
        dest.writeDouble(oneBtcEquivalent);
        dest.writeDouble(oneEthEquivalent);
        dest.writeInt(isActivated);
        dest.writeInt(countryFlagResource);
        dest.writeInt(currencySymbolResource);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "countryName='" + countryName + '\'' +
                ", countryShortCode='" + countryShortCode + '\'' +
                ", oneBtcEquivalent=" + oneBtcEquivalent +
                ", oneEthEquivalent=" + oneEthEquivalent +
                ", isActivated=" + isActivated +
                ", countryFlagResource=" + countryFlagResource +
                ", currencySymbolResource=" + currencySymbolResource +
                '}';
    }
}
