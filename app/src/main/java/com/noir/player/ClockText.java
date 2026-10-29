package com.noir.player;
import java.text.SimpleDateFormat;
import java.util.*;
/** ASCII clock glyphs; respects 12/24-hour choice without localized format controls. */
public final class ClockText {
 private ClockText(){}
 public static String format(Date date,boolean twentyFourHour,TimeZone zone){SimpleDateFormat f=new SimpleDateFormat(twentyFourHour?"HH:mm":"h:mm a",Locale.US);f.setTimeZone(zone);return f.format(date);}
}
