package fr.formiko.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class BlendGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture donut;
	Texture visible;

	@Override
	public void create() {
		batch = new SpriteBatch();
		donut = new Texture("donut.png");
		visible = new Texture("visible.png");
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();


		// for the mask
		Gdx.gl.glColorMask(false, false, false, true);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		batch.draw(visible, 0, 0);

		batch.flush();


		// masked
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		batch.draw(donut, 0, 0);
		batch.flush();


		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		donut.dispose();
	}
}
