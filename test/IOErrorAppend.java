import java.io.IOException;

/**
 * For objects of this class if the append method is used, an IOException is thrown.
 * Ensures this exception will be thrown for testing purposes.
 */
public class IOErrorAppend implements Appendable {

  @Override
  public Appendable append(CharSequence csq) throws IOException {
    throw new IOException();
  }

  @Override
  public Appendable append(CharSequence csq, int start, int end) throws IOException {
    throw new IOException();
  }

  @Override
  public Appendable append(char c) throws IOException {
    throw new IOException();
  }
}
