package com.noir.player;

import android.media.*;
import android.view.Display;
import androidx.media3.common.*;
import androidx.media3.exoplayer.ExoPlayer;
import java.util.Locale;

/** Reports source and advertised device support, never invents codec entitlements. */
@android.annotation.SuppressLint("UnsafeOptInUsageError")
final class PlaybackDetails {
 static String failure(PlaybackException error){
  Throwable cause=error;while(cause!=null){if(cause instanceof androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException)return "Server returned HTTP "+((androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException)cause).responseCode+". Use a direct playable media URL.";cause=cause.getCause();}
  switch(error.errorCode){
   case PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES:return "This video exceeds the device decoder’s resolution, frame-rate or profile limits. Try a lower-resolution source.";
   case PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED:return "This device has no compatible decoder for the media codec or profile. Try a supported format.";
   case PlaybackException.ERROR_CODE_DECODER_INIT_FAILED:return "The device could not start a compatible decoder. Close other video apps or try a different source.";
   case PlaybackException.ERROR_CODE_DECODING_FAILED:return "The decoder could not play this stream. The file may be damaged or use an unsupported codec profile.";
  }
  if(error.errorCode>=6000&&error.errorCode<7000)return "This protected source requires a compatible DRM license and decoder. Pulse cannot unlock protected media.";
  return error.getErrorCodeName()+": check the media URL, network and device codec support.";
 }
 static String describe(ExoPlayer player,Display display){
  StringBuilder out=new StringBuilder();Format video=player==null?null:player.getVideoFormat(),audio=player==null?null:player.getAudioFormat();
  if(video!=null){out.append("VIDEO\n").append(video.width).append(" × ").append(video.height);if(video.frameRate>0)out.append(String.format(Locale.US," · %.2f fps",video.frameRate));out.append("\n").append(value(video.sampleMimeType)).append(" · ").append(value(video.codecs)).append("\n");
   if(video.colorInfo!=null)out.append("Color transfer: ").append(video.colorInfo.colorTransfer==C.COLOR_TRANSFER_ST2084?"HDR PQ":video.colorInfo.colorTransfer==C.COLOR_TRANSFER_HLG?"HDR HLG":"SDR / source-defined").append("\n");
   out.append("\nAdvertised decoders:\n");int matches=0;try{for(MediaCodecInfo info:new MediaCodecList(MediaCodecList.REGULAR_CODECS).getCodecInfos()){if(info.isEncoder())continue;for(String type:info.getSupportedTypes())if(type.equalsIgnoreCase(video.sampleMimeType)){out.append(info.getName()).append("\n");if(++matches>=5)break;}if(matches>=5)break;}}catch(RuntimeException ignored){}if(matches==0)out.append("No matching decoder reported.\n");
  }else out.append("Video format is available after the source is prepared.\n");
  if(audio!=null)out.append("\nAUDIO\n").append(value(audio.sampleMimeType)).append(" · ").append(value(audio.codecs)).append("\n").append(audio.channelCount).append(" channels · ").append(audio.sampleRate).append(" Hz\n");
  if(display!=null){try{out.append("\nDisplay HDR types: ");int[] types=display.getHdrCapabilities().getSupportedHdrTypes();if(types.length==0)out.append("none advertised");for(int type:types)out.append(type==Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION?"Dolby Vision ":type==Display.HdrCapabilities.HDR_TYPE_HDR10?"HDR10 ":type==Display.HdrCapabilities.HDR_TYPE_HLG?"HLG ":type==4?"HDR10+ ":type+" ");out.append("\n");}catch(RuntimeException ignored){}}
  out.append("\n2K, 4K/UHD, 8K, HDR and Dolby playback depend on the source, decoder, display and audio output. Advertised support is not a playback guarantee. Sketch changes the picture and adds GPU work; it does not preserve HDR grading. No additional Dolby or software codec pack is bundled.");return out.toString();
 }
 static String value(String value){return value==null?"not reported":value;}
}
