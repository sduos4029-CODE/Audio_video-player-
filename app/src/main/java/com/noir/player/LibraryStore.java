package com.noir.player;
import android.content.SharedPreferences;
import org.json.*;
import java.util.*;

public final class LibraryStore {
 public static final class Track {
  public final String uri,title,mime;
  public Track(String uri,String title,String mime){this.uri=uri;this.title=title;this.mime=mime==null?"":mime;}
  public boolean downloaded(){return uri.startsWith("file:");}
  public boolean video(){String name=title.toLowerCase(Locale.ROOT);return mime.startsWith("video/")||name.matches(".*\\.(mp4|mkv|webm|mov|avi|m4v|3gp)$");}
 }
 private LibraryStore(){}
 public static List<Track> read(SharedPreferences prefs){return read(prefs,"library");}
 static List<Track> read(SharedPreferences prefs,String key){List<Track> result=new ArrayList<>();Set<String> seen=new HashSet<>();try{JSONArray array=new JSONArray(prefs.getString(key,"[]"));for(int i=0;i<array.length();i++){JSONObject value=array.optJSONObject(i);if(value==null)continue;String uri=value.optString("uri","");if(uri.isEmpty()||!seen.add(uri))continue;result.add(new Track(uri,value.optString("title","Untitled"),value.optString("mime","")));}}catch(JSONException ignored){}return result;}
 public static void add(SharedPreferences prefs,Track track){List<Track> values=read(prefs);for(Track old:values)if(old.uri.equals(track.uri))return;values.add(track);write(prefs,values);}
 public static void remove(SharedPreferences prefs,String uri){List<Track> values=read(prefs);values.removeIf(t->t.uri.equals(uri));write(prefs,values);}
 static void write(SharedPreferences prefs,List<Track> tracks){write(prefs,"library",tracks);}
 static void write(SharedPreferences prefs,String key,List<Track> tracks){JSONArray values=new JSONArray();for(Track t:tracks)try{values.put(new JSONObject().put("uri",t.uri).put("title",t.title).put("mime",t.mime));}catch(JSONException ignored){}prefs.edit().putString(key,values.toString()).apply();}
 public static List<Track> filter(List<Track> tracks,String query,int category){String q=query.trim().toLowerCase(Locale.ROOT);List<Track> out=new ArrayList<>();for(Track t:tracks)if(t.title.toLowerCase(Locale.ROOT).contains(q)&&(category==0||category==1&&!t.video()||category==2&&t.video()||category==3&&t.downloaded()))out.add(t);out.sort(Comparator.comparing(t->t.title.toLowerCase(Locale.ROOT)));return out;}
}
