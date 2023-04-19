package fr.formiko.tests;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

public class BlendGame extends ApplicationAdapter {
	SpriteBatch spriteBatch;
	Texture donut;
	Texture visible;
	List<Integer> times = new ArrayList<>();
	Pixmap pixmap;
	Pixmap pxmVisible;
	private ShapeRenderer shapeRenderer;
	private FrameBuffer frameBuffer;
	private Sprite mask;

	@Override
	public void create() {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		Gdx.gl20.glLineWidth(2);
		donut = new Texture("donut.png");
		visible = new Texture("visible.png");
		mask = new Sprite(visible);

		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		spriteBatch = new SpriteBatch();
	}
	private void drawCircles() {

		/* An example circle, remember to flush before changing the blending function */
		// shapeRenderer.setColor(Color.RED);
		// shapeRenderer.circle(200, 200, 100);
		// shapeRenderer.flush();
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		spriteBatch.draw(donut, 0, 0);
		spriteBatch.draw(donut, 30, 0);

		/* We'll need blending enabled for the technique to work */
		Gdx.gl.glEnable(GL20.GL_BLEND);

		/*
		 * With this blending function, wherever we draw pixels next
		 * we will actually remove previously drawn pixels.
		 */
		spriteBatch.flush();
		Gdx.gl.glBlendFuncSeparate(GL20.GL_ZERO, GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		// shapeRenderer.circle(300, 200, 70);
		// shapeRenderer.circle(100, 200, 35);
		// shapeRenderer.flush();
		// spriteBatch.draw(visible, 0, 0);
		mask.draw(spriteBatch);
		spriteBatch.flush();
		spriteBatch.end();

		/* Restore defaults. */
		Gdx.gl.glDisable(GL20.GL_BLEND);

		/*
		 * The default blend function in case we need standard blending elsewhere.
		 * Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		 */
	}

	private void drawContours() {
		shapeRenderer.set(ShapeType.Line);

		/* Contour of the masked circle */
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.circle(200, 200, 100);

		/* Contour of the masks */
		shapeRenderer.setColor(Color.CYAN);
		shapeRenderer.circle(300, 200, 70);
		shapeRenderer.circle(100, 200, 35);
	}

	@Override
	public void render() {
		long time = System.currentTimeMillis();
		ScreenUtils.clear(Color.GREEN);

		frameBuffer.bind();
		// shapeRenderer.begin();
		// shapeRenderer.set(ShapeType.Filled);
		drawCircles();
		// drawContours();
		// shapeRenderer.end();
		frameBuffer.end();

		Texture texture = frameBuffer.getColorBufferTexture();
		Sprite sprite = new Sprite(texture);
		sprite.flip(false, true);

		spriteBatch.begin();
		sprite.draw(spriteBatch);
		spriteBatch.end();
		times.add((int) (System.currentTimeMillis() - time));
	}

	@Override
	public void dispose() {
		// spriteBatch.dispose();
		// donut.dispose();
		// visible.dispose();
		System.out.println(times.stream().mapToInt(Integer::intValue).summaryStatistics().getAverage() + " ms in average.");
	}
}
