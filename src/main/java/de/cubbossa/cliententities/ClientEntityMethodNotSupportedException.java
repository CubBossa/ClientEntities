package de.cubbossa.cliententities;

public class ClientEntityMethodNotSupportedException extends RuntimeException {
  public ClientEntityMethodNotSupportedException() {
    super("Method call not supported for client side only entities.");
  }
}
