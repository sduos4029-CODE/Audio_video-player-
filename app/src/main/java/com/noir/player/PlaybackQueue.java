package com.noir.player;

import java.util.ArrayList;
import java.util.List;

/** A stable snapshot of the selected library list. Reaching an edge never wraps. */
final class PlaybackQueue {
 private final List<LibraryStore.Track> tracks;
 private int index;

 PlaybackQueue(List<LibraryStore.Track> selection,String uri,String title){
  tracks=new ArrayList<>(selection);
  index=-1;
  for(int i=0;i<tracks.size();i++)if(tracks.get(i).uri.equals(uri)){index=i;break;}
  if(index<0){tracks.clear();tracks.add(new LibraryStore.Track(uri,title,""));index=0;}
 }
 boolean hasPrevious(){return index>0;}
 boolean hasNext(){return index+1<tracks.size();}
 LibraryStore.Track move(int direction){
  if(direction!=-1&&direction!=1)return null;
  if(direction<0&&!hasPrevious()||direction>0&&!hasNext())return null;
  index+=direction;return tracks.get(index);
 }
}
