package zaki.yokozuna.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

public class TextureManager
{

	private BaseGameActivity activity;
	
	private Map <String, TextureHolder> textures = new HashMap <String, TextureHolder> ();
	
	public TextureManager(BaseGameActivity activity)
	{
		this.activity = activity;
	}
	
	public void addTexture(String name, String filename) {
		
		textures.put( name, new TextureHolder(filename) );
	}
			
	public ITextureRegion getTextureRegion(String name) throws TextureException
	{
		TextureHolder holder = textures.get( name );
		if(holder == null)
			throw new TextureException("Texture [" + name + "] is not registered.");
		
		return holder.getRegion();
	}
	
	class TextureHolder
	{
		String filename;
		Texture texture;
		ITextureRegion region;
		
		public TextureHolder(String filename)
		{
			this.filename = filename;
		}
		
		private void load() throws TextureException 
		{
			try
			{
				texture = new BitmapTexture(activity.getTextureManager(), new IInputStreamOpener() {
					@Override
					public InputStream open() throws IOException { return activity.getAssets().open(filename); }
				});
			} 
			catch ( IOException e )
			{
				Debug.e(e);
				throw new TextureException(e);
			}

			texture.load();
			region = TextureRegionFactory.extractFromTexture(texture);

		}
		
		public ITextureRegion getRegion() throws TextureException
		{
			if(region == null)
				load();
			
			return region;
		}
	}
}
