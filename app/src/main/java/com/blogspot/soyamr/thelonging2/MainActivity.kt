package com.blogspot.soyamr.thelonging2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.M00271117.motraining.rpgame.MrNomGame
import com.blogspot.soyamr.thelonging2.elements.house.Room
import com.blogspot.soyamr.thelonging2.engine.GameSurface
import com.blogspot.soyamr.thelonging2.helpers.Utils
import com.blogspot.soyamr.thelonging2.helpers.Utils.appluScallingX
import com.blogspot.soyamr.thelonging2.helpers.Utils.appluScallingY
import com.example.kaushiknsanji.bookslibrary.BookSearchActivity
import com.heyletscode.ihavetofly.IHaveToFlyActivity
import com.nav.gamepack.GamePackActivity
import com.sIlence.androidracer.AndroidRacer
import dk.brams.android.myfirstgame.Game
import fsu.android.tictactoe.TicTacToe
import progark.a15.MainMenuActivity


class MainActivity : AppCompatActivity(), ViewParent {
    lateinit var parameterFlyGameButton: RelativeLayout.LayoutParams
    lateinit var parameterHelicopterButton: RelativeLayout.LayoutParams
    lateinit var parameterTikTokToeButton: RelativeLayout.LayoutParams
    private val time = 120L;
    private lateinit var scrollview: ScrollView
    private lateinit var textView1: TextView
    private lateinit var buttonGoBack: Button
    lateinit var gameSurface: GameSurface
    lateinit var backgroundImageView: ImageView
    lateinit var currentRoom: Room
    lateinit var rootLayout: RelativeLayout
    lateinit var buttonOpenLibirary: Button
    lateinit var buttonOpenTikTokToe: Button
    lateinit var buttonOpenPuzzle: Button
    lateinit var buttonOpenFlyGame: Button
    lateinit var buttonOpenHelicopterGame: Button
    lateinit var parameterOpenLibirary: RelativeLayout.LayoutParams
    lateinit var parameterOpenPuzzle: RelativeLayout.LayoutParams
    lateinit var fillParentLayout: RelativeLayout.LayoutParams
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Use a RelativeLayout to overlap both SurfaceView and ImageView
        fillParentLayout = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        );
        rootLayout = RelativeLayout(this)
        rootLayout.layoutParams = fillParentLayout;

        showStartingScreen()

        setContentView(rootLayout)

    }

    private fun setTheScreen(editTextId: Int, buttonString: Int): Button {
        scrollview = ScrollView(this)
        rootLayout.addView(scrollview)
        val relativeLayout = RelativeLayout(this)
        scrollview.addView(relativeLayout, fillParentLayout)

        val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val font = ResourcesCompat.getFont(this, R.font.new_font)
        val editText = EditText(this)
        editText.setText(editTextId)
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setTypeface(font, Typeface.BOLD)
        editText.textSize = 30f
        editText.setTextColor(Color.WHITE)
        editText.setId(View.generateViewId());

        relativeLayout.addView(editText)
        relativeLayout.setBackgroundColor(Color.BLACK)
        relativeLayout.setId(View.generateViewId());


        val button = Button(this)
        button.text = resources.getText(buttonString)
        button.setTypeface(font, Typeface.BOLD)

        lp.addRule(RelativeLayout.BELOW, editText.getId());
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL)
        relativeLayout.addView(button, lp)
        return button;
    }

    private fun showStartingScreen() {
        val button = setTheScreen(R.string.game_story, R.string.start)
        button.setOnClickListener {
            rootLayout.removeView(scrollview)
            showInstrucitons()
        }
    }

    var ctr = 0;

    private fun showInstrucitons() {

        val imageView = ImageView(this)
        imageView.background = resources.getDrawable(R.drawable.instruction2)
        val imageViewParameter = RelativeLayout
            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.setOnClickListener {
            if (++ctr == 2) {
                rootLayout.removeView(imageView)
                startGame()
            } else {
                imageView.background = resources.getDrawable(R.drawable.instruction1)
            }
        }
        rootLayout.addView(imageView, imageViewParameter)
    }

    private fun showEndingScreen() {
        this@MainActivity.runOnUiThread {
            removeButtons()
            gameSurface.pause()
            rootLayout.removeView(gameSurface)
            val button = setTheScreen(R.string.game_ending, R.string.end)
            button.setOnClickListener {
                rootLayout.removeView(scrollview)
                finish()
            }
        }
    }

    private fun startTimer() {
        object : CountDownTimer(60 * 60 * time, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished % (60 * 1000) / 1000
                val minute = millisUntilFinished % (60 * 60 * 1000) / (60 * 1000)
                val hour = millisUntilFinished % (24 * 60 * 60 * 1000) / (60 * 60 * 1000)
                val day = millisUntilFinished / (24 * 60 * 60 * 1000)
                textView1.text = "${day}D:${hour}H:${minute}M:${second}S"
            }

            override fun onFinish() {
                showEndingScreen()
            }
        }.start()
    }

    private fun startGame() {
        firstTime = false;
        setScallingFactor()
        gameSurface = GameSurface(this)
        gameSurface.setZOrderOnTop(true)
        gameSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);

        backgroundImageView = ImageView(this)
        backgroundImageView.scaleType = ImageView.ScaleType.FIT_XY

        rootLayout.addView(gameSurface, fillParentLayout);
        rootLayout.addView(backgroundImageView, fillParentLayout);

        initalizeButtons()
        initalizeTimer()
        startTimer()
    }

    private fun initalizeTimer() {
        val font = ResourcesCompat.getFont(this, R.font.new_font)
        textView1 = TextView(this)
        textView1.setTypeface(font, Typeface.BOLD)
        textView1.textSize = 20f
        textView1.setTextColor(Color.BLACK)
        val parameterTimerTV1 = RelativeLayout
            .LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        parameterTimerTV1.leftMargin = appluScallingX(appluScallingX(145))
        parameterTimerTV1.topMargin = appluScallingY(appluScallingY(50))

        val parameterTimerTV2 = RelativeLayout
            .LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        parameterTimerTV2.leftMargin = appluScallingX(appluScallingX(145))
        parameterTimerTV2.topMargin = appluScallingY(appluScallingY(125))

        rootLayout.addView(textView1, parameterTimerTV1)
    }


    private fun initalizeButtons() {
        buttonOpenLibirary = Button(this)
        buttonOpenLibirary.tag = "openLibraryButtonView"
        buttonOpenLibirary.background = getDrawable(R.drawable.openbook)
        buttonOpenLibirary.setOnClickListener {
            val intent = Intent(this, BookSearchActivity::class.java)
            removeButtons()
            startActivity(intent)
        }

        parameterOpenLibirary = RelativeLayout
            .LayoutParams(appluScallingX(200), appluScallingY(124))
        parameterOpenLibirary.leftMargin = appluScallingX(1883)
        parameterOpenLibirary.topMargin = appluScallingY(223)

        buttonOpenTikTokToe = Button(this)
        buttonOpenTikTokToe.tag = "buttonOpenTikTokToe"
        buttonOpenTikTokToe.background = getDrawable(R.drawable.o7background)
        buttonOpenTikTokToe.setOnClickListener {
            val intent = Intent(this, TicTacToe::class.java)
            removeButtons()
            startActivity(intent)
        }

        parameterTikTokToeButton = RelativeLayout
            .LayoutParams(appluScallingX(124), appluScallingY(124))
        parameterTikTokToeButton.leftMargin = appluScallingX(780)
        parameterTikTokToeButton.topMargin = appluScallingY(200)


        parameterOpenPuzzle = RelativeLayout
            .LayoutParams(appluScallingX(120), appluScallingY(150))
        parameterOpenPuzzle.leftMargin = appluScallingX(430)
        parameterOpenPuzzle.topMargin = appluScallingY(323)


        parameterFlyGameButton = RelativeLayout
            .LayoutParams(appluScallingX(200), appluScallingY(124))
        parameterFlyGameButton.leftMargin = appluScallingX(1883)
        parameterFlyGameButton.topMargin = appluScallingY(423)

        parameterHelicopterButton = RelativeLayout
            .LayoutParams(appluScallingX(200), appluScallingY(124))
        parameterHelicopterButton.leftMargin = appluScallingX(1883)
        parameterHelicopterButton.topMargin = appluScallingY(623)


        buttonOpenPuzzle = Button(this)
        buttonOpenPuzzle.tag = "buttonOpenPuzzle"
        buttonOpenPuzzle.background = getDrawable(R.drawable.puzzle)
        buttonOpenPuzzle.setOnClickListener {
            val intent = Intent(this, GamePackActivity::class.java)
            removeButtons()
            startActivity(intent)
        }


        buttonGoBack = Button(this)
        buttonGoBack.tag = "goBackButtonView"
        buttonGoBack.background = getDrawable(R.drawable.bed_room_day)
        buttonGoBack.setOnClickListener {
            gameSurface.changeBackground(currentRoom.nextRoom)
            removeButtons()
            gameSurface.moveToTheLeft()
        }

        buttonOpenFlyGame = Button(this)
        buttonOpenFlyGame.tag = "buttonOpenFlyGame"
        buttonOpenFlyGame.background = getDrawable(R.drawable.fly1)
        buttonOpenFlyGame.setOnClickListener {
            val intent = Intent(this, IHaveToFlyActivity::class.java)
            startActivity(intent)
        }

        buttonOpenHelicopterGame = Button(this)
        buttonOpenHelicopterGame.tag = "buttonOpenHelicopterGame"
        buttonOpenHelicopterGame.background = getDrawable(R.drawable.helicopter2)
        buttonOpenHelicopterGame.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }
    }

    private fun setScallingFactor() {
        val xy = getScreenMetrics();
        Utils.setXScalingFactor(xy.first, xy.second)
    }

    override fun getRoomBitmap(roomID: Int): Bitmap? {
        return BitmapFactory.decodeResource(this.resources, roomID)
    }

    override fun addButtonBookShelf() {
        this@MainActivity.runOnUiThread {
            if (rootLayout.findViewWithTag<Button>("openLibraryButtonView") == null)
                rootLayout.addView(buttonOpenLibirary, parameterOpenLibirary)

        }
    }


    override fun initailizeBalaconButtons() {
        this@MainActivity.runOnUiThread {
            if (rootLayout.findViewWithTag<Button>("goBackButtonView") == null)
                rootLayout.addView(buttonGoBack, parameterOpenLibirary)
            if (rootLayout.findViewWithTag<Button>("buttonOpenFlyGame") == null)
                rootLayout.addView(buttonOpenFlyGame, parameterFlyGameButton)
            if (rootLayout.findViewWithTag<Button>("buttonOpenHelicopterGame") == null)
                rootLayout.addView(buttonOpenHelicopterGame, parameterHelicopterButton)
        }
    }


    override fun addPuzzleButton() {
        this@MainActivity.runOnUiThread {
            if (rootLayout.findViewWithTag<Button>("buttonOpenPuzzle") == null)
                rootLayout.addView(buttonOpenPuzzle, parameterOpenPuzzle)
        }
    }

    override fun removeButtons() {
        this@MainActivity.runOnUiThread {
            rootLayout.removeView(buttonGoBack)
            rootLayout.removeView(buttonOpenFlyGame)
            rootLayout.removeView(buttonOpenHelicopterGame)
            rootLayout.removeView(buttonOpenLibirary)
            rootLayout.removeView(buttonOpenPuzzle)
            rootLayout.removeView(buttonOpenTikTokToe)
        }
    }

    override fun startPokimonGame() {
        val intent = Intent(this, MrNomGame::class.java)
        startActivity(intent)
    }

    override fun startRaceGame() {
        val intent = Intent(this, AndroidRacer::class.java)
        startActivity(intent)
    }

    override fun startWangGame() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun addTIKTOKTOEButton() {
        this@MainActivity.runOnUiThread {
            if (rootLayout.findViewWithTag<Button>("buttonOpenTikTokToe") == null)
                rootLayout.addView(buttonOpenTikTokToe, parameterTikTokToeButton)

        }
    }

    private fun getScreenMetrics(): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return Pair(height, width)
    }

    override fun changeBackground(room: Room) {
        this@MainActivity.runOnUiThread {
            backgroundImageView.setImageBitmap(room.roomBitmap);
            currentRoom = room
            removeButtons()
        }
    }

    override fun getContext(): Context {
        return this;
    }

    override fun onPause() {
        super.onPause()
        if (!firstTime) {
            gameSurface.pause()
        }
    }

    var firstTime = true
    override fun onResume() {
        super.onResume()
        if (!firstTime) {
            gameSurface.resume()
        }

    }
}
