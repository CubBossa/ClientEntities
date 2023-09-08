package de.cubbossa.cliententities;

public class ServerSideMethodNotSupported extends RuntimeException {
  public ServerSideMethodNotSupported() {
    super("Method call not supported for client side only entities.");
  }
}
