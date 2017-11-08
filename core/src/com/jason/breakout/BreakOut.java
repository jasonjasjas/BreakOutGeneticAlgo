package com.jason.breakout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import static com.badlogic.gdx.utils.TimeUtils.nanosToMillis;
import static com.jason.breakout.BreakOut.PADDLE_SPEED;

public class BreakOut extends ApplicationAdapter {
	SpriteBatch batch;
	Paddle paddle;
	Ball ball;
	Block[] blocks;
	Intersector intersector;
	Boolean flipX;
	Boolean flipY;
	Circle futureCircle;
	BitmapFont font ;
	Integer score;
	Boolean start;
	Long startTime;
	Long totalScore;
	Long totalTime;
	CheckBox fastForward;
    Table table;
    Stage stage;
    int gameHeight;
    int gameWidth;
    Texture background;
    int speed;
    int blocksLeft;
    float[] input = new float[2];
    float[] output = new float[2];
    Population pop;
	static final int PADDLE_SPEED = 400;
	int currentPlayer = 0;
	int generation = 0;
	Algorithm algo =  new Algorithm();
	@Override
	public void create () {
		batch = new SpriteBatch();
		ball = new Ball();
		futureCircle = new Circle((float)ball.futureX,(float)ball.futureY,ball.circ.radius);
		paddle = new Paddle();
		blocks = new Block[84];
		flipX = false;
		flipY = false;
		score = 0;
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		start = true;
		startTime = TimeUtils.nanoTime();
        totalScore = (long)0;
        totalTime = (long)0;
        gameHeight = Gdx.graphics.getHeight();
        gameWidth = Gdx.graphics.getWidth();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        background = new Texture(Gdx.files.internal("background.png"));
        speed = 1;
        blocksLeft = 84;


        TextureRegionDrawable checkOn = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("checkon.png"))));
        TextureRegionDrawable checkOff = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("checkoff.png"))));
        fastForward = new CheckBox("FastForward",new CheckBox.CheckBoxStyle(checkOff,checkOn,font,Color.BLACK));
        int k = 0;
		for(int i =0; i<12;i++){
			for(int j =0;j<7;j++){

				Block block = new Block(i*61,j*21+650);
				blocks[k] = block;
				k++;
			}
		}
        table.add(fastForward);
//		Block test = new Block(200,390);
//		blocks.add(test);
        fastForward.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(fastForward.isChecked()){
                    speed = 10;
                }else{
                    speed = 1;
                }
            }
        });
        pop = new Population(30, true);


	}

	@Override
	public void render () {

        for (int j = 0; j < speed; j++) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            input[0] = ball.circ.x;
            input[1] = paddle.rect.x;
            //System.out.println(currentPlayer);
            output = pop.players[currentPlayer].compute(input);
            if(output[0] > 0.5){
                paddle.left();
            }else{
                paddle.right();
            }

//            paddle.rect.setX(ball.circ.x);
//            //Keyboard control
//            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//                paddle.left();
//                paddle.rect.setX(ball.circ.x-50);
//            }
//            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//                paddle.right();
//            }
            fastForward.setPosition(Gdx.graphics.getWidth() - 200 + 10, Gdx.graphics.getHeight() - 200);
            ball.futureMove();
            futureCircle.set((float) ball.futureX, (float) ball.futureY, ball.circ.radius);

            //Ball Control
            //Wall Detection
            if (ball.futureX + ball.circ.radius > Gdx.graphics.getWidth() - 200) {
                ball.flipX();
            }
            if (ball.futureX - ball.circ.radius < 0) {
                ball.flipX();
            }
            if (ball.futureY + ball.circ.radius > Gdx.graphics.getHeight()) {
                ball.flipY();
            }

            for (int i = 0; i < blocks.length; i++) {
                if (blocks[i].exist) {
                    if (intersector.overlaps(futureCircle, blocks[i].rect)) {

                        //Left Brick
                        if (ball.circ.x + ball.circ.radius < blocks[i].rect.x) {
                            flipX = true;
                        }
                        //Right Brick
                        else if (ball.circ.x - ball.circ.radius > blocks[i].rect.x + blocks[i].rect.getWidth()) {
                            flipX = true;
                        }
                        //Bottom Brick
                        else if (ball.circ.y + ball.circ.radius < blocks[i].rect.y) {
                            flipY = true;
                        }
                        //Top Brick
                        else if (ball.circ.y - ball.circ.radius > blocks[i].rect.y + blocks[i].rect.getHeight()) {
                            flipY = true;
                        }

                        blocks[i].exist = false;
                        blocksLeft--;
                        score += 100;
                    }
                }

            }
            if (intersector.overlaps(ball.circ, paddle.rect)) {
                //ball.flipY();
                ball.ballAngle = ((paddle.rect.x + paddle.rect.getWidth() / 2) - ball.circ.x) / paddle.rect.getWidth() * 5 * Math.PI / 12 + Math.PI / 2;
                ball.setVelocity();
            }
            //Game over
            if (ball.circ.y <= 0) {
                if (start) {
                    totalScore = score.longValue();
                }
                start = false;
            }
            if (flipX) {
                ball.flipX();
                flipX = false;
            }
            if (flipY) {
                ball.flipY();
                flipY = false;
            }
            if (start) {
                ball.move();
            }else{
                restartGame();
            }
            if (blocksLeft == 0) {
                start = false;
            }
        }
        batch.begin();
        batch.draw(background,Gdx.graphics.getWidth()-200,0);
        for(Block b:blocks){
            if(b.exist){
                batch.draw(b.texture, b.rect.x, b.rect.y);
            }else{
                batch.draw(b.altTexture, b.rect.x, b.rect.y);
            }
        }
        batch.draw(paddle.texture, paddle.rect.x, paddle.rect.y);
        font.draw(batch, "Ball X : " +String.valueOf(ball.circ.x), Gdx.graphics.getWidth() - 150, 50);
        font.draw(batch,"Paddle X : " + String.valueOf(paddle.rect.x), Gdx.graphics.getWidth() - 150, 70);
        font.draw(batch,"Output 1 : " + String.valueOf(output[1]), Gdx.graphics.getWidth() - 150, 90);
        //font.draw(batch,"Output 2 : " + String.valueOf(output[0]), Gdx.graphics.getWidth() - 150, 110);
        font.draw(batch, "Current Population:" + String.valueOf( currentPlayer), Gdx.graphics.getWidth() - 150, 130);
        font.draw(batch, "Generation:" + String.valueOf( generation), Gdx.graphics.getWidth() - 150, 150);
        font.draw(batch, "Score : " + score.toString(), 50, 50);
//        if (start) {
//            totalTime = nanosToMillis(TimeUtils.nanoTime() - startTime) / 1000;
//            font.draw(batch, "Time (seconds) " + nanosToMillis(TimeUtils.nanoTime() - startTime) / 1000, Gdx.graphics.getWidth() - 400, 50);
//        } else {
//            font.draw(batch, "Time (seconds) " + totalTime, Gdx.graphics.getWidth() - 400, 50);
//            font.draw(batch, "Total Score : " + totalScore, Gdx.graphics.getWidth() -600, Gdx.graphics.getHeight() / 2);
//        }
        batch.draw(ball.texture, ball.circ.x - ball.circ.radius, ball.circ.y - ball.circ.radius);

        batch.end();
        stage.draw();

    }
	@Override
	public void dispose () {
		batch.dispose();
		stage.dispose();
	}

	public void restartGame(){
        pop.players[currentPlayer].fitness = score;
        for (int i = 0; i < pop.players[currentPlayer].gene.length; i++) {
            System.out.print(pop.players[currentPlayer].gene[i] + ", ");
        }
        System.out.println("");
        currentPlayer++;
        for(Block b:blocks){
            b.exist = true;
        }
        ball.circ.set(Gdx.graphics.getWidth()/2,400,5);
        paddle.rect.setX(400);

        blocksLeft = 84;
        score = 0;
        start = true;
        if(currentPlayer == 30){
            generation++;
            currentPlayer = 0;
            pop = algo.evolvePopulation(pop);
        }
    }
}

class Paddle{
	Texture texture;
	Rectangle rect;

	public Paddle(){
		texture = new Texture("paddle.png");
		rect = new Rectangle(400,50,texture.getWidth(),texture.getHeight());
	}
	public void left(){
		if(rect.x > 0)
		rect.x -= PADDLE_SPEED * Gdx.graphics.getDeltaTime();
	}

	public void right(){
		if(rect.x + rect.width< Gdx.graphics.getWidth()-200)
		rect.x += PADDLE_SPEED * Gdx.graphics.getDeltaTime();
	}
}

class Block{
	Texture texture;
    Texture altTexture;
	Rectangle rect;
	boolean exist;

	public Block(int x, int y){
		texture = new Texture("block.png");
		altTexture = new Texture("block2.png");
		rect = new Rectangle(x,y,texture.getWidth(),texture.getHeight());
        exist = true;
	}
}

class Ball{
	Texture texture;
	Circle circ;
	double ballVx;
	double ballVy;
	double ballAngle;
	double ballSpeed;
	double futureX;
	double futureY;
	public Ball(){
		texture = new Texture("ball.png");
		circ = new Circle(Gdx.graphics.getWidth()/2,400,5);
		ballSpeed = 400;
		ballAngle = 30;//Math.random()*Math.PI/3 + Math.PI/3;
		setVelocity();
	}
	public void setVelocity(){
		ballVx = ballSpeed * Math.cos(ballAngle);
		ballVy = ballSpeed * Math.sin(ballAngle);
	}
	public void move() {
		circ.x += ballVx * Gdx.graphics.getDeltaTime();
		circ.y += ballVy * Gdx.graphics.getDeltaTime();
	}
	public void futureMove(){
		futureX = circ.x + ballVx * Gdx.graphics.getDeltaTime();
		futureY = circ.y + ballVy * Gdx.graphics.getDeltaTime();
	}
	public void flipX(){
		ballVx = -ballVx;
	}
	public void flipY(){
		ballVy = -ballVy;
	}
}
