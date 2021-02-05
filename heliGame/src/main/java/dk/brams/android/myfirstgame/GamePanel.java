package dk.brams.android.myfirstgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "GamePanel";
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long smokeStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Exhaust> smoke;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BotBorder> botborder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;

    //increase to slow down difficulty progression, decrease to speed up difficulty progression
    private int progressDenom = 20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean gameStarted;
    private boolean collision=false;
    private int currentHighScore;
    private int score;
    private static final int SCORE_BOOSTER = 3;

    private static final int MAX_SOUNDS=10;
    private static final String SOUND_FOLDER = "sound";
    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;
    private Sound mp3Heli, mp3Missile, mp3Explode;
    private static final int LOOP=-1;
    private static final int PLAY_ONCE=0;



    // Listener interface for hosting activity to save score in shared preferences
    public interface HighScoreListener {
        public void onHighScoreUpdated(int best);
    }

    private HighScoreListener mHighScoreListener;

    public void setHighScoreListener(HighScoreListener listener){
        this.mHighScoreListener =listener;
    }


    public GamePanel(Context context, int currentHighScore) {
        super(context);

        this.currentHighScore = currentHighScore;


        // set null listener... just in case
        this.mHighScoreListener = null;

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}


    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter++<1000) {
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            }
            catch(InterruptedException ie) {
                Log.i(TAG, "surfaceDestroyed: Current thread has been interrupted", ie);
            }
        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        bg.setVector(-5);
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        player.resetScore();

        mAssets=getContext().getAssets();

        loadSounds();

        smoke = new ArrayList<Exhaust>();
        missiles = new ArrayList<Missile>();
        topborder = new ArrayList<TopBorder>();
        botborder = new ArrayList<BotBorder>();
        smokeStartTime=  System.nanoTime();
        missileStartTime = System.nanoTime();

        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }


    private void loadSounds() {
        // old constructor needed here for compatibility
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        String[] soundNames;
        try {
            soundNames = mAssets.list(SOUND_FOLDER);
            Log.i(TAG, "loadSounds: found "+soundNames.length + " sounds");
            for(String s:soundNames)
                Log.i(TAG, "Loaded sound "+s);

        } catch (IOException e) {
            Log.e(TAG, "loadSounds: Could not list assets", e);
            return;
        }

        for (String fileName : soundNames) {
            try {
                String assetPath = SOUND_FOLDER + "/" + fileName;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG, "loadSounds: Could not load sound: "+fileName, ioe );
            }
        }

        // initialize the sound variables for easy reading
        mp3Heli = mSounds.get(0);
        mp3Missile = mSounds.get(2);
        mp3Explode = mSounds.get(1);

    }


    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId=mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }


    public void startSound(Sound sound, int playOnceOrLoop) {
        if (sound.getStreamId()==null) {
            Integer soundId = sound.getSoundId();
            if (soundId == null)
                return;

            int streamId = mSoundPool.play(soundId, 1.0f, 1.0f, 1, playOnceOrLoop, 1.0f);
            sound.setStreamId(streamId);
        } else {
            mSoundPool.resume(sound.getStreamId());
        }
    }

    public void stopSound(Sound sound) {
        Integer streamId = sound.getStreamId();
        if (streamId==null)
            return;

        mSoundPool.pause(streamId);
    }


    public void releaseSoundPool() {
        mSoundPool.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying() && newGameCreated && reset) {
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying()) {

                if(!gameStarted)
                    gameStarted = true;

                startSound(mp3Heli, LOOP);
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }


    public void update() {
        if(player.getPlaying()) {

            bg.update();
            player.update();


            //check bottom border collision
            for(int i = 0; i<botborder.size(); i++) {
                if(collision(botborder.get(i), player)) {
                    player.setPlaying(false);
                    collision=true;
                    break;
                }

            }

            //check top border collision
            for(int i = 0; i <topborder.size(); i++) {
                if(collision(topborder.get(i),player)) {
                    player.setPlaying(false);
                    collision=true;
                    break;
                }
            }

            //calculate the threshold of height the border can have based on the score
            //max and min border height are updated, and the border switched direction when either max or
            //min is met

            maxBorderHeight = 30+player.getScore()/progressDenom;
            //cap max border height so that borders can only take up a total of 1/2 the screen
            if(maxBorderHeight > HEIGHT/4)maxBorderHeight = HEIGHT/4;
            minBorderHeight = 5+player.getScore()/progressDenom;

            //update borders
            updateTopBorder();
            updateBottomBorder();

            //add new missiles when it is time for a new one
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)){
                int mX= WIDTH + 10;
                int mY=0;

                if(missiles.size()==0)
                    mY=HEIGHT/2;   //first missile always goes down the middle
                 else
                    mY=(int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight);

                // Add new missile and reset timer
                missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), mX, mY, 45, 15, player.getScore(), 13));

                int missileStreamId = mSoundPool.play(mp3Missile.getSoundId(), .20f, .20f, 1, PLAY_ONCE, 0.8f);
                missiles.get(missiles.size()-1).setStreamId(missileStreamId);
                missileStartTime = System.nanoTime();
            }

            //loop through every missiles. Check collision and cleanup
            for(int i = 0; i<missiles.size();i++) {
                //update each missile position and image
                missiles.get(i).update();

                if(collision(missiles.get(i),player)) {
                    Log.i(TAG, "update: collision missile " + i);
                    mSoundPool.stop(missiles.get(i).getStreamId());
                    missiles.remove(i);
                    player.setPlaying(false);
                    collision=true;
                    break;
                }

                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100) {
                    mSoundPool.stop(missiles.get(i).getStreamId());
                    missiles.remove(i);
                    Log.i(TAG, "update: removing missile "+i);
                }
            }

            //add smoke puffs when it is time for a new one
            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(elapsed > 120){
                smoke.add(new Exhaust(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for(int i = 0; i<smoke.size();i++) {
                smoke.get(i).update();
                if(smoke.get(i).getX()<-10) {
                    smoke.remove(i);
                }
            }
        } else {

            stopSound(mp3Heli);

            player.resetDY();
            if(!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30, 100, 100, 25);

                if (collision) {
                    startSound(mp3Explode,PLAY_ONCE);
                    collision=false;
                }
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated) {
                newGame();
            }


        }

    }


    public boolean collision(GameObject a, GameObject b) {
        if(Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }


    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if(!disappear) {
                player.draw(canvas);
            }

            //draw smokepuffs
            for(Exhaust sp: smoke) {
                sp.draw(canvas);
            }
            //draw missiles
            for(Missile m: missiles) {
                m.draw(canvas);
            }


            //draw topborder
            for(TopBorder tb: topborder) {
                tb.draw(canvas);
            }

            //draw botborder
            for(BotBorder bb: botborder) {
                bb.draw(canvas);
            }

            //draw explosion
            if(gameStarted) {
                explosion.draw(canvas);
            }

            drawText(canvas);
            canvas.restoreToCount(savedState);
        }
    }


    public void updateTopBorder() {
        //every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%50 ==0) {
            topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
            ),topborder.get(topborder.size()-1).getX()+20,0,(int)((rand.nextDouble()*(maxBorderHeight
            ))+1)));
        }
        for(int i = 0; i<topborder.size(); i++) {
            topborder.get(i).update();
            if(topborder.get(i).getX()<-20) {
                topborder.remove(i);
                //remove element if out of screen, replace it by adding a new one

                //calculate topdown which determines the direction the border is moving (up or down)
                if(topborder.get(topborder.size()-1).getHeight()>=maxBorderHeight) {
                    topDown = false;
                }
                if(topborder.get(topborder.size()-1).getHeight()<=minBorderHeight) {
                    topDown = true;
                }
                //new border added will have larger height
                if(topDown) {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()+1));
                } else {
                    //new border added wil have smaller height
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()-1));
                }

            }
        }

    }


    public void updateBottomBorder() {
        //every 40 points, insert randomly placed bottom blocks that break pattern
        if(player.getScore()%40 == 0) {
            botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    botborder.get(botborder.size()-1).getX()+20,(int)((rand.nextDouble()
                    *maxBorderHeight)+(HEIGHT-maxBorderHeight))));
        }

        //update bottom border
        for(int i = 0; i<botborder.size(); i++) {
            botborder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(botborder.get(i).getX()<-20) {
                botborder.remove(i);


                //determine if border will be moving up or down
                if (botborder.get(botborder.size() - 1).getY() <= HEIGHT-maxBorderHeight) {
                    botDown = true;
                }
                if (botborder.get(botborder.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }

                if (botDown) {
                    botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botborder.get(botborder.size() - 1).getX() + 20, botborder.get(botborder.size() - 1
                    ).getY() + 1));
                } else {
                    botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botborder.get(botborder.size() - 1).getX() + 20, botborder.get(botborder.size() - 1
                    ).getY() - 1));
                }
            }
        }
    }


    public void newGame() {
        Log.i(TAG, "newGame: called - releasing and reloading soundpool");
        // release SoundPool and reference as required in documentation
        mSoundPool.release();
        mSoundPool=null;
        mSounds.clear();

        // create a new instance from scratch and initialize sound vars
        loadSounds();


        // If the new score is better that the record, update and notify the hosting activity
        if(player.getScore()> currentHighScore) {
            currentHighScore = player.getScore();
            if (mHighScoreListener!=null)
                mHighScoreListener.onHighScoreUpdated(currentHighScore);
        }

        player.resetScore();

        disappear = false;

        botborder.clear();
        topborder.clear();

        missiles.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetDY();
        player.setY(HEIGHT/2);

        //initial top border
        for(int i = 0; i*20<WIDTH+40;i++) {
            //create first top border
            if(i==0) {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, 10));
            } else {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, topborder.get(i-1).getHeight()+1));
            }
        }

        //initial bottom border
        for(int i = 0; i*20<WIDTH+40; i++) {
            if(i==0) {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20, HEIGHT - minBorderHeight));
            } else {
                //adding borders until the initial screen is filed
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, botborder.get(i - 1).getY() - 1));
            }
        }

        newGameCreated = true;
    }


    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + player.getScore()*SCORE_BOOSTER, 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + currentHighScore *SCORE_BOOSTER, WIDTH - 215, HEIGHT - 10, paint);

        if(!player.getPlaying()&&newGameCreated&&reset) {
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }

}
