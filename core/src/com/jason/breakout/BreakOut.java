package com.jason.breakout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import static com.badlogic.gdx.utils.TimeUtils.nanosToMillis;
import static com.jason.breakout.BreakOut.PADDLE_SPEED;

public class BreakOut extends ApplicationAdapter {
	SpriteBatch batch;
	Paddle paddle;
	Ball ball;
	Array<Block> blocks;
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

	static final int PADDLE_SPEED = 400;
	@Override
	public void create () {
		batch = new SpriteBatch();
		ball = new Ball();
		futureCircle = new Circle((float)ball.futureX,(float)ball.futureY,ball.circ.radius);
		paddle = new Paddle();
		blocks = new Array<Block>();
		flipX = false;
		flipY = false;
		score = 0;
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		start = true;
		startTime = TimeUtils.nanoTime();
        totalScore = (long)0;
        totalTime = (long)0;

		for(int i =0; i<12;i++){
			for(int j =0;j<7;j++){
				Block block = new Block(i*61,j*21+650);
				blocks.add(block);
			}
		}
//		Block test = new Block(200,390);
//		blocks.add(test);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//paddle.rect.setX(ball.circ.x );
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			paddle.left();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			paddle.right();
		}
		batch.begin();
		Iterator<Block> iter = blocks.iterator();
		while(iter.hasNext()){
			Block b = iter.next();
			batch.draw(b.texture,b.rect.x,b.rect.y);
		}
        ball.futureMove();
        futureCircle.set((float)ball.futureX,(float)ball.futureY,ball.circ.radius);

        //Ball Control
		//Wall Detection
		if(ball.futureX + ball.circ.radius > Gdx.graphics.getWidth()){
			ball.flipX();
		}
		if(ball.futureX - ball.circ.radius< 0){
			ball.flipX();
		}
		if(ball.futureY + ball.circ.radius > Gdx.graphics.getHeight()){
			ball.flipY();
		}

		for(int i = 0;i<blocks.size;i++){
			if (intersector.overlaps(futureCircle,blocks.get(i).rect)){
				System.out.println(ball.futureY+ ball.circ.radius );
				System.out.println( blocks.get(i).rect.y);

				//Left Brick
				if(ball.circ.x+ ball.circ.radius < blocks.get(i).rect.x){
					flipX = true;
					System.out.println("1");
				}
				//Right Brick
				else if(ball.circ.x- ball.circ.radius> blocks.get(i).rect.x + blocks.get(i).rect.getWidth()){
					flipX = true;
					System.out.println("2");
				}
				//Bottom Brick
				else if(ball.circ.y+ ball.circ.radius < blocks.get(i).rect.y ){
					flipY = true;
					System.out.println("hi");
				}
				//Top Brick
				else if(ball.circ.y - ball.circ.radius > blocks.get(i).rect.y + blocks.get(i).rect.getHeight()){
					flipY = true;
				}

				blocks.removeIndex(i);
				score+=100;
			}
		}
		if (intersector.overlaps(ball.circ,paddle.rect)){
			//ball.flipY();
			ball.ballAngle = ((paddle.rect.x + paddle.rect.getWidth()/2)- ball.circ.x)/paddle.rect.getWidth()*5*Math.PI/12 + Math.PI/2;
			ball.setVelocity();
		}
		if(ball.circ.y <= 0){
		    if(start){
                totalScore = score - nanosToMillis(TimeUtils.nanoTime() - startTime)/100 ;
            }
		    start = false;

        }
		if(flipX){
			ball.flipX();
			flipX = false;
		}
		if(flipY){
			ball.flipY();
			flipY = false;
		}
		if(start) {
		    ball.move();
        }
        if(blocks.size == 0){
		    start = false;
        }
		batch.draw(paddle.texture,paddle.rect.x,paddle.rect.y);
		font.draw(batch,"Score : " + score.toString(),50,50);
		if(start) {
		    totalTime = nanosToMillis(TimeUtils.nanoTime() - startTime) / 1000;
            font.draw(batch, "Time (seconds) " + nanosToMillis(TimeUtils.nanoTime() - startTime) / 1000, Gdx.graphics.getWidth() - 200, 50);
        }else{
            font.draw(batch, "Time (seconds) " + totalTime, Gdx.graphics.getWidth() - 200, 50);
            font.draw(batch,"Total Score : " + totalScore,Gdx.graphics.getWidth()/2 - 80,Gdx.graphics.getHeight()/2);
        }
		batch.draw(ball.texture,ball.circ.x - ball.circ.radius,ball.circ.y- ball.circ.radius);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
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
		if(rect.x + rect.width< Gdx.graphics.getWidth())
		rect.x += PADDLE_SPEED * Gdx.graphics.getDeltaTime();
	}
}

class Block{
	Texture texture;
	Rectangle rect;
	public Block(){
		texture = new Texture("block.png");
		rect = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
	}
	public Block(int x, int y){
		texture = new Texture("block.png");
		rect = new Rectangle(x,y,texture.getWidth(),texture.getHeight());
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
		ballAngle = Math.random()*Math.PI/3 + Math.PI/3;
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
