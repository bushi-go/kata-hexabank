package dev.rgoussu.hexabank.core.operations.model.types;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Currency code ISO_4217 as of january 2022.
 * Uses the java currency API to get symbol for each currency
 */
public enum Currency {
  AFN, ALL, AED, ANG, AMD, AOA, ARS, AUD, AWG, AZN,
  BAM, BBD, BDT, BGN, BHD, BIF, BMD, BND, BOB, BOV, BRL, BSD, BTN, BWP, BYN, BZD,
  CAD, CDF, CHE, CHF, CHW, CLF, CLP, CNY, COP, COU, CRC, CUC, CUP, CVE, CZK,
  DJF, DKK, DOP, DZD,
  EGP, ERN, ETB, EUR,
  FJD, FKP,
  GBP, GEL, GHS, GIP, GMD, GNF, GTQ, GYD,
  HKD, HNL, HRK, HTG, HUF,
  IDR, ILS, INR, IQD, IRR, ISK,
  JMD, JOD, JPY,
  KES, KGS, KHR, KMF, KPW, KRW, KWD, KYD, KZT,
  LAK, LBP, LKR, LRD, LSL, LYD,
  MAD, MDL, MGA, MKD, MMK, MNT, MOP, MRU, MVR, MWK, MXN, MXV, MYR, MZN,
  NAD, NGN, NIO, NOK, NPR, NZD,
  OMR, PAB, PEN, PGK, PHP, PKR, PLN, PYG,
  QAR,
  RON, RSD, RUB, RWF,
  SAR, SBD, SCR, SDG, SEK, SGD, SHP, SLL, SVC, SYP, SZL,
  THB, TJS, TMT, TND, TOP, TRY, TTD, TWD, TZS,
  UAH, UGX, USD, USN, UYI, UYU, UYW, UZS,
  VED, VES, VND, VUV,
  WST, XAF, XAG, XAU, XBA, XBB, XBC, XBD, XCD, XDR, XOF, XPD, XPF, XPT, XSU, XTS, XUA, XXX,
  YER,
  ZAR, ZMW, ZWL;

  private static final String UNSPECIFIED_CURRENCY = "¤";

  /**
   * Get the symbol for the currency, or the unspecified currency symbol ("¤")
   * if none was found via the java currency API
   *
   * s@return the symbol for the currency
   */
  public String getSymbol() {
    try {
      return java.util.Currency.getInstance(this.name()).getSymbol();
    } catch (IllegalArgumentException e) {
      return UNSPECIFIED_CURRENCY;
    }
  }

  public String format(Number amount){
    try {
      NumberFormat format = NumberFormat.getCurrencyInstance();
      format.setCurrency(java.util.Currency.getInstance(this.name()));
      return format.format(amount);
    }catch(UnsupportedOperationException e){
      return new DecimalFormat("#.###.00").format(amount) + " "+getSymbol();
    }
  }
}

