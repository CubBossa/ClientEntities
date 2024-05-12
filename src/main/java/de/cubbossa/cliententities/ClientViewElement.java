package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import java.util.List;

public interface ClientViewElement {

  List<UpdateInfo> spawn(boolean onlyIfChanged);

  List<UpdateInfo> state(boolean onlyIfChanged);

  List<UpdateInfo> delete(boolean onlyIfChanged);

  interface UpdateInfo {
  }

  interface CombineInfo extends UpdateInfo {
    CombineInfo merge(CombineInfo other);
    PacketWrapper<?> wrapper();
  }

  interface PacketInfo extends UpdateInfo {
    PacketWrapper<?> wrapper();

    static PacketInfo packet(PacketWrapper<?> wrapper) {
      return () -> wrapper;
    }
  }
}
