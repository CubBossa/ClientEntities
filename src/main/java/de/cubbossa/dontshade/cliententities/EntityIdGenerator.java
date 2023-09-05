package de.cubbossa.dontshade.cliententities;

public interface EntityIdGenerator {

  boolean isClaimed(int id);

  int nextEntityId();

  void releaseEntityId(int id);
}
