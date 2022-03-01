package com.mygdx.snake;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import org.w3c.dom.css.Rect;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import java.security.KeyStore;
/**
 * @author Mehdi Ouassou
 * @version 1.0
*/
public class Snake extends ApplicationAdapter {
	private Sound crunch;
	private Sound slam;
	private final int WIDTH_TILES = 19;
	private final int HEIGHT_TILES = 17;
	private final int MAX_BODY_COUNT = 500; //crashes and max
	private SpriteBatch batch;
	private Texture headImg;
	private Rectangle head;
	private Texture bodyImg;
	private Rectangle body;
	private Texture appleImg;
	private Rectangle apple;
	private final int SPRITE_SIZE = 32;
	private int bodyCount;
	private OrthographicCamera camera;
	private Array<Rectangle> snakeBody;
	private Queue<LastPos> lastHeadPos;
	private pDirection pdirection;
	private long lastMovement;
	private final static float SPEED = 0.2f;
	private float time = 0;
	private int snakeLength;
	private boolean flagStart = true;
	private String scoreName;
	BitmapFont myBitMapFont;

	@Override
	public void create () {
		snakeLength = 0;
		scoreName = "score: 0";
		myBitMapFont = new BitmapFont();
		crunch = Gdx.audio.newSound(Gdx.files.internal("crunch.wav"));
		slam = Gdx.audio.newSound(Gdx.files.internal("slam.wav"));
		headImg = new Texture("head.png");
		bodyImg = new Texture("body.png");
		appleImg = new Texture("apple.png");
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SPRITE_SIZE * WIDTH_TILES, SPRITE_SIZE * HEIGHT_TILES);
		batch = new SpriteBatch();
		snakeBody = new Array<Rectangle>();
		lastHeadPos = new Queue<LastPos>();
		head = new Rectangle();
		head.x = MathUtils.round( ( ( (SPRITE_SIZE * WIDTH_TILES) / 2) - (SPRITE_SIZE / 2) ) / SPRITE_SIZE ) * SPRITE_SIZE;
		head.y = MathUtils.round( ( ( (SPRITE_SIZE * HEIGHT_TILES) / 2) - (SPRITE_SIZE / 2) ) / HEIGHT_TILES ) * SPRITE_SIZE;
		apple = new Rectangle();
		apple.x = MathUtils.round(MathUtils.random(0, (SPRITE_SIZE  * WIDTH_TILES ) - SPRITE_SIZE) / SPRITE_SIZE) * SPRITE_SIZE;
		apple.y = MathUtils.round(MathUtils.random(0, (SPRITE_SIZE  * HEIGHT_TILES ) - SPRITE_SIZE) / SPRITE_SIZE) * SPRITE_SIZE;
		pdirection = pdirection.RIGHT;
	}

	public enum pDirection
	{
		RIGHT,
		LEFT,
		UP,
		DOWN,
		IDLE
	}

	public void move() {
		switch (pdirection) {
			case RIGHT:
				head.x += SPRITE_SIZE;
				break;
			case LEFT:
				head.x -= SPRITE_SIZE;
				break;
			case UP:
				head.y += SPRITE_SIZE;
				break;

			case DOWN:
				head.y -= SPRITE_SIZE;
				break;
			case IDLE:
				head.y = head.y;
				head.x = head.x;
				break;

			default:
				break;
		}
	}

	public void assignBody() {
		Queue<LastPos> lastHeadPos1 = lastHeadPos;
		for (int i = 0; i < snakeLength; i++) {
			Rectangle body = new Rectangle();
			body.x = lastHeadPos1.get(i + 1).x;
			body.y = lastHeadPos1.get(i + 1).y;
			snakeBody.add(body);
		}
	}
	public boolean checkCollisionBody() {
		for (int i = 0; i < snakeBody.size; i++) {
			if (snakeBody.get(i).x == head.x  &&  snakeBody.get(i).y == head.y ) {
				return true;
			}
		}
		return false;
	}
	public void checkDeath() {
		if (head.x < 0 || head.x > (SPRITE_SIZE * WIDTH_TILES) - SPRITE_SIZE || head.y < 0 || (head.y > (SPRITE_SIZE * HEIGHT_TILES) - SPRITE_SIZE || checkCollisionBody() ) ) {
			slam.play();
			head.x = 320;
			head.y = 320;
			snakeBody.clear();
			lastHeadPos.clear();
			randomizeApple();
			snakeLength = 0;
			scoreName = "score: " + snakeLength;

		}
		snakeBody.clear();
	}
	public void randomizeApple() {
		apple.x = MathUtils.round(MathUtils.random(0, (SPRITE_SIZE  * WIDTH_TILES ) - SPRITE_SIZE) / SPRITE_SIZE) * SPRITE_SIZE;
		apple.y = MathUtils.round(MathUtils.random(0, (SPRITE_SIZE  * HEIGHT_TILES ) - SPRITE_SIZE) / SPRITE_SIZE) * SPRITE_SIZE;
	}

	@Override
	public void render () {
		if (flagStart) {
			for (int i = 0; i < snakeLength; i++) {
				lastHeadPos.addFirst(new LastPos(head.x - (i * SPRITE_SIZE), head.y));
			}
			flagStart = false;
		}
		ScreenUtils.clear(0.545f, 0.992f, 0.447f, 1);
		camera.update();
		checkDeath();


		//BATCH
		batch.begin();
		batch.draw(appleImg, apple.x, apple.y, SPRITE_SIZE, SPRITE_SIZE);
		batch.draw(headImg, head.x, head.y, SPRITE_SIZE, SPRITE_SIZE);
		assignBody();
		for (Rectangle snakePiece : snakeBody) {
			batch.draw(bodyImg, snakePiece.x, snakePiece.y, SPRITE_SIZE, SPRITE_SIZE);
		}
		myBitMapFont.setColor(0f, 0f, 0f, 1.0f);
		myBitMapFont.draw(batch, scoreName, SPRITE_SIZE * WIDTH_TILES - 60, SPRITE_SIZE * HEIGHT_TILES - 10);
		batch.end();
		//BATCH

		time += Gdx.graphics.getDeltaTime();
		if (time > SPEED && pdirection != pdirection.IDLE) {
			move();
			lastHeadPos.addFirst(new LastPos(head.x, head.y));
			time -= SPEED;
		}
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
			if (pdirection != pdirection.DOWN) pdirection = pdirection.UP;
		}
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			if (pdirection != pdirection.RIGHT) pdirection = pdirection.LEFT;
		}
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
			if (pdirection != pdirection.UP) pdirection = pdirection.DOWN;
		}
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if (pdirection != pdirection.LEFT) pdirection = pdirection.RIGHT;
		}
		if (head.x == apple.x && head.y == apple.y) {
			boolean flagCheck = true;
			while (flagCheck) {
				flagCheck = false;
				//lastHeadPos.addFirst(new LastPos(head.x, head.y));
				snakeLength++;
				scoreName = "score: " + snakeLength;
				randomizeApple();
				for (int i = 0; i < lastHeadPos.size; i++) {
					if ( lastHeadPos.get(i).x == apple.x  &&  lastHeadPos.get(i).y == apple.y ) {
						flagCheck = true;
					}
				}
				crunch.play();

			}
		}
		if (lastHeadPos.size > MAX_BODY_COUNT) {
			lastHeadPos.removeLast();
		}
	}
	@Override
	public void dispose () {
		batch.dispose();
		headImg.dispose();
		bodyImg.dispose();
		appleImg.dispose();
		slam.dispose();
		crunch.dispose();
	}
}
