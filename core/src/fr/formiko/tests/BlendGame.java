package fr.formiko.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class BlendGame extends ApplicationAdapter {
	SpriteBatch spriteBatch;
	Texture donut;
	Texture visible;
	private Sprite mask, maskedSprite;

	@Override
	public void create() {
		spriteBatch = new SpriteBatch();
		donut = new Texture("donut.png");
		visible = new Texture("visible.png");
		// Gdx.gl.glEnable(GL20.GL_BLEND);

		/* Load the mask containing the alpha information. */
		mask = new Sprite(new Texture("visible.png"));

		/* Load the sprite which will be masked. */
		maskedSprite = new Sprite(new Texture("donut.png"));
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 1, 0, 1);
		spriteBatch.begin();


		// for the mask
		Gdx.gl.glColorMask(false, false, false, true);
		// spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		// spriteBatch.draw(visible, 0, 0);
		// spriteBatch.flush();
		/* Change the blending function for our alpha map. */
		spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

		/* Draw alpha masks. */
		mask.draw(spriteBatch);

		/* This blending function makes it so we subtract instead of adding to the alpha map. */
		spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);

		/* Remove the masked sprite's inverse alpha from the map. */
		maskedSprite.draw(spriteBatch);

		/* Flush the batch to the GPU. */
		spriteBatch.flush();


		// masked
		Gdx.gl.glColorMask(true, true, true, true);
		//
		// spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		// spriteBatch.setBlendFunctionSeparate(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_DST_ALPHA,
		// GL20.GL_ONE_MINUS_DST_ALPHA);

		// spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// spriteBatch.setBlendFunctionSeparate(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ZERO, GL20.GL_ONE_MINUS_DST_ALPHA);
		// spriteBatch.draw(donut, 0, 0);
		// spriteBatch.flush();
		/* Change the blending function so the rendered pixels alpha blend with our alpha map. */
		spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

		/* Draw our sprite to be masked. */
		maskedSprite.draw(spriteBatch);

		/* Remember to flush before changing GL states again. */
		spriteBatch.flush();


		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		donut.dispose();
	}
}
