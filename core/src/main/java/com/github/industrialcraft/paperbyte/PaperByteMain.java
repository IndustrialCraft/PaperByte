package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jetbrains.annotations.Nullable;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class PaperByteMain extends ApplicationAdapter {
	private SpriteBatch batch;
	@Override
	public void create() {
		batch = new SpriteBatch();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}