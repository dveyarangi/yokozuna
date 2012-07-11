package zaki.yokozuna.texture;

import java.io.IOException;

public class TextureException extends Exception
{

	private static final long serialVersionUID = -243963700482064448L;

	public TextureException(String msg) { super(msg); }

	public TextureException(IOException e) { super(e); }
}
